export const helpUsage = "Usage: ./blingbank help";

export const protectUsage =
  "Usage: ./blingbank protect <input-file> <output-file> <aes-key> <sign-key> [--hmac]";

export const checkUsage =
  "Usage: ./blingbank check <input-file> <session-secret-key> <verify-key> [--hmac]";

export const unprotectUsage =
  "Usage: ./blingbank unprotect <input-file> <output-file> <session-secret-key> <verify-key> [--hmac]";

export const unknownCommand =
  "Unknown command. Type ./blingbank help to display all available commands and their description";

export const help =
  "Available commands:\n\n" +
  helpUsage +
  ": display all available commands and their description\n\n\n" +
  "./blingbank protect: protect a message\n\n" +
  protectUsage +
  "\n\n\n" +
  "./blingbank check: check if a message is protected\n\n" +
  checkUsage +
  "\n\n\n" +
  "./blingbank unprotect: unprotect a message\n\n" +
  unprotectUsage +
  "\n";
