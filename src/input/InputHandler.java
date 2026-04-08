package input;

import command.CommandProcessor;
import engine.GameBoard;
import renderer.view.EngineView;
import utility.Logger;

import java.util.Scanner;


public final class InputHandler implements Input{

    private final EngineView renderer;
    private final Scanner sc;
    private final CommandProcessor commandProcessor;

    public InputHandler(Scanner sc, EngineView renderer, CommandProcessor commandProcessor) {
        this.sc = sc;
        this.renderer = renderer;
        this.commandProcessor = commandProcessor;
    }

    @Override
    public String readLine() {
        String line = sc.nextLine().trim().toUpperCase();
        if (commandProcessor.handle(line)) return null;
        return line;
    }

    @Override
    public void waitForEnter() {
        sc.nextLine();
    }

    @Override
    public boolean readYesNo() {
        String line = readLine();
        if (line == null) return false;
        return line.isEmpty() || Character.toUpperCase(line.charAt(0)) == 'Y';
    }

    @Override
    public boolean readYesNo_Specific() {
        String line = readLine();
        if (line == null) return false;
        return !line.isEmpty() && Character.toUpperCase(line.charAt(0)) == 'Y';
    }

    @Override
    public int readBotLevelChoice() {
        while (true) {
            String input = readLine();
            if (input != null && input.length() == 1 && input.charAt(0) >= '0' && input.charAt(0) <= '5')
                return input.charAt(0) - '0';
            Logger.warn("Invalid  input for session type attempted");
            renderer.showInvalidBotChoice();
        }
    }

    @Override
    public int readSessionChoice() {
        while (true) {
            String input = readLine();
            if (input != null && input.length() == 1 && input.charAt(0) >= '1' && input.charAt(0) <= '3')
                return input.charAt(0) - '0';
            Logger.warn("Invalid  input for session type attempted");
            renderer.showInvalidSessionChoice();
        }
    }

    @Override
    public int readCellChoice(GameBoard board) {
        while (true) {
            String input = readLine();
            if (input != null && input.length() == 1 && input.charAt(0) >= '1' && input.charAt(0) <= '9') {
                int idx = input.charAt(0) - '1';
                if (board.isCellFree(idx)) return idx;
            }
            Logger.warn("Invalid cell input for board cell choice attempted");
            renderer.showInvalidCellChoice();
        }
    }

    @Override
    public void waitForEnterWithoutCheck() {
        sc.nextLine();
    }
}