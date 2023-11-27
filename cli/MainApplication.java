package pt.ulisboa.tecnico;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import Commands;

public class MainApplication {
    public static void main(String[] args) {
        Commands commands = new Commands();

        final PrintStream out = System.out;
        final InputStream in = System.in;

        Scanner scanner = new Scanner(in);

        out.println(commands.start());

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if (line.equals("help")) {
                out.println(commands.help());
            } else {
                out.println("Unknown command");
            }
            out.println();
        }

        scanner.close();
    }
}
