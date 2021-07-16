package me.marknzl.shared;

import java.io.File;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Represents a UDP Server handle
 */
public class UDPServer {

    private final DatagramSocket socket;
    private final byte[] buf;
    private final File rootDir;

    /**
     * Creates an instance of a UDP server handle
     * @param listenPort The port which the UDP Server will listen on
     * @param bufSize Size of the UDP Server's internal buffer
     * @throws SocketException if an error occurs during the socket's construction
     */
    public UDPServer(int listenPort, int bufSize, String fileRoot) throws SocketException {
        this.buf = new byte[bufSize];
        this.socket = new DatagramSocket(listenPort);
        this.rootDir = new File(fileRoot);
    }

    /**
     * Gets the UDP Server handle's underlying Datagram socket
     * @return the UDP Server handle's underlying Datagram socket
     */
    public DatagramSocket getSocket() {
        return this.socket;
    }

    /**
     * Gets the UDP Server handle's underlying Datagram socket's buffer
     * @return the UDP Server handle's underlying Datagram socket's buffer
     */
    public byte[] getBuffer() {
        return this.buf;
    }

    /**
     * Get's the root directory
     * @return the root directory
     */
    public File getRootDir() {
        return this.rootDir;
    }

}
