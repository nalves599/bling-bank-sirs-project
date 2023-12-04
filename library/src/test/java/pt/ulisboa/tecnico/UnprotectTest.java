package pt.ulisboa.tecnico;

import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pt.ulisboa.tecnico.auxTests.TestConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class UnprotectTest {
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
        Either<String, byte[]> output = lib.protect(TestConfig.SOURCE_1_JSON.getBytes());
        Either<String, byte[]> decrypted = lib.unprotect(output.get());
        assertEquals(TestConfig.DEST_1_JSON, new String(decrypted.get()));
    }

    @Test
    public void unProtectTamperedFile() throws Exception {
        Library lib = new Library(tempPath + TestConfig.SECRET_KEY_TEST_PATH_1);
        Either<String, byte[]> output = lib.protect(TestConfig.SOURCE_1_JSON.getBytes());
        byte[] encrypted = output.get();
        encrypted[0] = (byte) (encrypted[0] + 1);
        Either<String, byte[]> decrypted = lib.unprotect(encrypted);
        assertTrue(decrypted.isLeft());
    }

    @Test
    public void unProtectMultipleFiles() throws Exception {
        Library lib = new Library(tempPath + TestConfig.SECRET_KEY_TEST_PATH_1);
        Either<String, byte[]> output1 = lib.protect(TestConfig.SOURCE_1_JSON.getBytes());
        Either<String, byte[]> output2 = lib.protect(TestConfig.SOURCE_1_JSON.getBytes());
        Either<String, byte[]> decrypted1 = lib.unprotect(output1.get());
        Either<String, byte[]> decrypted2 = lib.unprotect(output2.get());
        assertEquals(TestConfig.DEST_1_JSON, new String(decrypted1.get()));
        assertEquals(TestConfig.DEST_1_JSON, new String(decrypted2.get()));
        assertNotEquals(decrypted1.get(), decrypted2.get());
    }
}
