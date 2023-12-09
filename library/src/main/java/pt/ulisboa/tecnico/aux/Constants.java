package pt.ulisboa.tecnico.aux;

public class Constants {

    static String SYM_ALGO = "AES";
    public static int SYM_KEY_SIZE = 128;
    public static String SYM_CYPHER = "AES/CBC/PKCS5Padding";

    // TODO: change this to a random IV
    public static byte[] IV = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F };
    public static String ASYM_ALGO = "RSA";
    public static int ASYM_KEY_SIZE = 2048;
    public static String ASYM_CYPHER = "RSA";

    public static String DIGEST_ALGO = "SHA-256";
    public static int DIGEST_SIZE = 32; // 256 bits = 32 bytes

    public static int INITIAL_SEQUENCE_NUMBER = 0;

    public static int INT_SIZE = 4;

    public static int MAX_TIMESTAMP_DIFFERENCE = 3 * 1000; // 3 seconds
}
