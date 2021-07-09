package me.marknzl.client;

import me.marknzl.client.commands.Help;
import me.marknzl.client.commands.WRQ;

import java.util.HashMap;

public class CommandUtils {

    private static HashMap<String, Command> commands = new HashMap<>();

    public static void initializeCommands() {
        commands.put("help", new Help());
        commands.put("wrq", new WRQ());
    }

    public static HashMap<String, Command> getCommands() {
        return commands;
    }

}
