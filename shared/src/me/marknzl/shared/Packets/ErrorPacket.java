package me.marknzl.shared.Packets;

import me.marknzl.shared.ErrorCode;
import me.marknzl.shared.FileUtils;
import me.marknzl.shared.Opcode;
import me.marknzl.shared.Packet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Represents an Error Packet
 */
public class ErrorPacket extends Packet {

    public ErrorPacket(ErrorCode errorCode, String errorMessage) {
        super();
        writeOpcode(Opcode.ERROR);
        try {
            dataOutputStream.writeByte(0x0);
            dataOutputStream.writeByte(ErrorCode.ecToIntMappings.get(errorCode));
            dataOutputStream.write(errorMessage.getBytes(StandardCharsets.US_ASCII));
            dataOutputStream.writeByte(0x0);
            writePayload();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Creates an Error Packet from a pre-existing byte array
     * @param data The pre-existing byte array
     */
    public ErrorPacket(byte[] data) {
        super(data);
    }

    public String getErrorMessage() throws IOException {
        return FileUtils.charArrayToString(this.payload, 4);
    }

    public ErrorCode getErrorCode() {
        return ErrorCode.intToECMappings[this.payload[3]];
    }

}
