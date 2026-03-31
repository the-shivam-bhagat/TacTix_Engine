package utility;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Logger {

    private static final String FILE = Config.LOGGER_FILE_NAME;

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Logger() {}

    public static void init() throws IOException {
        new PrintWriter(new FileWriter(FILE)).close(); // clears file
    }

    private static String time() {
        return LocalDateTime.now().format(FORMAT);
    }

    // ==============================
    // CORE WRITE METHOD
    // ==============================
    private static void write(String level, String msg) {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE, true))) {
            out.println("[" + level + "  " + time() + "] " + msg);
        } catch (IOException e) {
            System.err.println("[LOGGER ERROR] " + e.getMessage());
        }
    }

    // ==============================
    // INFO
    // ==============================
    public static void info(String msg) {
        write("INFO", msg);
    }

    // ==============================
    // WARNING
    // ==============================
    public static void warn(String msg) {
        write("WARN", msg);
    }

    // ==============================
    // ERROR (simple)
    // ==============================
    public static void error(String msg) {
        write("ERROR", msg);
    }

    // ==============================
    // ERROR (with exception)
    // ==============================
    public static void error(String msg, Exception ex) {
        write("ERROR", msg + " | " + ex.toString());

        try (PrintWriter out = new PrintWriter(new FileWriter(FILE, true))) {
            for (StackTraceElement e : ex.getStackTrace()) {
                out.println("    at " + e);
            }
        } catch (IOException ignored) {}
    }
}