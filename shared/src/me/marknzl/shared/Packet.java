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

    /**
     * Writes the packet's opcode to the byte array payload
     * @param opcode The packet's opcode
     */
    public void writeOpcode(Opcode opcode) {
        try {
            dataOutputStream.writeInt(0);
            dataOutputStream.writeInt(opcode.op);
            writePayload();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Flushes the internal byte stream to the byte array payload
     * @throws IOException if an error occurs during payload writing
     */
    protected void writePayload() throws IOException {
        this.dataOutputStream.flush();
        this.payload = byteArrayOutputStream.toByteArray();
    }

    public byte[] getPayload() {
        return this.payload;
    }

}
