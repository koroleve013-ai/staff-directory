package com.example.directory.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Связь «пользователь добавил контакт в избранное»
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "contact_id"}))
@Getter @Setter @NoArgsConstructor
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "contact_id")
    private Contact contact;

    public Favorite(AppUser user, Contact contact) {
        this.user = user;
        this.contact = contact;
    }
}
