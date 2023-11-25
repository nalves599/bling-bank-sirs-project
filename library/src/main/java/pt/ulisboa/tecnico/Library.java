package pt.ulisboa.tecnico;

import lombok.Setter;
import pt.ulisboa.tecnico.aux.Constants;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.FileInputStream;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

@Setter
public class Library
{
    private Key secretKey;

    private Key publicKey;
    private Key privateKey;

    private int sequenceNumber = Constants.INITIAL_SEQUENCE_NUMBER;

    SecureRandom random = SecureRandom.getInstance(Constants.RANDOM_ALGO, Constants.RANDOM_PROVIDER);

    // Needs to throw exception because of SecureRandom.getInstance
    public Library (String secretKeyPath) throws Exception {
        assignSecretKey(secretKeyPath);
        createAsymmetricKeys();
    }
    public byte[] protect(byte[] input, byte[] receiverPublicKey) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream( );

        int randomNumber = random.nextInt();
        int sequenceNumber = this.sequenceNumber;

        System.err.println(receiverPublicKey.length);

        Key publicKey1 = KeyFactory.getInstance(Constants.ASYM_ALGO).generatePublic(new X509EncodedKeySpec(receiverPublicKey));
        byte[] publicKeyEncrypted = encrypt(publicKey.getEncoded(), publicKey1);

        os.write(input);
        os.write(randomNumber);
        os.write(sequenceNumber);
        os.write(publicKeyEncrypted);

        byte[] digest = digest(os.toByteArray());

        os.write(encrypt(digest, privateKey));

        return encrypt(os.toByteArray(), secretKey);
    }

    public void unprotect(String input, String output) {
        if (fileDoesNotExist(input)) {
            throw new IllegalArgumentException("Input file does not exist");
        }
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public boolean check(String input) {
        if (fileDoesNotExist(input)) {
            throw new IllegalArgumentException("Input file does not exist");
        }
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private boolean fileDoesNotExist(String input) {
        File f = new File(input);
        return !f.exists() || f.isDirectory();
    }

    private void assignSecretKey(String secretKeyPath) throws Exception {
        byte[] encoded = readFile(secretKeyPath);
        secretKey = new SecretKeySpec(encoded, Constants.SYM_ALGO);
    }

    private byte[] readFile(String path) throws Exception {
        FileInputStream fis = new FileInputStream(path);
        byte[] content = new byte[fis.available()];
        fis.read(content);
        fis.close();
        return content;
    }

    private byte[] encrypt(byte[] input, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(Constants.ASYM_ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(input);
    }

    private byte[] digest(byte[] input) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance(Constants.DIGEST_ALGO);
        return messageDigest.digest(input);
    }

    private void createAsymmetricKeys() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(Constants.ASYM_ALGO);
        keyGen.initialize(Constants.ASYM_KEY_SIZE);
        KeyPair keyPair = keyGen.generateKeyPair();
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }
}
