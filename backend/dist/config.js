"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.DB_PORT = exports.DB_HOST = exports.JWT_OPTIONS = exports.JWT_PUBLIC_KEY = exports.JWT_PRIVATE_KEY = exports.PORT = void 0;
require("dotenv/config");
const node_crypto_1 = __importDefault(require("node:crypto"));
exports.PORT = process.env.PORT || 3000;
const JWT_KEYS = node_crypto_1.default.generateKeyPairSync("rsa", {
    modulusLength: 2048,
    publicKeyEncoding: {
        type: "spki",
        format: "pem",
    },
    privateKeyEncoding: {
        type: "pkcs8",
        format: "pem",
    },
});
exports.JWT_PRIVATE_KEY = process.env.JWT_PRIVATE_KEY || JWT_KEYS.privateKey;
exports.JWT_PUBLIC_KEY = process.env.JWT_PUBLIC_KEY || JWT_KEYS.publicKey;
exports.JWT_OPTIONS = {
    expiresIn: '1d',
    issuer: 'DNT',
    algorithm: 'rs256',
};
exports.DB_HOST = process.env.DB_HOST;
exports.DB_PORT = process.env.DB_PORT;
