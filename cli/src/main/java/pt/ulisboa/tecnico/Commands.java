package pt.ulisboa.tecnico;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

public class Commands {

    private Library library;
    public final String start = """
            Welcome to BlingBank!
        
        Available commands:
          (blingbank) help
          (blingbank) protect
          (blingbank) check
          (blingbank) unprotect
          (blingbank) exit
        
        Type a command to proceed:
        (blingbank) 
        """;

    public final String help() {
        return """
            Available commands:
              (blingbank) help: display all available commands and their description
              (blingbank) protect: protect a message
                          usage: (blingbank) protect <input-file> <output-file> <...>
              (blingbank) check: check if a message is protected
                          usage: (blingbank) check <input-file>
              (blingbank) unprotect: unprotect a message
                          usage: (blingbank) unprotect <input-file> <output-file> <...>
            """;
    }

    public Commands(Library library) {
        this.library = library;
    }

    public void protect(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: (blingbank) protect <input-file> <output-file> <...>");
            return;
        }
        String inputFilePath = args[1];
        String outputFilePath = args[2];

        // more args needed? - TODO

        try {
            byte[] input = Utils.readFile(inputFilePath);
            byte[] output = library.protect(input);
            System.out.println(output.length);
            Utils.writeFile(outputFilePath, output);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void check(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: (blingbank) check <input-file>");
            return;
        }
        String inputFilePath = args[1];

        try {
            byte[] input = Utils.readFile(inputFilePath);
            boolean result = library.check(input);
            System.out.println(result ? "File protected" : "File not protected");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void unprotect(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: (blingbank) unprotect <input-file> <output-file> <...>");
            return;
        }
        String inputFilePath = args[1];
        String outputFilePath = args[2];

        // more args needed? - TODO

        try {
            byte[] input = Utils.readFile(inputFilePath);
            byte[] output = library.unprotect(input);
            Utils.writeFile(outputFilePath, output);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
