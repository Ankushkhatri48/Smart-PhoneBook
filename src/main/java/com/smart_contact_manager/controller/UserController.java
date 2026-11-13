package com.smart_contact_manager.controller;

import com.smart_contact_manager.entity.Contact;
import com.smart_contact_manager.entity.User;
import com.smart_contact_manager.service.ContactService;
import com.smart_contact_manager.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ContactService contactService;

    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);
        model.addAttribute("user", user);
    }

    @GetMapping("/index")
    public String dashboard(Model model, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        long totalContacts = contactService.getTotalContactsCount(user);

        model.addAttribute("title", "User Dashboard");
        model.addAttribute("totalContacts", totalContacts);
        return "user/dashboard";
    }

    @GetMapping("/add-contact")
    public String openAddContactForm(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "user/add_contact";
    }

    @PostMapping("/process-contact")
    public String processContact(
            @Valid @ModelAttribute("contact") Contact contact,
            BindingResult bindingResult,
            Principal principal,
            Model model,
            HttpSession session) {

        try {
            if (bindingResult.hasErrors()) {
                model.addAttribute("title", "Add Contact");
                return "user/add_contact";
            }

            User user = userService.getUserByEmail(principal.getName());
            contactService.saveContact(contact, user);

            session.setAttribute("message", "Contact saved successfully!");
            session.setAttribute("messageType", "alert-success");
            return "redirect:/user/add-contact";

        } catch (Exception e) {
            session.setAttribute("message", "Something went wrong: " + e.getMessage());
            session.setAttribute("messageType", "alert-danger");
            return "user/add_contact";
        }
    }

    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        Page<Contact> contacts = contactService.getContactsByUser(user, page, 5);

        model.addAttribute("title", "Your Contacts");
        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contacts.getTotalPages());

        return "user/view_contacts";
    }

    @GetMapping("/contact/{cId}")
    public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        Contact contact = contactService.getContactById(cId, user);

        model.addAttribute("title", contact.getName() + " - Details");
        model.addAttribute("contact", contact);
        return "user/contact_detail";
    }

    @GetMapping("/delete/{cId}")
    public String deleteContact(@PathVariable("cId") Integer cId, Principal principal, HttpSession session) {
        try {
            User user = userService.getUserByEmail(principal.getName());
            contactService.deleteContact(cId, user);

            session.setAttribute("message", "Contact deleted successfully.");
            session.setAttribute("messageType", "alert-success");
        } catch (Exception e) {
            session.setAttribute("message", "Unable to delete contact: " + e.getMessage());
            session.setAttribute("messageType", "alert-danger");
        }
        return "redirect:/user/show-contacts/0";
    }

    @PostMapping("/update-contact/{cId}")
    public String openUpdateForm(@PathVariable("cId") Integer cId, Model model, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        Contact contact = contactService.getContactById(cId, user);

        model.addAttribute("title", "Update Contact");
        model.addAttribute("contact", contact);
        return "user/update_contact";
    }

    @PostMapping("/process-update")
    public String updateHandler(
            @Valid @ModelAttribute("contact") Contact contact,
            BindingResult bindingResult,
            Principal principal,
            Model model,
            HttpSession session) {

        try {
            if (bindingResult.hasErrors()) {
                model.addAttribute("title", "Update Contact");
                return "user/update_contact";
            }

            User user = userService.getUserByEmail(principal.getName());
            Contact updated = contactService.updateContact(contact, user);

            session.setAttribute("message", "Contact updated successfully!");
            session.setAttribute("messageType", "alert-success");
            return "redirect:/user/contact/" + updated.getCId();

        } catch (Exception e) {
            session.setAttribute("message", "Update failed: " + e.getMessage());
            session.setAttribute("messageType", "alert-danger");
            return "redirect:/user/show-contacts/0";
        }
    }

    @GetMapping("/search/{query}")
    @ResponseBody
    public ResponseEntity<List<Contact>> search(@PathVariable("query") String query, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        List<Contact> contacts = contactService.searchContacts(query, user);
        return ResponseEntity.ok(contacts);
    }
}