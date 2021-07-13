package me.marknzl.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client {

    public static String CLIENT_ROOT;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java -jar client.jar <client_root>");
            return;
        }

        CLIENT_ROOT = args[0];

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        CommandParser commandParser = new CommandParser();

        while (true) {
            try {
                System.out.print("> ");
                String input = reader.readLine();
                commandParser.parse(input);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
