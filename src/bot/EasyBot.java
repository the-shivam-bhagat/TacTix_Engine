package bot;

import utility.Config;

import java.util.ArrayList;
import java.util.Random;

import static bot.BotUtility.*;
import static utility.Config.BotData.EASY_BOT_NAME;

// GREX — GReedy EXtension bot
// Difficulty: 37% (ELO: 1368)
// Heuristic-Aware Probabilistic Selection with Unreliable Block and Positional Bias

public class EasyBot implements Bot {

    private static final String MODE = "EASY_BOT";
    private static final int ELO_RATING = Config.BotData.EASY_BOT_ELO_RATING;

    private final String name;
    private final Random random = new Random();

    public EasyBot() {
        this.name = EASY_BOT_NAME;
    }

    public EasyBot(boolean firstInstance) {
        this.name = EASY_BOT_NAME.concat(firstInstance ? "-α" : "-β");
    }

    @Override
    public int chooseMove(int[] board, int botFlag, int step) {

        var valid = new ArrayList<>(getValidIndexes(board));

        // 40% random move → introduces weakness
        float RANDOMNESS_RATE = Config.BotData.EASY_BOT_RANDOMNESS_RATE;
        if (random.nextFloat() < RANDOMNESS_RATE)
            return valid.get(random.nextInt(valid.size()));

        // 1. Win
        var win = getWinIndexes(board, botFlag);
        if (!win.isEmpty()) return pickRandom(win, random);

        // 2. Block (ONLY sometimes)
        float BLOCKING_RATE = Config.BotData.EASY_BOT_BLOCKING_RATE;
        if (random.nextFloat() < BLOCKING_RATE) {
            var block = getWinIndexes(board, -botFlag);
            if (!block.isEmpty()) return pickRandom(block, random);
        }

        // 3. Prefer center (not always)
        float CENTER_PICK_RATE = Config.BotData.EASY_BOT_CENTER_PICK_RATE;
        if (board[4] == 0 && random.nextFloat() < CENTER_PICK_RATE)
            return 4;

        // 4. Corners
        var corners = new ArrayList<Integer>();
        for (int c : new int[]{0, 2, 6, 8})
            if (board[c] == 0) corners.add(c);

        if (!corners.isEmpty())
            return corners.get(random.nextInt(corners.size()));

        return valid.get(random.nextInt(valid.size()));
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