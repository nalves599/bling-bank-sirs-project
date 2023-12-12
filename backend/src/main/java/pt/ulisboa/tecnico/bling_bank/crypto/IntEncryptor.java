package pt.ulisboa.tecnico.bling_bank.crypto;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
@Converter
public class IntEncryptor implements AttributeConverter<Integer, String> {

    private final String SECRET = "C3F0E109F251D6C7BA0E300820A660D2"; // TODO: change this
    private final String initVector = "1234567812345678"; // TODO: change this
    private final String algo = "AES/CBC/PKCS5Padding";

    private final Key key = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "AES");

    private final Cipher cipher = Cipher.getInstance(algo);

    private final IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));

    public IntEncryptor() throws Exception {
    }

    @Override
    public String convertToDatabaseColumn(Integer attribute) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);

            byte[] encrypted = cipher.doFinal(attribute.toString().getBytes());
            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            System.err.println(ex);
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Integer convertToEntityAttribute(String dbData) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, key, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(dbData));
            return Integer.parseInt(new String(original));
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
