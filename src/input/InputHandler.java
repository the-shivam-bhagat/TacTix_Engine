package input;

import command.CommandProcessor;
import engine.GameBoard;
import renderer.view.EngineView;

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
        if (commandProcessor.handle(line)) return "";
        return line;
    }

    @Override
    public void waitForEnter() {
        readLine();
    }

    @Override
    public boolean readYesNo() {
        String line = readLine();
        return line.isEmpty() || Character.toUpperCase(line.charAt(0)) == 'Y';
    }

    @Override
    public boolean readYesNo_Specific() {
        String line = readLine();
        return !line.isEmpty() && Character.toUpperCase(line.charAt(0)) == 'Y';
    }

    @Override
    public int readCellChoice(GameBoard board) {
        while (true) {
            String input = readLine();
            if (input.length() == 1 && input.charAt(0) >= '1' && input.charAt(0) <= '9') {
                int idx = input.charAt(0) - '1';
                if (board.isCellFree(idx)) return idx;
            }
            renderer.prompt("Please enter a valid unoccupied cell no (1 - 9) : ");
        }
    }

    @Override
    public void waitForEnterWithoutCheck() {
        sc.nextLine();
    }
}