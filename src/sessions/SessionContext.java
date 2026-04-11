package sessions;

/// Tracks current execution state for context-sensitive command validation.
/// Commands check this to determine if they are eligible to execute.
/// GameEngine manages session state. Sessions manage round state.
public final class SessionContext {

    private boolean inSession = false;
    private boolean inRound = false;
    private boolean undoEnabled = false;

    /// Called by GameEngine before session.play()
    public void enterSession() {
        inSession = true;
        undoEnabled = false;
    }

    /// Called by GameEngine in finally after session.play()
    public void exitSession() {
        inSession = false;
        inRound = false;
    }

    /// Called by PvP and PvB sessions at the start of each round
    public void enterRound() {
        inRound = true;
    }

    /// Called by PvP and PvB sessions at the end of each round
    public void exitRound() {
        inRound = false;
    }

    public boolean isInSession() {
        return inSession;
    }

    /// True only during the active move-input phase of a round
    public boolean isInRound() {
        return inRound;
    }

    public void enableUndo() {
        undoEnabled = true;
    }

    public void disableUndo() {
        undoEnabled = false;
    }

    public boolean isUndoEnabled() {
        return undoEnabled;
    }
}