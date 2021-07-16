package me.marknzl.shared;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class SharedUtils {

    public static String charArrayToString(byte[] data, int offset) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        int i = offset;

        while (data[i] != 0x0) {
            dataOutputStream.writeByte(data[i]);
            i++;
        }

        dataOutputStream.flush();
        dataOutputStream.close();
        byteArrayOutputStream.close();

        return byteArrayOutputStream.toString(StandardCharsets.US_ASCII);
    }

    public static String md5DigestToString(MessageDigest messageDigest) {
        if (!messageDigest.getAlgorithm().equalsIgnoreCase("md5")) {
            return null;
        }
        byte[] digest = messageDigest.digest();
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : digest) {
            stringBuilder.append(String.format("%02x", (b & 0xff)));
        }
        return stringBuilder.toString();
    }

}
