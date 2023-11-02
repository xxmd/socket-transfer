package org.example;

import java.nio.ByteBuffer;

public class ByteBufferUtil {
    public static void putString(ByteBuffer byteBuffer, String str) {
        byte[] bytes = str.getBytes();
        byteBuffer.putInt(bytes.length);
        byteBuffer.put(bytes);
    }
    public static String getString(ByteBuffer byteBuffer) {
        int strBytesLength = byteBuffer.getInt();
        byte[] bytes = new byte[strBytesLength];
        byteBuffer.get(bytes);
        return new String(bytes);
    }
}
