import { Request, Response } from "express";
import * as UserService from "../services/UserService";
import * as SecurityService from "../services/SecurityService";
import * as EmailService from "../services/EmailService";
import * as KeyUtil from "../utils/KeyUtil";

import { crypto } from "blingbank-lib";

type POWChallenge = {
  content: string;
  ciphered: string;
  solution: string;
  numberOfTries: number;
};
const challenges = new Map<string, POWChallenge>();

export const register = async (req: Request, res: Response) => {
  const { name, email, password } = req.body;

  try {
    const user = await UserService.register({
      name,
      email,
      password,
    });

    const { friendlySharedSecret, sharedSecret } =
      await KeyUtil.generateSharedSecret();
    await SecurityService.saveSharedSecret(user.id, sharedSecret);
    EmailService.sendSharedSecret(email, friendlySharedSecret);

    res.json({
      message:
        "User registered. Please check your email for your shared secret.",
    });
  } catch (error) {
    console.error(error);
    res.status(400).json({ message: "Could not register user" });
  }
};

export const login = async (req: Request, res: Response) => {
  const { email, password } = req.body;

  try {
    const user = await UserService.getUserByEmail(email);

    if (!user) {
      return res.status(404).json({ message: "User not found" }); // TODO: Make this generic
    }

    const passwordMatches = await UserService.comparePasswords(
      password,
      user.passwordHash,
    );

    if (!passwordMatches) {
      return res.status(401).json({ message: "Invalid password" }); // TODO: Make this generic
    }

    const sharedSecret = (await SecurityService.getSharedSecret(user.id))!;
    const pow = crypto.generatePOWChallenge();
    const powSolution = crypto.solvePOWChallenge(pow);
    const ciphered = String(await crypto.paramProtect(pow, sharedSecret));

    console.log(
      "Challenge answer:",
      await crypto.paramProtect(powSolution, sharedSecret),
    );

    const challenge = {
      content: pow,
      ciphered,
      solution: powSolution,
      numberOfTries: 0,
    };
    challenges.set(user.id, challenge);

    res.json({ challenge: challenge.ciphered });
  } catch (error) {
    console.error(error);
    res.status(400).json({ message: "Could not login" });
  }
};

export const generateToken = async (req: Request, res: Response) => {
  const { email, solution } = req.body;

  try {
    const user = await UserService.getUserByEmail(email);

    if (!user) {
      return res.status(404).json({ message: "User not found" }); // TODO: Make this generic
    }

    const challenge = challenges.get(user.id);

    if (!challenge) {
      return res.status(404).json({ message: "Challenge not found" }); // TODO: Make this generic
    }

    const sharedSecret = (await SecurityService.getSharedSecret(user.id))!;
    const decrypted = crypto.decoder.decode(
      await crypto.paramUnprotect(solution, sharedSecret),
    );

    console.log("Solution:", decrypted);

    if (challenge.solution !== decrypted) {
      challenge.numberOfTries += 1;
      if (challenge.numberOfTries > 3) {
        challenges.delete(user.id);
        return res.status(401).json({ message: "Too many tries" }); // TODO: Make this generic
      }
      return res.status(401).json({ message: "Invalid solution" }); // TODO: Make this generic
    }

    const initialNonce = crypto.decoder.decode(await crypto.generateNonce());
    const { sessionId } = await SecurityService.createSessionKey();

    console.debug("Initial nonce:", initialNonce);
    console.debug("Session ID:", sessionId);

    const token = UserService.generateToken({
      userId: user.id,
      initialNonce,
      sessionId,
    });
    challenges.delete(user.id);

    res.json({ token });
  } catch (error) {
    console.error(error);
    res.status(400).json({ message: "Could not get token" });
  }
};

export const getMe = async (req: Request, res: Response) => {
  try {
    const user = await UserService.getUserById(req.authData?.userId!);

    if (!user) {
      return res.status(404).json({ message: "User not found" }); // TODO: Make this generic
    }

    const accounts = user.accounts.map((account) => ({
      id: account.id,
      name: account.name,
      currency: account.currency,
    }));

    res.json({ ...user, accounts, passwordHash: undefined });
  } catch (error) {
    console.error(error);
    res.status(400).json({ message: "Could not get user" });
  }
};
