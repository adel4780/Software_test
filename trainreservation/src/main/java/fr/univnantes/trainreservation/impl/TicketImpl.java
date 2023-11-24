package fr.univnantes.trainreservation.impl;

import fr.univnantes.trainreservation.ReservationException;
import fr.univnantes.trainreservation.Ticket;
import fr.univnantes.trainreservation.Trip;

public class TicketImpl implements Ticket {

    private boolean cancelled;
    private String passengerName;
    private Trip trip;

    public TicketImpl(String passengerName, Trip trip) {
        this.cancelled = false;
        this.passengerName = passengerName;
        this.trip = trip;
    }


    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    @Override
    public String getPassengerName() {
        return this.passengerName;
    }

    @Override
    public Trip getTrip() {
        return this.trip;
    }

    @Override
    public void cancel() {
        this.cancelled = true;
    }

    @Override
    public Ticket exchangeTicket(Trip trip) throws ReservationException {
        if (this.getTrip().getOrigin() != trip.getOrigin()
                || this.getTrip().getDestination() != trip.getDestination()
                || this.getTrip().isCancelled()
                || this.getTrip().getPlannedDepartureTime().isAfter(trip.getPlannedDepartureTime())
        ) {
            throw new ReservationException();
        }
        this.cancel();
        return new TicketImpl(this.getPassengerName(), trip);
    }
}
