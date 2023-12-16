import bcrypt from 'bcrypt';
import jwt from 'jsonwebtoken';

import db from '../database';
import { JWT_OPTIONS, JWT_PRIVATE_KEY } from '../config';

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
}

export const getUserByEmail = async (email: string) => {
  const user = await db.user.findUnique({
    where: {
      email,
    },
  });

  return user;
}

export const getUserById = async (id: string) => {
  const user = await db.user.findUnique({
    where: {
      id,
    },
  });

  return user;
}

export const comparePasswords = async (password: string, passwordHash: string) => {
  const passwordMatches = await bcrypt.compare(password, passwordHash);

  return passwordMatches;
}

export const generateToken = (userID: string) => {
  const token = jwt.sign({ userID, }, JWT_PRIVATE_KEY, { expiresIn: JWT_OPTIONS.expiresIn });
  return token;
}

