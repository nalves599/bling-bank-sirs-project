package pt.ulisboa.tecnico;

import lombok.Setter;
import pt.ulisboa.tecnico.aux.Constants;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
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
    public byte[] protect(byte[] input) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream( );

        int randomNumber = random.nextInt();
        int sequenceNumber = this.sequenceNumber;

        os.write(input);
        os.write(randomNumber);
        os.write(sequenceNumber);
        os.write(publicKey.getEncoded());

        byte[] digest = digest(os.toByteArray());

        os.write(asymEncrypt(digest, privateKey));

        return symEncrypt(os.toByteArray(), secretKey);
    }

    public byte[] unprotect(String input, String output) throws Exception {
        if (fileDoesNotExist(input)) {
            throw new IllegalArgumentException("Input file does not exist");
        }

        // decrypt file input to file output

        byte[] decryptedBytes = asymDecrypt(input.getBytes(), secretKey);
        // decryptedBytes is (A + K1 + C)
        // A is the original message + nonce
        // K1 is server's public key encrypted with client's public key
        // C is the encription of digest of A + K1 with server's private key

        // separate A, K1 and C
        byte[] A = new byte[decryptedBytes.length - Constants.ASYM_KEY_SIZE - Constants.DIGEST_SIZE];
        byte[] K1 = new byte[Constants.ASYM_KEY_SIZE];
        byte[] C = new byte[Constants.DIGEST_SIZE];

        System.arraycopy(decryptedBytes, 0, A, 0, A.length);
        System.arraycopy(decryptedBytes, A.length, K1, 0, K1.length);
        System.arraycopy(decryptedBytes, A.length + K1.length, C, 0, C.length);

        // decrypt K1 with client's private key
        byte[] K1Decrypted = asymDecrypt(K1, privateKey);

        // parse K1 as server's public key
        Key publicKey1 = KeyFactory.getInstance(Constants.ASYM_ALGO).generatePublic(new X509EncodedKeySpec(K1Decrypted));

        // decrypt C with server's public key
        byte[] CDecrypted = asymDecrypt(C, publicKey1);

        // calculate digest of A + K1
        ByteArrayOutputStream os = new ByteArrayOutputStream( );
        os.write(A);
        os.write(K1Decrypted);
        byte[] digest = digest(os.toByteArray());

        // compare digest with CDecrypted
        if (!MessageDigest.isEqual(digest, CDecrypted)) {
            throw new Exception("Digests don't match");
        }

        // check nonces - TO DO

        // write A to output file
        FileOutputStream fos = new FileOutputStream(output);
        fos.write(A);
        fos.close();

        return A;
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

    private byte[] asymEncrypt(byte[] input, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(Constants.ASYM_CYPHER);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(input);
    }

    private byte[] symEncrypt(byte[] input, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(Constants.SYM_MODE);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(input);
    }

    private byte[] asymDecrypt(byte[] input, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(Constants.ASYM_CYPHER);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(input);
    }

    private byte[] symDecrypt(byte[] input, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(Constants.SYM_MODE);
        cipher.init(Cipher.DECRYPT_MODE, key);
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
