package com.example.directory.controller;

import com.example.directory.entity.Department;
import com.example.directory.repository.ContactRepository;
import com.example.directory.repository.DepartmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
    private final DepartmentRepository departments;
    private final ContactRepository contacts;

    public DepartmentController(DepartmentRepository departments, ContactRepository contacts) {
        this.departments = departments;
        this.contacts = contacts;
    }

    @GetMapping
    public List<Department> getAll() {
        return departments.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Department create(@RequestBody Department department) {
        department.setId(null);
        return departments.save(department);
    }

    @PutMapping("/{id}")
    public Department update(@PathVariable Long id, @RequestBody Department data) {
        Department d = departments.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Отдел не найден"));
        d.setName(data.getName());
        return departments.save(d);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (contacts.existsByDepartmentId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "В отделе есть сотрудники");
        }
        departments.deleteById(id);
    }
}
