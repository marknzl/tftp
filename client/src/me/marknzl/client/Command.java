package me.marknzl.client;

import me.marknzl.shared.UDPClient;

public interface Command {

    String getCommand();
    String getDescription();
    String getUsage();
    void execute(String[] args);

}
