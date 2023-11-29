package pt.ulisboa.tecnico.aux;

import lombok.Getter;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import static pt.ulisboa.tecnico.aux.Constants.*;
import static pt.ulisboa.tecnico.aux.Constants.SYM_ALGO;

@Getter
public class Keys {

    public Key secretKey;

    public final IvParameterSpec iv = new IvParameterSpec(IV);

    public Key publicKey;

    public Key privateKey;

    public Keys(String secretKeyPath) throws Exception {
        assignSecretKey(secretKeyPath);
        createAsymmetricKeys();
    }

    private void createAsymmetricKeys() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ASYM_ALGO);
        keyGen.initialize(ASYM_KEY_SIZE);
        KeyPair keyPair = keyGen.generateKeyPair();
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }

    private void assignSecretKey(String secretKeyPath) throws Exception {
        byte[] encoded = FileReader.readFile(secretKeyPath);
        secretKey = new SecretKeySpec(encoded, SYM_ALGO);
    }

}
