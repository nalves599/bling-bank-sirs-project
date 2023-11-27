package pt.ulisboa.tecnico;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import static pt.ulisboa.tecnico.aux.Constants.*;

public class Library {
    private Key secretKey;

    private final IvParameterSpec iv = new IvParameterSpec(IV);

    private Key publicKey;
    private Key privateKey;

    private final int sequenceNumber = INITIAL_SEQUENCE_NUMBER;

    SecureRandom random = SecureRandom.getInstance(RANDOM_ALGO, RANDOM_PROVIDER);

    // Needs to throw exception because of SecureRandom.getInstance
    public Library(String secretKeyPath) throws Exception {
        assignSecretKey(secretKeyPath);
        createAsymmetricKeys();
    }

    public byte[] protect(byte[] input) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        output.write(iv.getIV());

        ByteArrayOutputStream payload = new ByteArrayOutputStream();

        payload.write(intToBytes(input.length));
        payload.write(input);

        int randomNumber = random.nextInt();
        int sequenceNumber = this.sequenceNumber;

        payload.write(intToBytes(randomNumber));
        payload.write(intToBytes(sequenceNumber));

        byte[] digestEncrypted = asymEncrypt(digest(payload.toByteArray()), privateKey);

        payload.write(intToBytes(digestEncrypted.length)); // length of digestEncrypted
        payload.write(digestEncrypted);

        output.write(symEncrypt(payload.toByteArray(), secretKey, iv));

        return output.toByteArray();
    }

    public byte[] unprotect(byte[] input) throws Exception {
        byte[] iv = Arrays.copyOfRange(input, 0, 16);

        byte[] payload = symDecrypt(Arrays.copyOfRange(input, 16, input.length), secretKey, new IvParameterSpec(iv));

        int payloadLength = bytesToInt(payload, 0);
        byte[] data = Arrays.copyOfRange(payload, INT_SIZE, INT_SIZE + payloadLength);

        int randomNumber = bytesToInt(payload, INT_SIZE + payloadLength);
        int sequenceNumber = bytesToInt(payload, INT_SIZE + payloadLength + INT_SIZE);

        int digestEncryptStart = INT_SIZE + payloadLength + INT_SIZE + INT_SIZE;
        int digestEncryptedLength = bytesToInt(payload, digestEncryptStart);
        byte[] digestEncrypted = Arrays.copyOfRange(payload, digestEncryptStart + INT_SIZE, digestEncryptStart +
                                                                                            INT_SIZE +
                                                                                            digestEncryptedLength);

        byte[] digestDecrypted = asymDecrypt(digestEncrypted, publicKey);
        byte[] digestCalculated = digest(Arrays.copyOfRange(payload, 0, digestEncryptStart));

        if (!Arrays.equals(digestDecrypted, digestCalculated)) {
            throw new Exception("Digests don't match");
        }

        return data;
    }

    public boolean check(byte[] input) {
        try {
            unprotect(input);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void assignSecretKey(String secretKeyPath) throws Exception {
        byte[] encoded = readFile(secretKeyPath);
        secretKey = new SecretKeySpec(encoded, SYM_ALGO);
    }

    private byte[] readFile(String path) throws Exception {
        FileInputStream fis = new FileInputStream(path);
        byte[] content = new byte[fis.available()];
        while (true) {
            int read = fis.read(content);
            if (read == -1) {
                break;
            }
        }
        fis.close();
        return content;
    }

    private byte[] asymEncrypt(byte[] input, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(ASYM_CYPHER);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(input);
    }

    private byte[] symEncrypt(byte[] input, Key key, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance(SYM_CYPHER);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        return cipher.doFinal(input);
    }

    private byte[] asymDecrypt(byte[] input, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(ASYM_CYPHER);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(input);
    }

    private byte[] symDecrypt(byte[] input, Key key, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance(SYM_CYPHER);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        return cipher.doFinal(input);
    }

    private byte[] digest(byte[] input) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGO);
        return messageDigest.digest(input);
    }

    private void createAsymmetricKeys() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ASYM_ALGO);
        keyGen.initialize(ASYM_KEY_SIZE);
        KeyPair keyPair = keyGen.generateKeyPair();
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }

    private byte[] intToBytes(int value) {
        return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value };
    }

    private int bytesToInt(byte[] bytes, int offset) {
        return new BigInteger(Arrays.copyOfRange(bytes, offset, offset + INT_SIZE)).intValue();
    }
}
