package renderer.view;

import player.Player;
import java.util.List;

public interface PlayerTableView {
    void showLeaderboard(List<Player> players, String title);   // rank, name, wins, daysOld
    void showAdminTable(List<Player> players, String title);    // rank, name, wins, daysOld, joinDate
}