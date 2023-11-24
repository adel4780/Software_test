package fr.univnantes.trainreservation;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Represents a ticket reservation system. It can be used both to manage
 * new trips, trains and cities, and to manage tickets from the customer side.
 * It is aware of all existing cities, trips, tickets, and trains.
 */
public interface TicketReservationSystem {


    /**
     * Retrieves the list of all booked tickets (not cancelled).
     * @return The list of all booked tickets (not cancelled).
     */
    List<Ticket> getAllBookedTickets();

    /**
     * Retrieves the list of all cancelled tickets.
     * @return The list of all cancelled tickets.
     */
    List<Ticket> getAllCancelledTickets();

    /**
     * Finds all the trips eligible for an exchange against a ticket.
     * A trip is eligible for an exchange if the trip has the same origin and the same destination as the ticket,
     * and if the trip has not been cancelled, and if the trip planned departure is after the ticket planned departure.
     * @param ticket The ticket for which to find exchange possibilities.
     * @return The list of all trips eligible for the exchange.
     */
    List<Trip> findPossibleExchanges(Ticket ticket);

    /**
     * Finds all the available trips (not cancelled) originating from a City.
     * @param origin The city from which trips should originate.
     * @return All non-cancelled trips matching the criteria.
     */
    List<Trip> findAvailableTrips(City origin, LocalDate date);

    /**
     * Finds all the available trips (not cancelled) originating from a City, and arriving in a specific city, at a specific date.
     * @param origin The city from which trips should originate.
     * @param destination The city to which trips should arrive.
     * @param date The date of the trip.
     * @return All non-cancelled trips matching the criteria.
     */
    List<Trip> findAvailableTrips(City origin, City destination, LocalDate date);

    /**
     * Retrieves the list of all cities registered in the system.
     * @return The list of all cities registered in the system.
     */
    List<City> getCities();

    /**
     * Retrieves the list of all trains registered in the system.
     * @return The list of all trains registered in the system.
     */
    List<Train> getAllTrains();

    /**
     * Retrieves the list of all non-cancelled trips registered in the system.
     * @return The list of all non-cancelled trips registered in the system.
     */
    List<Trip> getAllTrips();

    /**
     * Retrieves the list of all cancelled trips registered in the system.
     * @return The list of all cancelled trips registered in the system.
     */
    List<Trip> getAllCancelledTrips();

    /**
     * Adds a new city in the system.
     * @param city The city to add.
     */
    void addCity(City city);

    /**
     * Adds a new train in the system.
     * @param train The train to add.
     */
    void addTrain(Train train);
    
    /**
     * Creates and registers a new trip in the system.
     * This new trip must take place *after* the last trip of the chosen train (based on the real arrival time).
     * In addition, a trip can only be created if the following constraints are satisfied:
     * - The last destination of the train must be in the same city as the origin of the new trip.
     * - The last arrival of the train must be at least 10 minutes from the departure of the new trip (using real departure and arrival times).
     * - The arrival must come after the departure.
     * - The origin must be different from the destination.
     * @return The created registered trip.
     * @throws TripException If one of the above constraints was not satisfied.
     */
    Trip createTrip(City origin, City destination, Train train, Instant departure, Instant arrival) throws TripException;

    /**
     * Cancels a trip.
     * @param trip The trip to cancel.
     */
    void cancelTrip(Trip trip);


    /**
     * Adds a departure delay to a trip.
     * Also automatically adds an arrival delay of the same amount.
     * @param delay The amount of delay to add.
     */
    void delayTripDeparture(Trip trip, Duration delay);

    /**
     * Adds an arrival delay to a trip.
     * Also automatically also adds the delay to the departure of the next trip of the train.
     * @param delay The amount of delay to add.
     */
    void delayTripArrival(Trip trip, Duration delay);

    /**
     * Finds the trip that follows an existing trip of a train.
     * @param train The train.
     * @param trip The trip.
     * @return The trip following the given trip, for the train.
     * @throws TripException if the trip's train is not the same
     */
    Optional<Trip> findNextTripOfTrain(Train train, Trip trip) throws TripException;

    /**
     * Finds the trip that precedes an existing trip of a train.
     * @param train The train.
     * @param trip The trip.
     * @return The trip preceding the given trip, for the train.
     * @throws TripException if the trip's train is not the same
     */
    Optional<Trip> findPreviousTripOfTrain(Train train, Trip trip) throws TripException;

    /**
     * Finds and orders all the trips of a given train.
     * @param train The train.
     * @return The ordered list of all trips for this train.
     */
    List<Trip> findOrderedTripsOfTrain(Train train);


}

