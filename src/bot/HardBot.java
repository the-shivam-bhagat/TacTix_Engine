package bot;

import utility.Config;

import java.util.ArrayList;
import java.util.Random;

import static bot.BotUtility.*;
import static utility.Config.BotData.*;

// FLINT — Fork-aware Priority Rule Engine with Opening Strategy and Controlled Defect Injection
// Difficulty: 83% (ELO: 1833)
/// FLINT  Fallible Lookahead with Informed Non-deterministic Tactics
/// Win → Block → Fork → Block Fork → Positional → FLUX defect layer

public class HardBot implements Bot {

    private static final String MODE = "HARD_BOT";
    private static final int ELO_RATING = HARD_BOT_ELO_RATING;

    private final String name;

    // FLUX defect rate — 14% chance of a complete lapse (plays random)
    // This is what separates FORGE from PROBE: it's strong but not perfect
    private static final float DEFECT_RATE = Config.BotData.HARD_BOT_DEFECT_RATE;

    private final Random random = new Random();

    public HardBot() {
        this.name = HARD_BOT_NAME;
    }

    public HardBot(boolean firstInstance) {
        this.name = HARD_BOT_NAME.concat(firstInstance ? "-α" : "-β");
    }

    @Override
    public int chooseMove(int[] board, int botFlag, int stepNo) {
        // FLUX — rare complete lapse (human-like mistake)
        if (random.nextFloat() < DEFECT_RATE) {
            var valid = new ArrayList<>(getValidIndexes(board));
            return valid.get(random.nextInt(valid.size()));
        }

        // Opening strategy
        int opening = getOpeningStrategyMove(board, stepNo, random);
        if (opening != -1) return opening;

        // 1. Win
        var win = getWinIndexes(board, botFlag);
        if (!win.isEmpty()) return pickRandom(win, random);

        // 2. Block
        var block = getWinIndexes(board, -botFlag);
        if (!block.isEmpty()) return pickRandom(block, random);

        // 3. Create fork
        var botFork = getForkIndexes(board, botFlag);
        if (!botFork.isEmpty()) return pickRandom(botFork, random);

        // 4. Block opponent fork
        var playerForks = getForkIndexes(board, -botFlag);
        if (!playerForks.isEmpty()) {

            // Case 1: Single fork → directly block
            if (playerForks.size() == 1)
                return playerForks.iterator().next();

            // Case 2: Multiple forks → try forcing move (create threat)
            for (int move : priorityOrder) {
                if (board[move] != 0) continue;

                board[move] = botFlag;

                // If this creates a winning threat → opponent must respond
                var threat = getWinIndexes(board, botFlag);

                board[move] = 0;

                if (!threat.isEmpty()) return move;
            }

            // Case 3: fallback → block any fork
            return pickRandom(playerForks, random);
        }

        // 5. Positional fallback — center → corners → edges
        for (int move : priorityOrder)
            if (board[move] == 0) return move;

        return pickRandom(getValidIndexes(board), random);
    }

    @Override
    public String getNameWithELO() {
        return String.format("%s (%d)", name, ELO_RATING);
    }

    @Override
    public String getNameWithMode() {
        return String.format("%s (%s)", name, MODE);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getMode() {
        return MODE;
    }

    @Override
    public int getEloRating() {
        return ELO_RATING;
    }

    @Override
    public String getFullIdentity() {
        return String.format("%s (%s, %d)", name, MODE, ELO_RATING);
    }
}