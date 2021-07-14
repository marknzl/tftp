package me.marknzl.server;

import me.marknzl.shared.Constants;
import me.marknzl.shared.ErrorCode;
import me.marknzl.shared.Packet;
import me.marknzl.shared.Packets.*;

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
        byte[] recvBuf = new byte[1024];
        DatagramSocket socket = new DatagramSocket(Constants.SERVER_LISTEN_PORT);
        File rootDir = new File(fileRoot);

        while (true) {
            try {
                DatagramPacket clientPacket = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(clientPacket);
                Packet packet = new Packet(clientPacket.getData());

                switch (packet.getOpcode()) {
                    case RRQ -> {
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
                        byte[] fileBuf = new byte[Constants.BLOCK_SIZE];
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
                    }
                    case WRQ -> {
                        WRQPacket wrq = new WRQPacket(clientPacket.getData());
                        System.out.printf("WRQ received for '%s'\n", wrq.getFilename());
                        File file = new File(rootDir, wrq.getFilename());

                        if (file.exists()) {
                            ErrorPacket errorPacket = new ErrorPacket(ErrorCode.FILE_ALREADY_EXISTS, "File already exists!");
                            System.out.println("Error - File already exists!");
                            DatagramPacket response = new DatagramPacket(errorPacket.getPayload(), errorPacket.getPayload().length, clientPacket.getSocketAddress());
                            socket.send(response);
                            break;
                        }

                        ACKPacket ackPacket = new ACKPacket((short) 0);
                        DatagramPacket response = new DatagramPacket(ackPacket.getPayload(), ackPacket.getPayload().length, clientPacket.getSocketAddress());
                        socket.send(response);

                        ByteArrayOutputStream recvFileBuf = new ByteArrayOutputStream();
                        int bytesReceived = 0;
                        short blockNum;

                        while (true) {
                            socket.receive(clientPacket);
                            DataPacket dataPacket = new DataPacket(clientPacket.getData());
                            blockNum = dataPacket.getBlockNumber();
                            int receivedBlockSize = clientPacket.getLength() - 4;   // Minus 4 since DataPacket headers take 4 bytes
                            System.out.printf("Received %d bytes\n", receivedBlockSize);
                            bytesReceived += receivedBlockSize;

                            recvFileBuf.write(dataPacket.getPayload(), 4, receivedBlockSize);

                            ackPacket = new ACKPacket(blockNum);
                            response = new DatagramPacket(ackPacket.getPayload(), ackPacket.getPayload().length, clientPacket.getSocketAddress());
                            socket.send(response);

                            System.out.printf("ACK sent for block %d\n", blockNum);

                            if (receivedBlockSize < Constants.BLOCK_SIZE)
                                break;
                        }

                        System.out.printf("Received %d bytes, file transfer complete.\n", bytesReceived);

                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        recvFileBuf.writeTo(fileOutputStream);
                        fileOutputStream.close();
                        recvFileBuf.close();
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
