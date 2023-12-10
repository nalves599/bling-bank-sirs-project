package pt.ulisboa.tecnico;

class Commands {

    final String start = """
        Welcome to BlingBank!

        Available commands:
          (blingbank) help
          (blingbank) protect
          (blingbank) check
          (blingbank) unprotect
          (blingbank) timeout
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
                          Usage: (blingbank) protect <input-file> <output-file> <private-key> <session-secret-key>
              (blingbank) check: check if a message is protected
                          Usage: (blingbank) check <input-file> <receiver-public-key> <session-secret-key>
              (blingbank) unprotect: unprotect a message
                          Usage: (blingbank) unprotect <input-file> <output-file> <receiver-public-key> <session-secret-key>
              (blingbank) timeout: set the timeout for the nonces (in seconds)
                          Usage: (blingbank) timeout <seconds>
            """;
        System.out.print(help);
    }

    void protect(String[] args) {
        if (args.length != 5) {
            System.out.println(
                "Usage: (blingbank) protect <input-file> <output-file> <private-key> <session-secret-key>");
            return;
        }
        String inputFilePath = args[1];
        String outputFilePath = args[2];
        String privateKeyPath = args[3];
        String sessionSecretKeyFilePath = args[4];

        try {
            byte[] input = Utils.readFile(inputFilePath).orElseThrow(() -> new Exception("Could not read input file"));
            byte[] privateKey = Utils.readFile(privateKeyPath).orElseThrow(() -> new Exception(
                "Could not read private key file"));
            byte[] sessionSecretKey = Utils.readFile(sessionSecretKeyFilePath).orElseThrow(() -> new Exception(
                "Could not read Session Secret Key file"));
            library.setPrivateKey(privateKey);
            library.setSecretSessionKey(sessionSecretKey);
            byte[] output = library.protect(input).get();
            Utils.writeFile(outputFilePath, output);
        } catch (Exception e) {
            System.out.println("Could not protect file " + inputFilePath);
            System.out.println("Error: " + e.getMessage());
        }
    }

    void check(String[] args) {
        if (args.length != 4) {
            System.out.println("Usage: (blingbank) check <input-file> <receiver-public-key> <session-secret-key>");
            return;
        }
        String inputFilePath = args[1];
        String receiverPublicKeyFilePath = args[2];
        String sessionSecretKeyFilePath = args[3];

        try {
            byte[] input = Utils.readFile(inputFilePath).orElseThrow(() -> new Exception("Could not read input file"));
            byte[] receiverPublicKey = Utils.readFile(receiverPublicKeyFilePath).orElseThrow(() -> new Exception(
                "Could not read public key file"));
            byte[] sessionSecretKey = Utils.readFile(sessionSecretKeyFilePath).orElseThrow(() -> new Exception(
                "Could not read Session Secret Key file"));
            library.setReceiverPublicKey(receiverPublicKey);
            library.setSecretSessionKey(sessionSecretKey);
            boolean result = library.check(input);
            System.out.println(result ? "File protected" : "File not protected");
        } catch (Exception e) {
            System.out.println("Could not check file " + inputFilePath);
            System.out.println("Error: " + e.getMessage());
        }
    }

    void unprotect(String[] args) {
        if (args.length != 5) {
            System.out.println(
                "Usage: (blingbank) unprotect <input-file> <output-file> <receiver-public-key> <session-secret-key>");
            return;
        }
        String inputFilePath = args[1];
        String outputFilePath = args[2];
        String receiverPublicKeyFilePath = args[3];
        String sessionSecretKeyFilePath = args[4];

        try {
            byte[] input = Utils.readFile(inputFilePath).orElseThrow(() -> new Exception("Could not read input file"));
            byte[] receiverPublicKey = Utils.readFile(receiverPublicKeyFilePath).orElseThrow(() -> new Exception(
                "Could not read public key file"));
            byte[] sessionSecretKey = Utils.readFile(sessionSecretKeyFilePath).orElseThrow(() -> new Exception(
                "Could not read Session Secret Key file"));
            library.setReceiverPublicKey(receiverPublicKey);
            library.setSecretSessionKey(sessionSecretKey);
            byte[] output = library.unprotect(input).get();
            Utils.writeFile(outputFilePath, output);
        } catch (Exception e) {
            System.out.println("Could not unprotect file " + inputFilePath);
            System.out.println("Error: " + e.getMessage());
        }
    }

    void timeout(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: (blingbank) timeout <seconds>");
            return;
        }
        String seconds = args[1];
        try {
            library.setTimeout(Integer.parseInt(seconds) * 1000);
        } catch (Exception e) {
            System.out.println("Could not set timeout");
            System.out.println("Error: " + e.getMessage());
        }
    }
}
