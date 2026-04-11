package command.impl;

import admin.AdminService;
import command.Command;
import static utility.Config.AdminConfig.ADMIN_PASSWORD;

import renderer.view.ManageCommandView;
import utility.Logger;

import java.util.Scanner;

public class ManageCommand implements Command {

    private final Scanner sc;
    private final AdminService admin;
    private final ManageCommandView renderer;

    public ManageCommand(Scanner sc, AdminService admin, ManageCommandView renderer) {
        this.sc = sc;
        this.admin = admin;
        this.renderer = renderer;
    }

    @Override
    public void execute() {
        renderer.requestAdminPassword();
        String password = sc.nextLine();

        if (ADMIN_PASSWORD.equals(password)) {
            Logger.warn("Admin password has been entered.");
            admin.show(sc);
        } else {
            Logger.warn("Invalid admin password attempt");
            renderer.showInvalidAdminPassword();
        }

        renderer.showContinueFromManageCmd();
    }
}