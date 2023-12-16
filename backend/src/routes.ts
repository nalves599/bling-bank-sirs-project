import { Router } from 'express';
import * as UserController from './controllers/UserController';
import { jwtAuth } from './middlewares/authentication';

const routes = Router();

// Unauthenticated routes
routes.post('/register', UserController.register);
routes.post('/login', UserController.login);
routes.post('/login/token', UserController.generateToken);

// Authenticated routes
routes.get('/user', jwtAuth, UserController.getUser);

export default routes;
