import * as fs from "fs";

export class Utils {
  static readFile(path: string): Buffer {
    return fs.readFileSync(path);
  }

  static writeFile(path: string, data: ArrayBuffer): void {
    fs.writeFileSync(path, Buffer.from(data));
  }

  static getVerificationNonce(
    threshold: number = 10000,
  ): (nonce: ArrayBuffer) => boolean {
    return function (nonce: ArrayBuffer): boolean {
      const ts = parseInt(new TextDecoder().decode(nonce));
      const now = Date.now();

      return ts > now - threshold;
    };
  }
}
