package me.marknzl.shared;

public enum Opcode {

    RRQ(1),     // Read request
    WRQ(2),     // Write request
    DATA(3),    // Data
    ACK(4),     // Acknowledgement
    ERROR(5),   // Error
    OACK(6);    // Option acknowledgement

    public final int op;
    public static Opcode[] mapping = new Opcode[] {RRQ, WRQ, DATA, ACK, ERROR, OACK};

    Opcode(int op) {
        this.op = op;
    }

}
