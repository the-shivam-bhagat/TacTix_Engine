package input;

import command.CommandProcessor;
import core.GameBoard;
import renderer.view.InputView;
import utility.Logger;

import java.util.Scanner;


public final class InputHandler implements Input{

    private final InputView renderer;
    private final Scanner sc;
    private final CommandProcessor commandProcessor;

    public InputHandler(Scanner sc, InputView renderer, CommandProcessor commandProcessor) {
        this.sc = sc;
        this.renderer = renderer;
        this.commandProcessor = commandProcessor;
    }

    @Override
    public String readLine() {
        String raw = sc.nextLine().trim();
        String line = raw.toUpperCase();
        if (commandProcessor.handle(line)) return null;
        return line;
    }

    @Override
    public String readRawLine() {
        String raw = sc.nextLine().trim();
        if (commandProcessor.handle(raw.toUpperCase())) return null;
        return raw;
    }

    @Override
    public void waitForEnter() {
        readLine();
    }

    @Override
    public boolean readYesNo() {
        while (true) {
            String line = readLine(); // can throw SessionEndException
            if (line == null) continue; // command handled — re-prompt instead of defaulting
            return line.isEmpty() || Character.toUpperCase(line.charAt(0)) == 'Y';
        }
    }

    @Override
    public boolean readYesNo_Specific() {
        while (true) {
            String line = readLine();
            if (line == null) continue;
            return !line.isEmpty() && Character.toUpperCase(line.charAt(0)) == 'Y';
        }
    }

    @Override
    public int readBotLevelChoice() {
        while (true) {
            String input = readLine();
            if (input == null) continue;
            if (input.length() == 1 && input.charAt(0) >= '0' && input.charAt(0) <= '5')
                return input.charAt(0) - '0';
            Logger.warn("Invalid bot level input: " + input);
            renderer.showInvalidBotChoice();
        }
    }

    @Override
    public int readSessionChoice() {
        while (true) {
            String input = readLine();
            if (input == null) continue;
            if (input.length() == 1 && input.charAt(0) >= '1' && input.charAt(0) <= '3')
                return input.charAt(0) - '0';
            Logger.warn("Invalid session choice: " + input);
            renderer.showInvalidSessionChoice();
        }
    }

    @Override
    public int readCellChoice(GameBoard board) {
        while (true) {
            String input = readLine();
            if (input == null) continue;
            if (input.length() == 1 && input.charAt(0) >= '1' && input.charAt(0) <= '9') {
                int idx = input.charAt(0) - '1';
                if (board.isCellFree(idx)) return idx;
                // Cell occupied — this is an InvalidMoveException scenario
                Logger.warn("Cell already occupied: " + (idx + 1));
            }
            renderer.showInvalidCellChoice();
        }
    }

    @Override
    public int readBoundedInt(int min, int max) {
        while (true) {
            String raw = readLine();
            if (raw == null) continue;
            try {
                int val = Integer.parseInt(raw.trim());
                if (val >= min && val <= max) return val;
            } catch (NumberFormatException ignored) {}
            renderer.showInvalidBoundedInt(min, max);
        }
    }

    @Override
    public void waitForEnterWithoutCheck() {
        sc.nextLine();
    }
}