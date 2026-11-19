package com.example.directory.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    private String position;
    private String phone;
    private String email;

    // много сотрудников -> один отдел
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;

    public Contact(String fullName, String position, String phone, String email, Department department) {
        this.fullName = fullName;
        this.position = position;
        this.phone = phone;
        this.email = email;
        this.department = department;
    }
}
