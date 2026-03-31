package input;

import engine.GameBoard;

public interface Input {

    String readLine();

    void waitForEnter();

    void waitForEnterWithoutCheck();

    boolean readYesNo();

    boolean readYesNo_Specific();

    int readCellChoice(GameBoard board);
}