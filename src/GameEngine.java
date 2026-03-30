import java.io.PrintStream;
import java.util.Scanner;

public final class GameEngine {

    private static InputHandler input;
    static PlayerRegistry playerRegistry;
    private static GameHistory gameHistory;
    private static EngineRenderer engineRenderer;
    private static PlayerBoardRenderer boardRenderer;

    private static void runIntroSequence() {

        engineRenderer.showIntro();

        engineRenderer.prompt("\nDiscover the game features before we begin? (Y/N): ");
        if (input.readYesNo_Specific()) {
            engineRenderer.showFeatures();
        }

        engineRenderer.prompt("Let's see the instructions... (Press ENTER to continue)");
        input.waitForEnter();
        engineRenderer.showInstructions();

        engineRenderer.prompt("Let's take look at Current Global Leaderboard..... (Press ENTER to continue)");
        input.waitForEnter();
        displayLeaderboard(); // keep for now

        engineRenderer.prompt("Let's start the program..... (Press ENTER to continue) ");
        input.waitForEnter();
    }

    // Print Top 10 Leaderboard (we will refactor this later)
    static void displayLeaderboard() {
        boardRenderer.showBoard(
                playerRegistry.getTopPlayers(PlayerRegistry.TOP_PLAYERS),
                Strings.LEADERBOARD_TITLE
        );
    }

    // assign different players
    private static Player createPlayer(String pre, int number) {
        String name;

        while (true) {
            engineRenderer.prompt(String.format("Enter name of Player_%d : ", number));

            name = input.readLine().trim().toUpperCase();

            if (number == 2 && pre.equals(name)) {
                engineRenderer.prompt(name + " is already playing!\n");
            } else break;
        }

        return playerRegistry.getPlayer(name);
    }

    // Entry point
    public static void main(String[] args) {

        try {
            PrintStream output = new PrintStream(System.out);
            engineRenderer = new EngineRenderer(output);
            boardRenderer = new PlayerBoardRenderer(output);
            SessionRenderer sessionRenderer = new SessionRenderer(output);
            HistoryRenderer historyRenderer = new HistoryRenderer(output);

            playerRegistry = new PlayerRegistry(new FilePlayerStore());
            input = new InputHandler(new Scanner(System.in), playerRegistry, boardRenderer, engineRenderer);

            gameHistory = new GameHistory(historyRenderer);
            runIntroSequence();

            boolean playAnother = true;
            int gameNumber = 0;

            while (playAnother) {
                engineRenderer.showGameStart(++gameNumber);

                if (gameNumber > 1) {
                    engineRenderer.showContinuePrompt();
                    input.waitForEnter();
                } else engineRenderer.printLine();

                engineRenderer.printLine();

                Player p1 = createPlayer("", 1);
                Player p2 = createPlayer(p1.getName(), 2);

                GameSession session = new GameSession(p1, p2, input, playerRegistry, sessionRenderer);
                session.play();

                gameHistory.add(session);

                engineRenderer.showPlayAgainPrompt();
                playAnother = input.readYesNo();
                engineRenderer.printLine();
            }

            runFinalSequence();

        } catch (Exception ex) {
            System.out.println("\n" + ex.getMessage() + "\n");
            restart();
        }
    }

    private static void runFinalSequence() {

        // ---- History ----
        engineRenderer.showHistoryPrompt();
        input.waitForEnter();

        gameHistory.showHistory();

        // ---- Leaderboard ----
        engineRenderer.showUpdatedLeaderboardPrompt();
        input.waitForEnterWithoutCheck();

        playerRegistry.trimToMaxPlayers();
        boardRenderer.showBoard(
                playerRegistry.getTopPlayers(PlayerRegistry.TOP_PLAYERS),
                " 🏆 LEADERBOARD 🏆 "
        );

        // ---- Exit ----
        engineRenderer.showEndingMessage();
        input.waitForEnterWithoutCheck();
    }

    public static void restart() {

        engineRenderer.showRestartPrompt();

        if (input.readYesNo()) {
            engineRenderer.showRestartingMessage();
            input.waitForEnter();

            main(new String[0]);
        } else {
            engineRenderer.showExitMessage();
            System.exit(0);
        }
    }
}