package me.marknzl.client;

import java.io.IOException;
import java.net.*;

public class UDPClient {

    private final DatagramSocket socket;
    private final InetAddress address;
    private final int listenPort;
    private final byte[] buf;

    public UDPClient(String address, int listenPort) throws UnknownHostException, SocketException {
        this.address = InetAddress.getByName(address);
        this.socket = new DatagramSocket();
        this.buf = new byte[1024];
        this.listenPort = listenPort;
    }

    public void send(byte[] payload) throws IOException {
        DatagramPacket packet = new DatagramPacket(buf, buf.length, this.address, this.listenPort);
        socket.send(packet);
    }

}
