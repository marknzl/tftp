package me.marknzl.server;

import java.io.IOException;
import java.net.DatagramSocket;

public class UDPServer {

    private DatagramSocket socket;
    private int listenPort;
    private byte[] recvBuf;

    public UDPServer(int listenPort) {
        this.listenPort = listenPort;
        this.recvBuf = new byte[512];
    }

    public void run() throws IOException {
        this.socket = new DatagramSocket(this.listenPort);
    }

}
