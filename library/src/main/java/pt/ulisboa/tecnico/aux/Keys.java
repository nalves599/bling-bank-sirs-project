package pt.ulisboa.tecnico.aux;

import lombok.Getter;
import lombok.Setter;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.X509EncodedKeySpec;

import static pt.ulisboa.tecnico.aux.Constants.*;
import static pt.ulisboa.tecnico.aux.Constants.SYM_ALGO;
import static pt.ulisboa.tecnico.aux.FileReader.*;
import static pt.ulisboa.tecnico.aux.Conversion.*;

@Getter
public class Keys {

    @Setter(lombok.AccessLevel.NONE)
    private Key secretKey; // KEK

    private Key secretSessionKey;

    private Key publicKey;

    private Key privateKey;

    private Key receiverPublicKey;

    private final IvParameterSpec iv = new IvParameterSpec(Constants.IV);

    public Keys(String secretKeyPath) throws Exception {
        assignSecretKey(secretKeyPath);
        generateAsymKey();
    }

    private void assignSecretKey(String secretKeyPath) throws Exception {
        byte[] encoded = FileReader.readFile(secretKeyPath);
        secretKey = new SecretKeySpec(encoded, SYM_ALGO);
    }

    private void generateAsymKey() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ASYM_ALGO);
        keyPairGenerator.initialize(ASYM_KEY_SIZE);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }

    public void generateSecretSessionKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(SYM_ALGO);
        keyGenerator.init(SYM_KEY_SIZE);
        secretSessionKey = keyGenerator.generateKey();
    }

    public byte[] creationPayload() throws Exception {
        byte[] encodedSecretSessionKey = secretSessionKey.getEncoded();
        byte[] encodedPublicKey = publicKey.getEncoded();
        ByteArrayOutputStream payload = new ByteArrayOutputStream();

        if (!write(intToBytes(encodedSecretSessionKey.length), payload)) throw new Exception(
            "Error writing to byte array");
        if (!write(encodedSecretSessionKey, payload)) throw new Exception("Error writing to byte array");

        if (!write(intToBytes(encodedPublicKey.length), payload)) throw new Exception("Error writing to byte array");
        if (!write(encodedPublicKey, payload)) throw new Exception("Error writing to byte array");

        return payload.toByteArray();
    }

    public byte[] receiveSessionKey(byte[] input) throws Exception {
        int secretSessionKeyLength = bytesToInt(input, 0).orElseThrow(() -> new Exception(
            "Error reading from byte array"));
        byte[] encodedSecretSessionKey = read(input, INT_SIZE, secretSessionKeyLength).orElseThrow(() -> new Exception(
            "Error reading from byte array"));
        secretSessionKey = new SecretKeySpec(encodedSecretSessionKey, SYM_ALGO);

        int publicKeyLength = bytesToInt(input, INT_SIZE + secretSessionKeyLength).orElseThrow(() -> new Exception(
            "Error reading from byte array"));
        byte[] encodedReceiverPublicKey = read(input, INT_SIZE + secretSessionKeyLength + INT_SIZE, publicKeyLength)
            .orElseThrow(() -> new Exception("Error reading from byte array"));
        receiverPublicKey = KeyFactory.getInstance(ASYM_ALGO).generatePublic(new X509EncodedKeySpec(
            encodedReceiverPublicKey));

        byte[] encodedPublicKey = publicKey.getEncoded();
        ByteArrayOutputStream payload = new ByteArrayOutputStream();

        if (!write(intToBytes(encodedPublicKey.length), payload)) throw new Exception("Error writing to byte array");
        if (!write(encodedPublicKey, payload)) throw new Exception("Error writing to byte array");

        return payload.toByteArray();
    }

    public void receivePublicKey(byte[] input) throws Exception {
        int publicKeyLength = bytesToInt(input, 0).orElseThrow(() -> new Exception("Error reading from byte array"));
        byte[] encodedPublicKey = read(input, INT_SIZE, publicKeyLength).orElseThrow(() -> new Exception(
            "Error reading from byte array"));
        receiverPublicKey = KeyFactory.getInstance(ASYM_ALGO).generatePublic(new X509EncodedKeySpec(encodedPublicKey));
    }

}
