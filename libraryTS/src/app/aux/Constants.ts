export class Constants {
    static SYM_ALGO: string = "AES";
    static SYM_KEY_SIZE: number = 128;
    static SYM_CIPHER: string = "AES/CBC/PKCS5Padding";

    // TODO: change this to a random IV
    static IV: number[] = [0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F];
    static ASYM_ALGO: string = "RSA";
    static ASYM_KEY_SIZE: number = 2048;
    static ASYM_CIPHER: string = "RSA";

    static DIGEST_ALGO: string = "SHA-256";
    static DIGEST_SIZE: number = 32; // 256 bits = 32 bytes

    static INITIAL_SEQUENCE_NUMBER: number = 0;

    static INT_SIZE: number = 4;

    static MAX_TIMESTAMP_DIFFERENCE: number = 3 * 1000; // 3 seconds
}
