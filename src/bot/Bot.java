package bot;

public interface Bot {

    int chooseMove(int[] board, int playerFlag, int stepNo);

    String getNameWithELO();

    String getNameWithMode();

    String getName();

    String getMode();

    @SuppressWarnings("unused")
    int getEloRating();

    String getFullIdentity();
}

// RAVE   Random Any Valid-cell EquiSelect                               - Beginner
// GREX   Greedy Reactive Extension                                      - Easy
// WIRE   Win-first Immediate-block Rule-based EquiSelect                - Medium
// FLINT  Fallible Lookahead with Informed Non-deterministic Tactics     - Hard
// PROBE  Priority-Reordered Opening-Boosted EquiMinMax                  - Unbeatable