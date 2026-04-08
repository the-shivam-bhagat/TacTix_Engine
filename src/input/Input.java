package input;

import engine.GameBoard;

public interface Input {

    String readLine();

    void waitForEnter();

    void waitForEnterWithoutCheck();

    boolean readYesNo();

    boolean readYesNo_Specific();

    int readBotLevelChoice();

    int readSessionChoice();

    int readCellChoice(GameBoard board);
}