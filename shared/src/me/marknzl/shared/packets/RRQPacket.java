package me.marknzl.shared.packets;

import me.marknzl.shared.SharedUtils;
import me.marknzl.shared.Opcode;
import me.marknzl.shared.Packet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Represents a Read Request Packet
 */
public class RRQPacket extends Packet {

    public RRQPacket() {
        super();
        writeOpcode(Opcode.RRQ);
    }

    /**
     * Creates a Read Request Packet from a pre-existing byte array
     * @param data The pre-existing byte array
     */
    public RRQPacket(byte[] data) {
        super(data);
    }

    /**
     * Writes the bytes of the filename string (in ASCII) to the byte array
     * @param filename Name of the file
     */
    public void writeFilename(String filename) {
        try {
            dataOutputStream.write(filename.getBytes(StandardCharsets.US_ASCII));
            dataOutputStream.write(0x0);
            writePayload();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gets the filename
     * @return the filename
     */
    public String getFilename() throws IOException {
        return SharedUtils.charArrayToString(this.payload, 2);
    }

}
