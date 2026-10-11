package common.utils;

import java.util.Scanner;

public class ConsoleReader {
    private static final Scanner SCANNER = new Scanner(System.in);

    private ConsoleReader(){}

    private static String readString (String menuText) {
        System.out.print(menuText);
        return SCANNER.nextLine().trim();
    }

    private static int readInt (String menuText) {
        while(true){
            try{
                return Integer.parseInt(readString(menuText));
            } catch (NumberFormatException error){
                System.out.println("Enter a valid number.");
            }
        }
    }
}
