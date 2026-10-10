package common.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final DateTimeFormatter FORMAT_DATE =DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMAT_DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static LocalDate parseDate (String textDate){
            return LocalDate.parse(textDate, FORMAT_DATE);
    }

    public static String formatDate(LocalDate date){
        return date.format(FORMAT_DATE);
    }

    public static String formatDateTime (LocalDateTime dateTime) {
        return dateTime.format(FORMAT_DATE_TIME);
    }
}
