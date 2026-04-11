package renderer.view;

public interface SetupView {

    void showSessionTypes();

    void showSessionTypeInitialization(String sessionType);

    void showBotSelectionPrompt(int type);

    void showBotsPanelViewMessage();

    void showBotIntroduction(String title, String[] headers, String[][] data);

    void showBotChosen(String name, String slot);

    void showContinuePrompt();
}
