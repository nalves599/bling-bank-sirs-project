import { Router } from "express";
import * as UserController from "./controllers/UserController";
import * as AccountController from "./controllers/AccountController";
import * as KeyController from "./controllers/KeyController";
import { jwtAuth } from "./middlewares/authentication";

const routes = Router();

// Unauthenticated routes
routes.post("/register", UserController.register);
routes.post("/login", UserController.login);
routes.post("/login/token", UserController.generateToken);

const authenticatedRoutes = Router();
//authenticatedRoutes.use(jwtAuth); // TODO: Add authentication middleware
routes.use(authenticatedRoutes);

// Authenticated routes
authenticatedRoutes.get("/me", jwtAuth, UserController.getMe);
authenticatedRoutes.post("/me/keys", jwtAuth, KeyController.addKey);
authenticatedRoutes.get("/users", UserController.getUsers);

// Account routes
authenticatedRoutes.post("/accounts", AccountController.createAccount);
authenticatedRoutes.get("/accounts/:id", AccountController.getAccountById);
authenticatedRoutes.post("/accounts/:id/unlock", jwtAuth, AccountController.unlockAccount);
authenticatedRoutes.get("/accounts/holder/:holderId", AccountController.getAccountsByHolder);

// Movement routes
authenticatedRoutes.get("/movements/account/:accountId", AccountController.getAccountMovements);

// Payments routes
authenticatedRoutes.get("/payments/:accountId", AccountController.getAccountPayments);
authenticatedRoutes.post("/accounts/:id/payments", jwtAuth, AccountController.submitPayment);

// Keys

// Account routes

export default routes;
