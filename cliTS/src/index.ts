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
        if (mainArgs.length == 4) {
            const inputPath = mainArgs[1];
            const receiverPublicKeyPath = mainArgs[2];
            const sessionSecretKeyPath = mainArgs[3];
            console.log("Message is protected");
        } else {
            console.log("Invalid number of arguments.\nUsage: ./blingbank check <input-file> <receiver-public-key> <session-secret-key>");
        }
        break;

    case "unprotect":
        if (mainArgs.length == 5) {
            const inputPath = mainArgs[1];
            const outputPath = mainArgs[2];
            const receiverPublicKeyPath = mainArgs[3];
            const sessionSecretKeyPath = mainArgs[4];
            console.log("Message unprotected");
        } else {
            console.log("Invalid number of arguments.\nUsage: ./blingbank unprotect <input-file> <output-file> <receiver-public-key> <session-secret-key>");
        }
        break;

    default:
        console.log("Unknown command. Type ./blingbank help to see all available commands.");
        break;
}