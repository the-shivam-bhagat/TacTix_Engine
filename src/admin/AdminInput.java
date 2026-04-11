package admin;

import core.GameBoard;
import input.Input;

import java.util.Scanner;

/// Raw input handler for admin panel — no command interception, no case conversion.
/// Admin panel uses plain Scanner deliberately to prevent command pipeline interference.
public final class AdminInput {

    private final Scanner sc;

    public AdminInput(Scanner sc) {
        this.sc = sc;
    }

    /// Read a menu choice — trimmed, no conversion
    public String readChoice() {
        return sc.nextLine().trim();
    }

    /// Read a player name — trimmed and uppercased (names are always uppercase)
    public String readName() {
        return sc.nextLine().trim().toUpperCase();
    }

    /// Read a raw password — no trim, no conversion, preserves all characters
    public String readPassword() {
        return sc.nextLine();
    }

    /// Read a plain integer input — trimmed
    public String readNumber() {
        return sc.nextLine().trim();
    }

    /// Read a Y/N confirmation — returns true if Y or y
    public boolean readConfirm() {
        String line = sc.nextLine().trim();
        return !line.isEmpty() && Character.toUpperCase(line.charAt(0)) == 'Y';
    }
}