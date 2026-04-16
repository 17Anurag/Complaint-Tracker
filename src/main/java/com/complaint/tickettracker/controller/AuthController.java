package com.complaint.tickettracker.controller;

import com.complaint.tickettracker.entity.User;
import com.complaint.tickettracker.enums.Role;
import com.complaint.tickettracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ── Login ─────────────────────────────────────────────────────────────────

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null)  model.addAttribute("error",  "Invalid username or password.");
        if (logout != null) model.addAttribute("success", "You have been logged out.");
        return "login";
    }

    // ── Register ──────────────────────────────────────────────────────────────

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String fullName,
            @RequestParam(defaultValue = "ROLE_USER") String role,
            RedirectAttributes ra) {

        if (userRepository.existsByUsername(username)) {
            ra.addFlashAttribute("error", "Username '" + username + "' is already taken.");
            return "redirect:/register";
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .role(Role.valueOf(role))
                .build();

        userRepository.save(user);
        ra.addFlashAttribute("success", "Account created! Please log in.");
        return "redirect:/login";
    }
}
