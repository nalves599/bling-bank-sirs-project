package pt.ulisboa.tecnico;

import org.junit.Test;
import pt.ulisboa.tecnico.aux.Constants;

public class UnprotectTest
{
    @Test
    public void unprotectFile(){
        Library lib = new Library();
        lib.protect(Constants.SOURCE_TEST_PATH_1, Constants.DEST_TEST_PATH_1);
        lib.unprotect(Constants.DEST_TEST_PATH_1, Constants.DEST_TEST_PATH_1); // TODO: change to DEST_TEST_PATH_1
    }

    @Test
    public void unprotectNonExistentFile(){
        Library lib = new Library();
        lib.unprotect(Constants.NON_EXISTENT_FILE, Constants.DEST_TEST_PATH_1);
    }
}
