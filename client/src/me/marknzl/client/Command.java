package me.marknzl.client;

public interface Command {

    String getCommand();
    String getDescription();
    String getUsage();
    void execute(String[] args, UDPClient udpClient);

}
