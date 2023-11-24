package fr.univnantes.trainreservation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import fr.univnantes.trainreservation.impl.TicketReservationSystemImpl;
import fr.univnantes.trainreservation.impl.CityImpl;
import fr.univnantes.trainreservation.impl.TrainImpl;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource(".")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "fr.univnantes.trainreservation")
public class CucumberTest {
    private Map<String, Object> scenarioContext = new HashMap<>();
    private TicketReservationSystem system;
    City Isfahan, Tehran, Kerman;
    Train train1, train2, train3;
    Instant departure, arrival;

    @Before
    public void beforeStep() {
        system = new TicketReservationSystemImpl(ZoneId.systemDefault());
        Isfahan = new CityImpl("Isfahan");
        Tehran = new CityImpl("Tehran");
        Kerman = new CityImpl("Kerman");
        train1 = new TrainImpl("Train 1", 100);
        train2 = new TrainImpl("Train 2", 200);
        train3 = new TrainImpl("Train 3", 200);
        departure = Instant.now();
        arrival = departure.plus(Duration.ofHours(2));
    }

    @Given("a trip is created from Isfahan to Tehran with train 1, departure time, and arrival time")
    public void createTrip() throws TripException {
        Trip trip1 = system.createTrip(Isfahan, Tehran, train1, departure, arrival);
        scenarioContext.put("trip1", trip1);
    }

    @When("the trip departure is delayed by 1 hour")
    public void delayTripDeparture() {
        Trip trip = (Trip) scenarioContext.get("trip1");
        system.delayTripDeparture(trip, Duration.ofHours(1));
    }

    @Then("the trip's real departure time should be the original departure time plus 1 hour")
    public void verifyRealDepartureTime() {
        Trip trip = (Trip) scenarioContext.get("trip1");
        Instant expectedDeparture = departure.plus(Duration.ofHours(1));
        assertEquals(expectedDeparture, trip.findRealDepartureTime());
    }

    @Given("there are two trips from Isfahan to Tehran and from Tehran to Kerman")
    public void givenTwoTripsFromIsfahanToTehranAndFromTehranToKerman() throws TripException {
        Trip trip1 = system.createTrip(Isfahan, Tehran, train1, departure, arrival);
        Trip trip2 = system.createTrip(Tehran, Kerman, train1,
                departure.plus(Duration.ofHours(4)),
                arrival.plus(Duration.ofHours(5)));
        scenarioContext.put("trip1", trip1);
        scenarioContext.put("trip2", trip2);
    }

    @When("two tickets are booked for the trips")
    public void whenTwoTicketsAreBooked() throws ReservationException {
        Trip trip1 = (Trip) scenarioContext.get("trip1");
        Trip trip2 = (Trip) scenarioContext.get("trip2");
        trip1.bookTicket("Passenger");
        trip2.bookTicket("NewPassenger");
    }

    @Then("the system should return a list of two booked tickets")
    public void thenTheSystemShouldReturnListOfTwoBookedTickets() {
        List<Ticket> result = system.getAllBookedTickets();
        assertEquals(2, result.size());
        Trip trip1 = (Trip) scenarioContext.get("trip1");
        Trip trip2 = (Trip) scenarioContext.get("trip2");
        assertTrue(result.stream().anyMatch(ticket -> ticket.getTrip().equals(trip1)));
        assertTrue(result.stream().anyMatch(ticket -> ticket.getTrip().equals(trip2)));
    }

}
