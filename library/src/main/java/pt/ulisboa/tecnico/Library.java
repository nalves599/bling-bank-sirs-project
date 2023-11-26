package pt.ulisboa.tecnico;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import static pt.ulisboa.tecnico.aux.Constants.*;

public class Library
{
    private Key secretKey;

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
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        int randomNumber = random.nextInt();
        int sequenceNumber = this.sequenceNumber;

        os.write(input);
        os.write(intToBytes(randomNumber));
        os.write(intToBytes(sequenceNumber));
        os.write(publicKey.getEncoded());

        byte[] digestEncrypted = asymEncrypt(digest(os.toByteArray()), privateKey);
        os.write(digestEncrypted);

        os.write(intToBytes(digestEncrypted.length)); // length of digestEncrypted
        os.write(intToBytes(publicKey.getEncoded().length)) ; // length of K1

        return symEncrypt(os.toByteArray(), secretKey);
    }

    public byte[] unprotect(byte[] input) throws Exception {
        byte[] decrypted = symDecrypt(input, secretKey);

        int publicKey1Length = new BigInteger(Arrays.copyOfRange(decrypted, decrypted.length - INT_SIZE, decrypted.length)).intValue();
        int digestEncryptedLength = new BigInteger(Arrays.copyOfRange(decrypted, decrypted.length - INT_SIZE * 2, decrypted.length - INT_SIZE)).intValue();

        int startDigestEncrypted = decrypted.length  - digestEncryptedLength - INT_SIZE * 2;
        int startPublicKey1 = startDigestEncrypted - publicKey1Length;
        int startSequenceNumber = startPublicKey1 - INT_SIZE;
        int startRandomNumber = startSequenceNumber - INT_SIZE;

        byte[] digestEncrypted = Arrays.copyOfRange(decrypted, startDigestEncrypted, startDigestEncrypted + digestEncryptedLength);
        byte[] publicKey1 = Arrays.copyOfRange(decrypted, startDigestEncrypted - publicKey1Length, startDigestEncrypted);
        int sequenceNumber = new BigInteger(Arrays.copyOfRange(decrypted, startSequenceNumber, startSequenceNumber + INT_SIZE)).intValue();
        int randomNumber = new BigInteger(Arrays.copyOfRange(decrypted, startRandomNumber, startRandomNumber + INT_SIZE)).intValue();
        byte[] data = Arrays.copyOfRange(decrypted, 0, startRandomNumber);

        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKey1);
        Key publicKey2 = KeyFactory.getInstance(ASYM_ALGO).generatePublic(publicKeySpec);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        os.write(data);
        os.write(intToBytes(randomNumber));
        os.write(intToBytes(sequenceNumber));
        os.write(publicKey1);

        byte[] digest = digest(os.toByteArray());
        byte[] digestDecrypted = asymDecrypt(digestEncrypted, publicKey2);
        // Fix problem with padding
        digestDecrypted = Arrays.copyOfRange(digestDecrypted, digestEncrypted.length - digest.length, digestDecrypted.length);

        if (!Arrays.equals(digest, digestDecrypted)) {
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

    private byte[] symEncrypt(byte[] input, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(SYM_CYPHER);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(input);
    }

    private byte[] asymDecrypt(byte[] input, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(ASYM_CYPHER);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(input, cipher.getBlockSize(), input.length - cipher.getBlockSize());
    }

    private byte[] symDecrypt(byte[] input, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(SYM_CYPHER);
        cipher.init(Cipher.DECRYPT_MODE, key);
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
        return new byte[] {
            (byte)(value >>> 24),
            (byte)(value >>> 16),
            (byte)(value >>> 8),
            (byte)value};
    }
}
