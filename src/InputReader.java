import java.util.Scanner;

final class InputReader {

    private final Scanner sc;

    InputReader(Scanner sc) {
        this.sc = sc;
    }

    String readLine() {
        return sc.nextLine().trim();
    }

    void waitForEnter() {
        sc.nextLine();
    }

    boolean readYesNo() {
        String line = readLine();
        return line.isEmpty() || Character.toUpperCase(line.charAt(0)) == 'Y';
    }

    boolean readYesNo_Specific() {
        String line = readLine();
        return !line.isEmpty() && Character.toUpperCase(line.charAt(0)) == 'Y';
    }

    int readCellChoice(int[] freq) {
        while (true) {
            String input = readLine();
            if (input.length() == 1 && input.charAt(0) >= '1' && input.charAt(0) <= '9') {
                int idx = input.charAt(0) - '1';
                if (freq[idx] == 0) return idx;
            }
        }
    }

    void waitForEnterWithoutCheck() {
        sc.nextLine();
    }
}