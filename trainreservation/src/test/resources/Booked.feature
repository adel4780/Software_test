Feature: Booked
  Scenario: Successful Booking
    Given there are two trips from Isfahan to Tehran and from Tehran to Kerman
    When two tickets are booked for the trips
    Then the system should return a list of two booked tickets