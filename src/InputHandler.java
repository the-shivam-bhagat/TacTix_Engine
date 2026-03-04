import java.util.Scanner;

/// Centralises all console I/O and handles the 'exit' command.
final class InputHandler {

    final Scanner sc;

    InputHandler(Scanner sc) {
        this.sc = sc;
    }

    /// exits if user typed "exit".
    String readLine() {
        String line = sc.nextLine().trim();
        if (line.equalsIgnoreCase("exit")) {
            System.out.println("\nExiting game. Goodbye!");
            PlayerRegistry.trimToMaxPlayers();
            System.exit(0);
        }
        if (line.equalsIgnoreCase("manage")) {
            System.out.print("Provide Password :");
            String password = sc.nextLine().trim();
            if (password.equalsIgnoreCase("123456")) {
                manage();
            } else System.out.println("Wrong password!");
            System.out.println("Continue Your Game!");
        }
        return line;
    }

    /// Waits for ENTER (any text ignored).
    void waitForEnter() {
        readLine();
    }

    /// Reads a Y/N answer. (empty == Yes.)
    boolean readYesNo() {
        String line = readLine();
        return line.isEmpty() || Character.toUpperCase(line.charAt(0)) == 'Y';
    }

    /// Reads & validates board cell number 1–9 (non-zero means occupied)
    int readCellChoice(int[] freq) {
        while (true) {
            String input = readLine();
            if (input.length() == 1 && input.charAt(0) >= '1' && input.charAt(0) <= '9') {
                int idx = input.charAt(0) - '1';
                if (freq[idx] == 0) return idx;
                System.out.print("Invalid input, please enter again : ");
            } else
                System.out.print("Invalid input, please enter again : ");
        }
    }

    public void manage() {
        System.out.println("""
                
                ╔══════════════════════════════╗
                ║     🔧 PLAYER MANAGEMENT     ║
                ╚══════════════════════════════╝
                """);

        while (true) {

            System.out.print("\nEnter player name to delete (or type 'exit'): ");
            String name = sc.nextLine().trim().toUpperCase();

            if (name.isEmpty()) {
                System.out.println("⚠ Player name cannot be empty.");
                continue;
            }

            if (PlayerRegistry.deletePlayerByName(name)) {
                System.out.printf("✅ Player '%s' deleted successfully.%n", name);
            } else {
                System.out.printf("❌ Player '%s' not found.%n", name);
            }

            System.out.print("Delete another player? (Y/N): ");
            String choice = sc.nextLine().trim().toUpperCase();
            if (!choice.isEmpty() && Character.toUpperCase(choice.charAt(0)) != 'Y') break;
        }

        System.out.println("\n🔒 Exiting Player Management.\n");
    }
}