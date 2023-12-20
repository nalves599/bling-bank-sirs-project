import bcrypt from "bcrypt";
import jwt from "jsonwebtoken";

import db from "../database";
import { JWT_OPTIONS, JWT_PRIVATE_KEY } from "../config";
import { AuthData } from "../middlewares/authentication";

export type RegisterUser = {
  name: string;
  email: string;
  password: string;
};

export const register = async (user: RegisterUser) => {
  const passwordHash = bcrypt.hashSync(user.password, 10);

  const registeredUser = await db.user.create({
    data: {
      name: user.name,
      email: user.email,
      passwordHash,
    },
  });

  return registeredUser;
};

export const getUserByEmail = async (email: string) => {
  const user = await db.user.findUnique({
    where: { email },
  });

  return user;
};

export const getUserById = async (id: string) => {
  const user = db.user.findUnique({
    where: { id },
    include: {
      accounts: true,
    },
  });
  return user;
};

export const comparePasswords = async (
  password: string,
  passwordHash: string,
) => {
  const passwordMatches = await bcrypt.compare(password, passwordHash);

  return passwordMatches;
};

type TokenData = {
  userId: string;
  initialNonce: string;
  sessionId: string;
};

export const generateToken = (payload: TokenData) => {
  const token = jwt.sign(payload, JWT_PRIVATE_KEY, {
    expiresIn: JWT_OPTIONS.expiresIn,
    algorithm: "RS256",
  });
  return token;
};
