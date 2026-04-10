package utility;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Logger {

    private static final String FILE = Config.FileConfig.LOGGER_FILE_NAME;

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static PrintWriter writer;   // ✅ single writer

    private Logger() {}

    // ==============================
    // INITIALIZATION
    // ==============================
    public static void init() throws IOException {
        writer = new PrintWriter(new FileWriter(FILE)); // overwrite file
    }

    private static String time() {
        return LocalDateTime.now().format(FORMAT);
    }

    // ==============================
    // CORE WRITE METHOD
    // ==============================
    private static void write(String level, String msg) {
        if (writer == null) {
            throw new IllegalStateException("Logger not initialized. Call Logger.init() first.");
        }

        writer.println("[" + level + "  " + time() + "] " + msg);
        writer.flush(); // ensure immediate write
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
        write("ERROR", msg + " | " + ex);

        if (writer != null) {
            for (StackTraceElement e : ex.getStackTrace()) {
                writer.println("    at " + e);
            }
            writer.flush();
        }
    }
}