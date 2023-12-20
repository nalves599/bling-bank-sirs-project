declare namespace Express {
  export interface Request {
    authData?: {
      userId: string;
      initialNonce: string;
      sessionId: string;
    };
  }
}
