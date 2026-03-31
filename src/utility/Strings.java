package utility;

public final class Strings {
    private Strings() {}
    public static final String INTRO_STRING = """
            
            
            ████████╗██╗ ██████╗      ████████╗ █████╗  ██████╗      ████████╗ ██████╗ ███████╗
            ╚══██╔══╝██║██╔════╝      ╚══██╔══╝██╔══██╗██╔════╝      ╚══██╔══╝██╔═══██╗██╔════╝
               ██║   ██║██║              ██║   ███████║██║              ██║   ██║   ██║█████╗
               ██║   ██║██║              ██║   ██╔══██║██║              ██║   ██║   ██║██╔══╝
               ██║   ██║╚██████╗         ██║   ██║  ██║╚██████╗         ██║   ╚██████╔╝███████╗
               ╚═╝   ╚═╝ ╚═════╝         ╚═╝   ╚═╝  ╚═╝ ╚═════╝         ╚═╝    ╚═════╝ ╚══════╝
            ════════════════════════════════════════════════════════════════════════════════════
            
            ╔═════════════════════════════════════════════════════════════════════════╗
            ║                          👋 WELCOME TO THE GAME!                        ║
            ╠═════════════════════════════════════════════════════════════════════════╣
            ║                                                                         ║
            ║  🎮 This is a fun and interactive Tic Tac Toe experience                ║
            ║     built with clean logic and structured design.                       ║
            ║                                                                         ║
            ║  👨‍💻 Developed with passion by Shivam Bhagat                             ║
            ║     B.Tech CSE Student | Java Development & DSA Enthusiast              ║
            ║                                                                         ║
            ║  🚀 This project reflects dedication to learning,                       ║
            ║     problem-solving, and building real-world systems.                   ║
            ║                                                                         ║
            ║  😄 Now relax, focus, and enjoy the battle of X and O!                  ║
            ║                                                                         ║
            ╚═════════════════════════════════════════════════════════════════════════╝
            """;
    public static final String FEATURES_STRING = """
            
            ╔═════════════════════════════════════════════════════════════════════════╗
            ║                    🚀 SYSTEM HIGHLIGHTS / FEATURES 🚀                   ║
            ╠═════════════════════════════════════════════════════════════════════════╣
            ║  1) Data-Driven Board Engine                                            ║
            ║     The board is rendered dynamically from the game state,              ║
            ║     separating display logic from game logic for flexibility.           ║
            ║                                                                         ║
            ║  2) Centralized Game State Management                                   ║
            ║     All game progress is controlled through a single state flow,        ║
            ║     preventing inconsistent board states and ensuring reliable gameplay.║
            ║                                                                         ║
            ║  3) Intelligent Player Registry                                         ║
            ║     A registry system tracks players, prevents duplicates, and          ║
            ║     maintains performance statistics across matches.                    ║
            ║                                                                         ║
            ║  4) Ranking System with Automatic Ordering                              ║
            ║     Players are ranked automatically based on performance using         ║
            ║     sorted data structures and custom comparison logic.                 ║
            ║                                                                         ║
            ║  5) Fault-Tolerant Input Pipeline                                       ║
            ║     A defensive input system handles invalid data, unexpected           ║
            ║     inputs, and user mistakes without interrupting gameplay.            ║
            ║                                                                         ║
            ║  6) Self-Recovering Execution Flow                                      ║
            ║     The system safely recovers from runtime exceptions and              ║
            ║     continues execution instead of terminating the application.         ║
            ║                                                                         ║
            ║  7) Continuous Game Lifecycle                                           ║
            ║     Matches run inside a controlled loop allowing multiple rounds       ║
            ║     without restarting the program or losing session data.              ║
            ║                                                                         ║
            ║  8) Separation of Responsibilities                                      ║
            ║     Gameplay logic, player management, and display formatting           ║
            ║     are organized into independent components for maintainability.      ║
            ║                                                                         ║
            ║  9) Scalable Architecture                                               ║
            ║     The design allows easy expansion such as AI opponents,              ║
            ║     graphical interfaces, or networked multiplayer support.             ║
            ║                                                                         ║
            ║ 10) Developer-Friendly Code Structure                                   ║
            ║     Clean modular design makes the project easy to debug,               ║
            ║     test, extend, and maintain over time.                               ║
            ╚═════════════════════════════════════════════════════════════════════════╝
            
            """;

    public static final String ISTRUCTION_STRING = """
            
            ╔═════════════════════════════════════════════════════════════════════════╗
            ║                        ⚡ INSTRUCTIONS / GUIDE ⚡                         ║
            ╠═════════════════════════════════════════════════════════════════════════╣
            ║  1) Please turn on Full-Screen mode to properly view the game board.    ║
            ║                                                                         ║
            ║  2) At any moment (input), if you want to exit the game,                ║
            ║     type 'exit' and press Enter.                                        ║
            ║                                                                         ║
            ║  3) It is not necessary to enter player names;                          ║
            ║     you can just press Enter to use default names.                      ║
            ║                                                                         ║
            ║  4) For any input, if you just press Enter,                             ║
            ║     it will be accepted as 'Y' (for inputs that accept Y/N).            ║
            ║                                                                         ║
            ║  5) All games played will be listed at the end.                         ║
            ║                                                                         ║
            ║  6) The scores of the current match are shown after each round ends.    ║
            ║                                                                         ║
            ║  7) Before providing input, please read the prompt carefully.           ║
            ╚═════════════════════════════════════════════════════════════════════════╝
            """;

    public static final String ADMIN_PLAYER_BOARD_TITLE = " 👥 ALL PLAYERS 👥 ";
    public static final String LEADERBOARD_TITLE = " 🏆 LEADERBOARD 🏆 ";

    public static final String NO_PLAYERS_LEADERBOARD = """
            
            ╔════════════════════════╗
            ║   🏆 LEADERBOARD 🏆    ║
            ╠════════════════════════╣
            ║  No registered players ║
            ╚════════════════════════╝
            """;

    public static final String WELCOME_NEW_PLAYER_1 = """
            🎮 Welcome back, %s! Lifetime Wins: %d,
            Ready for another victory? 🚀%n%n
            """;

    public static final String WELCOME_NEW_PLAYER_2 = """
            🏆 Welcome back, %s! You’ve conquered %d battles,
            Let’s add one more! 💥%n%n
            """;

    public static final String WELCOME_REGISTERED_PLAYER_1 = """
            🎮 Welcome to the arena, %s!
            You're officially registered as a NEW player! 🚀%n%n
            """;

    public static final String WELCOME_REGISTERED_PLAYER_2 = """
            💥 Welcome %s! You're now in the game,
            Time to claim your first victory! 🏆%n%n
            """;

    public static final String MATCH_DRAW_BOARD = """
            
            ╔════════════════════════════════════╗
            ║           🤝 MATCH DRAW!           ║
            ╠════════════════════════════════════╣
            ║  What a battle! It's a tie! 🎮     ║
            ╚════════════════════════════════════╝
            """;

    public static final String PLAYER_MANAGEMENT_BOARD = """
                
                ╔══════════════════════════════╗
                ║     🔧 PLAYER MANAGEMENT     ║
                ╚══════════════════════════════╝
                
                List of All the Players :-
                """;
}
