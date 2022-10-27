package de.erdbeerbaerlp.splatcord2.util.wiiu;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteArrayUtils {
    public static int readUnsignedShort(byte[] array, int offset) {
        // First make integers to resolve signed vs. unsigned issues.
        int b0 = array[offset] & 0xFF;
        int b1 = array[offset + 1] & 0xFF;
        return ((b0 << 8) + (b1 << 0));
    }

    public static int toInt(byte[] array) {
        return ByteBuffer.wrap(array).getInt();
    }

    public static byte[] concat(byte[] a, byte[] b) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(a);

            outputStream.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }
}

