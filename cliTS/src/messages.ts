export const helpUsage = "Usage: ./blingbank help";

export const protectUsage =
  "Usage: ./blingbank protect <input-file> <output-file> <aes-key> <priv-key>";

export const checkUsage =
  "Usage: ./blingbank check <input-file> <receiver-public-key> <session-secret-key>";

export const unprotectUsage =
  "Usage: ./blingbank unprotect <input-file> <output-file> <receiver-public-key> <session-secret-key>";

export const timeoutUsage = "Usage: ./blingbank timeout <seconds>";

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
  "\n\n\n" +
  "./blingbank timeout: set the timeout for the nonces (in seconds)\n\n" +
  timeoutUsage +
  "\n";
