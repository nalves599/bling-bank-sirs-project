import * as fs from "fs";

export class Utils {
  static readFile(path: string): Buffer {
    return fs.readFileSync(path);
  }

  static writeFile(path: string, data: ArrayBuffer): void {
    fs.writeFileSync(path, Buffer.from(data));
  }
}
