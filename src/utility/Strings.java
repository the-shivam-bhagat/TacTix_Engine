package utility;

public final class Strings {
    private Strings() {
    }

    public static final String INTRO_STRING = """
            
            ╔ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ╗
            
              ████████╗██╗ ██████╗      ████████╗ █████╗  ██████╗      ████████╗ ██████╗ ███████╗
              ╚══██╔══╝██║██╔════╝      ╚══██╔══╝██╔══██╗██╔════╝      ╚══██╔══╝██╔═══██╗██╔════╝
                 ██║   ██║██║              ██║   ███████║██║              ██║   ██║   ██║█████╗
                 ██║   ██║██║              ██║   ██╔══██║██║              ██║   ██║   ██║██╔══╝
                 ██║   ██║╚██████╗         ██║   ██║  ██║╚██████╗         ██║   ╚██████╔╝███████╗
                 ╚═╝   ╚═╝ ╚═════╝         ╚═╝   ╚═╝  ╚═╝ ╚═════╝         ╚═╝    ╚═════╝ ╚══════╝
            
            ╚ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ╝
            
            ╔═════════════════════════════════════════════════════════════════════════════════════╗
            ║                        << TIC-TAC-TOE AI ENGINE SYSTEM >>                           ║
            ╠═════════════════════════════════════════════════════════════════════════════════════╣
            ║                                                                                     ║
            ║  • This system is a modular and extensible implementation of Tic-Tac-Toe            ║
            ║    featuring multiple gameplay modes and AI-driven decision engines.                ║
            ║                                                                                     ║
            ║  • It demonstrates structured programming, game flow control, and adversarial       ║
            ║    decision-making within a well-defined execution environment.                     ║
            ║                                                                                     ║
            ║  • The engine supports both interactive and automated sessions, including           ║
            ║    Player vs Player, Bot vs Player, and Bot vs Bot simulations.                     ║
            ║                                                                                     ║
            ║  • Each component is designed to reflect clean architecture, modular design         ║
            ║    principles, and scalable decision logic systems.                                 ║
            ║                                                                                     ║
            ║  • Developed by: Shivam Bhagat                                                      ║
            ║    B.Tech CSE | Java Development | Data Structures & Algorithms                     ║
            ║    Linkedin -> shivam-bhagat- | LeetCode -> shivam_bhagat_                          ║
            ║                                                                                     ║
            ║  • This project represents a progression from fundamental programming concepts      ║
            ║    to advanced AI-driven gameplay systems and design thinking.                      ║
            ║                                                                                     ║
            ╚═════════════════════════════════════════════════════════════════════════════════════╝
            """;
    public static final String FEATURES_STRING = """
            
            ╔═════════════════════════════════════════════════════════════════════════════════════╗
            ║                         << SYSTEM HIGHLIGHTS / FEATURES >>                          ║
            ╠═════════════════════════════════════════════════════════════════════════════════════╣
            ║                                                                                     ║
            ║  01)  Three Game Session Modes                                                      ║
            ║       Supports Player vs Player, Player vs Bot, and Bot vs Bot sessions,            ║
            ║       each with independent round control, scoreboard, and match result.            ║
            ║                                                                                     ║
            ║  02)  Six Engineered AI Bots                                                        ║
            ║       RAVE, GREX, WIRE, FLINT, PROBE, and STALL — each tuned to a distinct          ║
            ║       difficulty using 100,000-match simulations and ELO-based calibration.         ║
            ║                                                                                     ║
            ║  03)  Persistent Player Registry                                                    ║
            ║       Player names, wins, and rankings persist across sessions using a              ║
            ║       custom encoded file store with automatic load and save lifecycle.             ║
            ║                                                                                     ║
            ║  04)  Ranked Leaderboard with Automatic Ordering                                    ║
            ║       Players are ranked by lifetime wins using sorted data structures and          ║
            ║       custom comparison logic, trimmed to a configurable maximum capacity.          ║
            ║                                                                                     ║
            ║  05)  Complete Player Management                                                    ║
            ║       A password-protected admin panel allows viewing and deleting players          ║
            ║       mid-session without interrupting or restarting the game.                      ║
            ║                                                                                     ║
            ║  06)  SOLID, OOP, and LLD Design Principles                                         ║
            ║       Registry, RankingView, PlayerStore, SessionView, EngineView, and              ║
            ║       CommandProcessor interfaces enforce dependency inversion throughout.          ║
            ║                                                                                     ║
            ║  07)  UI Abstraction Layer                                                          ║
            ║       All rendering is routed through view interfaces, fully decoupling             ║
            ║       display logic so the console layer can be swapped for GUI or web.             ║
            ║                                                                                     ║
            ║  08)  Fault-Tolerant Input and Command Pipeline                                     ║
            ║       A defensive input system intercepts commands like exit at any prompt          ║
            ║       and handles invalid input without interrupting the session flow.              ║
            ║                                                                                     ║
            ║  09)  Self-Recovering Execution and Structured Logging                              ║
            ║       Runtime exceptions trigger a controlled restart path. All system              ║
            ║       events, warnings, and errors are written to a timestamped log file.           ║
            ║                                                                                     ║
            ║  10)  Session History and Match Result Tracking                                     ║
            ║       Every completed match is stored in-session and displayed as a                 ║
            ║       formatted summary at the end, alongside the updated leaderboard.              ║
            ║                                                                                     ║
            ╚═════════════════════════════════════════════════════════════════════════════════════╝
            
            """;


