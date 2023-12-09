package pt.ulisboa.tecnico.aux;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Optional;

public class FileReader {

    public static byte[] readFile(String path) throws Exception {
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

    public static Optional<byte[]> read(byte[] input, int start, int length) {
        try {
            return Optional.of(Arrays.copyOfRange(input, start, start + length));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static boolean write(byte[] input, ByteArrayOutputStream output) {
        try {
            output.write(input);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
