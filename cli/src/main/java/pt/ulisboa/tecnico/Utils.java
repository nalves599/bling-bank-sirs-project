package pt.ulisboa.tecnico;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Utils {
    public static byte[] readFile(String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            byte[] data = fis.readAllBytes();
            fis.close();
            return data;
        } catch (Exception e) {
            System.err.println("Could not read file: " + path);
            return null;
        }
    }

    public static void writeFile(String path, byte[] content) {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(content);
            fos.close();
        } catch (Exception e) {
            System.err.println("Could not write file: " + path);
        }
    }
}
