package pt.ulisboa.tecnico;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Optional;

class Utils {
    static Optional<byte[]> readFile(String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            byte[] data = fis.readAllBytes();
            fis.close();
            return Optional.of(data);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    static void writeFile(String path, byte[] content) throws Exception {
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(content);
        fos.close();
    }
}
