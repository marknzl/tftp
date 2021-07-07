package me.marknzl.shared;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Packet {

    private byte[] payload;
    private final ByteArrayOutputStream byteArrayOutputStream;
    private final DataOutputStream dataOutputStream;

    public Packet() {
        this.byteArrayOutputStream = new ByteArrayOutputStream();
        this.dataOutputStream = new DataOutputStream(this.byteArrayOutputStream);
    }

    public void writeOpcode(Opcode opcode) {
        try {
            dataOutputStream.writeInt(0);
            dataOutputStream.writeInt(opcode.op);
            writePayload();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void writeFilename(String filename) {
        try {
            dataOutputStream.write(filename.getBytes(StandardCharsets.US_ASCII));
            dataOutputStream.write(0x0);
            writePayload();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void writePayload() throws IOException {
        this.dataOutputStream.flush();
        this.payload = byteArrayOutputStream.toByteArray();
    }

    public byte[] getPayload() {
        return this.payload;
    }

}
