package pt.ulisboa.tecnico;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pt.ulisboa.tecnico.auxTests.TestConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckTest {
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

    @Test
    void checkTest() {
        String protect = "protect " + tempPath + TestConfig.SOURCE_TEST_PATH_1 + " " +
                         tempPath + TestConfig.DEST_TEST_PATH_1 + " " +
                         tempPath + TestConfig.PRIVATE_KEY_TEST_PATH_1 + " " +
                         tempPath + TestConfig.SECRET_SESSION_KEY_TEST_PATH_1 + "\n";

        String check = "check " + tempPath + TestConfig.DEST_TEST_PATH_1 + " " +
                       tempPath + TestConfig.PUBLIC_KEY_TEST_PATH_1 + " " +
                       tempPath + TestConfig.SECRET_SESSION_KEY_TEST_PATH_1 + "\n" +
                       "exit";

        String command = protect + check;

        ByteArrayInputStream is = new ByteArrayInputStream(command.getBytes());
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        System.setIn(is);
        System.setOut(new PrintStream(os));

        Cli.main(new String[] { secretKeyFile.toString() });

        assertTrue(Files.exists(Path.of(tempPath + TestConfig.DEST_TEST_PATH_1)));
        assertTrue(os.toString().contains("File protected"));
    }

    @Test
    void checkNonExistentFileTest() {
        String command = "check " + tempPath + TestConfig.NON_EXISTENT_FILE + " " +
                         tempPath + TestConfig.PUBLIC_KEY_TEST_PATH_1 + " " +
                         tempPath + TestConfig.SECRET_SESSION_KEY_TEST_PATH_1 + "\n" +
                         "exit";

        ByteArrayInputStream is = new ByteArrayInputStream(command.getBytes());
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        System.setIn(is);
        System.setOut(new PrintStream(os));

        Cli.main(new String[] { secretKeyFile.toString() });

        String output = os.toString();
        assertTrue(output.contains("Could not check file"));
    }

    @Test
    void checkMultipleFilesTest() {
        String protect = "protect " + tempPath + TestConfig.SOURCE_TEST_PATH_1 + " " +
                         tempPath + TestConfig.DEST_TEST_PATH_1 + " " +
                         tempPath + TestConfig.PRIVATE_KEY_TEST_PATH_1 + " " +
                         tempPath + TestConfig.SECRET_SESSION_KEY_TEST_PATH_1 + "\n";

        String protect2 = "protect " + tempPath + TestConfig.SOURCE_TEST_PATH_1 + " " +
                          tempPath + TestConfig.DEST_TEST_PATH_1 + "1" + " " +
                          tempPath + TestConfig.PRIVATE_KEY_TEST_PATH_1 + " " +
                          tempPath + TestConfig.SECRET_SESSION_KEY_TEST_PATH_1 + "\n";

        String check = "check " + tempPath + TestConfig.DEST_TEST_PATH_1 + " " +
                       tempPath + TestConfig.PUBLIC_KEY_TEST_PATH_1 + " " +
                       tempPath + TestConfig.SECRET_SESSION_KEY_TEST_PATH_1 + "\n";

        String check2 = "check " + tempPath + TestConfig.DEST_TEST_PATH_1 + "1" + " " +
                        tempPath + TestConfig.PUBLIC_KEY_TEST_PATH_1 + " " +
                        tempPath + TestConfig.SECRET_SESSION_KEY_TEST_PATH_1 + "\n";

        String command = protect + protect2 + check2 + check + "exit";

        ByteArrayInputStream is = new ByteArrayInputStream(command.getBytes());
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        System.setIn(is);
        System.setOut(new PrintStream(os));

        Cli.main(new String[] { secretKeyFile.toString() });

        String output = os.toString();
        assertTrue(output.contains("File protected"));
    }

    @Test
    void checkWithTimeout() {
        String timeout = "timeout -10000\n";

        String protect = "protect " + tempPath + TestConfig.SOURCE_TEST_PATH_1 + " " +
                         tempPath + TestConfig.DEST_TEST_PATH_1 + " " +
                         tempPath + TestConfig.PRIVATE_KEY_TEST_PATH_1 + " " +
                         tempPath + TestConfig.SECRET_SESSION_KEY_TEST_PATH_1 + "\n";

        String check = "check " + tempPath + TestConfig.DEST_TEST_PATH_1 + " " +
                       tempPath + TestConfig.PUBLIC_KEY_TEST_PATH_1 + " " +
                       tempPath + TestConfig.SECRET_SESSION_KEY_TEST_PATH_1 + "\n" +
                       "exit";

        String command = timeout + protect + check;

        ByteArrayInputStream is = new ByteArrayInputStream(command.getBytes());
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        System.setIn(is);
        System.setOut(new PrintStream(os));

        Cli.main(new String[] { secretKeyFile.toString() });

        String output = os.toString();
        assertTrue(output.contains("File not protected"));
    }

    @Test
    void checkWrongNumArgsTest() {
        String command = "check" + "\n" +
                         "exit";

        ByteArrayInputStream is = new ByteArrayInputStream(command.getBytes());
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        System.setIn(is);
        System.setOut(new PrintStream(os));

        Cli.main(new String[] { secretKeyFile.toString() });

        String output = os.toString();
        assertTrue(output.contains("Usage: (blingbank) check"));
    }
}
