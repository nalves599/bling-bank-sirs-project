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

    @BeforeAll
    static void init() throws Exception {
        tempPath = tempFolder.getAbsolutePath() + "/";

        Path tempFile = Files.createFile(tempFolder.toPath().resolve(TestConfig.SOURCE_TEST_PATH_1));
        Files.write(tempFile, TestConfig.SOURCE_1_JSON.getBytes());

        secretKeyFile = Files.createFile(tempFolder.toPath().resolve(TestConfig.SECRET_KEY_TEST_PATH_1));
        Files.write(secretKeyFile, TestConfig.SECRET_KEY_1.getBytes());
    }

    @BeforeEach
    void setUp() throws Exception {
        Files.deleteIfExists(Path.of(tempPath + TestConfig.DEST_TEST_PATH_1));
        Files.deleteIfExists(Path.of(tempPath + TestConfig.DEST_TEST_PATH_1 + "1"));
    }

    @Test
    void unProtectTest() throws Exception {
        String command = "protect " + tempPath + TestConfig.SOURCE_TEST_PATH_1 + " " +
                         tempPath + TestConfig.DEST_TEST_PATH_1 + "\n" +
                         "unprotect " + tempPath + TestConfig.DEST_TEST_PATH_1 + " " +
                         tempPath + TestConfig.DEST_TEST_PATH_1 + "1" + "\n" +
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
                         tempPath + TestConfig.DEST_TEST_PATH_1 + "\n" +
                         "exit";

        ByteArrayInputStream is = new ByteArrayInputStream(command.getBytes());
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        System.setIn(is);
        System.setOut(new PrintStream(os));

        Cli.main(new String[] { secretKeyFile.toString() });

        String output = os.toString();

        assertFalse(Files.exists(Path.of(tempPath + TestConfig.DEST_TEST_PATH_1)));
        assertTrue(output.contains("Could not unprotect file"));
        assertTrue(output.contains("Error: Could not read file"));
    }

    @Test
    void unProtectMultipleFilesTest() throws Exception {
        String command = "protect " + tempPath + TestConfig.SOURCE_TEST_PATH_1 + " " +
                         tempPath + TestConfig.DEST_TEST_PATH_1 + "\n" +
                         "protect " +
                         tempPath + TestConfig.SOURCE_TEST_PATH_1 + " " +
                         tempPath + TestConfig.DEST_TEST_PATH_1 + "1" + "\n" +
                         "unprotect " + tempPath + TestConfig.DEST_TEST_PATH_1 + "1" + " " +
                         tempPath + TestConfig.DEST_TEST_PATH_1 + "2" + "\n" +
                         "unprotect " + tempPath + TestConfig.DEST_TEST_PATH_1 + " " +
                         tempPath + TestConfig.DEST_TEST_PATH_1 + "3" + "\n" +
                         "exit";

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
    void unProtectWrongNumArgsTest() {
        String command = "unprotect" + "\n" +
                         "exit";

        ByteArrayInputStream is = new ByteArrayInputStream(command.getBytes());
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        System.setIn(is);
        System.setOut(new PrintStream(os));

        Cli.main(new String[] { secretKeyFile.toString() });

        String output = os.toString();
        assertTrue(output.contains("Usage: (blingbank) unprotect <input-file> <output-file> <...>"));
    }
}
