import {
  ProtectProps,
  UnprotectProps,
  protect,
  unprotect,
} from "../../libraryTS/src/utils/crypto";
import {
  bytesToAesKey,
  bytesToSigningKey,
  bytesToVerificationKey,
} from "./converter";
import { Utils } from "./utils";
import {
  help,
  helpUsage,
  protectUsage,
  checkUsage,
  unprotectUsage,
  timeoutUsage,
  unknownCommand,
} from "./messages";

export class Commands {
  help(args: string[]): void {
    if (args.length != 1) {
      console.log(helpUsage);
      return;
    }

    console.log(help);
  }

  async protect(args: string[]): Promise<void> {
    if (args.length != 5) {
      console.log(protectUsage);
      return;
    }

    const inputPath = args[1];
    const outputPath = args[2];
    const aesKeyPath = args[3];
    const signKeyPath = args[4];

    try {
      const input = Utils.readFile(inputPath);
      const aesKey = await bytesToAesKey(Utils.readFile(aesKeyPath));
      const signKey = await bytesToSigningKey(Utils.readFile(signKeyPath));

      const protectProps: ProtectProps = {
        aesKey: aesKey,
        signingKey: signKey,
      };

      const output = await protect(input, protectProps);
      Utils.writeFile(outputPath, output.messageEncrypted);
    } catch (error: any) {
      console.log("Could not protect file " + inputPath + ": " + error.message);
    }
  }

  async check(args: string[]): Promise<void> {
    if (args.length != 4) {
      console.log(checkUsage);
      return;
    }

    const inputPath = args[1];
    const aesKeyPath = args[2];
    const signKeyPath = args[3];

    try {
      const input = Utils.readFile(inputPath);
      const aesKey = await bytesToAesKey(Utils.readFile(aesKeyPath));
      const signKey = await bytesToSigningKey(Utils.readFile(signKeyPath));

      const protectProps: ProtectProps = {
        aesKey: aesKey,
        signingKey: signKey,
      };

      await protect(input, protectProps);
    } catch (error: any) {
      console.log("File is not protected");
    }
  }

  async unprotect(args: string[]): Promise<void> {
    if (args.length != 5) {
      console.log(unprotectUsage);
      return;
    }

    const inputPath = args[1];
    const outputPath = args[2];
    const aesKeyPath = args[3];
    const verifyKeyPath = args[4];

    try {
      const input = Utils.readFile(inputPath);
      const aesKey = await bytesToAesKey(Utils.readFile(aesKeyPath));
      const verifyKey = await bytesToVerificationKey(
        Utils.readFile(verifyKeyPath),
      );

      const unProtectProps: UnprotectProps = {
        iv: undefined,
        aesKey: aesKey,
        hmacKey: undefined,
        verifyingKey: verifyKey,
      };

      const output = await unprotect(input, unProtectProps);
      Utils.writeFile(outputPath, output.payload);
    } catch (error: any) {
      console.log(
        "Could not unprotect file " + inputPath + ": " + error.message,
      );
    }
  }

  async timeout(args: string[]): Promise<void> {
    if (args.length != 2) {
      console.log(timeoutUsage);
      return;
    }
    console.log("Timeout set");
  }

  async unknown(): Promise<void> {
    console.log(unknownCommand);
  }
}
