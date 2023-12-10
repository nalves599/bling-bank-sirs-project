export function intToBytes(value: number): Buffer {
    const buffer = Buffer.alloc(4);
    buffer.writeUInt32BE(value, 0);
    return buffer;
}

export function bytesToInt(bytes: Buffer, offset: number): number | undefined {
    try {
        return bytes.readUInt32BE(offset);
    } catch (e) {
        return undefined;
    }
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
