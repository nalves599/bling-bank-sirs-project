package pt.ulisboa.tecnico;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pt.ulisboa.tecnico.auxTests.TestConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class UnProtectTest {
    @TempDir
    static File tempFolder;
    static String tempPath;

    static Path secretKeyFile;

    static Path privateKeyFile;

    static Path publicKeyFile;

    static Path secretSessionKeyFile;

    @BeforeAll
    static void init() throws Exception {
        tempPath = tempFolder.getAbsolutePath() + "/";

        Path tempFile = Files.createFile(tempFolder.toPath().resolve(TestConfig.SOURCE_TEST_PATH_1));
        Files.write(tempFile, TestConfig.SOURCE_1_JSON.getBytes());

        secretKeyFile = Files.createFile(tempFolder.toPath().resolve(TestConfig.SECRET_KEY_TEST_PATH_1));
        Files.write(secretKeyFile, TestConfig.base64Decode(TestConfig.SECRET_KEY_1));

        privateKeyFile = Files.createFile(tempFolder.toPath().resolve(TestConfig.PRIVATE_KEY_TEST_PATH_1));
        Files.write(privateKeyFile, TestConfig.base64Decode(TestConfig.PRIVATE_KEY_1));

        publicKeyFile = Files.createFile(tempFolder.toPath().resolve(TestConfig.PUBLIC_KEY_TEST_PATH_1));
        Files.write(publicKeyFile, TestConfig.base64Decode(TestConfig.PUBLIC_KEY_1));

        secretSessionKeyFile = Files.createFile(tempFolder.toPath().resolve(TestConfig.SECRET_SESSION_KEY_TEST_PATH_1));
        Files.write(secretSessionKeyFile, TestConfig.base64Decode(TestConfig.SECRET_SESSION_KEY_1));
    }

    @BeforeEach
    void setUp() throws Exception {
        Files.deleteIfExists(Path.of(tempPath + TestConfig.DEST_TEST_PATH_1));
        Files.deleteIfExists(Path.of(tempPath + TestConfig.DEST_TEST_PATH_1 + "1"));
    }

    @Test
    void unProtectTest() throws Exception {
        String command = "protect " + tempPath + TestConfig.SOURCE_TEST_PATH_1 + " " +
                         tempPath + TestConfig.DEST_TEST_PATH_1 + " " +
                         tempPath + TestConfig.PRIVATE_KEY_TEST_PATH_1 + " " +
                         tempPath + TestConfig.SECRET_SESSION_KEY_TEST_PATH_1 + "\n" +
                         "unprotect " + tempPath + TestConfig.DEST_TEST_PATH_1 + " " +
                         tempPath + TestConfig.DEST_TEST_PATH_1 + "1" + " " +
                         tempPath + TestConfig.PUBLIC_KEY_TEST_PATH_1 + " " +
                         tempPath + TestConfig.SECRET_SESSION_KEY_TEST_PATH_1 + "\n" +
                         "exit";

        ByteArrayInputStream is = new ByteArrayInputStream(command.getBytes());
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        System.setIn(is);
        System.setOut(new PrintStream(os));

        Cli.main(new String[] { secretKeyFile.toString() });

        assertTrue(Files.exists(Path.of(tempPath + TestConfig.DEST_TEST_PATH_1)));
        assertTrue(Files.exists(Path.of(tempPath + TestConfig.DEST_TEST_PATH_1 + "1")));
        assertEquals(TestConfig.SOURCE_1_JSON, Files.readString(Path.of(tempPath + TestConfig.DEST_TEST_PATH_1 + "1")));
    }

    @Test
    void unProtectNonExistentFileTest() {
        String command = "unprotect " + tempPath + TestConfig.NON_EXISTENT_FILE + " " +
                         tempPath + TestConfig.DEST_TEST_PATH_1 + "1" + " " +
                         tempPath + TestConfig.PUBLIC_KEY_TEST_PATH_1 + " " +
                         tempPath + TestConfig.SECRET_SESSION_KEY_TEST_PATH_1 + "\n" +
                         "exit";

        ByteArrayInputStream is = new ByteArrayInputStream(command.getBytes());
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        System.setIn(is);
        System.setOut(new PrintStream(os));

        Cli.main(new String[] { secretKeyFile.toString() });

        String output = os.toString();

        assertFalse(Files.exists(Path.of(tempPath + TestConfig.DEST_TEST_PATH_1)));
        assertTrue(output.contains("Could not unprotect file"));
    }

    @Test
    void unProtectMultipleFilesTest() throws Exception {
        String protect1 = "protect " + tempPath + TestConfig.SOURCE_TEST_PATH_1 + " " +
                          tempPath + TestConfig.DEST_TEST_PATH_1 + " " +
                          tempPath + TestConfig.PRIVATE_KEY_TEST_PATH_1 + " " +
                          tempPath + TestConfig.SECRET_SESSION_KEY_TEST_PATH_1 + "\n";

        String protect2 = "protect " + tempPath + TestConfig.SOURCE_TEST_PATH_1 + " " +
                          tempPath + TestConfig.DEST_TEST_PATH_1 + "1" + " " +
                          tempPath + TestConfig.PRIVATE_KEY_TEST_PATH_1 + " " +
                          tempPath + TestConfig.SECRET_SESSION_KEY_TEST_PATH_1 + "\n";

        String unprotect1 = "unprotect " + tempPath + TestConfig.DEST_TEST_PATH_1 + " " +
                            tempPath + TestConfig.DEST_TEST_PATH_1 + "2" + " " +
                            tempPath + TestConfig.PUBLIC_KEY_TEST_PATH_1 + " " +
                            tempPath + TestConfig.SECRET_SESSION_KEY_TEST_PATH_1 + "\n";

        String unprotect2 = "unprotect " + tempPath + TestConfig.DEST_TEST_PATH_1 + "1" + " " +
                            tempPath + TestConfig.DEST_TEST_PATH_1 + "3" + " " +
                            tempPath + TestConfig.PUBLIC_KEY_TEST_PATH_1 + " " +
                            tempPath + TestConfig.SECRET_SESSION_KEY_TEST_PATH_1 + "\n";

        String command = protect1 + protect2 + unprotect2 + unprotect1 + "exit";

        ByteArrayInputStream is = new ByteArrayInputStream(command.getBytes());
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        System.setIn(is);
        System.setOut(new PrintStream(os));

        Cli.main(new String[] { secretKeyFile.toString() });

        assertTrue(Files.exists(Path.of(tempPath + TestConfig.DEST_TEST_PATH_1)));
        assertTrue(Files.exists(Path.of(tempPath + TestConfig.DEST_TEST_PATH_1 + "1")));
        assertTrue(Files.exists(Path.of(tempPath + TestConfig.DEST_TEST_PATH_1 + "2")));
        assertTrue(Files.exists(Path.of(tempPath + TestConfig.DEST_TEST_PATH_1 + "3")));
        assertEquals(TestConfig.SOURCE_1_JSON, Files.readString(Path.of(tempPath + TestConfig.DEST_TEST_PATH_1 + "3")));
        assertEquals(TestConfig.SOURCE_1_JSON, Files.readString(Path.of(tempPath + TestConfig.DEST_TEST_PATH_1 + "2")));
    }

    @Test
    void unProtectWrongKeysTest() throws Exception {
        String command = "protect " + tempPath + TestConfig.SOURCE_TEST_PATH_1 + " " +
                         tempPath + TestConfig.DEST_TEST_PATH_1 + " " +
                         tempPath + TestConfig.PRIVATE_KEY_TEST_PATH_1 + " " +
                         tempPath + TestConfig.SECRET_SESSION_KEY_TEST_PATH_1 + "\n" +
                         "unprotect " + tempPath + TestConfig.DEST_TEST_PATH_1 + " " +
                         tempPath + TestConfig.DEST_TEST_PATH_1 + "1" + " " +
                         tempPath + TestConfig.PUBLIC_KEY_TEST_PATH_1 + " " +
                         tempPath + TestConfig.PRIVATE_KEY_TEST_PATH_1 + "\n" +
                         "exit";

        ByteArrayInputStream is = new ByteArrayInputStream(command.getBytes());
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        System.setIn(is);
        System.setOut(new PrintStream(os));

        Cli.main(new String[] { secretKeyFile.toString() });

        String output = os.toString();

        assertTrue(Files.exists(Path.of(tempPath + TestConfig.DEST_TEST_PATH_1)));
        assertFalse(Files.exists(Path.of(tempPath + TestConfig.DEST_TEST_PATH_1 + "1")));
        assertTrue(output.contains("Could not unprotect file"));
    }

    @Test
    void unProtectWrongNumArgsTest() {
        String command = "unprotect" + "\n" +
                         "exit";

        ByteArrayInputStream is = new ByteArrayInputStream(command.getBytes());
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        System.setIn(is);
        System.setOut(new PrintStream(os));

        Cli.main(new String[] { secretKeyFile.toString() });

        String output = os.toString();
        assertTrue(output.contains("Usage: (blingbank) unprotect"));
    }
}
