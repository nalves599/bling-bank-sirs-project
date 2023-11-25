package pt.ulisboa.tecnico;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pt.ulisboa.tecnico.aux.Constants;
import pt.ulisboa.tecnico.auxTests.TestConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProtectTest
{
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
        byte[] output = lib.protect(TestConfig.SOURCE_1_JSON.getBytes());
    }
}
