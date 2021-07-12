package me.marknzl.client.commands;

import me.marknzl.client.Command;
import me.marknzl.client.Utils;
import me.marknzl.shared.Constants;
import me.marknzl.shared.Opcode;
import me.marknzl.shared.Packets.WRQPacket;
import me.marknzl.shared.UDPClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.UnknownHostException;
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

        String filename = args[0];

        File file = new File(filename);
        if (!(file.exists() || file.isDirectory())) {
            System.out.println("File doesn't exist!");
            return;
        }

        Scanner scanner = new Scanner(new InputStreamReader(System.in));
        System.out.print("Enter server address: ");
        String address = scanner.nextLine();

        UDPClient client = null;

        try {
            client = new UDPClient(address, Constants.LISTEN_PORT);
        } catch (SocketException | UnknownHostException ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        WRQPacket packet = new WRQPacket();
        packet.writeOpcode(Opcode.WRQ);
        packet.writeFilename(filename);
        byte[] payload = packet.getPayload();

        try {
            client.send(payload);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
