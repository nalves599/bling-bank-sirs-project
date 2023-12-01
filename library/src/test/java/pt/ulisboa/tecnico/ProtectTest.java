package pt.ulisboa.tecnico;

import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pt.ulisboa.tecnico.auxTests.TestConfig;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProtectTest {
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
    public void protectFile() throws Exception {
        Library lib = new Library(tempPath + TestConfig.SECRET_KEY_TEST_PATH_1);
        Either<String, byte[]> output = lib.protect(TestConfig.SOURCE_1_JSON.getBytes());
        assertTrue(output.isRight() && output.get().length > 0);
    }

    @Test
    public void protectEmptyFile() throws Exception {
        Library lib = new Library(tempPath + TestConfig.SECRET_KEY_TEST_PATH_1);
        Either<String, byte[]> output = lib.protect("".getBytes());
        assertTrue(output.isRight() && output.get().length > 0);
    }

    @Test
    public void protectMultipleFiles() throws Exception {
        Library lib = new Library(tempPath + TestConfig.SECRET_KEY_TEST_PATH_1);
        Either<String, byte[]> output1 = lib.protect(TestConfig.SOURCE_1_JSON.getBytes());
        Either<String, byte[]> output2 = lib.protect(TestConfig.SOURCE_1_JSON.getBytes());
        assertTrue(output1.isRight() && output1.get().length > 0);
        assertTrue(output2.isRight() && output2.get().length > 0);
    }
}
