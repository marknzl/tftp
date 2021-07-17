package me.marknzl.server;

import me.marknzl.shared.Constants;
import me.marknzl.shared.ErrorCode;
import me.marknzl.shared.packets.ACKPacket;
import me.marknzl.shared.packets.DataPacket;
import me.marknzl.shared.packets.ErrorPacket;
import me.marknzl.shared.packets.WRQPacket;
import me.marknzl.shared.SharedUtils;
import me.marknzl.shared.UDPServer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class WRQ {

    public static void handleOperation(UDPServer server, DatagramPacket clientPacket) {
        DatagramSocket socket = server.getSocket();
        WRQPacket wrqPacket = new WRQPacket(clientPacket.getData());

        String filename = null;
        try {
            filename = wrqPacket.getFilename();
        } catch (IOException ex) {
            System.out.println("Failed to deserialize file name!");
            ex.printStackTrace();
        }
        if (filename == null)
            System.exit(1);

        File file = new File(server.getRootDir(), filename);

        if (file.exists()) {
            ErrorPacket errorPacket = new ErrorPacket(ErrorCode.FILE_ALREADY_EXISTS, "File already exists!");
            System.out.println("Error - file already exists!");
            DatagramPacket response = new DatagramPacket(errorPacket.getPayload(), errorPacket.getPayload().length, clientPacket.getSocketAddress());

            try {
                socket.send(response);
            } catch (IOException ex) {
                System.out.println("Failed to send response packet!");
                ex.printStackTrace();
            }
        }

        ByteArrayOutputStream recvFileBuf = new ByteArrayOutputStream();
        int bytesReceived = 0;
        short blockNum = 0;
        int tries = 5;

        try {
            socket.setSoTimeout(Constants.BASE_TIMEOUT);

            ACKPacket initialAck = new ACKPacket(blockNum);
            DatagramPacket response = new DatagramPacket(initialAck.getPayload(), initialAck.getPayload().length, clientPacket.getSocketAddress());

            try {
                socket.send(response);
            } catch (IOException ex) {
                System.out.println("Failed to send initial ACK!");
                ex.printStackTrace();
                System.exit(1);
            }

            MessageDigest messageDigest = null;

            try {
                messageDigest = MessageDigest.getInstance("md5");
            } catch (NoSuchAlgorithmException ex) {
                ex.printStackTrace();
                return;
            }

            while (true) {
                if (tries == 0) {
                    System.out.println("Max transmission attempts reached. File transfer failed.");
                    socket.setSoTimeout(Constants.BASE_TIMEOUT);
                    recvFileBuf.close();
                }

                try {
                    socket.receive(clientPacket);
                    DataPacket dataPacket = new DataPacket(clientPacket.getData());
                    blockNum = dataPacket.getBlockNumber();

                    int receivedBlockSize = clientPacket.getLength() - 4;   // Minus 4 since DataPacket headers take 4 bytes
                    System.out.printf("Received %d bytes\n", receivedBlockSize);
                    bytesReceived += receivedBlockSize;
                    recvFileBuf.write(dataPacket.getPayload(), 4, receivedBlockSize);
                    messageDigest.update(dataPacket.getPayload(), 4, receivedBlockSize);
                    tries = 5;

                    ACKPacket ackPacket = new ACKPacket(blockNum);
                    response = new DatagramPacket(ackPacket.getPayload(), ackPacket.getPayload().length, clientPacket.getSocketAddress());
                    socket.send(response);
                    System.out.printf("ACK sent for block %d\n", blockNum);

                    if (receivedBlockSize < Constants.BLOCK_SIZE)
                        break;
                } catch (IOException ex) {
                    if (ex.getCause() instanceof SocketTimeoutException) {
                        tries--;
                        System.out.println("No data received for next block - retransmitting ACK.");
                        socket.setSoTimeout(socket.getSoTimeout() + 1000);
                    } else {
                        System.out.println("Failed to send initial ACK!");
                        ex.printStackTrace();
                        System.exit(1);
                    }
                }
            }

            System.out.printf("Received %d bytes, file transfer complete.\n", bytesReceived);
            System.out.printf("MD5 checksum of file = %s\n", SharedUtils.md5DigestToString(messageDigest));
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            recvFileBuf.writeTo(fileOutputStream);
            fileOutputStream.close();
            recvFileBuf.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
