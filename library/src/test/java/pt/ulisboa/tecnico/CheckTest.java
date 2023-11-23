package pt.ulisboa.tecnico;

import org.junit.Test;
import pt.ulisboa.tecnico.aux.Constants;

public class CheckTest
{
    @Test
    public void checkFile(){
        Library lib = new Library();
        lib.check(Constants.SOURCE_TEST_PATH_1);
    }

    @Test
    public void checkNonExistentFile(){
        Library lib = new Library();
        lib.check(Constants.NON_EXISTENT_FILE);
    }
}
