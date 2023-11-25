package pt.ulisboa.tecnico.aux;

public class Constants {

    public static String SYM_ALGO = "AES";
    public static int SYM_KEY_SIZE = 128;
    public static String SYM_MODE = "AES/CCB/PKCS5Padding";

    public static String ASYM_ALGO = "RSA";
    public static int ASYM_KEY_SIZE = 2048;
    public static String ASYM_MODE = "RSA/ECB/PKCS1Padding";

    public static String RANDOM_ALGO = "SHA1PRNG";
    public static String RANDOM_PROVIDER = "SUN";

    public static int INITIAL_SEQUENCE_NUMBER = 0;
}
