package com.example.directory.controller;

import com.example.directory.entity.AppUser;
import com.example.directory.repository.FavoriteRepository;
import com.example.directory.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// Управление пользователями — только ADMIN
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository users;
    private final FavoriteRepository favorites;
    private final PasswordEncoder encoder;

    public UserController(UserRepository users, FavoriteRepository favorites, PasswordEncoder encoder) {
        this.users = users;
        this.favorites = favorites;
        this.encoder = encoder;
    }

    @GetMapping
    public List<AppUser> getAll() {
        return users.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppUser create(@RequestBody AppUser user) {
        if (user.getUsername() == null || user.getPassword() == null || user.getRole() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нужны username, password и role");
        }
        if (users.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Логин занят");
        }
        user.setId(null);
        user.setPassword(encoder.encode(user.getPassword()));
        return users.save(user);
    }

    @PutMapping("/{id}")
    public AppUser update(@PathVariable Long id, @RequestBody AppUser data) {
        AppUser u = users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));
        if (data.getRole() != null) u.setRole(data.getRole());
        if (data.getPassword() != null) u.setPassword(encoder.encode(data.getPassword()));
        return users.save(u);
    }

    @DeleteMapping("/{id}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication auth) {
        AppUser u = users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));
        if (u.getUsername().equals(auth.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нельзя удалить самого себя");
        }
        favorites.deleteByUserId(id);
        users.delete(u);
    }
}
