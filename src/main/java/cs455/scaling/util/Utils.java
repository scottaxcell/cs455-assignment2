package cs455.scaling.util;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static final int EIGHT_KB = 8129;
    public static final int HASH_CODE_BYTE_SIZE = 40;
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static boolean debug = true;

    public static void out(Object o) {
        System.out.print(o);
    }

    public static void info(Object o) {
        System.out.println("\nINFO: " + o);
    }

    public static void debug(Object o) {
        if (debug)
            System.out.println(String.format("DEBUG: [%s] %s", SIMPLE_DATE_FORMAT.format(new Date()), o));
    }

    public static void error(Object o) {
        System.err.println("\nERROR: " + o);
    }

    public static String createSha1FromBytes(byte[] data) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA1");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hash = digest.digest(data);
        BigInteger hashInt = new BigInteger(1, hash);
        return hashInt.toString(16);
    }

    public static byte[] readBytesFromChannel(SocketChannel socketChannel, int sizeOfBuffer) throws IOException {
        ByteBuffer dst = ByteBuffer.allocateDirect(sizeOfBuffer);
        int numBytesRead = 0;
        while (dst.hasRemaining() && numBytesRead != -1)
            numBytesRead = socketChannel.read(dst);
        dst.flip();
        byte[] bytes = new byte[dst.remaining()];
        dst.get(bytes);
        return bytes;
    }

    public static void writeBytesToChannel(SocketChannel socketChannel, byte[] bytes) {
        ByteBuffer src = ByteBuffer.wrap(bytes);
        while (src.hasRemaining()) {
            try {
                socketChannel.write(src);
            }
            catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    public static String padStringWithZeros(String string, int expectedStringLength) {
        String paddedString = string;
        if (paddedString.length() < expectedStringLength) {
            StringBuilder sb = new StringBuilder(paddedString);
            while (sb.length() < expectedStringLength)
                sb.insert(0, "0");
            paddedString = sb.toString();
        }
        return paddedString;
    }

    public static String padHashCodeWithZeros(String hashCode) {
        return padStringWithZeros(hashCode, HASH_CODE_BYTE_SIZE);
    }
}
