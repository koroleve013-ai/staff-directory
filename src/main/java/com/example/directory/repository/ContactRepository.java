package com.example.directory.repository;

import com.example.directory.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByDepartmentId(Long departmentId);
    boolean existsByDepartmentId(Long departmentId);
}
