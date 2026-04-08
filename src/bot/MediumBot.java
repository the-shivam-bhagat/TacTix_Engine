package bot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import static bot.BotUtility.*;
import static utility.Config.BotData.*;

// WIRE — Win-first Immediate-block Rule-based Priority with Partial Opening Awareness
// Difficulty: 72% (ELO: 1716)
///  Reactive (win + block) -- Win → Block → Line Extension → Random

public class MediumBot implements Bot {

    private static final String MODE = "MEDIUM_BOT";
    private static final int ELO_RATING = MEDIUM_BOT_ELO_RATING;

    private final String name;
    private final Random random = new Random();

    // Correct opening Rate - 20%
    private static final float OPENING_AWARENESS_RATE = MEDIUM_BOT_OPENING_AWARENESS_RATE;

    public MediumBot() {
        this.name = MEDIUM_BOT_NAME;
    }

    public MediumBot(boolean firstInstance) {
        this.name = MEDIUM_BOT_NAME.concat(firstInstance ? "-α" : "-β");
    }

    @Override
    public int chooseMove(int[] board, int botFlag, int stepNo) {
        // Logic:
        // 1. If it can win → win
        // 2. If it can block opponent → block
        // 2. Else → extend own line (row/col/diagonal scoring)
        // 3. Else → random

        // adding a defect for best opening Strategy play
        if (stepNo < 2 && random.nextFloat() < OPENING_AWARENESS_RATE) {
            int move = BotUtility.getOpeningStrategyMove(board, stepNo, random);
            if (move != -1) return move;
        }

        var choices = new ArrayList<>(getChoices(board, botFlag));
        return choices.get(random.nextInt(choices.size()));
    }

    private HashSet<Integer> getChoices(int[] board, int botFlag) {
        // win
        var win = getWinIndexes(board, botFlag);
        if (!win.isEmpty()) return win;

        // block
        var block = getWinIndexes(board, -botFlag);
        if (!block.isEmpty()) return block;

        // extend
        var extend = getExtendIndexes(board, botFlag);
        if (!extend.isEmpty()) return extend;

        // valid cells
        return getValidIndexes(board);
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