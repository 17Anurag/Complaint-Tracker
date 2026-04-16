package com.complaint.tickettracker.controller;

import com.complaint.tickettracker.dto.*;
import com.complaint.tickettracker.enums.TicketCategory;
import com.complaint.tickettracker.enums.TicketStatus;
import com.complaint.tickettracker.exception.TicketNotFoundException;
import com.complaint.tickettracker.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final TicketService ticketService;

    // ── Dashboard ────────────────────────────────────────────────────────────

    @GetMapping("/")
    public String dashboard(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            Model model) {

        TicketStatus ts = parseStatus(status);
        TicketCategory tc = parseCategory(category);

        List<TicketResponse> tickets = ticketService.filterTickets(ts, tc);
        List<TicketResponse> all     = ticketService.getAllTickets();

        model.addAttribute("tickets",          tickets);
        model.addAttribute("selectedStatus",   status);
        model.addAttribute("selectedCategory", category);

        // Stats (always from full list)
        model.addAttribute("totalCount",      all.size());
        model.addAttribute("openCount",       all.stream().filter(t -> t.getStatus() == TicketStatus.OPEN).count());
        model.addAttribute("inProgressCount", all.stream().filter(t -> t.getStatus() == TicketStatus.IN_PROGRESS).count());
        model.addAttribute("resolvedCount",   all.stream().filter(t -> t.getStatus() == TicketStatus.RESOLVED).count());

        return "index";
    }

    // ── New ticket form ───────────────────────────────────────────────────────

    @GetMapping("/tickets/new")
    public String newTicketForm() {
        return "new-ticket";
    }

    @PostMapping("/tickets")
    public String submitTicket(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String category,
            @RequestParam String raisedBy,
            RedirectAttributes ra) {

        try {
            CreateTicketRequest req = new CreateTicketRequest();
            req.setTitle(title);
            req.setDescription(description);
            req.setCategory(TicketCategory.valueOf(category));
            req.setRaisedBy(raisedBy);
            TicketResponse created = ticketService.createTicket(req);
            ra.addFlashAttribute("success", "Ticket #" + created.getId() + " submitted successfully!");
            return "redirect:/";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to submit: " + e.getMessage());
            return "redirect:/tickets/new";
        }
    }

    // ── Ticket detail ─────────────────────────────────────────────────────────

    @GetMapping("/tickets/{id}")
    public String ticketDetail(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("ticket", ticketService.getTicketById(id));
            return "ticket-detail";
        } catch (TicketNotFoundException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/";
        }
    }

    // ── Assign ticket ─────────────────────────────────────────────────────────

    @PostMapping("/tickets/{id}/assign")
    public String assignTicket(
            @PathVariable Long id,
            @RequestParam String assignedTo,
            RedirectAttributes ra) {

        try {
            AssignTicketRequest req = new AssignTicketRequest();
            req.setAssignedTo(assignedTo);
            ticketService.assignTicket(id, req);
            ra.addFlashAttribute("success", "Ticket assigned to " + assignedTo);
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tickets/" + id;
    }

    // ── Update status ─────────────────────────────────────────────────────────

    @PostMapping("/tickets/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            RedirectAttributes ra) {

        try {
            UpdateStatusRequest req = new UpdateStatusRequest();
            req.setStatus(TicketStatus.valueOf(status));
            ticketService.updateStatus(id, req);
            ra.addFlashAttribute("success", "Status updated to " + status.replace("_", " "));
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tickets/" + id;
    }

    // ── Delete ticket ─────────────────────────────────────────────────────────

    @PostMapping("/tickets/{id}/delete")
    public String deleteTicket(@PathVariable Long id, RedirectAttributes ra) {
        try {
            ticketService.deleteTicket(id);
            ra.addFlashAttribute("success", "Ticket #" + id + " deleted.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/";
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private TicketStatus parseStatus(String s) {
        try { return s != null && !s.isBlank() ? TicketStatus.valueOf(s) : null; }
        catch (IllegalArgumentException e) { return null; }
    }

    private TicketCategory parseCategory(String c) {
        try { return c != null && !c.isBlank() ? TicketCategory.valueOf(c) : null; }
        catch (IllegalArgumentException e) { return null; }
    }
}
