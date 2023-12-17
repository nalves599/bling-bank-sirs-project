import { ProtectProps, UnprotectProps } from "../../library/src/utils/crypto";
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
  unknownCommand,
} from "./messages";

import { Account, Doc, Movement } from "./document";

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

      const jsonData = JSON.parse(input.toString());
      const doc = this.createDoc(jsonData);

      // Protect data
      await doc.protect(protectProps);

      const docJson = JSON.stringify(doc, null, 2);

      Utils.writeFile(outputPath, Buffer.from(docJson));
    } catch (error: any) {
      console.log("Could not protect file " + inputPath + ": " + error.message);
    }
  }

  async check(args: string[]): Promise<void> {
    if (args.length != 4 && args.length != 5) {
      console.log(checkUsage);
      return;
    }

    const inputPath = args[1];
    const aesKeyPath = args[2];
    const verifyKeyPath = args[3];

    try {
      const input = Utils.readFile(inputPath);
      const aesKey = await bytesToAesKey(Utils.readFile(aesKeyPath));
      const verifyKey = await bytesToVerificationKey(
        Utils.readFile(verifyKeyPath),
      );
      const threshold = args.length == 5 ? parseInt(args[4]) : undefined;
      const nonceVerification = Utils.getVerificationNonce(threshold);

      const unprotectedProps: UnprotectProps = {
        iv: undefined,
        aesKey: aesKey,
        hmacKey: undefined,
        verifyingKey: verifyKey,
        nonceVerification: nonceVerification,
      };

      const jsonData = JSON.parse(input.toString());
      const doc = this.createDoc(jsonData);

      // Check data
      const checkResult = await doc.check(unprotectedProps);
      console.log("File is protected: " + checkResult);
    } catch (error: any) {
      console.log("File is not protected");
    }
  }

  async unprotect(args: string[]): Promise<void> {
    if (args.length != 5 && args.length != 6) {
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
      const threshold = args.length == 6 ? parseInt(args[5]) : undefined;
      const nonceVerification = Utils.getVerificationNonce(threshold);

      const unProtectProps: UnprotectProps = {
        iv: undefined,
        aesKey: aesKey,
        hmacKey: undefined,
        verifyingKey: verifyKey,
        nonceVerification: nonceVerification,
      };

      const jsonData = JSON.parse(input.toString());
      const doc = this.createDoc(jsonData);

      // Unprotect data
      await doc.unprotect(unProtectProps);

      const docJson = JSON.stringify(doc, null, 2);

      Utils.writeFile(outputPath, Buffer.from(docJson));
    } catch (error: any) {
      console.log(
        "Could not unprotect file " + inputPath + ": " + error.message,
      );
    }
  }

  async unknown(): Promise<void> {
    console.log(unknownCommand);
  }

  private createDoc(jsonData: any): Doc {
    // Extract data and create instances
    if (jsonData.account.balance instanceof Number)
      jsonData.account.balance = jsonData.account.balance.toString();

    for (let movement of jsonData.account.movements) {
      if (movement.value instanceof Number)
        movement.value = movement.value.toString();
    }

    const account = new Account(
      jsonData.account.accountHolder,
      jsonData.account.balance,
      jsonData.account.currency,
      jsonData.account.movements.map(
        (movement: any) =>
          new Movement(movement.date, movement.value, movement.description),
      ),
    );

    return new Doc(account);
  }
}
