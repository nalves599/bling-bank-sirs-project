package pt.ulisboa.tecnico;

import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pt.ulisboa.tecnico.auxTests.TestConfig;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class CheckTest {
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
    public void checkFile() throws Exception {
        Either<String, byte[]> output = lib1.protect(TestConfig.SOURCE_1_JSON.getBytes());
        assertTrue(lib2.check(output.get()));
    }

    @Test
    public void checkTamperedFile() throws Exception {
        Either<String, byte[]> output = lib1.protect(TestConfig.SOURCE_1_JSON.getBytes());
        output.get()[0] = (byte) (output.get()[0] + 1);
        assertFalse(lib2.check(output.get()));
    }

    @Test
    public void checkMultipleFiles() throws Exception {
        Either<String, byte[]> output1 = lib1.protect(TestConfig.SOURCE_1_JSON.getBytes());
        Either<String, byte[]> output2 = lib2.protect(TestConfig.SOURCE_1_JSON.getBytes());
        assertTrue(lib2.check(output1.get()));
        assertTrue(lib1.check(output2.get()));
    }

    @Test
    public void checkSameMachine() throws Exception {
        Either<String, byte[]> output1 = lib1.protect(TestConfig.SOURCE_1_JSON.getBytes());
        assertFalse(lib1.check(output1.get()));
    }
}
