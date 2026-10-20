package common.utils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleReader {
    private static final Scanner SCANNER = new Scanner(System.in);

    private ConsoleReader(){}

    public static String readString (String menuText) {
        System.out.print(menuText);
        return SCANNER.nextLine().trim();
    }

    public static int readInt (String menuText) {
        while(true){
            try{
                return Integer.parseInt(readString(menuText));
            } catch (NumberFormatException error){
                System.out.println("Enter a valid number.");
            }
        }
    }

    public static Optional<LocalDate> readDateOptional (String menuText){
        while(true){
            String input = readString(menuText);
            if (input.isEmpty()){
                return Optional.empty();
            }
            try {
                return Optional.of(DateUtils.parseDate(input));
            } catch (DateTimeParseException error) {
                System.out.println("Invalid date. Use: " +DateUtils.DATE_PATTERN);
            }
        }
    }

    public static boolean confirmAction(String message) {
        while (true) {
            String input = readString(message + " (S/N): ").toLowerCase();
            switch (input) {
                case "s", "y" -> {
                    return true;
                }
                case "n" -> {
                    return false;
                }
                default -> System.out.println("Invalid option. Type S or N.");
            }
        }
    }
}
