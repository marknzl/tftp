package me.marknzl.client.commands;

import me.marknzl.client.Command;
import me.marknzl.client.Utils;
import me.marknzl.shared.Constants;
import me.marknzl.shared.Packets.WRQPacket;
import me.marknzl.shared.UDPClient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class WRQ implements Command {

    @Override
    public String getCommand() {
        return "wrq";
    }

    @Override
    public String getDescription() {
        return "Initiates a write request";
    }

    @Override
    public String getUsage() {
        return "wrq <filename>";
    }

    @Override
    public void execute(String[] args) {
        if (args == null) {
            System.out.println(Utils.commandUsageFormat(this));
        } else if (args.length != 1) {
            System.out.println(Utils.commandUsageFormat(this));
        }

        assert args != null;
        String filename = args[0];

        Scanner scanner = new Scanner(new InputStreamReader(System.in));
        System.out.print("Enter server address: ");
        String address = scanner.nextLine();

        UDPClient client = null;

        try {
            client = new UDPClient(address, Constants.SERVER_LISTEN_PORT);

            WRQPacket packet = new WRQPacket();
            packet.writeFilename(filename);
            byte[] payload = packet.getPayload();
            System.out.println(payload.length);

            client.send(payload);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
