package bot;

import utility.Config;

import java.util.ArrayList;
import java.util.Random;

import static bot.BotUtility.*;
import static utility.Config.BotData.BEGINNER_BOT_ELO_RATING;
import static utility.Config.BotData.BEGINNER_BOT_NAME;

// RAVE — Random Any Valid-cell EquiSelect
// Difficulty: 0% (ELO: 1000)
// Random-dominant with Occasional Instinctive Extension and Rare Win Detection

public class BeginnerBot implements Bot {

    private static final String MODE = "BEGINNER_BOT";
    private static final int ELO_RATING = BEGINNER_BOT_ELO_RATING;

    private final String name;
    private final Random random = new Random();

    public BeginnerBot() {
        this.name = BEGINNER_BOT_NAME;
    }

    public BeginnerBot(boolean firstInstance) {
        this.name = BEGINNER_BOT_NAME.concat(firstInstance ? "-α" : "-β");
    }

    @Override
    public int chooseMove(int[] board, int botFlag, int stepNo) {

        var valid = new ArrayList<>(getValidIndexes(board));

        // 50% random → keeps it weak
        if (random.nextFloat() < Config.BotData.BEGINNER_BOT_RANDOMNESS_RATE)
            return valid.get(random.nextInt(valid.size()));

        // 1. Win (sometimes) 50%
        if (random.nextFloat() < Config.BotData.BEGINNER_BOT_PICK_WIN_RATE) {
            var win = getWinIndexes(board, botFlag);
            if (!win.isEmpty()) return pickRandom(win, random);
        }

        // 2. Extend (instinctive behavior)
        var extend = getExtendIndexes(board, botFlag);
        if (!extend.isEmpty()) return pickRandom(extend, random);

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