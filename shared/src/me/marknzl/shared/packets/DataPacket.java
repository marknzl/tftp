package me.marknzl.shared.packets;

import me.marknzl.shared.Opcode;
import me.marknzl.shared.Packet;

import java.io.IOException;

/**
 * Represents a Data Packet
 */
public class DataPacket extends Packet {

    public DataPacket(short blockNumber, byte[] data, int offset, int length) {
        super();
        writeOpcode(Opcode.DATA);

        try {
            dataOutputStream.writeShort(blockNumber);
            dataOutputStream.write(data, offset, length);
            writePayload();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Creates a Data Packet from a pre-existing byte array
     * @param data The pre-existing byte array
     */
    public DataPacket(byte[] data) {
        super(data);
    }

    /**
     * Gets the block number for the corresponding Data packet
     * @return the block number for the corresponding Data packet
     */
    public short getBlockNumber() {
        return (short)(((payload[2] & 0xFF) << 8) | (payload[3] & 0xFF));
    }

}
