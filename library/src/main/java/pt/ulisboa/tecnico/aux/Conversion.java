package pt.ulisboa.tecnico.aux;

import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Optional;

import static pt.ulisboa.tecnico.aux.Constants.*;

public class Conversion {

    public static byte[] intToBytes(int value) {
        return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value };
    }

    public static Optional<Integer> bytesToInt(byte[] bytes, int offset) {
        try {
            return Optional.of(new BigInteger(Arrays.copyOfRange(bytes, offset, offset + INT_SIZE)).intValue());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static SecretKeySpec bytesToSecretKey(byte[] bytes) {
        return new SecretKeySpec(bytes, SYM_ALGO);
    }

    public static PublicKey bytesToPublicKey(byte[] bytes) throws Exception {
        return KeyFactory.getInstance(ASYM_ALGO).generatePublic(new X509EncodedKeySpec(bytes));
    }

    public static PrivateKey bytesToPrivateKey(byte[] bytes) throws Exception {
        return KeyFactory.getInstance(ASYM_ALGO).generatePrivate(new X509EncodedKeySpec(bytes));
    }
}
