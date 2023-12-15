package pt.ulisboa.tecnico;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pt.ulisboa.tecnico.auxTests.TestConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProtectTest {
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
    void protectTest() {
        String command = "protect " + tempPath + TestConfig.SOURCE_TEST_PATH_1 + " " +
                         tempPath + TestConfig.DEST_TEST_PATH_1 + " " +
                         tempPath + TestConfig.PRIVATE_KEY_TEST_PATH_1 + " " +
                         tempPath + TestConfig.SECRET_SESSION_KEY_TEST_PATH_1 + "\n" +
                         "exit";

        ByteArrayInputStream is = new ByteArrayInputStream(command.getBytes());
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        System.setIn(is);
        System.setOut(new PrintStream(os));

        Cli.main(new String[] { secretKeyFile.toString() });

        assertTrue(Files.exists(Path.of(tempPath + TestConfig.DEST_TEST_PATH_1)));
    }

    @Test
    void protectNonExistentFileTest() {
        String command = "protect " + tempPath + TestConfig.NON_EXISTENT_FILE + " " +
                         tempPath + TestConfig.DEST_TEST_PATH_1 + " " +
                         tempPath + TestConfig.PRIVATE_KEY_TEST_PATH_1 + " " +
                         tempPath + TestConfig.SECRET_SESSION_KEY_TEST_PATH_1 + "\n" +
                         "exit";

        ByteArrayInputStream is = new ByteArrayInputStream(command.getBytes());
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        System.setIn(is);
        System.setOut(new PrintStream(os));

        Cli.main(new String[] { secretKeyFile.toString() });

        String output = os.toString();
        assertTrue(Files.notExists(Path.of(tempPath + TestConfig.DEST_TEST_PATH_1)));
        assertTrue(output.contains("Could not protect file"));
    }

    @Test
    void protectMultipleFilesTest() {
        String command = "protect " + tempPath + TestConfig.SOURCE_TEST_PATH_1 + " " +
                         tempPath + TestConfig.DEST_TEST_PATH_1 + " " +
                         tempPath + TestConfig.PRIVATE_KEY_TEST_PATH_1 + " " +
                         tempPath + TestConfig.SECRET_SESSION_KEY_TEST_PATH_1 + "\n" +
                         "protect " + tempPath + TestConfig.SOURCE_TEST_PATH_1 + " " +
                         tempPath + TestConfig.DEST_TEST_PATH_1 + "1" + " " +
                         tempPath + TestConfig.PRIVATE_KEY_TEST_PATH_1 + " " +
                         tempPath + TestConfig.SECRET_SESSION_KEY_TEST_PATH_1 + "\n" +
                         "exit";

        ByteArrayInputStream is = new ByteArrayInputStream(command.getBytes());
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        System.setIn(is);
        System.setOut(new PrintStream(os));

        Cli.main(new String[] { secretKeyFile.toString() });

        assertTrue(Files.exists(Path.of(tempPath + TestConfig.DEST_TEST_PATH_1)));
        assertTrue(Files.exists(Path.of(tempPath + TestConfig.DEST_TEST_PATH_1 + "1")));
    }

    @Test
    void protectWrongNumArgsTest() {
        String command = "protect " + tempPath + TestConfig.SOURCE_TEST_PATH_1 + "\n" +
                         "exit";

        ByteArrayInputStream is = new ByteArrayInputStream(command.getBytes());
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        System.setIn(is);
        System.setOut(new PrintStream(os));

        Cli.main(new String[] { secretKeyFile.toString() });

        String output = os.toString();
        assertTrue(output.contains("Usage: (blingbank) protect"));
    }
}
