import * as fs from 'fs';
import { promisify } from 'util';

export class Utils {
    static async readFile(path: string): Promise<Buffer | undefined> {
        try {
            const readFileAsync = promisify(fs.readFile);
            const data = await readFileAsync(path);
            return data;
        } catch (error: unknown) {
            // Check if 'error' is an instance of 'Error' before returning undefined
            if (error instanceof Error) {
                console.error(`Error reading file: ${error.message}`);
            }
            return undefined;
        }
    }

    static async writeFile(path: string, content: Uint8Array): Promise<void> {
        try {
            const writeFileAsync = promisify(fs.writeFile);
            await writeFileAsync(path, content);
        } catch (error: unknown) {
            // Check if 'error' is an instance of 'Error' before throwing a new error
            if (error instanceof Error) {
                throw new Error(`Error writing file: ${error.message}`);
            } else {
                throw new Error(`Unknown error occurred: ${error}`);
            }
        }
    }
}
