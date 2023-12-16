import { Router } from "express";
import * as UserController from "./controllers/UserController";


const routes = Router();

// Unauthenticated routes
routes.post('/register', UserController.register);
routes.post('/login', UserController.login);
routes.post('/login/token', UserController.generateToken);

export default routes;
