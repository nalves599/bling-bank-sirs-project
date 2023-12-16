import { Request, Response } from 'express';
import * as UserService from '../services/UserService';
import * as SecurityService from '../services/SecurityService';
import * as EmailService from '../services/EmailService';

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
      SecurityService.generateSharedSecret();
    await SecurityService.saveSharedSecret(user.id, sharedSecret);
    // EmailService.sendSharedSecret(email, friendlySharedSecret); // TODO: Uncomment this line
    console.log(friendlySharedSecret);

    res.json({
      message:
        'User registered. Please check your email for your shared secret.',
    });
  } catch (error) {
    console.error(error);
    res.status(400).json({ message: 'Could not register user' });
  }
};

export const login = async (req: Request, res: Response) => {
  const { email, password } = req.body;

  try {
    const user = await UserService.getUserByEmail(email);

    if (!user) {
      return res.status(404).json({ message: 'User not found' }); // TODO: Make this generic
    }

    const passwordMatches = await UserService.comparePasswords(
      password,
      user.passwordHash,
    );

    if (!passwordMatches) {
      return res.status(401).json({ message: 'Invalid password' }); // TODO: Make this generic
    }

    const sharedSecret = (await SecurityService.getSharedSecret(user.id))!;
    const pow = SecurityService.generatePOWChallenge();
    const powSolution = SecurityService.solvePOWChallenge(pow);

    const challenge = {
      content: pow,
      ciphered: SecurityService.encryptWithSharedSecret(pow, sharedSecret),
      solution: powSolution,
      numberOfTries: 0,
    };
    challenges.set(user.id, challenge);

    res.json({ challenge: challenge.ciphered });
  } catch (error) {
    console.error(error);
    res.status(400).json({ message: 'Could not login' });
  }
};

export const generateToken = async (req: Request, res: Response) => {
  const { email, solution } = req.body;

  try {
    const user = await UserService.getUserByEmail(email);

    if (!user) {
      return res.status(404).json({ message: 'User not found' }); // TODO: Make this generic
    }

    const challenge = challenges.get(user.id);

    if (!challenge) {
      return res.status(404).json({ message: 'Challenge not found' }); // TODO: Make this generic
    }

    if (challenge.solution !== solution) {
      challenge.numberOfTries += 1;
      if (challenge.numberOfTries > 3) {
        challenges.delete(user.id);
        return res.status(401).json({ message: 'Too many tries' }); // TODO: Make this generic
      }
      return res.status(401).json({ message: 'Invalid solution' }); // TODO: Make this generic
    }

    const token = UserService.generateToken(user.id);
    challenges.delete(user.id);

    // FIXME: Add session key

    res.json({ token });
  } catch (error) {
    console.error(error);
    res.status(400).json({ message: 'Could not get token' });
  }
};
