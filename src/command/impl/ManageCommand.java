package command.impl;

import admin.AdminService;
import command.Command;
import renderer.view.EngineView;
import utility.Config;
import utility.Logger;

import java.util.Scanner;

public class ManageCommand implements Command {

    private final Scanner sc;
    private final AdminService admin;
    private final EngineView renderer;

    public ManageCommand(Scanner sc, AdminService admin, EngineView renderer) {
        this.sc = sc;
        this.admin = admin;
        this.renderer = renderer;
    }

    @Override
    public void execute() {
        renderer.prompt("\nProvide Password : ");
        String password = sc.nextLine();

        if (password.equals(Config.ADMIN_PASSWORD)) {
            admin.show(sc);
        } else {
            Logger.warn("Invalid admin password attempt");
            renderer.prompt("Wrong password!\n");
        }

        renderer.prompt("""
                
                (Continue Your Game)
                
                """);
    }
}