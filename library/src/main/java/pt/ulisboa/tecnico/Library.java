package pt.ulisboa.tecnico;

import java.io.File;

public class Library
{
    public void protect(String input, String output) {
        if (FileDoesNotExist(input)) {
            throw new IllegalArgumentException("Input file does not exist");
        }
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void unprotect(String input, String output) {
        if (FileDoesNotExist(input)) {
            throw new IllegalArgumentException("Input file does not exist");
        }
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public boolean check(String input) {
        if (FileDoesNotExist(input)) {
            throw new IllegalArgumentException("Input file does not exist");
        }
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private boolean FileDoesNotExist(String input) {
        File f = new File(input);
        return !f.exists() || f.isDirectory();
    }
}
