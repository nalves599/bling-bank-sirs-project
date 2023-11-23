package pt.ulisboa.tecnico;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pt.ulisboa.tecnico.aux.Constants;

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
    }
    @Test
    public void protectFile(){
        Library lib = new Library();
        lib.protect(tempPath + Constants.SOURCE_TEST_PATH_1, tempPath + Constants.DEST_TEST_PATH_1);
    }

    @Test
    public void protectNonExistentFile(){
        Library lib = new Library();
        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> lib.protect(Constants.NON_EXISTENT_FILE, tempPath + Constants.DEST_TEST_PATH_1));
        assertEquals("Input file does not exist", exception.getMessage());    }
}
