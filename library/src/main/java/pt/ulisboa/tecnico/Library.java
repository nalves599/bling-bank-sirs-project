package pt.ulisboa.tecnico;

import lombok.Setter;
import pt.ulisboa.tecnico.aux.Constants;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.FileInputStream;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;

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
        // createAssymetricKeys();
    }
    public byte[] protect(byte[] input, byte[] receiverPublicKey) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );

        // TODO: Add nonces
        int randomNumber = random.nextInt();
        int sequenceNumber = this.sequenceNumber;


        outputStream.write(input);
        outputStream.write(randomNumber);
        outputStream.write(sequenceNumber);
        // TODO: Encrypt digest

        byte[] payload = outputStream.toByteArray();

        Cipher cipher = Cipher.getInstance(Constants.SYM_ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        return cipher.doFinal(payload);
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
}
