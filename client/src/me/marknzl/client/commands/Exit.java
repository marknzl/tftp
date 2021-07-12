package me.marknzl.client.commands;

import me.marknzl.client.Command;

public class Exit implements Command {

    @Override
    public String getCommand() {
        return "exit";
    }

    @Override
    public String getDescription() {
        return "Exits the program";
    }

    @Override
    public String getUsage() {
        return "exit";
    }

    @Override
    public void execute(String[] args) {
        System.exit(0);
    }
}
