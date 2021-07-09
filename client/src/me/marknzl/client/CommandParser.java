package me.marknzl.client;

import java.util.HashMap;

public class CommandParser {

    private HashMap<String, Command> commands;
    private UDPClient udpClient;

    public CommandParser(UDPClient udpClient) {
        CommandUtils.initializeCommands();
        this.commands = CommandUtils.getCommands();
        this.udpClient = udpClient;
    }

    public void parse(String input) {
        String[] content = input.split(" ");
        String cmd = content[0];
        String[] args = null;
        if (content.length > 1) {
            args = new String[content.length];
            System.arraycopy(content, 1, args, 0, content.length - 1);
        }

        if (commands.containsKey(cmd)) {
            commands.get(cmd).execute(args, this.udpClient);
        } else {
            System.out.println("Unknown command!");
        }
    }

}
