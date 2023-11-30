package pt.ulisboa.tecnico;

import io.vavr.control.Either;
import org.apache.commons.lang3.ArrayUtils;
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

    public Either<String, byte[]> protect(byte[] input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        if (!write(keys.getIv().getIV(), output)) return Either.left("Check the initialization vector");

        ByteArrayOutputStream payload = new ByteArrayOutputStream();

        if (!write(intToBytes(input.length), payload)) return Either.left("Check the payload length");
        if (!write(input, payload)) return Either.left("Check the payload");

        int randomNumber = crypto.getRandomNumber();
        int sequenceNumber = crypto.getAndSetSequenceNumber();

        if (!write(intToBytes(randomNumber), payload)) return Either.left("Check the random number");
        if (!write(intToBytes(sequenceNumber), payload)) return Either.left("Check the sequence number");

        byte[] digestEncrypted;
        try {
            digestEncrypted = crypto.asymEncrypt(crypto.digest(payload.toByteArray()), keys.privateKey);
        } catch (Exception e) {
            return Either.left("Check the asymmetric encryption method");
        }

        if (!write(intToBytes(digestEncrypted.length), payload)) return Either.left(
            "Check the length of digestEncrypted");
        if (!write((byte[]) ArrayUtils.toPrimitive(digestEncrypted), payload)) return Either.left(
            "Check the digestEncrypted");

        byte[] encryptedPayload;

        try {
            encryptedPayload = crypto.symEncrypt(payload.toByteArray(), keys.secretKey, keys.iv);
        } catch (Exception e) {
            return Either.left("Check the symmetric encryption method");
        }

        if (!write(encryptedPayload, output)) return Either.left("Check the encrypted payload");

        return Either.right(output.toByteArray());
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

    private boolean write(byte[] input, ByteArrayOutputStream output) {
        try {
            output.write(input);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
