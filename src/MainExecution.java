import core.GameEngine;
import utility.Logger;

import java.io.IOException;

public class MainExecution {
    public static void main(String[] args) {
        try {
            Logger.init();
            Logger.info("Application started");
        } catch (IOException e) {
            // Logger failed — game can still run, just warn on stderr
            System.err.println("[WARN] Logger initialization failed: " + e.getMessage());
            System.err.println("[WARN] Continuing without file logging.");
            // Don't exit — the game is still playable without logs
        }

        try {
            GameEngine engine = new GameEngine();
            engine.start();

        } catch (Exception e) {
            // Logger may or may not be available here
            try {
                Logger.error("Unhandled exception in main execution", e);
            } catch (Exception ignored) {}

            System.err.println("⚠️ A critical error occurred. Please restart.");
            System.err.println("Details: " + e.getMessage());

        } finally {
            try {
                Logger.info("Application terminated");
            } catch (Exception ignored) {}
        }
    }
}