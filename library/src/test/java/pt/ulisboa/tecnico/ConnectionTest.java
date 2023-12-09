package pt.ulisboa.tecnico;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pt.ulisboa.tecnico.auxTests.TestConfig;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConnectionTest {

    @TempDir
    static File tempFolder;
    static String tempPath;

    @BeforeAll
    static void init() throws Exception {
        tempPath = tempFolder.getAbsolutePath() + "/";

        Path tempFile = Files.createFile(tempFolder.toPath().resolve(TestConfig.SOURCE_TEST_PATH_1));
        Files.write(tempFile, TestConfig.SOURCE_1_JSON.getBytes());

        Path secretKeyFile = Files.createFile(tempFolder.toPath().resolve(TestConfig.SECRET_KEY_TEST_PATH_1));
        Files.write(secretKeyFile, TestConfig.SECRET_KEY_1.getBytes());
    }

    @Test
    public void connectTwoLibraries() throws Exception {
        Library lib1 = new Library(tempPath + TestConfig.SECRET_KEY_TEST_PATH_1);
        Library lib2 = new Library(tempPath + TestConfig.SECRET_KEY_TEST_PATH_1);
        byte[] encryptedKeys = lib1.createSessionKeys().get();
        byte[] encryptedPublicKey = lib2.receiveSessionKeys(encryptedKeys).get();
        assertTrue(lib1.receivePublicKey(encryptedPublicKey));

        byte[] payload = lib1.protect(TestConfig.SOURCE_1_JSON.getBytes()).get();
        byte[] decryptedPayload = lib2.unprotect(payload).get();
        assertEquals(TestConfig.SOURCE_1_JSON, new String(decryptedPayload));
    }

    @Test
    public void connectMultipleLibraries() throws Exception {
        Library lib1 = new Library(tempPath + TestConfig.SECRET_KEY_TEST_PATH_1);
        Library lib2 = new Library(tempPath + TestConfig.SECRET_KEY_TEST_PATH_1);
        byte[] encryptedKeys = lib1.createSessionKeys().get();
        byte[] encryptedPublicKey = lib2.receiveSessionKeys(encryptedKeys).get();
        assertTrue(lib1.receivePublicKey(encryptedPublicKey));

        byte[] payload = lib1.protect(TestConfig.SOURCE_1_JSON.getBytes()).get();
        byte[] decryptedPayload = lib2.unprotect(payload).get();
        assertEquals(TestConfig.SOURCE_1_JSON, new String(decryptedPayload));

        Library lib3 = new Library(tempPath + TestConfig.SECRET_KEY_TEST_PATH_1);
        byte[] encryptedKeys2 = lib3.createSessionKeys().get();
        byte[] encryptedPublicKey2 = lib2.receiveSessionKeys(encryptedKeys2).get();
        assertTrue(lib3.receivePublicKey(encryptedPublicKey2));

        byte[] payload2 = lib3.protect(TestConfig.SOURCE_1_JSON.getBytes()).get();
        byte[] decryptedPayload2 = lib2.unprotect(payload2).get();

        assertEquals(TestConfig.SOURCE_1_JSON, new String(decryptedPayload2));

        byte[] encryptedKeys3 = lib2.createSessionKeys().get();
        byte[] encryptedPublicKey3 = lib1.receiveSessionKeys(encryptedKeys3).get();
        assertTrue(lib2.receivePublicKey(encryptedPublicKey3));

        byte[] payload3 = lib2.protect(TestConfig.SOURCE_1_JSON.getBytes()).get();
        byte[] decryptedPayload3 = lib1.unprotect(payload3).get();
        assertEquals(TestConfig.SOURCE_1_JSON, new String(decryptedPayload3));
    }

    @Test
    public void tiktokTest() throws Exception {
        Library lib1 = new Library(tempPath + TestConfig.SECRET_KEY_TEST_PATH_1);
        Library lib2 = new Library(tempPath + TestConfig.SECRET_KEY_TEST_PATH_1);

        byte[] encryptedKeys = lib1.createSessionKeys().get();
        byte[] encryptedPublicKey = lib2.receiveSessionKeys(encryptedKeys).get();
        assertTrue(lib1.receivePublicKey(encryptedPublicKey));

        byte[] payload = lib1.protect(TestConfig.SOURCE_1_JSON.getBytes()).get();
        byte[] decryptedPayload = lib2.unprotect(payload).get();
        assertEquals(TestConfig.SOURCE_1_JSON, new String(decryptedPayload));

        byte[] payload2 = lib2.protect(TestConfig.SOURCE_1_JSON.getBytes()).get();
        byte[] decryptedPayload2 = lib1.unprotect(payload2).get();
        assertEquals(TestConfig.SOURCE_1_JSON, new String(decryptedPayload2));
    }

}