    public static final String BOTS_INTRODUCTION_PANEL = """
            
            ╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
            ║                                                     << ADVANCED AI BOT SYSTEM >>                                                     ║
            ╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
            ║   Level - 1                        Level - 2                        Level - 3                        Level - 4                       ║
            ║  ┌─────────────────────────────┐  ┌─────────────────────────────┐  ┌─────────────────────────────┐  ┌─────────────────────────────┐  ║
            ║  │  RAVE           [ELO 1000]  │  │  GREX           [ELO 1368]  │  │  WIRE           [ELO 1716]  │  │  FLINT          [ELO 1833]  │  ║
            ║  │                             │  │                             │  │                             │  │                             │  ║
            ║  │  DIFFICULTY - Beginner      │  │  DIFFICULTY - Easy          │  │  DIFFICULTY - Medium        │  │  DIFFICULTY - Hard          │  ║
            ║  │  0%                         │  │  37%                        │  │  72%                        │  │  83%                        │  ║
            ║  │  ░░░░░░░░░░░░░░░░░░░░░░░░   │  │  █████████░░░░░░░░░░░░░░░   │  │  █████████████████░░░░░░░   │  │  ████████████████████░░░░   │  ║
            ║  │                             │  │                             │  │                             │  │                             │  ║
            ║  │  ALGORITHM                  │  │  ALGORITHM                  │  │  ALGORITHM                  │  │  ALGORITHM                  │  ║
            ║  │  Random-dominant with       │  │  Heuristic-aware            │  │  Win-first immediate-block  │  │  Fork-aware priority        │  ║
            ║  │  occasional instinctive     │  │  probabilistic selection    │  │  rule-based priority with   │  │  rule engine with opening   │  ║
            ║  │  extension and rare         │  │  with unreliable block      │  │  partial opening            │  │  strategy and controlled    │  ║
            ║  │  win detection              │  │  and positional bias        │  │  awareness                  │  │  defect injection           │  ║
            ║  │ [RANDOM-PLAY, NO-DEFENCE]   │  │ [WEAK HEURISTICS, MISSES]   │  │ [WIN/BLOCK-RULES, BASIC]    │  │ [FORK-AWARE, STRATEGIC]     │  ║
            ║  └─────────────────────────────┘  └─────────────────────────────┘  └─────────────────────────────┘  └─────────────────────────────┘  ║
            ║   Level - 5                                        Level - ??                                                                        ║
            ║  ┌─────────────────────────────────────────────┐  ┌───────────────────────────────────────────────────────────────────────────────┐  ║
            ║  │  PROBE                          [ELO 2000]  │  │  STALL                                                   [Special - ELO ?? ]  │  ║
            ║  │                                             │  │  Draw Optimization Engine — Non-Competitive & Never-Loosing                   │  ║
            ║  │  GOAL -> Never losing — optimal play with   │  │  ( In progress - 70 % success rate )                                          │  ║
            ║  │               controlled variability        │  │                                                                               │  ║
            ║  │                                             │  │  GOAL                                 │ ALGORITHM          │ OVERALL DAR      │  ║
            ║  │  DIFFICULTY - UNBEATABLE                    │  │  Always draw — win avoidance system   │ Modified minimax   │ 69.7% (soon 100%)│  ║
            ║  │  100%                                       │  │                                                                               │  ║
            ║  │  ███████████████████████████████████████    │  │  DRAW ACHIEVEMENT RATE BY OPPONENT                                            │  ║
            ║  │                                             │  │ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ │  ║
            ║  │  ALGORITHM                                  │  │ │ vs STALL │ │ vs PROBE │ │ vs FLINT │ │ vs WIRE  │ │ vs GREX  │ │ vs RAVE  │ │  ║
            ║  │  Heuristic-guided priority ordering with    │  │ │  100%    │ │  100%    │ │  93.6%   │ │  77.1%   │ │  57.1%   │ │  21.0%   │ │  ║
            ║  │  alpha-beta pruned minimax and EquiMinMax   │  │ └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘ └──────────┘ │  ║
            ║  │  based decision optimised EquiSelect        │  │  [ NON-COMPETITIVE ]    [ WIN AVOIDANCE ]    [ EQUILIBRIUM AI ]               │  ║
            ║  └─────────────────────────────────────────────┘  └───────────────────────────────────────────────────────────────────────────────┘  ║
            ╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
            
            """;

