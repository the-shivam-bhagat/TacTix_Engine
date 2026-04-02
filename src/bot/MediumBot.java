package bot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import static bot.BotUtility.*;
import static utility.Config.BotNames.MEDIUM_BOT_NAME;

// WIRE   Win-first Immediate-block Rule-based EquiSelect
///  Reactive (win + block) -- Win → Block → Line Extension → Random
public class MediumBot implements Bot {
    private static final String MODE = "MEDIUM_BOT";
    private final String name;

    private final Random random;
    private static final float PERFECT_RATE = 0.1f;

    public MediumBot(boolean secondInstance) {
        name = MEDIUM_BOT_NAME.concat(secondInstance ? "2.0" : "")
                .concat(String.format(" (%s)", MODE));
        random = new Random();
    }

    @Override
    public int chooseMove(int[] board, int botFlag, int stepNo) {
        // Logic:
        // 1. If it can win → win
        // 2. If it can block opponent → block
        // 2. Else → extend own line (row/col/diagonal scoring)
        // 3. Else → random

        // adding a defect for best opening Strategy play
        if (stepNo < 2 && random.nextFloat() < PERFECT_RATE)
            return BotUtility.getOpeningStrategyMove(board, stepNo, random);

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
    public String getName() {
        return name;
    }

    @Override
    public String getMode() {
        return MODE;
    }
}