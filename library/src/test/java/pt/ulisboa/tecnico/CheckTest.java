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

public class CheckTest {
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
    public void checkFile() throws Exception {
        Library lib = new Library(tempPath + TestConfig.SECRET_KEY_TEST_PATH_1);
        Either<String, byte[]> output = lib.protect(TestConfig.SOURCE_1_JSON.getBytes());
        assertTrue(lib.check(output.get()));
    }

    @Test
    public void checkTamperedFile() throws Exception {
        Library lib = new Library(tempPath + TestConfig.SECRET_KEY_TEST_PATH_1);
        Either<String, byte[]> output = lib.protect(TestConfig.SOURCE_1_JSON.getBytes());
        output.get()[0] = (byte) (output.get()[0] + 1);
        assertFalse(lib.check(output.get()));
    }
}
