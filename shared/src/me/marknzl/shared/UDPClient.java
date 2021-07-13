package me.marknzl.shared;

import java.io.IOException;
import java.net.*;

/** Represents a UDP Client handle
 *
 */
public class UDPClient {

    private final DatagramSocket socket;
    private final InetAddress address;
    private final int listenPort;
    private byte[] buf;

    /**
     * Creates an instance of a UDP Client handle
     * @param address Address of the UDP server
     * @param listenPort Port number of the UDP server
     * @throws UnknownHostException if the host is invalid
     * @throws SocketException if the socket cannot be initialized
     */
    public UDPClient(String address, int listenPort) throws UnknownHostException, SocketException {
        this.address = InetAddress.getByName(address);
        this.socket = new DatagramSocket();
        this.listenPort = listenPort;
        this.buf = new byte[1024];
    }

    /**
     * Sends a byte array payload to the host
     * @param payload A byte array payload
     * @throws IOException if an error occurs during transmission
     */
    public void send(byte[] payload) throws IOException {
        DatagramPacket packet = new DatagramPacket(payload, payload.length, this.address, this.listenPort);
        socket.send(packet);
    }

    /**
     * Closes the Datagram socket associated with the UDP Client handle
     */
    public void close() {
        this.socket.close();
    }

    /**
     * Gets the underlying DatagramSocket for the UDP Client handle
     * @return the underlying DatagramSocket for the UDP Client handle
     */
    public DatagramSocket getSocket() {
        return this.socket;
    }

    public byte[] getBuffer() {
        return this.buf;
    }

}
