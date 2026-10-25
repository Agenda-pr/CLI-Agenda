import common.cli.MainMenuCli;
import common.config.AppInitializer;
import common.db.ConnectionFactory;
import common.exception.DatabaseException;

public class Main {
    public static void main(String[] args) {
        try {
            MainMenuCli mainMenu = AppInitializer.init();
            mainMenu.showMenu();
        } catch (DatabaseException error) {
            System.out.println("Database error: " + error.getMessage());
        } finally {
            ConnectionFactory.closeConnection();
        }
    }
}
