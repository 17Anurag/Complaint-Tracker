package com.complaint.tickettracker.repository;

import com.complaint.tickettracker.entity.Ticket;
import com.complaint.tickettracker.enums.TicketCategory;
import com.complaint.tickettracker.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByStatus(TicketStatus status);

    List<Ticket> findByCategory(TicketCategory category);

    List<Ticket> findByStatusAndCategory(TicketStatus status, TicketCategory category);
}
