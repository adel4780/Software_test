package fr.univnantes.trainreservation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import fr.univnantes.trainreservation.impl.CityImpl;
import fr.univnantes.trainreservation.impl.TicketImpl;
import fr.univnantes.trainreservation.impl.TicketReservationSystemImpl;
import fr.univnantes.trainreservation.impl.TrainImpl;

public class JUnitTest {
    TicketReservationSystem system;
    City Isfahan, Tehran, Kerman;
    Train train1, train2, train3;
    Instant departure, arrival;
    @BeforeEach
    public void setUp() throws Exception {
        ZoneId timZone = ZoneId.systemDefault();
        system = new TicketReservationSystemImpl(timZone);
        Isfahan = new CityImpl("Isfahan");
        Tehran = new CityImpl("Tehran");
        Kerman = new CityImpl("Kerman");
        system.addCity(Isfahan);system.addCity(Tehran);system.addCity(Kerman);
        train1 = new TrainImpl("Train1", 100);
        train2 = new TrainImpl("Train2", 200);
        train3 = new TrainImpl("Train3", 300);
        system.addTrain(train1);system.addTrain(train2);system.addTrain(train3);
        //departure = TimeManagement.createInstant("2022-05-12 12:00", timZone);
        //arrival = TimeManagement.createInstant("2022-05-12 15:00", timZone);
        departure = Instant.now();
        arrival = departure.plus(Duration.ofHours(2));
    }
    @Test 
    public void testFindPossibleExchanges() throws TripException{
        Trip trip1 = system.createTrip(Isfahan, Tehran, train1, departure.plus(Duration.ofHours(1)), arrival);
        Trip trip2 = system.createTrip(Isfahan, Tehran, train2, departure, arrival);
        Ticket ticket = new TicketImpl("Adel Karimi", trip2);
        List<Trip> result = system.findPossibleExchanges(ticket);
        assertEquals(1, result.size());
    }  
    @Test
    public void testFindNoPossibleExchanges() throws TripException {
        Trip trip1 = system.createTrip(Isfahan, Tehran, train1, departure, arrival);
        Trip trip2 = system.createTrip(Isfahan, Tehran, train3, departure.plus(Duration.ofHours(1)), arrival);
        Ticket ticket = new TicketImpl("Adel Karimi", trip2);
        List<Trip> result = system.findPossibleExchanges(ticket);
        assertEquals(0, result.size()); 
    }      
    @Test
    public void testCancelTrips() throws TripException {
        Trip trip1 = system.createTrip(Isfahan, Tehran, train1, departure, arrival);
        Trip trip2 = system.createTrip(Tehran, Isfahan, train2, departure, arrival);
        system.cancelTrip(trip1);
        assertTrue(system.getAllCancelledTrips().stream().anyMatch(trip -> trip.equals(trip1)));
        assertFalse(system.getAllTrips().stream().anyMatch(trip -> trip.equals(trip1)));
    }
    @Test
    public void testFindPreviousTripOfTrain() throws TripException{
        Trip trip1 = system.createTrip(Isfahan, Tehran, train1, departure, arrival.plus(Duration.ofHours(1)));
        Trip trip2 = system.createTrip(Tehran, Kerman, train1, departure.plus(Duration.ofHours(4)), arrival.plus(Duration.ofHours(5)));
        Optional<Trip> result = system.findPreviousTripOfTrain(train1, trip2);
        assertEquals(trip1, result.get());
    }
    @Test
    public void testFindNextTripOfTrain() throws TripException {
        Trip trip1 = system.createTrip(Isfahan, Tehran, train1, departure, arrival.plus(Duration.ofHours(1)));
        Trip trip2 = system.createTrip(Tehran, Kerman, train1, departure.plus(Duration.ofHours(4)), arrival.plus(Duration.ofHours(5)));
        Optional<Trip> result = system.findNextTripOfTrain(train1, trip1);
        assertEquals(trip2, result.get());
    }
    @Test
    public void testGetAllBookedTickets() throws TripException, ReservationException{
        Trip trip1 = system.createTrip(Isfahan, Tehran, train1, departure, arrival.plus(Duration.ofHours(1)));
        Trip trip2 = system.createTrip(Tehran, Kerman, train1, departure.plus(Duration.ofHours(4)), arrival.plus(Duration.ofHours(5)));
        trip1.bookTicket("Adel Karimi");
        trip2.bookTicket("Ali Karimi");
        List<Ticket> result = system.getAllBookedTickets();
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(ticket -> ticket.getTrip().equals(trip1)));
        assertTrue(result.stream().anyMatch(ticket -> ticket.getTrip().equals(trip2)));
    }
    @Test
    public void testGetAllCancelledTickets() throws TripException, ReservationException{
        Trip trip1 = system.createTrip(Isfahan, Tehran, train1, departure, arrival.plus(Duration.ofHours(1)));
        Trip trip2 = system.createTrip(Tehran, Kerman, train1, departure.plus(Duration.ofHours(4)), arrival.plus(Duration.ofHours(5)));
        Ticket ticket1 = trip1.bookTicket("Adel Karimi");
        Ticket ticket2 = trip2.bookTicket("Ali Karimi");
        trip1.cancelTicket(ticket1);
        List<Ticket> result = system.getAllCancelledTickets();
        assertEquals(1, result.size());
        assertEquals(ticket1.getPassengerName(), result.get(0).getPassengerName());
        List<Ticket> tickets = system.getAllBookedTickets();
        assertEquals(1, tickets.size());
        assertEquals(ticket2.getPassengerName(), tickets.get(0).getPassengerName());
    }
    @Test
    public void testDelayTripDeparture() throws TripException{
        Trip trip = system.createTrip(Tehran, Isfahan, train1, departure, arrival);
        system.delayTripDeparture(trip, Duration.ofHours(1));
        assertEquals(departure.plus(Duration.ofHours(1)), trip.findRealDepartureTime());
    }
    @Test
    public void testDelayTripArrival() throws TripException{ 
        Trip trip = system.createTrip(Tehran, Isfahan, train1, departure, arrival);
        system.delayTripArrival(trip, Duration.ofHours(1));
        assertEquals(arrival.plus(Duration.ofHours(1)), trip.findRealArrivalTime());
    }
    @Test
    public void testFindOrderedTripOfTrain() throws TripException{
        Trip trip1 = system.createTrip(Isfahan, Tehran, train1, departure, arrival.plus(Duration.ofHours(1)));
        Trip trip2 = system.createTrip(Tehran, Kerman, train1, departure.plus(Duration.ofHours(4)), arrival.plus(Duration.ofHours(5)));
        List<Trip> result = system.findOrderedTripsOfTrain(train1);
        assertEquals(trip1, result.get(0));
        assertEquals(trip2, result.get(1));
    }
}