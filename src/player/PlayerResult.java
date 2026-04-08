package player;

public class PlayerResult {
    private final Player player;
    private final boolean isNew;

    public PlayerResult(Player player, boolean isNew) {
        this.player = player;
        this.isNew = isNew;
    }

    public Player getPlayer() { return player; }
    public boolean isNew() { return isNew; }
}
