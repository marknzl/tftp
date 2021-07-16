package me.marknzl.server;

import me.marknzl.shared.Constants;
import me.marknzl.shared.ErrorCode;
import me.marknzl.shared.Packets.ACKPacket;
import me.marknzl.shared.Packets.DataPacket;
import me.marknzl.shared.Packets.ErrorPacket;
import me.marknzl.shared.Packets.RRQPacket;
import me.marknzl.shared.UDPServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

public class RRQ {

    public static void handleOperation(UDPServer server, DatagramPacket clientPacket) {
        DatagramSocket socket = server.getSocket();
        byte[] recvBuf = server.getBuffer();
        RRQPacket rrqPacket = new RRQPacket(clientPacket.getData());

        String filename = null;
        try {
            filename = rrqPacket.getFilename();
        } catch (IOException ex) {
            System.out.println("Failed to deserialize file name!");
            ex.printStackTrace();
        }
        if (filename == null)
            System.exit(1);

        File file = new File(server.getRootDir(), filename);
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            System.out.println("File not found!");
            ErrorPacket errorPacket = new ErrorPacket(ErrorCode.FILE_NOT_FOUND, "File not found!");
            DatagramPacket response = new DatagramPacket(errorPacket.getPayload(), errorPacket.getPayload().length, clientPacket.getSocketAddress());

            try {
                socket.send(response);
            } catch (IOException exc) {
                System.out.println("Failed to send response packet!");
                exc.printStackTrace();
            }
        }

        if (fileInputStream == null)
            System.exit(1);

        short blockNum = 1;
        int tries = 5;  // Default tries before timeout
        byte[] fileBuf = new byte[Constants.BLOCK_SIZE];

        try {
            int bytesRead = fileInputStream.read(fileBuf);

            while (bytesRead != -1) {
                if (tries == 0) {
                    System.out.println("Max transmission attempts reached. File transfer failed.");
                    socket.setSoTimeout(Constants.BASE_TIMEOUT);
                    fileInputStream.close();
                    break;
                }

                DataPacket dataPacket = new DataPacket(blockNum, fileBuf, 0, bytesRead);
                DatagramPacket outgoingPacket = new DatagramPacket(dataPacket.getPayload(), dataPacket.getPayload().length, clientPacket.getSocketAddress());
                socket.send(outgoingPacket);
                System.out.printf("Sent %d bytes\n", outgoingPacket.getLength());

                System.out.printf("Waiting for client's ACK for block %d\n", blockNum);

                try {
                    DatagramPacket incomingPacket = new DatagramPacket(recvBuf, recvBuf.length);
                    socket.receive(incomingPacket);
                    ACKPacket ackPacket = new ACKPacket(incomingPacket.getData());
                    System.out.printf("ACK received for block %d\n", ackPacket.getBlockNumber());

                    bytesRead = fileInputStream.read(fileBuf);
                    blockNum = (short) ((blockNum == Short.MAX_VALUE) ? 0 : blockNum + 1);  // To handle exceeding the max short value (32,767)

                    tries = 5;  // Reset tries for successful block transfer
                    socket.setSoTimeout(Constants.BASE_TIMEOUT);    // Reset socket timeout to base timeout
                } catch (SocketTimeoutException ex) {
                    tries--;
                    System.out.printf("No ACK received for block %d\n. %d tries remaining.", blockNum, tries);
                    socket.setSoTimeout(socket.getSoTimeout() + 1000);  // Increase socket timeout interval by 1s for each subsequent retransmission attempt
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        System.out.printf("Transfer of '%s' complete.\n", filename);

        try {
            fileInputStream.close();
        } catch (IOException ex) {
            System.out.println("Failed to close file input stream!");
            ex.printStackTrace();
        }
    }

}
