package com.example.directory.controller;

import com.example.directory.entity.Contact;
import com.example.directory.entity.Favorite;
import com.example.directory.repository.ContactRepository;
import com.example.directory.repository.FavoriteRepository;
import com.example.directory.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// Избранное текущего пользователя
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {
    private final FavoriteRepository favorites;
    private final UserRepository users;
    private final ContactRepository contacts;

    public FavoriteController(FavoriteRepository favorites, UserRepository users, ContactRepository contacts) {
        this.favorites = favorites;
        this.users = users;
        this.contacts = contacts;
    }

    @GetMapping
    public List<Contact> myFavorites(Authentication auth) {
        return favorites.findByUserUsername(auth.getName()).stream()
                .map(Favorite::getContact).toList();
    }

    @PostMapping("/{contactId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@PathVariable Long contactId, Authentication auth) {
        if (favorites.findByUserUsernameAndContactId(auth.getName(), contactId).isPresent()) return;
        var user = users.findByUsername(auth.getName()).orElseThrow();
        var contact = contacts.findById(contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Контакт не найден"));
        favorites.save(new Favorite(user, contact));
    }

    @DeleteMapping("/{contactId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long contactId, Authentication auth) {
        favorites.findByUserUsernameAndContactId(auth.getName(), contactId)
                .ifPresent(favorites::delete);
    }
}
