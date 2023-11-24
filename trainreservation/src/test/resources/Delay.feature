Feature: Delay
  Scenario: Successful Delay
    Given a trip is created from Isfahan to Tehran with train 1, departure time, and arrival time
    When the trip departure is delayed by 1 hour
    Then the trip's real departure time should be the original departure time plus 1 hour