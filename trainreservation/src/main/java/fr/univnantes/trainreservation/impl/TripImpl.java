package fr.univnantes.trainreservation.impl;

import fr.univnantes.trainreservation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TripImpl implements Trip {

    private final City origin;
    private final City destination;
    private final Train train;
    private boolean cancelled;
    private final Instant departureTime;
    private final Instant arrivalTime;
    private Duration departureDelay;
    private Duration arrivalDelay;
    private final List<Ticket> bookedTickets;
    private final List<Ticket> cancelledTickets;


    public TripImpl(City origin, City destination, Train train, Instant departureTime, Instant arrivalTime) {
        this.origin = origin;
        this.destination = destination;
        this.train = train;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.bookedTickets = new ArrayList<>();
        this.cancelledTickets = new ArrayList<>();
        this.cancelled = false;
        this.departureDelay = Duration.ZERO;
        this.arrivalDelay = Duration.ZERO;
    }

    @Override
    public City getOrigin() {
        return this.origin;
    }

    @Override
    public City getDestination() {
        return this.destination;
    }

    @Override
    public Train getTrain() {
        return this.train;
    }

    @Override
    public Duration findPlannedDuration() {
        return Duration.between(this.departureTime, this.arrivalTime);
    }

    @Override
    public Duration findRealDuration() {
        return Duration.between(this.findRealDepartureTime(), this.findRealArrivalTime());
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public boolean isDelayed() {
        return !this.getDepartureDelay().equals(Duration.ZERO) && !this.getArrivalDelay().equals(Duration.ZERO.isZero());
    }

    @Override
    public void cancel() {
        this.cancelled = true;
        for (Ticket ticket : bookedTickets) {
            ticket.cancel();
        }
    }

    @Override
    public void cancelTicket(Ticket ticket) throws ReservationException {
        if (ticket.getTrip() != this) {
            throw new ReservationException();
        }
        ticket.cancel();
        this.bookedTickets.remove(ticket);
        this.cancelledTickets.add(ticket);
    }

    @Override
    public Instant getPlannedDepartureTime() {
        return departureTime;
    }

    @Override
    public Instant getPlannedArrivalTime() {
        return arrivalTime;
    }

    @Override
    public Instant findRealDepartureTime() {
        return departureTime.plus(departureDelay);
    }

    @Override
    public Instant findRealArrivalTime() {
        return arrivalTime.plus(arrivalDelay);
    }

    @Override
    public Duration getDepartureDelay() {
        return departureDelay;
    }

    @Override
    public Duration getArrivalDelay() {
        return arrivalDelay;
    }

    @Override
    public List<Ticket> getBookedTickets() {
        return Collections.unmodifiableList(bookedTickets);
    }

    @Override
    public List<Ticket> getCancelledTickets() {
        return Collections.unmodifiableList(cancelledTickets);
    }

    @Override
    public Ticket bookTicket(String passengerName) throws ReservationException {
        if (this.getBookedTickets().size() >= this.getTrain().getMaxPassengers()) {
            throw new ReservationException();
        }
        Ticket ticket = new TicketImpl(passengerName, this);
        this.bookedTickets.add(ticket);
        return ticket;
    }

    @Override
    public void addDepartureDelay(Duration delay) {
        this.departureDelay = this.departureDelay.plus(delay);
    }

    @Override
    public void addArrivalDelay(Duration delay) {
        this.arrivalDelay = this.arrivalDelay.plus(delay);
    }
}
