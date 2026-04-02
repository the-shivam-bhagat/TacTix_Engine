package bot;

import java.util.ArrayList;
import java.util.Random;

import static bot.BotUtility.*;
import static utility.Config.BotNames.HARD_BOT_NAME;

/// FLINT  Fallible Lookahead with Informed Non-deterministic Tactics
/// Win → Block → Fork → Block Fork → Positional → FLUX defect layer
public class HardBot implements Bot {
    private static final String MODE = "HARD_BOT";
    private final String name;

    // FLUX defect rate — 8% chance of a complete lapse (plays random)
    // This is what separates FORGE from PROBE: it's strong but not perfect
    private static final float DEFECT_RATE = 0.08f;

    private final Random random;

    public HardBot(boolean secondInstance) {
        name = HARD_BOT_NAME.concat(secondInstance ? "2.0" : "")
                .concat(String.format(" (%s)", MODE));
        random = new Random();
    }

    @Override
    public int chooseMove(int[] board, int botFlag, int stepNo) {

        // FLUX — rare complete lapse, applied first to mimic genuine human error
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

        // 3. Create fork (move that creates 2+ simultaneous threats)
        var botFork = getForkIndexes(board, botFlag);
        if (!botFork.isEmpty()) return pickRandom(botFork, random);

        // 4. Block opponent fork
        var playerFork = getForkIndexes(board, -botFlag);
        if (!playerFork.isEmpty()) {
            // prefer cells that block fork AND force opponent to defend us (tempo gain)
            var smartBlock = getWinIndexes(board, botFlag);
            smartBlock.retainAll(playerFork);
            if (!smartBlock.isEmpty()) return pickRandom(smartBlock, random);
            return pickRandom(playerFork, random);
        }

        // 5. Positional fallback — center → corners → edges
        for (int move : priorityOrder)
            if (board[move] == 0) return move;

        return -1; // unreachable
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getMode() {
        return MODE;
    }
}