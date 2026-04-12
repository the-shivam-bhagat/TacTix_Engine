package renderer.view;

public interface InputView {
    void showInvalidSessionChoice();

    void showInvalidBotChoice();

    void showInvalidCellChoice();

    void showInvalidBoundedInt(int min, int max);
}
