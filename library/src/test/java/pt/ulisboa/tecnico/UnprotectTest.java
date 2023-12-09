package pt.ulisboa.tecnico;

import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pt.ulisboa.tecnico.aux.Constants;
import pt.ulisboa.tecnico.auxTests.TestConfig;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class UnprotectTest {
    @TempDir
    static File tempFolder;
    static String tempPath;

    static Library lib1;

    static Library lib2;

    @BeforeAll
    static void init() throws Exception {
        tempPath = tempFolder.getAbsolutePath() + "/";

        Path tempFile = Files.createFile(tempFolder.toPath().resolve(TestConfig.SOURCE_TEST_PATH_1));
        Files.write(tempFile, TestConfig.SOURCE_1_JSON.getBytes());

        Path secretKeyFile = Files.createFile(tempFolder.toPath().resolve(TestConfig.SECRET_KEY_TEST_PATH_1));
        Files.write(secretKeyFile, TestConfig.SECRET_KEY_1.getBytes());

        lib1 = new Library(tempPath + TestConfig.SECRET_KEY_TEST_PATH_1);
        lib2 = new Library(tempPath + TestConfig.SECRET_KEY_TEST_PATH_1);
        byte[] encryptedKeys = lib1.createSessionKeys().get();
        byte[] encryptedPublicKey = lib2.receiveSessionKeys(encryptedKeys).get();
        assertTrue(lib1.receivePublicKey(encryptedPublicKey));
    }

    @Test
    public void unProtectFile() throws Exception {
        Either<String, byte[]> output = lib1.protect(TestConfig.SOURCE_1_JSON.getBytes());
        Either<String, byte[]> decrypted = lib2.unprotect(output.get());
        assertEquals(TestConfig.SOURCE_1_JSON, new String(decrypted.get()));
    }

    @Test
    public void unProtectTamperedFile() throws Exception {
        Either<String, byte[]> output = lib1.protect(TestConfig.SOURCE_1_JSON.getBytes());
        byte[] encrypted = output.get();
        encrypted[0] = (byte) (encrypted[0] + 1);
        Either<String, byte[]> decrypted = lib2.unprotect(encrypted);
        assertTrue(decrypted.isLeft());
    }

    @Test
    public void unProtectMultipleFiles() throws Exception {
        Either<String, byte[]> output1 = lib1.protect(TestConfig.SOURCE_1_JSON.getBytes());
        Either<String, byte[]> output2 = lib2.protect(TestConfig.SOURCE_1_JSON.getBytes());
        Either<String, byte[]> decrypted1 = lib2.unprotect(output1.get());
        Either<String, byte[]> decrypted2 = lib1.unprotect(output2.get());
        assertEquals(TestConfig.SOURCE_1_JSON, new String(decrypted1.get()));
        assertEquals(TestConfig.SOURCE_1_JSON, new String(decrypted2.get()));
        assertNotEquals(decrypted1.get(), decrypted2.get());
    }

    @Test
    public void unProtectSameFileTwice() throws Exception {
        Either<String, byte[]> output = lib1.protect(TestConfig.SOURCE_1_JSON.getBytes());
        Either<String, byte[]> decrypted1 = lib2.unprotect(output.get());
        Either<String, byte[]> decrypted2 = lib2.unprotect(output.get());
        assertEquals(TestConfig.SOURCE_1_JSON, new String(decrypted1.get()));
        assertTrue(decrypted2.isLeft());
        assertTrue(decrypted2.getLeft().contains("Repeated sequence number"));
    }

    @Test
    public void unProtectFileAfterTimeout() throws Exception {
        Either<String, byte[]> output = lib1.protect(TestConfig.SOURCE_1_JSON.getBytes());
        Thread.sleep(Constants.MAX_TIMESTAMP_DIFFERENCE + 1000);
        Either<String, byte[]> decrypted = lib2.unprotect(output.get());
        assertTrue(decrypted.isLeft());
        assertTrue(decrypted.getLeft().contains("Timestamps don't match"));
    }
}
