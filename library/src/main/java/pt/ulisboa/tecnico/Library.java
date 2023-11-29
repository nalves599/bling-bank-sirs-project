package pt.ulisboa.tecnico;

import pt.ulisboa.tecnico.aux.Cryptography;
import pt.ulisboa.tecnico.aux.Keys;

import javax.crypto.spec.IvParameterSpec;
import java.io.ByteArrayOutputStream;

import java.util.Arrays;

import static pt.ulisboa.tecnico.aux.Constants.*;
import static pt.ulisboa.tecnico.aux.Conversion.*;

public class Library {

    final private Keys keys;

    final private Cryptography crypto;

    public Library(String secretKeyPath) throws Exception {
        keys = new Keys(secretKeyPath);
        crypto = new Cryptography();
    }

    public byte[] protect(byte[] input) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        output.write(keys.getIv().getIV());

        ByteArrayOutputStream payload = new ByteArrayOutputStream();

        payload.write(intToBytes(input.length));
        payload.write(input);

        int randomNumber = crypto.getRandomNumber();
        int sequenceNumber = crypto.getAndSetSequenceNumber();

        payload.write(intToBytes(randomNumber));
        payload.write(intToBytes(sequenceNumber));

        byte[] digestEncrypted = crypto.asymEncrypt(crypto.digest(payload.toByteArray()), keys.privateKey);

        payload.write(intToBytes(digestEncrypted.length)); // length of digestEncrypted
        payload.write(digestEncrypted);

        output.write(crypto.symEncrypt(payload.toByteArray(), keys.secretKey, keys.iv));

        return output.toByteArray();
    }

    public byte[] unprotect(byte[] input) throws Exception {
        byte[] iv = Arrays.copyOfRange(input, 0, 16);

        byte[] payload = crypto.symDecrypt(Arrays.copyOfRange(input, 16, input.length), keys.secretKey,
            new IvParameterSpec(iv));

        int payloadLength = bytesToInt(payload, 0);
        byte[] data = Arrays.copyOfRange(payload, INT_SIZE, INT_SIZE + payloadLength);

        int randomNumber = bytesToInt(payload, INT_SIZE + payloadLength);
        int sequenceNumber = bytesToInt(payload, INT_SIZE + payloadLength + INT_SIZE);

        int digestEncryptStart = INT_SIZE + payloadLength + INT_SIZE + INT_SIZE;
        int digestEncryptedLength = bytesToInt(payload, digestEncryptStart);
        byte[] digestEncrypted = Arrays.copyOfRange(payload, digestEncryptStart + INT_SIZE, digestEncryptStart +
                                                                                            INT_SIZE +
                                                                                            digestEncryptedLength);

        byte[] digestDecrypted = crypto.asymDecrypt(digestEncrypted, keys.publicKey);
        byte[] digestCalculated = crypto.digest(Arrays.copyOfRange(payload, 0, digestEncryptStart));

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
}
