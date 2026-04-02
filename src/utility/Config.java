package utility;

public final class Config {

    private Config() {}

    // ==============================
    // ADMIN SETTINGS
    // ==============================
    public static final String ADMIN_PASSWORD = "123456";

    // ==============================
    // PLAYER SETTINGS
    // ==============================
    public static final int MAX_PLAYERS = 1000;
    public static final int TOP_PLAYERS = 10;

    // ==============================
    // FILE SETTINGS
    // ==============================
    public static final String PLAYER_FILE_NAME = "players.dat";
    public static final String LOGGER_FILE_NAME = "loggers.log";

    public static final class BotNames {
        private BotNames() {}
        public static final String BEGINNER_BOT_NAME = "BEGINNER_BOT";
        public static final String EASY_BOT_NAME = "EASY_BOT";
        public static final String MEDIUM_BOT_NAME = "MEDIUM_BOT";
        public static final String HARD_BOT_NAME = "HARD_BOT";
        public static final String UNBEATABLE_BOT_NAME = "UNBEATABLE_BOT";
    }
}