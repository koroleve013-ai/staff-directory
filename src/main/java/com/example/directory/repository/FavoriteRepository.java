package com.example.directory.repository;

import com.example.directory.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserUsername(String username);
    Optional<Favorite> findByUserUsernameAndContactId(String username, Long contactId);
    void deleteByContactId(Long contactId);
    void deleteByUserId(Long userId);
}
