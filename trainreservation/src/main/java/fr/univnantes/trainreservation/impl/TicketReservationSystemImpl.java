package fr.univnantes.trainreservation.impl;

import fr.univnantes.trainreservation.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class TicketReservationSystemImpl implements TicketReservationSystem {

    private List<Trip> trips;
    private List<Trip> cancelledTrips;
    private List<City> cities;
    private List<Train> trains;
    private ZoneId timeZone;

    /**
     * TODO
     *
     * @param timeZone
     */
    public TicketReservationSystemImpl(ZoneId timeZone) {
        this.trips = new ArrayList<>();
        this.cancelledTrips = new ArrayList<>();
        this.cities = new ArrayList<>();
        this.trains = new ArrayList<>();
        this.timeZone = timeZone;
    }

    @Override
    public List<Ticket> getAllBookedTickets() {
        return trips.stream().map(t -> t.getBookedTickets()).flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public List<Ticket> getAllCancelledTickets() {
        return trips.stream().map(t -> t.getCancelledTickets()).flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public List<Trip> findPossibleExchanges(Ticket ticket) {
        return trips.stream().filter(trip -> trip.getOrigin() == ticket.getTrip().getOrigin()
                && trip.getDestination() == ticket.getTrip().getDestination()
                && !trip.isCancelled()
                && trip.getPlannedDepartureTime().isAfter(ticket.getTrip().getPlannedDepartureTime())).collect(Collectors.toList());
    }

    @Override
    public List<Trip> findAvailableTrips(City origin, LocalDate date) {
        return trips.stream().filter(trip -> trip.getOrigin() == origin
                && trip.getPlannedDepartureTime().atZone(timeZone).toLocalDate().equals(date)).collect(Collectors.toList());
    }

    @Override
    public List<Trip> findAvailableTrips(City origin, City destination, LocalDate date) {
        return findAvailableTrips(origin, date).stream().filter(trip -> trip.getDestination() == destination).collect(Collectors.toList());
    }

    @Override
    public List<City> getCities() {
        return Collections.unmodifiableList(this.cities);
    }

    @Override
    public List<Train> getAllTrains() {
        return Collections.unmodifiableList(this.trains);
    }

    @Override
    public List<Trip> getAllTrips() {
        return Collections.unmodifiableList(this.trips);
    }

    @Override
    public List<Trip> getAllCancelledTrips() {
        return Collections.unmodifiableList(this.cancelledTrips);
    }

    @Override
    public void addCity(City city) {
        this.cities.add(city);
    }

    @Override
    public Trip createTrip(City origin, City destination, Train train, Instant departure, Instant arrival) throws TripException {
        List<Trip> trainTrips = this.findOrderedTripsOfTrain(train);


        if (!trainTrips.isEmpty()) {

            Trip lastTrainTrip = trainTrips.get(trainTrips.size() - 1);

            boolean comesAfter = lastTrainTrip.findRealArrivalTime().isBefore(departure);

            boolean c1 = lastTrainTrip.getDestination() == origin;

            boolean c2 = Duration.between(lastTrainTrip.findRealArrivalTime(), departure)
                    .compareTo(Duration.ofMinutes(10)) == 1;

            boolean c3 = arrival.isAfter(departure);

            boolean c4 = origin != destination;

            if (!(comesAfter && c1 && c2 && c3 && c4))
                throw new TripException();
        }
        Trip trip = new TripImpl(origin, destination, train, departure, arrival);
        trips.add(trip);
        return trip;
    }

    @Override
    public void cancelTrip(Trip trip) {
        trip.cancel();
        trips.remove(trip);
        cancelledTrips.add(trip);
    }

    @Override
    public void delayTripDeparture(Trip trip, Duration delay) {
        trip.addDepartureDelay(delay);
        this.delayTripArrival(trip, delay);
    }

    @Override
    public void delayTripArrival(Trip trip, Duration delay) {
        trip.addArrivalDelay(delay);
        try {
            Optional<Trip> nextTrip = findNextTripOfTrain(trip.getTrain(), trip);
            if (nextTrip.isPresent()) {
                delayTripDeparture(nextTrip.get(), delay);
            }
        } catch (TripException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Trip> findNextTripOfTrain(Train train, Trip trip) throws TripException {
        if (trip.getTrain() != train) {
            throw new TripException();
        }
        List<Trip> orderedTrips = findOrderedTripsOfTrain(train);
        int nextTripIndex = orderedTrips.indexOf(trip) + 1;
        Trip result = nextTripIndex < orderedTrips.size() ? orderedTrips.get(nextTripIndex) : null;
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Trip> findPreviousTripOfTrain(Train train, Trip trip) throws TripException {
        if (trip.getTrain() != train) {
            throw new TripException();
        }
        List<Trip> orderedTrips = findOrderedTripsOfTrain(train);
        int previousTripIndex = orderedTrips.indexOf(trip) - 1;
        Trip result = previousTripIndex >= 0 ? orderedTrips.get(previousTripIndex) : null;
        return Optional.ofNullable(result);
    }

    @Override
    public List<Trip> findOrderedTripsOfTrain(Train train) {
        return trips.stream().filter((someTrip -> someTrip.getTrain() == train)).sorted((t1, t2) -> t1.findRealArrivalTime().isBefore(t2.findRealDepartureTime()) ? -1 : 1).collect(Collectors.toList());
    }

    @Override
    public void addTrain(Train train) {
        this.trains.add(train);
        
    }
}
