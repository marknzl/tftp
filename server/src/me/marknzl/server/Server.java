package me.marknzl.server;

import me.marknzl.shared.Constants;
import me.marknzl.shared.Packet;
import me.marknzl.shared.UDPServer;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java -jar server.jar <file_root>");
            return;
        }

        try {
            start(args[0]);
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
    }

    private static void start(String fileRoot) throws SocketException {
        UDPServer server = new UDPServer(Constants.SERVER_LISTEN_PORT, 1024, fileRoot);
        DatagramSocket socket = server.getSocket();
        byte[] recvBuf = server.getBuffer();
        System.out.printf("TFTP server started on port %d\n", Constants.SERVER_LISTEN_PORT);

        //noinspection InfiniteLoopStatement - to suppress the false positive warning in IntelliJ.
        while (true) {
            try {
                socket.setSoTimeout(0);
                DatagramPacket clientPacket = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(clientPacket);
                Packet packet = new Packet(clientPacket.getData());

                switch (packet.getOpcode()) {
                    case RRQ -> {
                        RRQ.handleOperation(server, clientPacket);
                    }
                    case WRQ -> {
                        WRQ.handleOperation(server, clientPacket);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
