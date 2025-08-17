import express from 'express';
import type { Request, Response } from 'express';
import prisma from './prismaClient.js';
import datacenterRouter from './routes/datacenter.js';
import serviceCatalogRouter from './routes/serviceCatalog.js';
import serviceTypeRouter from './routes/serviceType.js';
import cors from 'cors'
import { errorHandler } from './middleware/errorHandler.js';

const app = express();
const PORT = 8080;

app.use(express.json());

app.use(cors({
  origin: 'http://localhost:5173',
  methods: ['GET','POST','PUT','DELETE'],
  allowedHeaders: ['Content-Type'],
}))

app.get('/health', async (req: Request, res: Response) => {
  try {
    await prisma.$queryRaw`SELECT 1`;
    res.json({ status: 'ok' });
  } catch (err: any) {
    res.status(500).json({ status: 'error', error: err.message });
  }
});

app.use('/datacenters', datacenterRouter);
app.use('/service-catalogs', serviceCatalogRouter);
app.use('/service-types', serviceTypeRouter);
// ...after all routes...
app.use(errorHandler);

app.listen(PORT, () => {
  console.log(`Backend server running on http://localhost:${PORT}`);
});
