package schuitj.drone.lib.util;

import java.io.IOException;
import java.io.InputStream;

public class ByteUtils {
    public static String toHexString(byte...bytes) {
        StringBuilder sb = new StringBuilder(bytes.length);

        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }

        return sb.toString();
    }

    public static byte[] asUnsigned(int...bytes) {
        byte[] out = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            int value = bytes[i];
            if (value > Byte.MAX_VALUE) {
                out[i] = (byte) value;
            } else {
                out[i] = (byte) (value & 0xff);
            }
        }
        return out;
    }

    public static byte[] readAll(InputStream inputStream) throws IOException {
        byte[] result = new byte[inputStream.available()];
        inputStream.read(result);
        return result;
    }
}
