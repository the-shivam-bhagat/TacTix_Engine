import java.util.Iterator;
import java.util.Scanner;


public final class GameEngine {
    private static InputHandler input;
    static PlayerRegistry playerRegistry;


    private static void runIntroSequence(InputHandler input) {
        System.out.println(Strings.INTRO_STRING);

        System.out.print("\nDiscover the game features before we begin? (Y/N): ");
        if (input.readYesNo_Specific()) System.out.println(Strings.FEATURES_STRING);

        System.out.print("Let's see the instructions... (Press ENTER to continue)");
        input.waitForEnter();
        System.out.println(Strings.ISTRUCTION_STRING);

        System.out.print("Let's take look at Current Global Leaderboard..... (Press ENTER to continue)");
        input.waitForEnter();
        displayLeaderboard();

        System.out.print("Let's start the program..... (Press ENTER to continue) ");
        input.waitForEnter();
    }

    // Print Top 10 Leaderboard
    static void displayLeaderboard() {
        if (playerRegistry.ranking.isEmpty()) {
            System.out.println(Strings.NO_PLAYERS_LEADERBOARD);
            return;
        }

        int noOfPlayers = Math.min(PlayerRegistry.TOP_PLAYERS, playerRegistry.ranking.size());
        Iterator<Player> itr = playerRegistry.ranking.iterator();

        // ---- compute max widths ----
        int maxWinsLen = Math.max(5, Integer.toString(playerRegistry.ranking.first().getLifetimeWins()).length());
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
        itr = playerRegistry.ranking.iterator();
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
        Player existing = playerRegistry.players.get(name);
        if (existing != null) {
            if (flip) System.out.printf(Strings.WELCOME_NEW_PLAYER_1, name, existing.getLifetimeWins());
            else System.out.printf(Strings.WELCOME_NEW_PLAYER_2, name, existing.getLifetimeWins());
            flip = !flip;
            return existing;
        }

        // Case 2: New player
        // no name;
        if (name == null || name.isEmpty()) {
            for (int i = 1; i <= 60; i++) {
                String newName = "PLAYER_" + i;
                if (!playerRegistry.players.containsKey(newName)) {
                    name = newName;
                    break;
                }
            }
        }

        Player newPlayer = new Player(name);
        playerRegistry.addPlayer(newPlayer);

        if (flip) System.out.printf(Strings.WELCOME_REGISTERED_PLAYER_1, name);
        else System.out.printf(Strings.WELCOME_REGISTERED_PLAYER_2, name);

        flip = !flip;
        return newPlayer;
    }

    /// assign different players
    private static Player createPlayer(String pre, int number) {
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
        GameEngine.input = new InputHandler(new Scanner(System.in));
        GameHistory history = new GameHistory();
        GameEngine.playerRegistry = new PlayerRegistry();
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

            Player p1 = createPlayer("", 1);
            Player p2 = createPlayer(p1.name, 2);

            GameSession session = new GameSession(p1, p2, input);
            session.play();
            history.add(session);

            System.out.print("\n🎮 Play another game? (Y/N): ");
            playAnother = input.readYesNo();
            System.out.println();
        } while (playAnother);

        history.print(input);
    }

    public static void restart() {

        System.out.printf("""
                
                ⚠ A system error occurred.
                Do you want to restart the game (Y/N) ? :%1s""", "");

        if (GameEngine.input.readYesNo()) {
            System.out.print("\nRestarting the game..... (Press ENTER to continue) ");
            GameEngine.input.waitForEnter();

            main(new String[0]);   // restart program
        } else {
            System.out.print("\nProgram terminated. Thank you for playing! (Press ENTER to exit) ");
            System.exit(0);
        }
    }
}
