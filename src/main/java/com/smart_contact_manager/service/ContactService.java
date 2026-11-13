package com.smart_contact_manager.service;

import com.smart_contact_manager.entity.Contact;
import com.smart_contact_manager.entity.User;
import com.smart_contact_manager.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    public Contact saveContact(Contact contact, User user) {
        contact.setUser(user);
        return contactRepository.save(contact);
    }

    public Page<Contact> getContactsByUser(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return contactRepository.findByUser(user, pageable);
    }

    public Contact getContactById(int cId, User user) {
        Contact contact = contactRepository.findById(cId)
                .orElseThrow(() -> new RuntimeException("Contact not found with ID: " + cId));

        if (contact.getUser().getId() != user.getId()) {
            throw new SecurityException("You are not authorized to view this contact.");
        }
        return contact;
    }

    public Contact updateContact(Contact updatedContact, User user) {
        Contact existingContact = getContactById(updatedContact.getCId(), user);

        existingContact.setName(updatedContact.getName());
        existingContact.setSecondName(updatedContact.getSecondName());
        existingContact.setWork(updatedContact.getWork());
        existingContact.setEmail(updatedContact.getEmail());
        existingContact.setPhone(updatedContact.getPhone());
        existingContact.setDescription(updatedContact.getDescription());

        return contactRepository.save(existingContact);
    }

    public void deleteContact(int cId, User user) {
        Contact contact = getContactById(cId, user);
        contactRepository.delete(contact);
    }

    public List<Contact> searchContacts(String query, User user) {
        return contactRepository.findByNameContainingIgnoreCaseAndUser(query, user);
    }

    public long getTotalContactsCount(User user) {
        return contactRepository.countByUser(user);
    }
}