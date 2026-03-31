package command.impl;

import admin.AdminService;
import command.Command;
import renderer.EngineRenderer;

import java.util.Scanner;

public class ManageCommand implements Command {

    private final Scanner sc;
    private final AdminService admin;
    private final EngineRenderer renderer;

    public ManageCommand(Scanner sc, AdminService admin, EngineRenderer renderer) {
        this.sc = sc;
        this.admin = admin;
        this.renderer = renderer;
    }

    @Override
    public void execute() {
        renderer.prompt("\nProvide Password : ");
        String password = sc.nextLine();

        if (password.equals("123456")) {
            admin.show(sc);
        } else {
            renderer.prompt("Wrong password!\n");
        }

        renderer.prompt("""
                
                (Continue Your Game)
                
                """);
    }
}