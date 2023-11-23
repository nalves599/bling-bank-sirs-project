package pt.ulisboa.tecnico;

import org.junit.Test;
import org.junit.jupiter.api.io.TempDir;
import pt.ulisboa.tecnico.aux.Constants;

import java.io.File;

public class UnprotectTest
{
    @TempDir
    File tempFolder;
    @Test
    public void unprotectFile(){
        Library lib = new Library();
        lib.protect(Constants.SOURCE_TEST_PATH_1, tempFolder.getAbsolutePath() + Constants.DEST_TEST_PATH_1);
        lib.unprotect(Constants.DEST_TEST_PATH_1, tempFolder.getAbsolutePath() + Constants.DEST_TEST_PATH_1); // TODO: change to DEST_TEST_PATH_1
    }

    @Test
    public void unprotectNonExistentFile(){
        Library lib = new Library();
        lib.unprotect(Constants.NON_EXISTENT_FILE, tempFolder.getAbsolutePath() + Constants.DEST_TEST_PATH_1);
    }
}
