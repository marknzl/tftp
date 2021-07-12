package me.marknzl.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client {

    public static void main(String[] args) {
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
