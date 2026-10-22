package common.cli;

import common.utils.ConsoleReader;
import event.cli.EventCli;
import note.cli.NoteCli;
import task.cli.TaskCli;

public class MainMenuCli {
    private final TaskCli taskCli;
    private final NoteCli noteCli;
    private final EventCli eventCli;

    public MainMenuCli(TaskCli taskCli, NoteCli noteCli, EventCli eventCli) {
        this.taskCli = taskCli;
        this.noteCli = noteCli;
        this.eventCli = eventCli;
    }

    public void showMenu() {
        printLogo();
        boolean exit = false;
        while (!exit) {
            System.out.println("\n****CLI-AGENDA - MAIN MENU****");
            System.out.println("1. Tasks");
            System.out.println("2. Notes");
            System.out.println("3. Events");
            System.out.println("0. Exit");

            int option = ConsoleReader.readInt("Choose an option: ");
            switch (option) {
                case 1 -> taskCli.showMenu();
                case 2 -> noteCli.showMenu();
                case 3 -> eventCli.showMenu();
                case 0 -> {
                    System.out.println("Goodbye!");
                    exit = true;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void printLogo() {
        System.out.println("""
                   ___    ____  _____  _   _  ____     _   \s
                  /   \\  / ___|| ____|| \\ | ||  _ \\   / \\  \s
                 / /_\\ \\| |  _ |  _|  |  \\| || | | | / _ \\ \s
                / / _ \\ \\ |_| || |___ | |\\  || |_| |/ ___ \\\s
                \\/_/ \\_\\_\\____||_____||_| \\_||____//_/   \\_\\
                """);
    }
}
