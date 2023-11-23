package pt.ulisboa.tecnico;

import org.junit.Test;
import org.junit.jupiter.api.io.TempDir;
import pt.ulisboa.tecnico.aux.Constants;

import java.io.File;

public class ProtectTest
{
    @TempDir
    File tempFolder;

    @Test
    public void protectFile(){

        Library lib = new Library();
        lib.protect(Constants.SOURCE_TEST_PATH_1, Constants.DEST_TEST_PATH_1);
    }

    @Test
    public void protectNonExistentFile(){
        Library lib = new Library();
        lib.protect(Constants.NON_EXISTENT_FILE, Constants.DEST_TEST_PATH_1);
    }
}
