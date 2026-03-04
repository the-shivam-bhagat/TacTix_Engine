import java.util.ArrayList;
import java.util.List;

/// Accumulates all completed sessions and prints a final summary.
final class GameHistory {
    private final List<GameSession> sessions = new ArrayList<>();

    void add(GameSession session) {
        sessions.add(session);
    }

    void print(InputHandler input) {

        System.out.println("""
                
                ╔════════════════════════════════════╗
                ║            📜 GAME SUMMARY         ║
                ╚════════════════════════════════════╝
                """);

        if (sessions.isEmpty())
            System.out.println("📋 No games recorded yet.\n");
        else
            for (int i = 0; i < sessions.size(); i++)
                System.out.println(sessions.get(i).summaryLine(i + 1));

        PlayerRegistry.trimToMaxPlayers();
        System.out.println("\n🏆 Updated Global Leaderboard:\n");
        GameEngine.displayLeaderboard();

        System.out.print("🎮 Session complete. Thank you for playing! (Press ENTER to exit) ");
        input.sc.nextLine();
    }
}


/// Manages a full match (one or more rounds) between two players.
final class GameSession {

    private final Player p1;
    private final Player p2;
    private final InputHandler input;

    // P1 = FIRST P2 = SECOND (Maybe swapped)
    private Player first;    // plays X
    private Player second;   // plays O

    private int wins1 = 0;   // wins for p1
    private int wins2 = 0;   // wins for p2
    private int ties = 0;

    private String result = "[Match Abandoned]";

    GameSession(Player p1, Player p2, InputHandler input) {
        this.first = this.p1 = p1;
        this.second = this.p2 = p2;
        this.input = input;
    }

    /// Loops rounds until the players choose to stop, then declares a match winner.
    void play() {
        int roundNumber = 0;
        boolean keepPlaying;
        do {
            System.out.printf("%n🚀 Round %d is about to begin! (Press ENTER to continue) ", ++roundNumber);
            input.waitForEnter();

            // Ask who goes first this round.
            System.out.printf("%n⚔️ %s vs %s!%n%s, want to make the first move? (Y/N): ",
                    first, second, first);
            if (!input.readYesNo()) {
                Player tmp = first;
                first = second;
                second = tmp;
            }

            playRound();

            // Show score
            System.out.printf("""
                            
                            📊 Current Scoreboard
                            ─────────────────────
                            %s : %d
                            %s : %d%s
                            
                            (Press ENTER to continue)""",
                    p1.name, wins1, p2.name, wins2,
                    ties > 0 ? String.format("%nTie : %d", ties) : ""
            );
            input.waitForEnter();

            System.out.print("\n\n🚀 Ready for the next round? (Y/N): ");
            keepPlaying = input.readYesNo();
        } while (keepPlaying);

        declareMatchResult();
    }

    /// one match/round in a session
    private void playRound() {
        // Fresh board + resources.
        char[][] playBoard = Utility.getPlayBoard();
        char[][][] xo = Utility.xo;
        int[][] idx = Utility.getStartIndexesOfEachBlock_1_to_9();

        /*
          this freq array is a map for checking is position empty for laying
          0 - no moves in ths cell;
          1 - first players move;
          -1 - second players move
         */

        int[] freq = new int[9];
        int stepCount = 0;   // step counter

        System.out.println("\n🎮 Here’s the Play Board:");
        Utility.displayPlayBoard(playBoard);

        // GameEngine game loop.
        while (true) {
            Player current = (stepCount % 2 == 0) ? first : second;
            char mark = (stepCount % 2 == 0) ? 'X' : 'O';

            System.out.printf("⚔️ %s, make your move (%c) — pick a position (1-9): ",
                    current, mark);

            int blockNo = input.readCellChoice(freq);

            freq[blockNo] = (stepCount % 2 == 0) ? 1 : -1;  // freq marking
            Utility.placeXO(playBoard, xo[stepCount % 2], idx[blockNo]);
            Utility.displayPlayBoard(playBoard);

            Boolean winCheck = null;
            if (++stepCount >= 5) winCheck = Utility.winnerCheck(freq); //check for winner


            if (winCheck != null) {
                if (winCheck) {
                    System.out.printf("%n🏆 Congratulations, %s! You WON this round! 🎉%n%n", first);
                    recordWin(first);
                    PlayerRegistry.incrementWin(first);

                } else {
                    System.out.printf("%n🏆 Congratulations, %s! You WON this round! 🎉%n%n", second);
                    recordWin(second);
                    PlayerRegistry.incrementWin(second);
                }
                return;
            }

            // Only increment AFTER confirming no winner
            if (stepCount > 8) {
                System.out.println("\n💥 Deadlock! Neither side claims victory this round! ⚔️\n");
                ties++;
                return;
            }
        }
    }

    private void recordWin(Player player) {
        if (player == p1) wins1++;
        else wins2++;
    }

    private void declareMatchResult() {

        if (wins1 == wins2) {
            System.out.println("""
                    
                    ╔════════════════════════════════════╗
                    ║           🤝 MATCH DRAW!           ║
                    ╠════════════════════════════════════╣
                    ║  What a battle! It's a tie! 🎮     ║
                    ╚════════════════════════════════════╝
                    """);
            result = "DRAW";

        } else {
            result = wins1 > wins2 ? p1.name :  p2.name;
            dynamicResultTable(result);
        }
    }

    String summaryLine(int index) {
        return String.format("""
                %d) Players : %s vs %s
                    Result  : %s
                """, index, p1.name, p2.name, result);
    }

    private static void dynamicResultTable(String name) {
        String line1 = "🏆 MATCH WINNER!";
        String line2 = "Congratulations, " + name + "! 🎉";
        String line3 = "You dominated this match! 💪";

        int innerWidth = Math.max(line2.length(), line3.length()) + 4;

        System.out.println();
        System.out.println("╔" + "═".repeat(innerWidth) + "╗");

        System.out.printf("║ %-" + (innerWidth - 2) + "s ║%n", line1);

        System.out.println("╠" + "═".repeat(innerWidth) + "╣");

        System.out.printf("║ %-" + (innerWidth - 2) + "s ║%n", line2);
        System.out.printf("║ %-" + (innerWidth - 2) + "s ║%n", line3);

        System.out.println("╚" + "═".repeat(innerWidth) + "╝");
    }
}