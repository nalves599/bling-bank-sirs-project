import { Commands } from "./commands";

const mainArgs = process.argv.slice(2);
const command = mainArgs[0];

const commands = new Commands();

switch (command) {
  case "help":
  case "h":
    commands.help(mainArgs);
    break;

  case "protect":
  case "p":
    commands.protect(mainArgs);
    break;

  case "check":
  case "c":
    commands.check(mainArgs);
    break;

  case "unprotect":
  case "u":
    commands.unprotect(mainArgs);
    break;

  default:
    commands.unknown();
    break;
}
