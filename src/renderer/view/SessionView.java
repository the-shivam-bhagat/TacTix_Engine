package renderer.view;

import bot.Bot;
import player.Player;

public interface SessionView {
    void showRoundStart(int round);

    void showFirstMovePrompt(Player first, Player second);

    void showBoard(char[][] board);

    void showMovePrompt(Player player, char mark);

    void showWinner(Player player);

    void showTie();

    void showScoreboard(String p1name, int w1, String p2name, int w2, int ties);

    void showNextRoundPrompt();

    void showMatchDraw();

    void showMatchWinnerBox(String name);

    void prompt(String message);

    void showBotThinking(Bot bot);

    void showBotMove(Bot bot, int blockNo, char mark);

    void showBotWinner(Bot bot);

    void showFirstMovePrompt(Player player, Bot bot);
}
