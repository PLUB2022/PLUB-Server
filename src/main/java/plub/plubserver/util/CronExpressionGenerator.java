package plub.plubserver.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CronExpressionGenerator {

    public static String generate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ss mm HH dd MM ? yyyy");
        String formattedDateTime = dateTime.format(formatter);
        Date date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());

        return String.format("%1$s %2$s %3$s %4$s %5$s ?",
                formattedDateTime.substring(0, 2), // seconds
                formattedDateTime.substring(3, 5), // minutes
                formattedDateTime.substring(6, 8), // hours
                formattedDateTime.substring(9, 11), // day of month
                formattedDateTime.substring(12, 14)); // month
    }
}