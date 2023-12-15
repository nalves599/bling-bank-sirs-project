import { Utils } from "./utils";
import {ProtectProps} from "../../libraryTS/src/utils/crypto";

export class Commands {

    help(args: string[]): void {
        const help = "Available commands:\n\n" +
            "./blingbank help: display all available commands and their description\n\n\n" +
            "./blingbank protect: protect a message\n\n" +
            "Usage: ./blingbank protect <input-file> <output-file> <aes-key> <priv-key>\n\n\n" +
            "./blingbank check: check if a message is protected\n\n" +
            "Usage: ./blingbank check <input-file> <receiver-public-key> <session-secret-key>\n\n\n" +
            "./blingbank unprotect: unprotect a message\n\n" +
            "Usage: ./blingbank unprotect <input-file> <output-file> <receiver-public-key> <session-secret-key>\n\n\n" +
            "./blingbank timeout: set the timeout for the nonces (in seconds)\n\n" +
            "Usage: ./blingbank timeout <seconds>";

        if (args.length != 1) {
            console.log("Usage: ./blingbank help");
            return
        }

        console.log(help);
    }

    protect(args: string[]): void {
        if (args.length != 5) {
            console.log("Usage: ./blingbank protect <input-file> <output-file> <aes-key> <priv-key>");
            return;
        }

        const inputPath = args[1];
        const outputPath = args[2];
        const aesKeyPath = args[3];
        const privKeyPath = args[4];

        try {
            const input = Utils.readFile(inputPath) ?? (() => { throw new Error("Could not read input file"); })();
            const privateKey = Utils.readFile(aesKeyPath) ?? (() => { throw new Error("Could not read public key file"); })();
            const sessionSecretKey = Utils.readFile(privKeyPath) ?? (() => { throw new Error("Could not read Session Secret Key file"); });

            const protectProps : ProtectProps = {
              aesKey: ,
                hmacKey: null,
                signingKey: privateKey,
                nonce: null
            }

        } catch (error: any) {
            console.log("Could not protect file " + inputPath + ": " + error.message);
        }

    }
}