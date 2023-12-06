package pt.ulisboa.tecnico;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pt.ulisboa.tecnico.auxTests.TestConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HelpTest {
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

    @Test
    void helpTest() throws Exception {
        String command = """
            help
            exit
            """;

        ByteArrayInputStream is = new ByteArrayInputStream(command.getBytes());
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        System.setIn(is);
        System.setOut(new PrintStream(os));

        Cli.main(new String[] { secretKeyFile.toString() });

        String output = os.toString();
        assertTrue(output.contains("Available commands:"));
    }

    @Test
    void multipleHelpTest() throws Exception {
        String command = """
            help
            help
            help
            exit
            """;

        ByteArrayInputStream is = new ByteArrayInputStream(command.getBytes());
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        System.setIn(is);
        System.setOut(new PrintStream(os));

        Cli.main(new String[] { secretKeyFile.toString() });

        String output = os.toString();
        assertTrue(output.contains("Available commands:"));
    }

    @Test
    void invalidCommandTest() throws Exception {
        String command = """
            invalid
            exit
            """;

        ByteArrayInputStream is = new ByteArrayInputStream(command.getBytes());
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        System.setIn(is);
        System.setOut(new PrintStream(os));

        Cli.main(new String[] { secretKeyFile.toString() });

        String output = os.toString();
        assertTrue(output.contains("Invalid command"));
    }
}
