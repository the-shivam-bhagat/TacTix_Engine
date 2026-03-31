package renderer.view;

import player.Player;

public interface SessionView {
    void showRoundStart(int round);

    void showFirstMovePrompt(Player first, Player second);

    void showBoard(char[][] board);

    void showMovePrompt(Player player, char mark);

    void showWinner(Player player);

    void showTie();

    void showScoreboard(Player p1, int w1, Player p2, int w2, int ties);

    void showNextRoundPrompt();

    void showMatchDraw();

    void showMatchWinnerBox(String name);
}
