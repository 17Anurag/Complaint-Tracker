package com.complaint.tickettracker.config;

import com.complaint.tickettracker.entity.Ticket;
import com.complaint.tickettracker.entity.User;
import com.complaint.tickettracker.enums.Role;
import com.complaint.tickettracker.enums.TicketCategory;
import com.complaint.tickettracker.enums.TicketStatus;
import com.complaint.tickettracker.repository.TicketRepository;
import com.complaint.tickettracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(TicketRepository ticketRepo,
                               UserRepository userRepo,
                               PasswordEncoder passwordEncoder) {
        return args -> {

            // ── Seed users (only if not already present) ──────────────────────
            if (!userRepo.existsByUsername("admin")) {
                userRepo.save(User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .fullName("System Admin")
                        .role(Role.ROLE_ADMIN)
                        .build());
            }

            if (!userRepo.existsByUsername("user")) {
                userRepo.save(User.builder()
                        .username("user")
                        .password(passwordEncoder.encode("user123"))
                        .fullName("Regular User")
                        .role(Role.ROLE_USER)
                        .build());
            }

            // ── Seed tickets (only if table is empty) ─────────────────────────
            if (ticketRepo.count() == 0) {
                ticketRepo.save(Ticket.builder()
                        .title("Incorrect billing amount")
                        .description("I was charged twice for my subscription this month.")
                        .category(TicketCategory.BILLING)
                        .status(TicketStatus.OPEN)
                        .raisedBy("alice@example.com")
                        .build());

                ticketRepo.save(Ticket.builder()
                        .title("App crashes on login")
                        .description("The mobile app crashes every time I try to log in on Android 14.")
                        .category(TicketCategory.TECHNICAL)
                        .status(TicketStatus.IN_PROGRESS)
                        .raisedBy("bob@example.com")
                        .assignedTo("dev-team")
                        .build());

                ticketRepo.save(Ticket.builder()
                        .title("Package not delivered")
                        .description("My order #12345 was marked delivered but I never received it.")
                        .category(TicketCategory.DELIVERY)
                        .status(TicketStatus.RESOLVED)
                        .raisedBy("carol@example.com")
                        .assignedTo("logistics-team")
                        .build());
            }
        };
    }
}
