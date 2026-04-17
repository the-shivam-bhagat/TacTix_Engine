package utility;

public final class Strings {
    private Strings() {
    }

    public static final String INTRO_STRING = """
            
            ╔ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ╗
            
                 ████████╗ █████╗  ██████╗ ████████╗██╗██╗  ██╗    ███████╗███╗   ██╗ ██████╗
                 ╚══██╔══╝██╔══██╗██╔════╝ ╚══██╔══╝██║╚██╗██╔╝    ██╔════╝████╗  ██║██╔════╝
                    ██║   ███████║██║         ██║   ██║ ╚███╔╝     █████╗  ██╔██╗ ██║██║  ███╗
                    ██║   ██╔══██║██║         ██║   ██║ ██╔██╗     ██╔══╝  ██║╚██╗██║██║   ██║
                    ██║   ██║  ██║╚██████╗    ██║   ██║██╔╝ ██╗    ███████╗██║ ╚████║╚██████╔╝
                    ╚═╝   ╚═╝  ╚═╝ ╚═════╝    ╚═╝   ╚═╝╚═╝  ╚═╝    ╚══════╝╚═╝  ╚═══╝ ╚═════╝
            
            ╚ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ═ ╝
            
            ╔═════════════════════════════════════════════════════════════════════════════════════╗
            ║                         << TACTIX ENGINE INITIALIZATION >>                          ║
            ╠═════════════════════════════════════════════════════════════════════════════════════╣
            ║                                                                                     ║
            ║  ► Production-grade adversarial Tic-Tac-Toe engine built in Java.                   ║
            ║    Designed to showcase clean architecture, AI systems, and CLI engineering.        ║
            ║                                                                                     ║
            ║  ► Supports multiple execution modes:                                               ║
            ║    Player vs Player | Player vs Bot | Bot vs Bot Simulation                         ║
            ║                                                                                     ║
            ║  ► Includes persistent player registry, authentication, replay history,             ║
            ║    leaderboard tracking, admin controls, and recoverable runtime flow.              ║
            ║                                                                                     ║
            ║  ► Multi-tier bot ladder ranges from beginner randomness to perfect                 ║
            ║    alpha-beta minimax decision systems.                                             ║
            ║                                                                                     ║
            ║  ► Built with modular packages, interface-driven rendering,                         ║
            ║    command interception pipeline, and scalable component design.                    ║
            ║                                                                                     ║
            ║  ► Developed by Shivam Bhagat                                                       ║
            ║    B.Tech CSE | Java Developer | DSA Enthusiast                                     ║
            ║    LinkedIn : shivam-bhagat- | LeetCode : shivam_bhagat_                            ║
            ║                                                                                     ║
            ║  ► This project reflects progression from core programming                          ║
            ║    fundamentals to system-level engineering mindset.                                ║
            ║                                                                                     ║
            ╠═════════════════════════════════════════════════════════════════════════════════════╣
            ║ STATUS : BOOTING   │ MODULES : LOADING   │ STORAGE : CHECKING   │ AI CORE : READY   ║
            ╚═════════════════════════════════════════════════════════════════════════════════════╝
            
            """;
    public static final String FEATURES_STRING = """
            
            ╔═════════════════════════════════════════════════════════════════════════════════════╗
            ║                         << SYSTEM HIGHLIGHTS / FEATURES >>                          ║
            ╠═════════════════════════════════════════════════════════════════════════════════════╣
            ║                                                                                     ║
            ║  01)  Six-Tier Adversarial AI System                                                ║
            ║       RAVE → GREX → WIRE → FLINT → PROBE & STALL, spanning random play to           ║
            ║       alpha-beta pruned minimax with EquiSelect output and draw optimization.       ║
            ║                                                                                     ║
            ║  02)  Three Independent Session Modes                                               ║
            ║       Player vs Player, Player vs Bot, and Bot vs Bot — each with its own           ║
            ║       round loop, undo policy, scoreboard, and replayable match record.             ║
            ║                                                                                     ║
            ║  03)  Command Pipeline with Mid-Game Interception                                   ║
            ║       Every input passes through a CommandProcessor. Type exit, manage, end,        ║
            ║       or undo at any prompt — no session interruption required.                     ║
            ║                                                                                     ║
            ║  04)  Snapshot-Based Undo with Full State Restoration                               ║
            ║       GameBoard snapshots the full board state before every move. Undo              ║
            ║       restores freq[], visual board, and stepCount in O(1) per move.                ║
            ║                                                                                     ║
            ║  05)  Persistent Player Registry with Custom Encoding                               ║
            ║       Players persist across restarts via a char-pair encoded flat file.            ║
            ║       SHA-256 + salt password hashing with backward-compatible deserialization.     ║
            ║                                                                                     ║
            ║  06)  Password-Protected Admin Panel — Accessible Mid-Session                       ║
            ║       Select players by name or rank. Change name, manage password, set             ║
            ║       wins, or delete — all without restarting or affecting session state.          ║
            ║                                                                                     ║
            ║  07)  Full Match Replay Engine                                                      ║
            ║       Step through any round from any historical match cell-by-cell.                ║
            ║       Abandoned rounds are detected and reported gracefully.                        ║
            ║                                                                                     ║
            ║  08)  Fully Decoupled Rendering Layer                                               ║
            ║       13 view interfaces isolate all output. No System.out in business logic.       ║
            ║       Swap the console renderer for GUI or web without touching game code.          ║
            ║                                                                                     ║
            ║  09)  Typed Exception Hierarchy with Structured Error Codes                         ║
            ║       GameErrorCode enum drives 6 typed exception classes. Failures are             ║
            ║       recovered, restarted, or surfaced — never silently swallowed.                 ║
            ║                                                                                     ║
            ║  10)  Ranked Leaderboard and Session History Tracking                               ║
            ║       TreeSet-backed ranking auto-sorts by lifetime wins. Wins are withheld         ║
            ║       when undo is enabled — fairness enforced at the registry level.               ║
            ║                                                                                     ║
            ╠═════════════════════════════════════════════════════════════════════════════════════╣
            ║ STATUS : BOOTING   │ MODULES : LOADING   │ STORAGE : CHECKING   │ AI CORE : READY   ║
            ╚═════════════════════════════════════════════════════════════════════════════════════╝
            
            """;


