import * as crypto from 'crypto';
import * as fs from 'fs';

import { intToBytes, bytesToInt, read, write } from './Conversion';
import { readFile } from './FileReader';
import { Constants } from './Constants';

export class Keys {
    private secretKey: crypto.KeyObject | null = null; // KEK

    private secretSessionKey: crypto.KeyObject | null = null;

    private publicKey: crypto.KeyObject | null = null;

    private privateKey: crypto.KeyObject | null = null;

    private receiverPublicKey: crypto.KeyObject | null = null;

    private readonly iv: Buffer = Buffer.from(Constants.IV);

    constructor(secretKeyPath: string) {
        this.assignSecretKey(secretKeyPath);
    }

    private async assignSecretKey(secretKeyPath: string): Promise<void> {
        const encoded = await readFile(secretKeyPath);
        this.secretKey = crypto.createSecretKey(encoded);
    }

    async generateAsymKey(): Promise<void> {
        const { publicKey, privateKey } = crypto.generateKeyPairSync(Constants.ASYM_ALGO, {
            modulusLength: Constants.ASYM_KEY_SIZE,
            publicKeyEncoding: { type: 'spki', format: 'der' },
            privateKeyEncoding: { type: 'pkcs8', format: 'der' },
        });

        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    async generateSecretSessionKey(): Promise<void> {
        this.secretSessionKey = crypto.createSecretKey(crypto.randomBytes(Constants.SYM_KEY_SIZE / 8));
    }

    creationPayload(): Buffer {
        const encodedSecretSessionKey = this.secretSessionKey!.export();
        const encodedPublicKey = this.publicKey!.export({ type: 'spki', format: 'der' });

        const payload = Buffer.concat([
            intToBytes(encodedSecretSessionKey.length),
            encodedSecretSessionKey,
            intToBytes(encodedPublicKey.length),
            encodedPublicKey,
        ]);

        return payload;
    }

    receiveSessionKey(input: Buffer): void {
        const secretSessionKeyLength = bytesToInt(input, 0)!;
        const encodedSecretSessionKey = read(input, Constants.INT_SIZE, secretSessionKeyLength)!;
        this.secretSessionKey = crypto.createSecretKey(encodedSecretSessionKey);

        const publicKeyLength = bytesToInt(
            input,
            Constants.INT_SIZE + secretSessionKeyLength
        )!;
        const encodedReceiverPublicKey = read(
            input,
            Constants.INT_SIZE + secretSessionKeyLength + Constants.INT_SIZE,
            publicKeyLength
        )!;
        this.receiverPublicKey = crypto.createPublicKey({
            key: encodedReceiverPublicKey,
            format: 'der',
            type: 'spki',
        });
    }

    receivePublicKey(input: Buffer): void {
        const publicKeyLength = bytesToInt(input, 0)!;
        const encodedPublicKey = read(input, Constants.INT_SIZE, publicKeyLength)!;
        this.receiverPublicKey = crypto.createPublicKey({
            key: encodedPublicKey,
            format: 'der',
            type: 'spki',
        });
    }

    getPublicKeyPayload(): Buffer {
        const encodedPublicKey = this.publicKey!.export({ type: 'spki', format: 'der' });
        const payload = Buffer.concat([intToBytes(encodedPublicKey.length), encodedPublicKey]);
        return payload;
    }

    clearKeys(): void {
        this.privateKey = null;
        this.publicKey = null;
        this.secretSessionKey = null;
        this.receiverPublicKey = null;
    }
}
