package pt.ulisboa.tecnico.aux;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Optional;

import static pt.ulisboa.tecnico.aux.Constants.INT_SIZE;

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
}
