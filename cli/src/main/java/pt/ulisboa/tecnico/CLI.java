package pt.ulisboa.tecnico;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class CLI {
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Only one argument is needed: path to secret key");
            return;
        }
        String secretKeyPath = args[0]; // path to secret key

        try {
            Library library = new Library(secretKeyPath);

            Commands commands = new Commands(library);

            final PrintStream out = System.out;
            final InputStream in = System.in;

            Scanner scanner = new Scanner(in);

            out.println(commands.start);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                String[] line_args = line.split(" ");

                // line_args[0] is the command, the rest are the arguments to the command
                switch (line_args[0]) {
                    case "help":
                        out.println(commands.help());
                        break;
                    case "protect":
                        commands.protect(line_args);
                        break;
                    case "check":
                        commands.check(line_args);
                        break;
                    case "unprotect":
                        commands.unprotect(line_args);
                        break;
                    case "exit":
                        out.println("Exiting...");
                        return;
                    default:
                        out.println("Invalid command");
                        break;
                }
                out.print("\nType a command to proceed:\n(blingbank) ");
            }
            scanner.close();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
