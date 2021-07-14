package me.marknzl.client.commands;

import me.marknzl.client.Client;
import me.marknzl.client.Command;
import me.marknzl.client.Utils;
import me.marknzl.shared.*;
import me.marknzl.shared.Packets.*;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class RRQ implements Command {

    @Override
    public String getCommand() {
        return "rrq";
    }

    @Override
    public String getDescription() {
        return "Initiates a read-request";
    }

    @Override
    public String getUsage() {
        return "rrq <filename>";
    }

    @Override
    public void execute(String[] args) {
        if (!Utils.validFileArgs(args)) {
            System.out.println(Utils.commandUsageFormat(this));
            return;
        }

        String filename = args[0];

        Scanner scanner = new Scanner(new InputStreamReader(System.in));
        System.out.print("Enter server address: ");
        String address = scanner.nextLine();

        UDPClient client;

        try {
            client = new UDPClient(address, Constants.SERVER_LISTEN_PORT);
            DatagramSocket socket = client.getSocket();
            byte[] buf = client.getBuffer();
            RRQPacket rrqPacket = new RRQPacket();

            rrqPacket.writeFilename(filename);
            byte[] payload = rrqPacket.getPayload();

            socket.setSoTimeout(5000);
            client.send(payload);

            DatagramPacket serverPacket = new DatagramPacket(buf, buf.length);
            socket.receive(serverPacket);

            Packet packet = new Packet(serverPacket.getData());

            if (packet.getOpcode() == Opcode.ERROR) {
                ErrorPacket errorPacket = new ErrorPacket(serverPacket.getData());
                String error = errorPacket.getErrorMessage();
                System.out.println(error);
                return;
            }

            ByteArrayOutputStream fileBuf = new ByteArrayOutputStream();
            int timeout = Constants.BASE_TIMEOUT;
            socket.setSoTimeout(timeout);

            DataPacket dataPacket = new DataPacket(serverPacket.getData());
            int tries = 5;
            int bytesReceived = 0;
            boolean retransmit = false;

            while (true) {
                if (tries == 0)
                    break;
                try {
                    short ackNum = dataPacket.getBlockNumber();
                    int receivedBlockSize = serverPacket.getLength() - 4;

                    if (!retransmit) {
                        bytesReceived += receivedBlockSize;
                        fileBuf.write(dataPacket.getPayload(), 4, receivedBlockSize);
                        System.out.printf("Received block %d of size %d bytes\nSending ACK for block %d\n", ackNum, receivedBlockSize, ackNum);
                    }

                    ACKPacket ackPacket = new ACKPacket(ackNum);
                    serverPacket.setData(ackPacket.getPayload());
                    socket.send(serverPacket);

                    if (receivedBlockSize < Constants.BLOCK_SIZE) {  // Block size < 512 indicates end of file transfer
                        System.out.printf("Block %d has size < 512 bytes - file transfer complete!\n", ackNum);
                        break;
                    }

                    // Start receiving next block of data...
                    serverPacket = new DatagramPacket(buf, buf.length);
                    socket.receive(serverPacket);
                    dataPacket = new DataPacket(serverPacket.getData());
                    retransmit = false;
                } catch (SocketTimeoutException ex) {
                    System.out.printf("Timed out... %d tries left.\n", tries);
                    timeout += 1000;    // Increase the timeout by 1 second for every subsequent retry
                    tries--;
                    socket.setSoTimeout(timeout);
                    retransmit = true;
                }
            }

            System.out.printf("Received %d bytes\n", bytesReceived);
            File file = new File(Client.CLIENT_ROOT, filename);
            if (!file.exists()) {
                boolean created = file.createNewFile();
                if (!created) {
                    System.out.println("Couldn't create file!");
                    client.close();
                    return;
                }
            }

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileBuf.writeTo(fileOutputStream);
            fileOutputStream.close();
            client.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
