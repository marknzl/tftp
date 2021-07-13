package me.marknzl.shared;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Base class for a packet
 */
public class Packet {

    protected byte[] payload;
    protected final ByteArrayOutputStream byteArrayOutputStream;
    protected final DataOutputStream dataOutputStream;

    /**
     * Creates a packet
     */
    public Packet() {
        this.byteArrayOutputStream = new ByteArrayOutputStream();
        this.dataOutputStream = new DataOutputStream(this.byteArrayOutputStream);
    }

    public Packet(byte[] data) {
        this.byteArrayOutputStream = new ByteArrayOutputStream();
        this.dataOutputStream = new DataOutputStream(this.byteArrayOutputStream);

        try {
            dataOutputStream.write(data);
            dataOutputStream.flush();
            writePayload();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Writes the packet's opcode to the byte array payload
     * @param opcode The packet's opcode
     */
    public void writeOpcode(Opcode opcode) {
        try {
            dataOutputStream.writeByte(0);
            dataOutputStream.writeByte(opcode.op);
            writePayload();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gets the packet's TFTP opcode
     * @return the packet's TFTP opcode
     */
    public Opcode getOpcode() {
        return Opcode.mapping[this.payload[1] - 1];
    }

    /**
     * Flushes the internal byte stream to the byte array payload
     * @throws IOException if an error occurs during payload writing
     */
    protected void writePayload() throws IOException {
        this.dataOutputStream.flush();
        this.payload = byteArrayOutputStream.toByteArray();
    }

    /**
     * Gets the packet's byte array payload
     * @return the packet's byte array payload
     */
    public byte[] getPayload() {
        return this.payload;
    }

}
