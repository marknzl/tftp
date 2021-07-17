package me.marknzl.shared.packets;

import me.marknzl.shared.Opcode;
import me.marknzl.shared.Packet;

import java.io.IOException;

/**
 * Represents an Acknowledgement Packet
 */
public class ACKPacket extends Packet {

    public ACKPacket(short blockNum) {
        super();
        writeOpcode(Opcode.ACK);

        try {
            dataOutputStream.writeShort(blockNum);
            writePayload();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Creates an Acknowledgement Packet from a pre-existing byte array
     * @param data The pre-existing byte array
     */
    public ACKPacket(byte[] data) {
        super(data);
    }

    /**
     * Gets the block number for the corresponding ACK
     * @return the block number for the corresponding ACK
     */
    public short getBlockNumber() {
        return (short)(((payload[2] & 0xFF) << 8) | (payload[3] & 0xFF));
    }

}
