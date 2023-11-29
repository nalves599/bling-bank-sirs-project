package pt.ulisboa.tecnico.aux;

import java.io.FileInputStream;

class FileReader {

    static byte[] readFile(String path) throws Exception {
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
}
