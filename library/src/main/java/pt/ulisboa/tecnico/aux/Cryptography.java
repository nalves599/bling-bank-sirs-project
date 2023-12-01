package pt.ulisboa.tecnico.aux;

import io.vavr.control.Either;
import lombok.Getter;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import java.security.*;
import java.util.Optional;

import static pt.ulisboa.tecnico.aux.Constants.*;

@Getter
public class Cryptography {

    private final Cipher symCipher = Cipher.getInstance(SYM_CYPHER);

    private final Cipher asymCipher = Cipher.getInstance(ASYM_CYPHER);

    private final MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGO);

    SecureRandom random = SecureRandom.getInstance(RANDOM_ALGO, RANDOM_PROVIDER);

    private int sequenceNumber = INITIAL_SEQUENCE_NUMBER;

    public Cryptography() throws Exception {}

    public Optional<byte[]> symEncrypt(byte[] input, Key key, IvParameterSpec iv) {
        try {
            symCipher.init(Cipher.ENCRYPT_MODE, key, iv);
            return Optional.of(symCipher.doFinal(input));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<byte[]> symDecrypt(byte[] input, Key key, IvParameterSpec iv) {
        try {
            symCipher.init(Cipher.DECRYPT_MODE, key, iv);
            return Optional.of(symCipher.doFinal(input));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<byte[]> asymEncrypt(byte[] input, Key key) {
        try {
            asymCipher.init(Cipher.ENCRYPT_MODE, key);
            return Optional.of(asymCipher.doFinal(input));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<byte[]> asymDecrypt(byte[] input, Key key) {
        try {
            asymCipher.init(Cipher.DECRYPT_MODE, key);
            return Optional.of(asymCipher.doFinal(input));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public byte[] digest(byte[] input) {
        return messageDigest.digest(input);
    }

    public int getRandomNumber() { return random.nextInt(); }

    public int getAndSetSequenceNumber() { return sequenceNumber++; }
}
