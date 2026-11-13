package com.smart_contact_manager.repository;

import com.smart_contact_manager.entity.Contact;
import com.smart_contact_manager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {

    // Fetch paginated contacts belonging to a specific user
    Page<Contact> findByUser(User user, Pageable pageable);

    // Search contacts by name for a specific user
    List<Contact> findByNameContainingIgnoreCaseAndUser(String name, User user);

    // Count total contacts for dashboard statistics
    long countByUser(User user);
}