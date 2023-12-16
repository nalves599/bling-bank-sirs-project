import {
  ProtectProps,
  UnprotectProps,
  protect,
  unprotect,
  check,
} from "../../library/src/utils/crypto";
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
import { it } from "node:test";

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

      const jsonInput = JSON.parse(input.toString());
      iterateJSON(jsonInput, '', protectProps);

      //console.log(JSON.stringify(jsonInput, null, 2));

      //const output = await protect(input, protectProps);
      //Utils.writeFile(outputPath, output.messageEncrypted);
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

      const valid = await check(input, unprotectedProps);
      console.log("File is protected: " + valid);
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

      const output = await unprotect(input, unProtectProps);
      Utils.writeFile(outputPath, output.payload);
    } catch (error: any) {
      console.log(
        "Could not unprotect file " + inputPath + ": " + error.message,
      );
    }
  }

  async unknown(): Promise<void> {
    console.log(unknownCommand);
  }
}

async function iterateJSON(obj: any, parentKey = '', props: ProtectProps): Promise<void> {
  for (const key in obj) {
    if (Object.prototype.hasOwnProperty.call(obj, key)) {
      const value = obj[key];

      if (Array.isArray(value)) {
        //console.log(`${parentKey}.${key}: [${value.join(', ')}]`);
        for (let i = 0; i < value.length; i++) {
          if (typeof value[i] === 'object') {
            iterateJSON(value[i], `${parentKey}.${key}[${i}]`, props);
          }
        }
      } else if (typeof value === 'object' && value !== null) {
        //console.log(`${parentKey}.${key}:`);
        iterateJSON(value, `${parentKey}.${key}`, props);
      } else {
        if (typeof value === 'number') {
          obj[key] = value.toString();
        }
        const valueToProtect = obj[key];
        console.log(`${parentKey}.${key}: ${valueToProtect}`);
        console.log(typeof valueToProtect)
        obj[key] = await protect(valueToProtect, props);
      }
    }
  }
}
