package me.marknzl.client;

import me.marknzl.shared.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter server address: ");
        String address = null;

        try {
            address = reader.readLine();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (address == null)
            System.exit(1);

        UDPClient udpClient = null;

        try {
            udpClient = new UDPClient(address, Constants.LISTEN_PORT);
        } catch (UnknownHostException | SocketException ex) {
            ex.printStackTrace();
        }

        if (udpClient == null)
            System.exit(1);

        CommandParser commandParser = new CommandParser(udpClient);

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
