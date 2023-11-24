package fr.univnantes.trainreservation.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeManagement {

    /**
     * Creates an Instant based on a date+time string formatted in this fashion: "2021-03-11 11:00"
     * Example of usage:
     *    Instant i = TimeManagement.createInstant("2021-03-11 11:00", ZoneId.systemDefault());
     * @param dateTimeString The well formatted String.
     * @param zoneID The time zone to consider.
     * @return The resulting Instant corresponding to the date and timezone.
     */
    public static Instant createInstant(String dateTimeString, ZoneId zoneID) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(dateTime, zoneID);
        return zonedDateTime.toInstant();
    }

}
