package com.example.directory.controller;

import com.example.directory.entity.Contact;
import com.example.directory.entity.Department;
import com.example.directory.repository.ContactRepository;
import com.example.directory.repository.DepartmentRepository;
import com.example.directory.repository.FavoriteRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {
    private final ContactRepository contacts;
    private final DepartmentRepository departments;
    private final FavoriteRepository favorites;

    public ContactController(ContactRepository contacts, DepartmentRepository departments,
                             FavoriteRepository favorites) {
        this.contacts = contacts;
        this.departments = departments;
        this.favorites = favorites;
    }

    // GET /api/contacts?search=иван&departmentId=1  (оба параметра необязательны)
    @GetMapping
    public List<Contact> getAll(@RequestParam(required = false) String search,
                                @RequestParam(required = false) Long departmentId) {
        List<Contact> list = (departmentId == null) ? contacts.findAll()
                                                    : contacts.findByDepartmentId(departmentId);
        if (search == null || search.isBlank()) return list;
        String q = search.toLowerCase();
        return list.stream()
                .filter(c -> contains(c.getFullName(), q) || contains(c.getPosition(), q)
                          || contains(c.getEmail(), q) || contains(c.getPhone(), q))
                .toList();
    }

    @GetMapping("/{id}")
    public Contact getOne(@PathVariable Long id) {
        return find(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Contact create(@RequestBody Contact contact) {
        if (contact.getFullName() == null || contact.getFullName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ФИО обязательно");
        }
        contact.setId(null);
        contact.setDepartment(resolveDepartment(contact.getDepartment()));
        return contacts.save(contact);
    }

    @PutMapping("/{id}")
    public Contact update(@PathVariable Long id, @RequestBody Contact data) {
        Contact c = find(id);
        c.setFullName(data.getFullName());
        c.setPosition(data.getPosition());
        c.setPhone(data.getPhone());
        c.setEmail(data.getEmail());
        c.setDepartment(resolveDepartment(data.getDepartment()));
        return contacts.save(c);
    }

    @DeleteMapping("/{id}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        find(id);
        favorites.deleteByContactId(id);   // сначала убираем из избранного
        contacts.deleteById(id);
    }

    private Contact find(Long id) {
        return contacts.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Контакт не найден"));
    }

    private Department resolveDepartment(Department d) {
        if (d == null || d.getId() == null) return null;
        return departments.findById(d.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Отдел не найден"));
    }

    private boolean contains(String field, String q) {
        return field != null && field.toLowerCase().contains(q);
    }
}
