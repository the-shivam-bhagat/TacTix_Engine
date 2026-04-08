package engine;

public final class GameResult {

    private final String p1Name;
    private final String p2Name;
    private final int wins1;
    private final int wins2;
    private final String result;

    public GameResult(String p1Name, String p2Name,
                      int wins1, int wins2, String result) {
        this.p1Name = p1Name;
        this.p2Name = p2Name;
        this.wins1 = wins1;
        this.wins2 = wins2;
        this.result = result;
    }

    public String getP1Name() {
        return p1Name;
    }

    public String getP2Name() {
        return p2Name;
    }

    public int getWins1() {
        return wins1;
    }

    public int getWins2() {
        return wins2;
    }

    public String getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "GameResult{" +
                "p1Name='" + p1Name + '\'' +
                ", p2Name='" + p2Name + '\'' +
                ", wins1=" + wins1 +
                ", wins2=" + wins2 +
                ", result='" + result + '\'' +
                '}';
    }
}