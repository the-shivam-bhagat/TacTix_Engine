final class InputHandler {

    private final InputReader reader;
    private final CommandHandler commandHandler;

    InputHandler(InputReader reader, CommandHandler commandHandler) {
        this.reader = reader;
        this.commandHandler = commandHandler;
    }

    String readLine() {
        String line = reader.readLine();

        if (commandHandler.handle(line)) {
            return "";
        }

        return line;
    }

    void waitForEnter() {
        readLine();
    }

    boolean readYesNo() {
        return reader.readYesNo();
    }

    boolean readYesNo_Specific() {
        return reader.readYesNo_Specific();
    }

    int readCellChoice(int[] freq) {
        return reader.readCellChoice(freq);
    }

    void waitForEnterWithoutCheck() {
        reader.waitForEnterWithoutCheck();
    }
}