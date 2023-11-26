package pt.ulisboa.tecnico.aux;

public class Constants {

    public static String SYM_ALGO = "AES";
    public static int SYM_KEY_SIZE = 128;
    public static String SYM_CYPHER = "AES/ECB/PKCS5Padding";

    public static String ASYM_ALGO = "RSA";
    public static int ASYM_KEY_SIZE = 2048;
    public static String ASYM_CYPHER = "RSA/ECB/NoPadding";

    public static String DIGEST_ALGO = "SHA-256";
    public static int DIGEST_SIZE = 32; // 256 bits = 32 bytes

    public static String RANDOM_ALGO = "SHA1PRNG";
    public static String RANDOM_PROVIDER = "SUN";

    public static int INITIAL_SEQUENCE_NUMBER = 0;

    public static int INT_SIZE = 4;
}
