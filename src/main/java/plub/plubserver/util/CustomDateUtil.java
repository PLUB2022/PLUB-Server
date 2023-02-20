package plub.plubserver.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomDateUtil {

    public static List<String> getWeekDatesFromToday() {
        LocalDate today = LocalDate.now();
        List<String> week = new ArrayList<>();
        for (int i = 6; i >= 0; i--) week.add(today.minusDays(i).toString());
        return week;
    }
}
