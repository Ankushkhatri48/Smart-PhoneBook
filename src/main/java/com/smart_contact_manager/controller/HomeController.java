package com.smart_contact_manager.controller;

import com.smart_contact_manager.entity.User;
import com.smart_contact_manager.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home - Smart PhoneBook");
        return "home";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("title", "Register - Smart PhoneBook");
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/do_register")
    public String registerUser(
            @Valid @ModelAttribute("user") User user,
            BindingResult bindingResult,
            @RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
            Model model,
            HttpSession session) {

        try {
            if (!agreement) {
                throw new IllegalArgumentException("You must agree to the terms and conditions.");
            }

            if (bindingResult.hasErrors()) {
                model.addAttribute("user", user);
                return "signup";
            }

            userService.registerUser(user);
            model.addAttribute("user", new User());
            session.setAttribute("message", "Successfully Registered! You can now log in.");
            session.setAttribute("messageType", "alert-success");
            return "signup";

        } catch (Exception e) {
            model.addAttribute("user", user);
            session.setAttribute("message", "Error: " + e.getMessage());
            session.setAttribute("messageType", "alert-danger");
            return "signup";
        }
    }

    @GetMapping("/signin")
    public String customLogin(Model model) {
        model.addAttribute("title", "Login - Smart PhoneBook");
        return "login";
    }
}