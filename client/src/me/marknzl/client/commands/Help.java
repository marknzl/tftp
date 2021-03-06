package me.marknzl.client.commands;

import me.marknzl.client.Command;
import me.marknzl.client.CommandUtils;
import me.marknzl.client.ClientUtils;

import java.util.HashMap;

public class Help implements Command {

    @Override
    public String getCommand() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Displays a list of available commands with their descriptions.";
    }

    @Override
    public String getUsage() {
        return "help | help <command_name>";
    }

    @Override
    public void execute(String[] args) {
        HashMap<String, Command> commands = CommandUtils.getCommands();

        if (args == null) { // Generic help message
            for (Command command : commands.values()) {
                System.out.printf("%s - %s\n", command.getCommand(), command.getDescription());
            }
        } else {
            if (commands.containsKey(args[0])) {    // Specific command help
                Command command = commands.get(args[0]);
                System.out.println(ClientUtils.commandUsageFormat(command));
            } else {
                System.out.println("Unknown command!");
            }
        }
    }

}
