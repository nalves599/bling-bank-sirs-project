import * as fs from 'fs';

export async function readFile(path: string): Promise<Buffer> {
    return fs.promises.readFile(path);
}

export function read(input: Buffer, start: number, length: number): Buffer | undefined {
    try {
        return input.subarray(start, start + length);
    } catch (e) {
        return undefined;
    }
}

export function write(input: Buffer, output: Buffer): boolean {
    try {
        input.copy(output);
        return true;
    } catch (e) {
        return false;
    }
}
