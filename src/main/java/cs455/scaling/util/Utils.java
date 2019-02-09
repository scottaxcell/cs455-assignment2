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
    public static final int FORTY_B = 40;
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("HH:mm:ss.S");
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

    public static byte[] readBytesFromChannel(SocketChannel socketChannel, int sizeOfBuffer) {
        ByteBuffer dst = ByteBuffer.allocateDirect(sizeOfBuffer);
        int numBytesRead = 0;
        while (dst.hasRemaining() && numBytesRead != 1) {
            try {
                numBytesRead = socketChannel.read(dst);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        Utils.debug(numBytesRead);
        dst.flip();
        byte[] bytes = new byte[dst.remaining()];
        dst.get(bytes);
        return bytes;
    }

    public static void writeBytesToChannel(SocketChannel socketChannel, byte[] bytes) {
        ByteBuffer src = ByteBuffer.wrap(bytes);
        int numBytesWritten = 0;
        while (src.hasRemaining() && numBytesWritten != -1) {
            try {
                numBytesWritten = socketChannel.write(src);
            }
            catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }
}
