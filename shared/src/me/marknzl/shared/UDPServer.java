package me.marknzl.shared;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Represents a UDP server handle
 */
public class UDPServer {

    private final int listenPort;
    private final byte[] recvBuf;
    private final DatagramSocket socket;

    /**
     * Creates an instance of a UDP server handle
     * @param listenPort Address of the UDP server's listen port
     * @param recvBufSize Receive buffer size (in bytes)
     */
    public UDPServer(int listenPort, int recvBufSize) throws SocketException {
        this.listenPort = listenPort;
        this.recvBuf = new byte[recvBufSize];
        this.socket = new DatagramSocket(listenPort);
    }

//    /**
//     * Attempts to receive bytes from the socket
//     * @return the amount of bytes received
//     * @throws IOException if an error occurs
//     */
//    public int receive() throws IOException {
//        DatagramSocket socket = new DatagramSocket(this.listenPort);
//        DatagramPacket packet = new DatagramPacket(this.recvBuf, this.recvBuf.length);
//        socket.receive(packet);
//        socket.close();
//        return packet.getLength();
//    }

    public DatagramSocket getSocket() {
        return this.socket;
    }

    /**
     * Gets the received data payload
     * @return the data payload
     */
    public byte[] getData() {
        return this.recvBuf;
    }

}