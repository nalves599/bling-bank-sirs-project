import { Commands } from "./commands";

const mainArgs = process.argv.slice(2);
const command = mainArgs[0];

const commands = new Commands();

switch (command) {
    case "help":
        commands.help(mainArgs);
        break;

    case "protect":
        commands.protect(mainArgs);
        break;

    case "check":
        commands.check(mainArgs);
        break;

    case "unprotect":
        commands.unprotect(mainArgs);
        break;

    case "timeout":
        commands.timeout(mainArgs);
        break;

    default:
        commands.unknown();
        break;
}