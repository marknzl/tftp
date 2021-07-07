package me.marknzl.shared;

public enum Opcode {

    RRQ(1),     // Read request
    WRQ(2),     // Write request
    DATA(3),    // Data
    ACK(4),     // Acknowledgement
    ERROR(5),   // Error
    OACK(6);    // Option acknowledgement

    public final int op;

    Opcode(int op) {
        this.op = op;
    }

}
