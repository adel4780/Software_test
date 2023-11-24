package fr.univnantes.trainreservation;

/**
 * Represents a ticket booked for a trip by a customer.
 * A ticket is nominative, and is only for one specific trip.
 */
public interface Ticket {

    /**
     * Retrieves the status of the trip (cancelled or not).
     * @return True of the trip was cancelled, false otherwise.
     */
    boolean isCancelled();

    /**
     * Retrieves the name if the passenger.
     * @return The name of the passenger.
     */
    String getPassengerName();

    /**
     * Retrieves the trip that this ticket was booked for.
     * @return the trip that this ticket was booked for.
     */
    Trip getTrip();

    /**
     * Cancels this ticket. Cannot be undone.
     */
    void cancel();

    /**
     * Exchanges the ticket for a new ticket for a different trip.
     * Once exchanged, a ticket becomes cancelled.
     * It is only possible to exchange a ticket if the new trip has the same origin and the same destination,
     * and if the new trip has not been cancelled, and if the new trip planned departure is after the ticket planned departure.
     * @param trip The trip for the new ticket.
     * @throws ReservationException If the trip does not satisfy the constraints.
     * @return The new ticket
     */
    Ticket exchangeTicket(Trip trip) throws ReservationException;
}
