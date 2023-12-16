import express, { Application } from 'express';
import cors from 'cors';

import {PORT} from './config';
import routes from './routes';

import './database';
import './email';

const app: Application = express();

app.use(cors());
app.use(express.json());
app.use(routes);

app.listen(PORT, () => {
  console.log(`Server started on port ${PORT}`);
});
