package input;

import core.GameBoard;

public interface Input {

    String readLine();

    String readRawLine();

    void waitForEnter();

    void waitForEnterWithoutCheck();

    boolean readYesNo();

    boolean readYesNo_Specific();

    int readBotLevelChoice();

    int readSessionChoice();

    int readCellChoice(GameBoard board);

    /// Read an integer in [min, max] inclusive. Re-prompts on invalid input.
    @SuppressWarnings("JavadocReference")
    int readBoundedInt(int min, int max);
}