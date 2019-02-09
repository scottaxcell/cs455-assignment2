package cs455.scaling.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static final int EIGHT_K = 8129;
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
}
