package pt.ulisboa.tecnico.aux;

import lombok.Getter;
import lombok.Setter;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import java.security.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

import static pt.ulisboa.tecnico.aux.Constants.*;

public class Cryptography {

    private final Cipher symCipher = Cipher.getInstance(SYM_CYPHER);

    private final Cipher asymCipher = Cipher.getInstance(ASYM_CYPHER);

    private final MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGO);

    private long timestamp;

    private int sequenceNumber = INITIAL_SEQUENCE_NUMBER;

    @Setter
    @Getter
    private int timestampDifference = MAX_TIMESTAMP_DIFFERENCE;

    // <timestamp, <sequenceNumber>>
    private final HashMap<Long, HashSet<Integer>> sequenceNumbers = new HashMap<>();

    public Cryptography() throws Exception {
        createTimestamp();
    }

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

    public void createTimestamp() {
        timestamp = System.currentTimeMillis();
        sequenceNumber = 0;
    }

    public String getTimestamp() { return String.valueOf(timestamp); }

    public int getAndIncrementSequenceNumber() { return sequenceNumber++; }

    public void cleanStructure() {
        sequenceNumbers.entrySet().removeIf(entry -> entry.getKey() < System.currentTimeMillis() -
                                                                      timestampDifference);
    }

    public boolean addSequenceNumber(long timestamp, int sequenceNumber) {
        if (!sequenceNumbers.containsKey(timestamp)) {
            sequenceNumbers.put(timestamp, new HashSet<>());
        }
        if (sequenceNumbers.get(timestamp).contains(sequenceNumber)) {
            return false;
        }
        sequenceNumbers.get(timestamp).add(sequenceNumber);
        return true;
    }
}
