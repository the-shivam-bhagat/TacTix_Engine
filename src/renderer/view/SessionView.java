package renderer.view;

import player.Player;

public interface SessionView {
    void showRoundStart(int round);

    void showFirstMovePrompt(String first, String second);

    void showMovePrompt(Player player, char mark);

    void showWinner(Player player);

    void showTie();

    void showScoreboard(String p1name, int w1, String p2name, int w2, int ties);

    void showNextRoundPrompt();

    void showMatchDraw();

    void showMatchWinnerBox(String name);

    @SuppressWarnings("unused")
    void prompt(String message);

    // time in ms means the time each dot will take to appear
    void showBotThinking(String BotName, int dotDelayInMS);

    void showBotMove(String botName, int blockNo, char mark);

    void showBotWinner(String  BotName);

    void showBotVsBotRoundWinner(String winnerName, String looserName);
}
