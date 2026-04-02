package bot;

import java.util.ArrayList;
import java.util.Random;

import static bot.BotUtility.getValidIndexes;
import static utility.Config.BotNames.BEGINNER_BOT_NAME;

// RAVE — Random Any Valid-cell EquiSelect
///  Pure random - random
public class BeginnerBot implements Bot {
    private static final String MODE = "BEGINNER_BOT";
    private final String name;

    private final Random random;

    public BeginnerBot(boolean secondInstance) {
        name = BEGINNER_BOT_NAME.concat(secondInstance ? "2.0" : "")
                .concat(String.format(" (%s)", MODE));
        random = new Random();
    }

    @Override
    public int chooseMove(int[] board, int playerFlag, int stepNo) {
        // Logic - random
        var choices = new ArrayList<>(getValidIndexes(board));
        return choices.get(random.nextInt(choices.size()));
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