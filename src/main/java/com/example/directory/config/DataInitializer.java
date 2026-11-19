package com.example.directory.config;

import com.example.directory.entity.*;
import com.example.directory.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// Тестовые данные при первом запуске
@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository users;
    private final DepartmentRepository departments;
    private final ContactRepository contacts;
    private final PasswordEncoder encoder;

    public DataInitializer(UserRepository users, DepartmentRepository departments,
                           ContactRepository contacts, PasswordEncoder encoder) {
        this.users = users;
        this.departments = departments;
        this.contacts = contacts;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (users.count() > 0) return;

        users.save(new AppUser("admin", encoder.encode("admin123"), Role.ADMIN));
        users.save(new AppUser("hr", encoder.encode("hr123"), Role.HR));
        users.save(new AppUser("user", encoder.encode("user123"), Role.USER));

        Department it = departments.save(new Department("IT-отдел"));
        Department acc = departments.save(new Department("Бухгалтерия"));
        Department hr = departments.save(new Department("Отдел кадров"));

        contacts.save(new Contact("Иванов Иван Петрович", "Системный администратор", "+7 900 111-11-11", "ivanov@company.ru", it));
        contacts.save(new Contact("Петрова Анна Сергеевна", "Главный бухгалтер", "+7 900 222-22-22", "petrova@company.ru", acc));
        contacts.save(new Contact("Сидоров Олег Викторович", "Программист", "+7 900 333-33-33", "sidorov@company.ru", it));
        contacts.save(new Contact("Кузнецова Мария Ивановна", "Специалист по кадрам", "+7 900 444-44-44", "kuznetsova@company.ru", hr));
    }
}
