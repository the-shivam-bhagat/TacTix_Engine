package command.impl;

import command.Command;
import exception.SessionEndException;
import renderer.view.EndCommandView;
import sessions.SessionContext;
import utility.Logger;

/// Ends the current session — only valid during an active session.
/// Throws SessionEndException which propagates to GameEngine.startGameLoop().
/// Outside a session, prints ineligible and returns normally.
public class EndCommand implements Command {

    private final SessionContext context;
    private final EndCommandView renderer;

    public EndCommand(SessionContext context, EndCommandView renderer) {
        this.context = context;
        this.renderer = renderer;
    }

    @Override
    public void execute() {
        if (context.isInSession()) {
            Logger.info("End command executed — terminating session");
            renderer.showSessionTerminating();
            throw new SessionEndException();
        } else {
            Logger.warn("End command used outside active session");
            renderer.showCommandIneligible("end");
        }
    }
}