    public static final String INSTRUCTION_STRING = """
            
            ╔═════════════════════════════════════════════════════════════════════════════════════╗
            ║                              << INSTRUCTIONS / GUIDE >>                             ║
            ╠═════════════════════════════════════════════════════════════════════════════════════╣
            ║                                                                                     ║
            ║  01)  Turn on Full-Screen mode before starting — the game board requires            ║
            ║       a wide terminal to render correctly without wrapping.                         ║
            ║                                                                                     ║
            ║  02)  At any input prompt, type  exit  and press Enter to safely quit.              ║
            ║       The system will save all player data before closing.                          ║
            ║                                                                                     ║
            ║  03)  Type  manage  at any input prompt to open the admin panel.                    ║
            ║       A password is required. Use this to view or delete registered players.        ║
            ║                                                                                     ║
            ║  04)  Player names are optional — press Enter to receive an auto-assigned           ║
            ║       default name. Names are stored and tracked across all sessions.               ║
            ║                                                                                     ║
            ║  05)  For any Y/N prompt, pressing Enter alone counts as Y.                         ║
            ║       Use N or any other key to decline.                                            ║
            ║                                                                                     ║
            ║  06)  Choose your session type at the start of each game:                           ║
            ║       Player vs Player  /  Player vs Bot  /  Bot vs Bot.                            ║
            ║                                                                                     ║
            ║  07)  When playing against a bot, select its difficulty level.                      ║
            ║       Six bots are available ranging from Beginner to Unbeatable.                   ║
            ║                                                                                     ║
            ║  08)  In Bot vs Bot mode, choose which bot moves first each match.                  ║
            ║       The first mover alternates automatically each round thereafter.               ║
            ║                                                                                     ║
            ║  09)  To place a move, enter a number from 1 to 9 matching the board cell.          ║
            ║       Occupied cells are rejected — the prompt will repeat until valid.             ║
            ║                                                                                     ║
            ║  10)  Match scores are shown after every round. A full history of all               ║
            ║       completed matches and the updated leaderboard appear at the end.              ║
            ║                                                                                     ║
            ╚═════════════════════════════════════════════════════════════════════════════════════╝
            
            """;

    public static final String ADMIN_PLAYER_BOARD_TITLE = " [ADMIN] ALL PLAYERS ";
    public static final String LEADERBOARD_TITLE = "<< LEADERBOARD >>";

    public static final String NO_PLAYERS_LEADERBOARD = """
            
            ╔═════════════════════════╗
            ║    << LEADERBOARD >>    ║
            ╠═════════════════════════╣
            ║  No registered players  ║
            ╚═════════════════════════╝
            """;

    public static final String WELCOME_REGISTERED_PLAYER_1 = """
            
            > [INFO] Welcome back, %s (Wins: %d)
            > [READY] Ready for another match
            """;

    public static final String WELCOME_REGISTERED_PLAYER_2 = """
            
            > [INFO] Welcome back, %s (Wins: %d)
            > [READY] Let’s begin
            """;

    public static final String WELCOME_NEW_PLAYER_1 = """
            
            > [INFO] Welcome, %s
            > [READY] You are now registered as a new player
            """;

    public static final String WELCOME_NEW_PLAYER_2 = """
            
            > [INFO] Welcome, %s
            > [READY] Your journey begins now
            """;

    public static final String MATCH_DRAW_BOARD = """
            
            ╔════════════════════════════════════╗
            ║          << MATCH DRAW >>          ║
            ╠════════════════════════════════════╣
            ║  Result: The match ended in a draw ║
            ╚════════════════════════════════════╝
            """;

    public static final String ADMIN_PANEL_INTRO_BOARD = """
            
            ╔══════════════════════════════╗
            ║       PLAYER MANAGEMENT      ║
            ╚══════════════════════════════╝
            
            > [INFO] List of All the Players :-""";
}
