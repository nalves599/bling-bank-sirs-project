package pt.ulisboa.tecnico;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pt.ulisboa.tecnico.auxTests.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProtectTest
{
    @TempDir
    static File tempFolder;
    static String tempPath;
    @BeforeAll
    static void init() throws IOException {
        tempPath = tempFolder.getAbsolutePath() + "/";

        Path tempFile = Files.createFile(tempFolder.toPath().resolve(Constants.SOURCE_TEST_PATH_1));
        Files.write(tempFile, Constants.SOURCE_1_JSON.getBytes());

        Path secretKeyFile = Files.createFile(tempFolder.toPath().resolve(Constants.SECRET_KEY_TEST_PATH_1));
        Files.write(secretKeyFile, Constants.SECRET_KEY_1.getBytes());
    }
    @Test
    public void protectFile() throws Exception {
        Library lib = new Library(tempPath + Constants.SECRET_KEY_TEST_PATH_1);
        byte[] output = lib.protect(Constants.SOURCE_1_JSON.getBytes(), Constants.PUBLIC_KEY_1);

    }
}
