package pt.ulisboa.tecnico;

import java.io.PrintStream;
import java.util.Scanner;

public class Cli {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Only one argument is allowed: path to secret key");
            return;
        }

        String secretKeyPath = args[0]; // path to secret key

        Library library;
        try {
            library = new Library(secretKeyPath);
        } catch (Exception e) {
            System.out.println("Could not initialize library: " + e.getMessage());
            return;
        }

        Commands commands = new Commands(library);

        final PrintStream out = System.out;
        Scanner scanner = new Scanner(System.in);

        out.print(commands.start);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            String[] line_args = line.split(" ");

            // line_args[0] is the command, the rest are the arguments to the command
            switch (line_args[0]) {
                case "help":
                case "h":
                    commands.help();
                    break;
                case "protect":
                case "p":
                    commands.protect(line_args);
                    break;
                case "check":
                case "c":
                    commands.check(line_args);
                    break;
                case "unprotect":
                case "u":
                    commands.unprotect(line_args);
                    break;
                case "timeout":
                case "t":
                    commands.timeout(line_args);
                    break;
                case "exit":
                case "e":
                    out.println("Exiting...");
                    return;
                default:
                    out.println("Invalid command");
                    break;
            }
            out.print("\nType a command to proceed:\n(blingbank) ");
        }
        scanner.close();
    }
}
