package utility;

public final class Config {

    private Config() {
    }

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

    // ==============================
    // BOT NAMES
    // ==============================

    public static final class BotData {
        private BotData() {
        }

        // ==============================
        // BOT NAMES
        // ==============================

        // Random-dominant with Occasional Instinctive Extension and Rare Win Detection EquiSelect
        public static final String BEGINNER_BOT_NAME = "RAVE";
        public static final int BEGINNER_BOT_ELO_RATING = 1000;

        // Random-dominant with Occasional Instinctive Extension and Rare Win Detection EquiSelect
        public static final String EASY_BOT_NAME = "HAZE";
        public static final int EASY_BOT_ELO_RATING = 1368;

        // Win-first Immediate-block Rule-based Priority with Partial Opening Awareness EquiSelect
        public static final String MEDIUM_BOT_NAME = "WIRE";
        public static final int MEDIUM_BOT_ELO_RATING = 1716;

        // Fork-aware Priority Rule Engine with Opening Strategy and Controlled Defect Injection
        public static final String HARD_BOT_NAME = "FLINT";
        public static final int HARD_BOT_ELO_RATING = 1833;

        // Priority-Reordered Opening-Boosted EquiMinMax
        public static final String UNBEATABLE_BOT_NAME = "PROBE";
        public static final int UNBEATABLE_BOT_ELO_RATING = 2000;


        // ==============================
        // BOT Rates - Do Not Change If Not necessary
        // ==============================
        public static final float HARD_BOT_DEFECT_RATE = 0.08f;
        public static final float MEDIUM_BOT_OPENING_AWARENESS_RATE = 0.15f;
        public static final float EASY_BOT_RANDOMNESS_RATE = 0.4f;
        public static final float EASY_BOT_BLOCKING_RATE = 0.7f;
        public static final float EASY_BOT_CENTER_PICK_RATE = 0.7f;
        public static final float BEGINNER_BOT_RANDOMNESS_RATE = 0.5f;
        public static final float BEGINNER_BOT_PICK_WIN_RATE = 0.5f;


        // ==============================
        // Session Delay times
        // ==============================
        public static final int BOT_THINK_DOT_DELAY_MS_PVG = 170;
        public static final int BOT_THINK_DOT_DELAY_MS_BVB = 240;

        // ==============================
        // Session Print Helpers
        // ==============================
        public static String title = "<< BOT Introduction Panel >>";

        public static final String[][] BOT_TABLE = {
                {"1", "RAVE", "Beginner", "0%   (ELO: 1000)"},
                {"2", "GREX", "Easy", "37%  (ELO: 1368)"},
                {"3", "WIRE", "Medium", "72%  (ELO: 1716)"},
                {"4", "FLINT", "Hard", "83%  (ELO: 1833)"},
                {"5", "PROBE", "Unbeatable", "100% (ELO: 2000)"},
                {"0", "STALL", "Draw-Maximizing", "70% Success"}
        };

        public static final String[] BOT_TABLE_HEADERS = {
                "Level", "Code-Name", "Mode", "Difficulty"
        };

    }


    //

}