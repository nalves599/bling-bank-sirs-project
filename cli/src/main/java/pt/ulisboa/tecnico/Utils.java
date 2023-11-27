package pt.ulisboa.tecnico;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Utils {
    public static byte[] readFile(String path) throws Exception {
        FileInputStream fis = new FileInputStream(path);
        byte[] data = fis.readAllBytes();
        fis.close();
        return data;
    }

    public static void writeFile(String path, byte[] content) throws Exception {
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(content);
        fos.close();
    }
}
