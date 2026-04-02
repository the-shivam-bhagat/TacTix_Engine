package bot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import static bot.BotUtility.*;
import static utility.Config.BotNames.EASY_BOT_NAME;

// GREX — GReedy EXtension bot
/// Selfish strategy (no blocking) -- Win → Line Extension → Random
public class EasyBot implements Bot {
    private static final String MODE = "EASY_BOT";
    private final String name;

    private final Random random;

    public EasyBot(boolean secondInstance) {
        name = EASY_BOT_NAME.concat(secondInstance ? "2.0" : "")
                .concat(String.format(" (%s)", MODE));
        random = new Random();
    }

    @Override
    public int chooseMove(int[] board, int botFlag, int step) {
        // Logic:
        // 1. If it can win → win
        // 2. Else → extend own line (row/col/diagonal scoring)
        // 3. Else → random

        var choices = new ArrayList<>(getChoices(board, botFlag));
        return choices.get(random.nextInt(choices.size()));
    }

    private HashSet<Integer> getChoices(int[] board, int botFlag) {
        // win
        var win = getWinIndexes(board, botFlag);
        if (!win.isEmpty()) return win;

        // extend
        var extend = getExtendIndexes(board, botFlag);
        if (!extend.isEmpty()) return extend;

        // valid cell
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