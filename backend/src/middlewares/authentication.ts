import {Request, Response, NextFunction} from 'express';
import jwt from 'jsonwebtoken';

import { JWT_PUBLIC_KEY } from '../config';

export const jwtAuth = async (req: Request, res: Response, next: NextFunction) => {

  const authHeader = req.headers.authorization;
  if (!authHeader) {
    return res.status(401).json({ message: 'Token is missing' });
  }

  const [schema, token] = authHeader.split(' ');
  if (!token || schema !== 'Bearer') {
    return res.status(401).json({ message: 'Invalid token format' });
  }

  try {
    jwt.verify(token, JWT_PUBLIC_KEY, { algorithms: ["RS256"] });
  } catch {
    return res.status(401).json({ message: 'Invalid token' });
  }

  req.authData = jwt.decode(token);
  return next();
}
