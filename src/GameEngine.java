import java.util.Iterator;
import java.util.Scanner;


public final class GameEngine {
    private static void runIntroSequence(InputHandler input) {
        System.out.println("""
                
                
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
                """);

        System.out.print("Let's see the instructions... (Press ENTER to continue)");
        input.waitForEnter();
        System.out.println("""
                
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
                """);

        System.out.print("Let's take look at Current Global Leaderboard..... (Press ENTER to continue)");
        input.waitForEnter();
        displayLeaderboard();

        System.out.print("Let's start the program..... (Press ENTER to continue) ");
        input.waitForEnter();
    }

    // Print Top 10 Leaderboard
    static void displayLeaderboard() {
        if (PlayerRegistry.ranking.isEmpty()) {
            System.out.println("""
                    
                    ╔════════════════════════╗
                    ║   🏆 LEADERBOARD 🏆    ║
                    ╠════════════════════════╣
                    ║  No registered players ║
                    ╚════════════════════════╝
                    """);
            return;
        }

        int noOfPlayers = Math.min(PlayerRegistry.TOP_PLAYERS, PlayerRegistry.ranking.size());
        Iterator<Player> itr = PlayerRegistry.ranking.iterator();

        // ---- compute max widths ----
        int maxWinsLen = Math.max(5, Integer.toString(PlayerRegistry.ranking.first().getLifetimeWins()).length());
        int maxNameLen = 5, count = 0;
        while (itr.hasNext() && count < noOfPlayers) {
            Player p = itr.next();

            int nameLen = p.name.length();
            if (nameLen > maxNameLen) maxNameLen = nameLen;
            count++;
        }

        // Padding - extra space
        maxNameLen += 4;
        maxWinsLen += 2;

        // column widths
        int rankWidth = 4;

        // inner width = (rankWidth + 2) + 1 + (maxNameLen + 2) + 1 + (maxWinsLen + 2)
        int innerWidth = rankWidth + maxNameLen + maxWinsLen + 8;

        // ---- PRINT HEADER ----
        System.out.println();
        System.out.println("╔" + "═".repeat(innerWidth) + "╗");

        String title = " 🏆 LEADERBOARD 🏆 ";
        int sidePadding = (innerWidth - title.length()) / 2;
        System.out.println("║" +
                " ".repeat(sidePadding) + title +
                " ".repeat(innerWidth - title.length() - sidePadding) +
                "║");
        System.out.println("╠" + "═".repeat(innerWidth) + "╣");

        // ---- COLUMN HEADERS ----
        System.out.printf(
                "║ %-" + rankWidth + "s │ %-" + maxNameLen + "s │ %-" + maxWinsLen + "s ║%n",
                "Rank", "Player", "Wins"
        );

        System.out.println(
                "╟" +
                        "─".repeat(rankWidth + 2) + "┼" +
                        "─".repeat(maxNameLen + 2) + "┼" +
                        "─".repeat(maxWinsLen + 2) + "╢"
        );

        // ---- ROWS ----
        itr = PlayerRegistry.ranking.iterator();
        int rank = 1;

        while (itr.hasNext() && rank <= noOfPlayers) {
            Player p = itr.next();

            System.out.printf(
                    "║ %-" + rankWidth + "d │ %-" + maxNameLen + "s │ %-" + maxWinsLen + "d ║%n",
                    rank, p.name, p.getLifetimeWins()
            );

            rank++;
        }

        System.out.println("╚" + "═".repeat(innerWidth) + "╝\n");
    }

    // for greeting message
    private static boolean flip = (int) Math.floor(Math.random() * 100) % 2 == 0;

    /// if players exists returns it, if not then create and return
    static Player getPlayer(String name) {
        // Case 1: Player exists
        Player existing = PlayerRegistry.players.get(name);
        if (existing != null) {
            if (flip) System.out.printf("""
                    🎮 Welcome back, %s! Lifetime Wins: %d,
                    Ready for another victory? 🚀%n%n
                    """, name, existing.getLifetimeWins());
            else System.out.printf("""
                    🏆 Welcome back, %s! You’ve conquered %d battles,
                    Let’s add one more! 💥%n%n
                    """, name, existing.getLifetimeWins());
            flip = !flip;
            return existing;
        }

        // Case 2: New player
        // no name;
        if (name == null || name.isEmpty()) {
            for (int i = 1; i <= 60; i++) {
                String newName = "PLAYER_" + i;
                if (!PlayerRegistry.players.containsKey(newName)) {
                    name = newName;
                    break;
                }
            }
        }

        Player newPlayer = new Player(name);
        PlayerRegistry.addPlayer(newPlayer);

        if (flip) System.out.printf("""
                🎮 Welcome to the arena, %s!
                You're officially registered as a NEW player! 🚀%n%n
                """, name);
        else System.out.printf("""
                💥 Welcome %s! You're now in the game,
                Time to claim your first victory! 🏆%n%n
                """, name);

        flip = !flip;
        return newPlayer;
    }

    /// assign different players
    private static Player createPlayer(InputHandler input, String pre, int number) {
        String name = null;
        while (true) {
            System.out.printf("Enter name of Player_%d : ", number);
            name = input.readLine().trim().toUpperCase();
            if (number == 2 && pre.equals(name))
                System.out.println(name + " is already playing!");
            else break;
        }
        return getPlayer(name);
    }

    /// Entry point
    public static void main(String[] args) {
        InputHandler input = new InputHandler(new Scanner(System.in));
        GameHistory history = new GameHistory();
        runIntroSequence(input);

        boolean playAnother;
        int gameNumber = 0;
        do {
            System.out.printf("%n⚔️ Game %d — Let the battle begin!%n", ++gameNumber);
            if (gameNumber > 1) {
                System.out.print(" ..... (Press ENTER to continue) ");
                input.waitForEnter();
            } else System.out.println();
            System.out.println();

            Player p1 = createPlayer(input, "", 1);
            Player p2 = createPlayer(input, p1.name, 2);

            GameSession session = new GameSession(p1, p2, input);
            session.play();
            history.add(session);

            System.out.print("\n🎮 Play another game? (Y/N): ");
            playAnother = input.readYesNo();
            System.out.println();
        } while (playAnother);

        history.print(input);
    }
}
