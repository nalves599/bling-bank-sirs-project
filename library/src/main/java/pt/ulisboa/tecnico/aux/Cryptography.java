package pt.ulisboa.tecnico.aux;

import lombok.Getter;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import java.security.*;

import static pt.ulisboa.tecnico.aux.Constants.*;

@Getter
public class Cryptography {

    private final Cipher symCipher = Cipher.getInstance(SYM_CYPHER);

    private final Cipher asymCipher = Cipher.getInstance(ASYM_CYPHER);

    private final MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGO);

    SecureRandom random = SecureRandom.getInstance(RANDOM_ALGO, RANDOM_PROVIDER);

    private int sequenceNumber = INITIAL_SEQUENCE_NUMBER;

    public Cryptography() throws Exception {}

    public byte[] symEncrypt(byte[] input, Key key, IvParameterSpec iv) throws Exception {
        symCipher.init(Cipher.ENCRYPT_MODE, key, iv);
        return symCipher.doFinal(input);
    }

    public byte[] symDecrypt(byte[] input, Key key, IvParameterSpec iv) throws Exception {
        symCipher.init(Cipher.DECRYPT_MODE, key, iv);
        return symCipher.doFinal(input);
    }

    public byte[] asymDecrypt(byte[] input, Key key) throws Exception {
        asymCipher.init(Cipher.DECRYPT_MODE, key);
        return asymCipher.doFinal(input);
    }

    public byte[] asymEncrypt(byte[] input, Key key) throws Exception {
        asymCipher.init(Cipher.ENCRYPT_MODE, key);
        return asymCipher.doFinal(input);
    }

    public byte[] digest(byte[] input) {
        return messageDigest.digest(input);
    }

    public int getRandomNumber() { return random.nextInt(); }

    public int getAndSetSequenceNumber() { return sequenceNumber++; }
}
