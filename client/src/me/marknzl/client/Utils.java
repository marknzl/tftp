package me.marknzl.client;

import java.util.Random;

public class Utils {

    public static String commandUsageFormat(Command command) {
        String usage = String.format("%s - %s\n", command.getCommand(), command.getDescription()) +
                String.format("Usage: \n\t%s\n", command.getUsage());
        return usage;
    }

    public static int generateTID() {
        Random random = new Random();
        int low = 0;
        int high = 65534;
        return random.nextInt(high - low) + low;
    }

}
