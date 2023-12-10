package pt.ulisboa.tecnico;

class Commands {

    final String start = """
        Welcome to BlingBank!

        Available commands:
          (blingbank) help
          (blingbank) protect
          (blingbank) check
          (blingbank) unprotect
          (blingbank) exit

        Type a command to proceed:
        (blingbank)\s""";

    private final Library library;

    Commands(Library library) {
        this.library = library;
    }

    void help() {
        String help = """
            \nAvailable commands:
              (blingbank) help: display all available commands and their description
              (blingbank) protect: protect a message
                          usage: (blingbank) protect <input-file> <output-file> <...>
              (blingbank) check: check if a message is protected
                          usage: (blingbank) check <input-file>
              (blingbank) unprotect: unprotect a message
                          usage: (blingbank) unprotect <input-file> <output-file> <...>
            """;
        System.out.print(help);
    }

    void protect(String[] args) {
        if (args.length != 4) {
            System.out.println("Usage: (blingbank) protect <input-file> <output-file> <secret_key> <SessionSecret_key>");
            return;
        }
        String inputFilePath = args[1];
        String outputFilePath = args[2];
        String secretKeyFilePath = args[3];
        String SessionSecretKeyFilePath = args[4];

        // more args needed? - TODO
        try {
            byte[] input = Utils.readFile(inputFilePath).orElseThrow(() -> new Exception("Could not read file"));
            byte[] secretKey = Utils.readFile(secretKeyFilePath).orElseThrow(() -> new Exception("Could not read file"));
            byte[] SessionSecretKey = Utils.readFile(SessionSecretKeyFilePath).orElseThrow(() -> new Exception("Could not read file"));
            library.setPrivateKey(secretKey);
            library.setSecretSessionKey(SessionSecretKey);
            byte[] output = library.protect(input).getOrElseThrow((r) -> new Exception(r));
            Utils.writeFile(outputFilePath, output);
        } catch (Exception e) {
            System.out.println("Could not protect file " + inputFilePath);
            System.out.println("Error: " + e.getMessage());
        }
    }

    void check(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: (blingbank) check <input-file> <public_key> <SessionSecret_key>");
            return;
        }
        String inputFilePath = args[1];
        String publicKeyFilePath = args[2];
        String SessionSecretKeyFilePath = args[3];

        try {
            byte[] input = Utils.readFile(inputFilePath).orElseThrow(() -> new Exception("Could not read file"));
            byte[] publicKey = Utils.readFile(publicKeyFilePath).orElseThrow(() -> new Exception("Could not read file"));
            byte[] SessionSecretKey = Utils.readFile(SessionSecretKeyFilePath).orElseThrow(() -> new Exception("Could not read file"));
            library.setReceiverPublicKey(publicKey);
            library.setSecretSessionKey(SessionSecretKey);
            boolean result = library.check(input);
            System.out.println(result ? "File protected" : "File not protected");
        } catch (Exception e) {
            System.out.println("Could not check file " + inputFilePath);
            System.out.println("Error: " + e.getMessage());
        }
    }

    void unprotect(String[] args) {
        // TODO: check number of args
        if (args.length < 3) {
            System.out.println("Usage: (blingbank) unprotect <input-file> <output-file> <...>");
            return;
        }
        String inputFilePath = args[1];
        String outputFilePath = args[2];
        String publicKeyFilePath = args[2];
        String SessionSecretKeyFilePath = args[3];

        // more args needed? - TODO
        try {
            byte[] input = Utils.readFile(inputFilePath).orElseThrow(() -> new Exception("Could not read file"));
            byte[] publicKey = Utils.readFile(publicKeyFilePath).orElseThrow(() -> new Exception("Could not read file"));
            byte[] SessionSecretKey = Utils.readFile(SessionSecretKeyFilePath).orElseThrow(() -> new Exception("Could not read file"));
            library.setReceiverPublicKey(publicKey);
            library.setSecretSessionKey(SessionSecretKey);
            byte[] output = library.unprotect(input).getOrElseThrow((r) -> new Exception(r));
            Utils.writeFile(outputFilePath, output);
        } catch (Exception e) {
            System.out.println("Could not unprotect file " + inputFilePath);
            System.out.println("Error: " + e.getMessage());
        }
    }
}
