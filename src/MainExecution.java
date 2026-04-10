import core.GameEngine;
import utility.Logger;

public class MainExecution {

    public static void main(String[] args) {
        try {
            Logger.init();
            Logger.info("Application started");

            GameEngine engine = new GameEngine();
            engine.start();

        } catch (Exception e) {
            Logger.error("Unhandled exception in main execution", e);

            System.out.println("⚠️ A critical error occurred. Please restart the application.");
        } finally {
            Logger.info("Application terminated");
        }
    }
}