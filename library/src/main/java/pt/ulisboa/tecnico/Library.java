package pt.ulisboa.tecnico;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Either;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pt.ulisboa.tecnico.aux.Cryptography;
import pt.ulisboa.tecnico.aux.Keys;

import javax.crypto.spec.IvParameterSpec;
import java.io.ByteArrayOutputStream;

import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

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
        try {
            crypto.CreateTimestamp();

            JSONObject json = new JSONObject(new String(input));

            iterateJSON(json, true);

            return Either.right(prettifyJSON(json));
        } catch (Exception e) {
            return Either.left((e.getMessage()));
        }
    }

    public Either<String, byte[]> unprotect(byte[] input) {
        try {
            crypto.cleanStructure();

            JSONObject json = new JSONObject(new String(input));

            iterateJSON(json, false);

            return Either.right(prettifyJSON(json));

        } catch (Exception e) {
            return Either.left(e.getMessage());
        }
    }

    public boolean check(byte[] input) {
        return unprotect(input).isRight();
    }

    private byte[] doProtect(byte[] input) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        if (!write(keys.getIv().getIV(), output)) throw new Exception("Check the initialization vector");

        ByteArrayOutputStream payload = new ByteArrayOutputStream();

        if (!write(intToBytes(input.length), payload)) throw new Exception("Check the payload length");
        if (!write(input, payload)) throw new Exception("Check the payload");

        if (!write(intToBytes(crypto.getTimestamp().length()), payload)) throw new Exception(
            "Check the length of the timestamp");
        if (!write(crypto.getTimestamp().getBytes(), payload)) throw new Exception("Check the timestamp");
        if (!write(intToBytes(crypto.getAndIncrementSequenceNumber()), payload)) throw new Exception(
            "Check the sequence number");

        byte[] digestEncrypted = crypto.asymEncrypt(crypto.digest(payload.toByteArray()), keys.privateKey).orElseThrow(
            () -> new Exception("Check the asymmetric encryption method"));

        if (!write(intToBytes(digestEncrypted.length), payload)) throw new Exception(
            "Check the length of digestEncrypted");
        if (!write(digestEncrypted, payload)) throw new Exception(
            "Check the digestEncrypted");

        byte[] encryptedPayload = crypto.symEncrypt(payload.toByteArray(), keys.secretKey, keys.iv).orElseThrow(
            () -> new Exception("Check the symmetric encryption method"));

        if (!write(encryptedPayload, output)) throw new Exception("Check the encrypted payload");

        return output.toByteArray();
    }

    private byte[] doUnprotect(byte[] input) throws Exception {
        byte[] iv = read(input, 0, 16).orElseThrow(() -> new Exception("Check the initialization vector"));

        byte[] encryptedPayload = read(input, 16, input.length - 16).orElseThrow(
            () -> new Exception("Check the encrypted payload"));

        byte[] payload = crypto.symDecrypt(encryptedPayload, keys.secretKey, new IvParameterSpec(iv)).orElseThrow(
            () -> new Exception("Check the symmetric decryption method"));

        int payloadLength = bytesToInt(payload, 0).orElseThrow(() -> new Exception("Check the payload length"));
        byte[] data = read(payload, INT_SIZE, payloadLength).orElseThrow(() -> new Exception("Check the payload"));

        int timestampLength = bytesToInt(payload, INT_SIZE + payloadLength).orElseThrow(
            () -> new Exception("Check the length of the timestamp"));
        String timestamp = new String(read(payload, INT_SIZE + payloadLength + INT_SIZE, timestampLength).orElseThrow(
            () -> new Exception("Check the timestamp")));

        int sequenceNumber = bytesToInt(payload, INT_SIZE + payloadLength + INT_SIZE + timestampLength).orElseThrow(
            () -> new Exception("Check the sequence number"));

        if (MAX_TIMESTAMP_DIFFERENCE < System.currentTimeMillis() - Long.parseLong(timestamp)) {
            throw new Exception("Timestamps don't match");
        }

        if (!crypto.addSequenceNumber(Long.parseLong(timestamp), sequenceNumber)) {
            throw new Exception("Repeated sequence number");
        }

        int digestEncryptStart = INT_SIZE + payloadLength + INT_SIZE + timestampLength + INT_SIZE;
        int digestEncryptedLength = bytesToInt(payload, digestEncryptStart).orElseThrow(
            () -> new Exception("Check the length of digestEncrypted"));

        byte[] digestEncrypted = read(payload, digestEncryptStart + INT_SIZE, digestEncryptedLength)
            .orElseThrow(() -> new Exception("Check the digestEncrypted"));

        byte[] digestDecrypted = crypto.asymDecrypt(digestEncrypted, keys.publicKey).orElseThrow(
            () -> new Exception("Check the asymmetric decryption method"));
        byte[] digestCalculated = crypto.digest(Arrays.copyOfRange(payload, 0, digestEncryptStart));

        if (!Arrays.equals(digestDecrypted, digestCalculated)) {
            throw new Exception("Digests don't match");
        }

        return data;
    }

    private Optional<byte[]> read(byte[] input, int start, int length) {
        try {
            return Optional.of(Arrays.copyOfRange(input, start, start + length));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private boolean write(byte[] input, ByteArrayOutputStream output) {
        try {
            output.write(input);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void iterateJSON(JSONObject json, boolean encryption) throws Exception {
        for (String k : json.keySet()) {
            try {
                if (k.equals("accountHolder") || k.equals("movements")) {
                    JSONArray array = json.getJSONArray(k);
                    if (array.isEmpty()) continue;
                    boolean isString = array.get(0) instanceof String;
                    for (int i = 0; i < array.length(); i++) {
                        if (isString) {
                            operateStringArray(encryption, array, i);
                        } else {
                            iterateJSON(array.getJSONObject(i), encryption);
                        }
                    }
                } else {
                    iterateJSON(json.getJSONObject(k), encryption);
                }
            } catch (JSONException e) {
                operate(json, k, json.get(k).toString().getBytes(), encryption);
            }
        }
    }

    private void operateStringArray(boolean encryption, JSONArray array, int i) throws Exception {
        if (encryption) {
            byte[] p = doProtect(array.get(i).toString().getBytes());
            array.put(i, Base64.getEncoder().encodeToString(p));
        } else {
            byte[] u = Base64.getDecoder().decode(array.get(i).toString());
            byte[] unprotectedValue = doUnprotect(u);
            array.put(i, new String(unprotectedValue));
        }
    }

    private void operate(JSONObject json, String entry, byte[] data, boolean encryption) throws Exception {
        if (encryption) {
            byte[] p = doProtect(data);
            json.put(entry, Base64.getEncoder().encodeToString(p));
        } else {
            byte[] u = Base64.getDecoder().decode(json.get(entry).toString());
            byte[] unprotectedValue = doUnprotect(u);
            if (entry.equals("balance") || entry.equals("value")) {
                json.put(entry, Double.parseDouble(new String(unprotectedValue)));
                return;
            }
            json.put(entry, new String(unprotectedValue));
        }
    }

    private byte[] prettifyJSON(JSONObject json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(json.toString()).toPrettyString().getBytes();
    }

}
