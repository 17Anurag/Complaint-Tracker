package com.complaint.tickettracker.service;

import com.complaint.tickettracker.dto.*;
import com.complaint.tickettracker.entity.Ticket;
import com.complaint.tickettracker.enums.TicketCategory;
import com.complaint.tickettracker.enums.TicketStatus;
import com.complaint.tickettracker.exception.InvalidStatusTransitionException;
import com.complaint.tickettracker.exception.TicketNotFoundException;
import com.complaint.tickettracker.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    // ── Submit a new complaint ──────────────────────────────────────────────────

    @Transactional
    public TicketResponse createTicket(CreateTicketRequest request) {
        Ticket ticket = Ticket.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .raisedBy(request.getRaisedBy())
                .status(TicketStatus.OPEN)
                .build();

        return toResponse(ticketRepository.save(ticket));
    }

    // ── Get all tickets ─────────────────────────────────────────────────────────

    public List<TicketResponse> getAllTickets() {
        return ticketRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Get single ticket ───────────────────────────────────────────────────────

    public TicketResponse getTicketById(Long id) {
        return toResponse(findOrThrow(id));
    }

    // ── Filter tickets by status and/or category ────────────────────────────────

    public List<TicketResponse> filterTickets(TicketStatus status, TicketCategory category) {
        List<Ticket> tickets;

        if (status != null && category != null) {
            tickets = ticketRepository.findByStatusAndCategory(status, category);
        } else if (status != null) {
            tickets = ticketRepository.findByStatus(status);
        } else if (category != null) {
            tickets = ticketRepository.findByCategory(category);
        } else {
            tickets = ticketRepository.findAll();
        }

        return tickets.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Admin: assign ticket to a person ────────────────────────────────────────

    @Transactional
    public TicketResponse assignTicket(Long id, AssignTicketRequest request) {
        Ticket ticket = findOrThrow(id);
        ticket.setAssignedTo(request.getAssignedTo());

        // Auto-advance to IN_PROGRESS when assigned (if still OPEN)
        if (ticket.getStatus() == TicketStatus.OPEN) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
        }

        return toResponse(ticketRepository.save(ticket));
    }

    // ── Update ticket status (enforces OPEN → IN_PROGRESS → RESOLVED) ──────────

    @Transactional
    public TicketResponse updateStatus(Long id, UpdateStatusRequest request) {
        Ticket ticket = findOrThrow(id);
        validateTransition(ticket.getStatus(), request.getStatus());
        ticket.setStatus(request.getStatus());
        return toResponse(ticketRepository.save(ticket));
    }

    // ── Delete ticket ───────────────────────────────────────────────────────────

    @Transactional
    public void deleteTicket(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new TicketNotFoundException(id);
        }
        ticketRepository.deleteById(id);
    }

    // ── Helpers ─────────────────────────────────────────────────────────────────

    private Ticket findOrThrow(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
    }

    /**
     * Enforces the allowed status flow: OPEN → IN_PROGRESS → RESOLVED.
     * A ticket cannot go backwards or skip a step.
     */
    private void validateTransition(TicketStatus current, TicketStatus next) {
        boolean valid = switch (current) {
            case OPEN        -> next == TicketStatus.IN_PROGRESS;
            case IN_PROGRESS -> next == TicketStatus.RESOLVED;
            case RESOLVED    -> false; // terminal state
        };

        if (!valid) {
            throw new InvalidStatusTransitionException(current, next);
        }
    }

    private TicketResponse toResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .category(ticket.getCategory())
                .status(ticket.getStatus())
                .assignedTo(ticket.getAssignedTo())
                .raisedBy(ticket.getRaisedBy())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }
}
