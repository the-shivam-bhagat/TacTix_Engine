package command.impl;

import command.Command;
import exception.UndoRequestException;
import renderer.view.UndoCommandView;
import sessions.SessionContext;
import utility.Logger;

/// Requests an undo — only valid during an active round in PvP or PvB.
/// Throws UndoRequestException which is caught inside the session's round loop.
/// Outside a round, prints ineligible and returns normally.
public class UndoCommand implements Command {

    private final SessionContext context;
    private final UndoCommandView renderer;

    public UndoCommand(SessionContext context, UndoCommandView renderer) {
        this.context = context;
        this.renderer = renderer;
    }

    @Override
    public void execute() {
        if (context.isUndoEnabled() && context.isInRound()) {
            Logger.info("Undo command executed");
            throw new UndoRequestException();
        } else {
            Logger.warn("Undo ineligible: undoEnabled=" + context.isUndoEnabled()
                    + ", inRound=" + context.isInRound());
            renderer.showCommandIneligible("undo");
        }
    }
}