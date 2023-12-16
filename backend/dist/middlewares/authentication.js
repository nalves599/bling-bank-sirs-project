"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.jwtAuth = void 0;
const jsonwebtoken_1 = __importDefault(require("jsonwebtoken"));
const config_1 = require("../config");
const jwtAuth = (req, res, next) => __awaiter(void 0, void 0, void 0, function* () {
    const authHeader = req.headers.authorization;
    if (!authHeader) {
        return res.status(401).json({ message: 'Token is missing' });
    }
    const [schema, token] = authHeader.split(' ');
    if (!token || schema !== 'Bearer') {
        return res.status(401).json({ message: 'Invalid token format' });
    }
    try {
        jsonwebtoken_1.default.verify(token, config_1.JWT_PUBLIC_KEY, { algorithms: ["RS256"] });
    }
    catch (_a) {
        return res.status(401).json({ message: 'Invalid token' });
    }
    req.authData = jsonwebtoken_1.default.decode(token);
    return next();
});
exports.jwtAuth = jwtAuth;