    public static final String BOTS_INTRODUCTION_PANEL = """
            
            ╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
            ║                                          << TACTIX ENGINE ::: MULTI-TIER AI DECISION LAB >>                                          ║
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
            ║  │                                             │  │  Draw Optimization Engine — Non-Competitive & Never-Losing                    │  ║
            ║  │  GOAL -> Never losing — optimal play with   │  │  ( In progress - 70 % success rate )                                          │  ║
            ║  │               controlled variability        │  │                                                                               │  ║
            ║  │                                             │  │  GOAL                                │ ALGORITHM         │ OVERALL DAR        │  ║
            ║  │  DIFFICULTY - UNBEATABLE                    │  │  Always draw — win avoidance system  │ Modified minimax  │ 69.7% (Improving)  │  ║
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
            ║                            << SYSTEM OPERATIONS MANUAL >>                           ║
            ╠═════════════════════════════════════════════════════════════════════════════════════╣
            ║                                                                                     ║
            ║  [01] DISPLAY MODE                                                                  ║
            ║       Run in Full-Screen / Maximized terminal for perfect board rendering.          ║
            ║       Narrow consoles may shift borders or wrap large visuals.                      ║
            ║                                                                                     ║
            ║  [02] UNIVERSAL COMMANDS                                                            ║
            ║       exit   → Save registry + shutdown engine safely                               ║
            ║       manage → Open admin control panel (password required)                         ║
            ║       end    → Terminate current match only                                         ║
            ║       undo   → Revert previous move(s) if session allows                            ║
            ║                                                                                     ║
            ║  [03] SMART INPUT PIPELINE                                                          ║
            ║       Commands execute before normal input reading.                                 ║
            ║       After command completion, your original prompt returns automatically.         ║
            ║                                                                                     ║
            ║  [04] MOVE ENTRY                                                                    ║
            ║       Use digits 1-9 matching board cells.                                          ║
            ║       Occupied / invalid / text entries are rejected and re-requested.              ║
            ║                                                                                     ║
            ║  [05] UNDO PROTOCOL                                                                 ║
            ║       Works only during active rounds when enabled at match start.                  ║
            ║       PvB mode reverses both human + bot turns together.                            ║
            ║                                                                                     ║
            ║  [06] PLAYER IDENTITY                                                               ║
            ║       Names auto-normalize to UPPERCASE.                                            ║
            ║       Empty entry may assign auto-generated player identity.                        ║
            ║                                                                                     ║
            ║  [07] SECURITY LAYER                                                                ║
            ║       Passwords remain case-sensitive.                                              ║
            ║       4 failed login attempts lock that account for this session.                   ║
            ║                                                                                     ║
            ║  [08] QUICK CONFIRM RULE                                                            ║
            ║       On Y/N prompts: Enter = YES by default.                                       ║
            ║       Use N to decline actions explicitly.                                          ║
            ║                                                                                     ║
            ║  [09] BOT SIMULATION MODE                                                           ║
            ║       Choose which bot starts first, then bots continue automatically.              ║
            ║       Random think delays (1.5s-2.5s) simulate live decision pacing.                ║
            ║                                                                                     ║
            ║  [10] DATA + HISTORY                                                                ║
            ║       Finished or aborted matches are preserved.                                    ║
            ║       Previous printed boards remain viewable in terminal scrollback.               ║
            ║                                                                                     ║
            ╠═════════════════════════════════════════════════════════════════════════════════════╣
            ║  STATUS : READY   │ INPUT ENGINE : ACTIVE   │ REGISTRY : LOADED   │ LOGGING : ON    ║
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
