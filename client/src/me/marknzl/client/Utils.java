package me.marknzl.client;

public class Utils {

    public static String commandUsageFormat(Command command) {
        return String.format("%s - %s\n", command.getCommand(), command.getDescription()) +
                String.format("Usage: \n\t%s\n", command.getUsage());
    }

    public static boolean validFileArgs(String[] args) {
        if (args == null) {
            return false;
        } else return args.length == 1;
    }

}
