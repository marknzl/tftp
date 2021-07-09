package me.marknzl.client.commands;

import me.marknzl.client.Command;
import me.marknzl.client.UDPClient;

import java.net.DatagramSocket;

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
    public void execute(String[] args, UDPClient udpClient) {
        // TODO
    }

}
