package me.marknzl.server;

import me.marknzl.shared.Constants;
import me.marknzl.shared.ErrorCode;
import me.marknzl.shared.Packet;
import me.marknzl.shared.Packets.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
        byte[] recvBuf = new byte[1024];
        DatagramSocket socket = new DatagramSocket(Constants.SERVER_LISTEN_PORT);
        File rootDir = new File(fileRoot);

        while (true) {
            try {
                DatagramPacket clientPacket = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(clientPacket);
                Packet packet = new Packet(clientPacket.getData());

                switch (packet.getOpcode()) {
                    case RRQ:
                        RRQPacket rrq = new RRQPacket(clientPacket.getData());
                        System.out.printf("RRQ received for '%s'\n", rrq.getFilename());

                        File file = new File(rootDir, rrq.getFilename());
                        if (!(file.exists() || file.isDirectory())) {
                            ErrorPacket errorPacket = new ErrorPacket(ErrorCode.FILE_NOT_FOUND, "File not found!");
                            DatagramPacket response = new DatagramPacket(errorPacket.getPayload(), errorPacket.getPayload().length, clientPacket.getSocketAddress());
                            socket.send(response);
                            break;
                        }

                        short blockNum = 1;
                        byte[] fileBuf = new byte[512];

                        FileInputStream fileInputStream = new FileInputStream(file);
                        int bytesRead = fileInputStream.read(fileBuf);

                        while (bytesRead != -1) {
                            DataPacket dataPacket = new DataPacket(blockNum, fileBuf, 0, bytesRead);
                            DatagramPacket outgoingDataPacket = new DatagramPacket(dataPacket.getPayload(), dataPacket.getPayload().length, clientPacket.getSocketAddress());
                            System.out.printf("Sent %d bytes\n", outgoingDataPacket.getLength());
                            socket.send(outgoingDataPacket);

                            System.out.printf("Waiting for client's ACK for block %d\n", blockNum);

                            DatagramPacket incomingAck = new DatagramPacket(recvBuf, recvBuf.length);
                            socket.receive(incomingAck);
                            ACKPacket ackPacket = new ACKPacket(incomingAck.getData());

                            System.out.printf("ACK received for block %d\n", ackPacket.getBlockNumber());

                            bytesRead = fileInputStream.read(fileBuf);
                            blockNum = (short) ((blockNum == Short.MAX_VALUE) ? 0 : blockNum + 1);  // To handle exceeding the max short value (32,767)
                        }

                        System.out.printf("Transfer of '%s' complete.\n", rrq.getFilename());
                    case WRQ:
                        WRQPacket wrq = new WRQPacket(clientPacket.getData());
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
