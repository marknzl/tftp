package me.marknzl.client.commands;

import me.marknzl.client.Client;
import me.marknzl.client.Command;
import me.marknzl.client.Utils;
import me.marknzl.shared.Constants;
import me.marknzl.shared.Opcode;
import me.marknzl.shared.Packet;
import me.marknzl.shared.Packets.ACKPacket;
import me.marknzl.shared.Packets.DataPacket;
import me.marknzl.shared.Packets.ErrorPacket;
import me.marknzl.shared.Packets.WRQPacket;
import me.marknzl.shared.UDPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Scanner;

public class WRQ implements Command {

    @Override
    public String getCommand() {
        return "wrq";
    }

    @Override
    public String getDescription() {
        return "Initiates a write request";
    }

    @Override
    public String getUsage() {
        return "wrq <filename>";
    }

    @Override
    public void execute(String[] args) {
        if (!Utils.validFileArgs(args)) {
            System.out.println(Utils.commandUsageFormat(this));
            return;
        }

        String filename = args[0];

        File file = new File(Client.CLIENT_ROOT, filename);
        if (!(file.exists() || file.isDirectory())) {
            System.out.println("File doesn't exist!");
            return;
        }

        Scanner scanner = new Scanner(new InputStreamReader(System.in));
        System.out.print("Enter server address: ");
        String address = scanner.nextLine();

        UDPClient client;
        int baseTimeout = Constants.BASE_TIMEOUT;

        try {
            client = new UDPClient(address, Constants.SERVER_LISTEN_PORT);
            DatagramSocket socket = client.getSocket();
            socket.setSoTimeout(baseTimeout);
            byte[] buf = client.getBuffer();

            WRQPacket packet = new WRQPacket();
            packet.writeFilename(filename);
            byte[] payload = packet.getPayload();

            client.send(payload);

            DatagramPacket serverPacket = new DatagramPacket(buf, buf.length);
            socket.receive(serverPacket);
            Packet receivedPacket = new Packet(serverPacket.getData());

            if (receivedPacket.getOpcode() == Opcode.ERROR) {
                ErrorPacket errorPacket = new ErrorPacket(receivedPacket.getPayload());
                System.out.printf("%s: %s\n", errorPacket.getErrorCode().toString(), errorPacket.getErrorMessage());
                return;
            }

            System.out.println("Initial acknowledgement received from server - commencing file transfer...");

            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] fileBuf = new byte[Constants.BLOCK_SIZE];
            short blockNum = 1;
            int bytesRead = fileInputStream.read(fileBuf);

            while (bytesRead != -1) {
                DataPacket dataPacket = new DataPacket(blockNum, fileBuf, 0, bytesRead);
                int bytesSent = client.send(dataPacket.getPayload()) - 4;
                System.out.printf("Block %d - Sent %d bytes\n", blockNum, bytesSent);

                System.out.printf("Waiting for server's ACK for block %d\n", blockNum);
                socket.receive(serverPacket);
                ACKPacket ackPacket = new ACKPacket(serverPacket.getData());
                System.out.printf("ACK received for block %d\n", ackPacket.getBlockNumber());

                bytesRead = fileInputStream.read(fileBuf);
                blockNum = (short) ((blockNum == Short.MAX_VALUE) ? 0 : blockNum + 1);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
