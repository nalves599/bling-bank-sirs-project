package pt.ulisboa.tecnico.aux;

import java.math.BigInteger;
import java.util.Arrays;

import static pt.ulisboa.tecnico.aux.Constants.INT_SIZE;

public class Conversion {

    public static byte[] intToBytes(int value) {
        return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value };
    }

    public static int bytesToInt(byte[] bytes, int offset) throws Exception {
        return new BigInteger(Arrays.copyOfRange(bytes, offset, offset + INT_SIZE)).intValue();
    }
}
