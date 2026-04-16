package com.complaint.tickettracker.controller;

import com.complaint.tickettracker.dto.*;
import com.complaint.tickettracker.enums.TicketCategory;
import com.complaint.tickettracker.enums.TicketStatus;
import com.complaint.tickettracker.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    /**
     * POST /api/tickets
     * Submit a new complaint ticket.
     */
    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody CreateTicketRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.createTicket(request));
    }

    /**
     * GET /api/tickets
     * Get all tickets, with optional filters:
     *   ?status=OPEN|IN_PROGRESS|RESOLVED
     *   ?category=BILLING|TECHNICAL|DELIVERY|PRODUCT|SERVICE|OTHER
     *   Both params can be combined.
     */
    @GetMapping
    public ResponseEntity<List<TicketResponse>> getTickets(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketCategory category) {
        return ResponseEntity.ok(ticketService.filterTickets(status, category));
    }

    /**
     * GET /api/tickets/{id}
     * Get a single ticket by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicket(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    /**
     * PATCH /api/tickets/{id}/assign
     * Admin assigns the ticket to a person.
     * Auto-advances status from OPEN → IN_PROGRESS.
     */
    @PatchMapping("/{id}/assign")
    public ResponseEntity<TicketResponse> assignTicket(
            @PathVariable Long id,
            @Valid @RequestBody AssignTicketRequest request) {
        return ResponseEntity.ok(ticketService.assignTicket(id, request));
    }

    /**
     * PATCH /api/tickets/{id}/status
     * Update ticket status (OPEN → IN_PROGRESS → RESOLVED).
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(ticketService.updateStatus(id, request));
    }

    /**
     * DELETE /api/tickets/{id}
     * Delete a ticket.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}
