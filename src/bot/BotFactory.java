package bot;

import exception.InvalidBotSelectionException;
import utility.Logger;

public class BotFactory {

    // no objects created
    private BotFactory() {}

    public static Bot createBot(int level) {
        return switch (level) {
            case 1 -> new BeginnerBot();
            case 2 -> new EasyBot();
            case 3 -> new MediumBot();
            case 4 -> new HardBot();
            case 5 -> new UnbeatableBot();
            case 0 -> new StallBot();
            default -> {
                Logger.error("Invalid bot level selected: " + level);
                throw new InvalidBotSelectionException(level);
            }
        };
    }

    public static Bot createBot(int level, boolean isFirstInstance) {
        return switch (level) {
            case 1 -> new BeginnerBot(isFirstInstance);
            case 2 -> new EasyBot(isFirstInstance);
            case 3 -> new MediumBot(isFirstInstance);
            case 4 -> new HardBot(isFirstInstance);
            case 5 -> new UnbeatableBot(isFirstInstance);
            case 0 -> new StallBot(isFirstInstance);
            default -> {
                Logger.error("Invalid bot level selected: " + level);
                throw new InvalidBotSelectionException(level);
            }
        };
    }
}