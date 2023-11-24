package fr.univnantes.trainreservation;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Represents a trip between two cities, using a specific train and at a specific time.
 */
public interface Trip {

    /**
     * Retrieves the origin city of the trip.
     *
     * @return the origin city of the trip.
     */
    City getOrigin();

    /**
     * Retrieves the destination city of the trip.
     *
     * @return the destination city of the trip.
     */
    City getDestination();

    /**
     * Retrieves the train used for the trip.
     *
     * @return the train used for the trip.
     */
    Train getTrain();

    /**
     * Finds the planned duration of the trip.
     *
     * @return The planned duration of the trip (ie. the duration between planned departure and arrival).
     */
    Duration findPlannedDuration();

    /**
     * Finds the real duration of the trip.
     *
     * @return The real duration of the trip (ie. the duration between real departure and arrival).
     */
    Duration findRealDuration();

    /**
     * Retrieves whether the trip was cancelled or not.
     *
     * @return true if the trip was cancelled, false otherwise.
     */
    boolean isCancelled();

    /**
     * Retrieves whether the trip was delayed or not.
     *
     * @return true if the trip was delayed (ie. if the amount of delay it above zero), false otherwise.
     */
    boolean isDelayed();

    /**
     * Cancels the trip.
     * This also automatically cancels all tickets for this trip.
     */
    void cancel();

    /**
     * Cancels one ticket made for this trip, and register this change in the trip.
     * @param ticket The ticket to cancel.
     * @throws ReservationException If the ticket is not for this trip.
     */
    void cancelTicket(Ticket ticket) throws ReservationException;

    /**
     * Get the initially planned departure time.
     *
     * @return the initially planned departure time.
     */
    Instant getPlannedDepartureTime();

    /**
     * Get the initially planned arrival time.
     *
     * @return the initially planned arrival time.
     */
    Instant getPlannedArrivalTime();

    /**
     * Finds the real departure time, considering delays.
     *
     * @return the real departure time, considering delays.
     */
    Instant findRealDepartureTime();

    /**
     * Finds the real arrival time, considering delays.
     *
     * @return the real arrival time, considering delays.
     */
    Instant findRealArrivalTime();

    /**
     * Retrieves the departure delay for this trip.
     *
     * @return the departure delay.
     */
    Duration getDepartureDelay();

    /**
     * Retrieves the arrival delay for this trip.
     *
     * @return the arrival delay.
     */
    Duration getArrivalDelay();

    /**
     * Retrieves the list of all (non-cancelled) booked tickets for this trip.
     *
     * @return the list of all (non-cancelled) booked tickets for this trip.
     */
    List<Ticket> getBookedTickets();

    /**
     * Retrieves the list of all cancelled tickets for this trip.
     *
     * @return the list of all cancelled tickets for this trip.
     */
    List<Ticket> getCancelledTickets();

    /**
     * Creates a new Ticket for the trip, and records the ticket in the trip.
     * It is only possible to book a ticket if the trip had not already reached the maximum amount of passengers.
     * @param passengerName The name of the passenger.
     * @return The ticket that has been booked.
     * @throws ReservationException If the trip has already reached the maximum amount of passengers.
     */
    Ticket bookTicket(String passengerName) throws ReservationException;

    /**
     * Adds a duration to the departure delay.
     * @param delay The duration delay.
     */
    void addDepartureDelay(Duration delay);

    /**
     * Adds a duration to the arrival delay.
     * @param delay The duration delay.
     */
    void addArrivalDelay(Duration delay);


}
