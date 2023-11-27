package pt.ulisboa.tecnico;

public class Commands {

    public final String start = """
            Welcome to BlingBank!
        
        Available commands:
          (blingbank) help
          (blingbank) protect
          (blingbank) check
          (blingbank) unprotect
        
        Type a command to proceed: 
        """;

    public final String help = """
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