package pt.ulisboa.tecnico;

public class Commands {

    public final String start = """
        Welcome to BlingBank!

        Available commands:
          (blingbank) help
          (blingbank) protect
          (blingbank) check
          (blingbank) unprotect
          (blingbank) exit

        Type a command to proceed:
        (blingbank)\s""";

    public final String help = """
        \nAvailable commands:
          (blingbank) help: display all available commands and their description
          (blingbank) protect: protect a message
                      usage: (blingbank) protect <input-file> <output-file> <...>
          (blingbank) check: check if a message is protected
                      usage: (blingbank) check <input-file>
          (blingbank) unprotect: unprotect a message
                      usage: (blingbank) unprotect <input-file> <output-file> <...>
        """;
    private final Library library;

    public Commands(Library library) {
        this.library = library;
    }

    public void help() {
        System.out.print(help);
    }

    public void protect(String[] args) {
        // TODO: check number of args
        if (args.length < 3) {
            System.out.println("Usage: (blingbank) protect <input-file> <output-file> <...>");
            return;
        }
        String inputFilePath = args[1];
        String outputFilePath = args[2];

        // more args needed? - TODO
        try {
            byte[] input = Utils.readFile(inputFilePath).orElseThrow(() -> new Exception("Could not read file"));
            byte[] output = library.protect(input).getOrElseThrow((r) -> new Exception(r));
            Utils.writeFile(outputFilePath, output);
        } catch (Exception e) {
            System.out.println("Could not protect file " + inputFilePath);
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
            byte[] input = Utils.readFile(inputFilePath).orElseThrow(() -> new Exception("Could not read file"));
            boolean result = library.check(input);
            System.out.println(result ? "File protected" : "File not protected");
        } catch (Exception e) {
            System.out.println("Could not check file " + inputFilePath);
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void unprotect(String[] args) {
        // TODO: check number of args
        if (args.length < 3) {
            System.out.println("Usage: (blingbank) unprotect <input-file> <output-file> <...>");
            return;
        }
        String inputFilePath = args[1];
        String outputFilePath = args[2];

        // more args needed? - TODO
        try {
            byte[] input = Utils.readFile(inputFilePath).orElseThrow(() -> new Exception("Could not read file"));
            byte[] output = library.unprotect(input).getOrElseThrow((r) -> new Exception(r));
            Utils.writeFile(outputFilePath, output);
        } catch (Exception e) {
            System.out.println("Could not unprotect file " + inputFilePath);
            System.out.println("Error: " + e.getMessage());
        }
    }
}
