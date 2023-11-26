package pt.ulisboa.tecnico;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pt.ulisboa.tecnico.auxTests.TestConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UnprotectTest
{
    @TempDir
    static File tempFolder;
    static String tempPath;
    @BeforeAll
    static void init() throws IOException {
        tempPath = tempFolder.getAbsolutePath() + "/";

        Path tempFile = Files.createFile(tempFolder.toPath().resolve(TestConfig.SOURCE_TEST_PATH_1));
        Files.write(tempFile, TestConfig.SOURCE_1_JSON.getBytes());

        Path secretKeyFile = Files.createFile(tempFolder.toPath().resolve(TestConfig.SECRET_KEY_TEST_PATH_1));
        Files.write(secretKeyFile, TestConfig.SECRET_KEY_1.getBytes());
    }

    @Test
    public void unProtectFile() throws Exception {
        Library lib = new Library(tempPath + TestConfig.SECRET_KEY_TEST_PATH_1);
        byte[] encrypted = lib.protect(TestConfig.SOURCE_1_JSON.getBytes());
        byte[] decrypted = lib.unprotect(encrypted);
        assertEquals(TestConfig.SOURCE_1_JSON, new String(decrypted));
    }

    @Test
    public void unProtectTamperedFile() throws Exception {
        Library lib = new Library(tempPath + TestConfig.SECRET_KEY_TEST_PATH_1);
        byte[] encrypted = lib.protect(TestConfig.SOURCE_1_JSON.getBytes());
        encrypted[0] = (byte) (encrypted[0] + 1);
        assertThrows(Exception.class, () -> lib.unprotect(encrypted));
    }
}
