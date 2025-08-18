import express from 'express';
import type { Request, Response } from 'express';
import prisma from './prismaClient.js';
import cors from 'cors'
import { errorHandler } from './middleware/errorHandler.js';



import applicationRoutes from './routes/applicationRoutes.js';
import environmentRoutes from './routes/environmentRoutes.js';
import serviceRoutes from './routes/serviceRoutes.js';
import hostRoutes from './routes/hostRoutes.js';
import applicationCatalogRoutes from './routes/applicationCatalogRoutes.js';
import serviceCatalogRoutes from './routes/serviceCatalogRoutes.js';
import serviceOrAppTypeRoutes from './routes/serviceOrAppTypeRoutes.js';
import teamRoutes from './routes/teamRoutes.js';
import datacenterRoutes from './routes/datacenterRoutes.js';
import regionRoutes from './routes/regionRoutes.js';


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




app.use('/applications', applicationRoutes);
app.use('/environments', environmentRoutes);
app.use('/services', serviceRoutes);
app.use('/hosts', hostRoutes);
app.use('/application-catalogs', applicationCatalogRoutes);
app.use('/service-catalogs', serviceCatalogRoutes);
app.use('/service-or-app-types', serviceOrAppTypeRoutes);
app.use('/teams', teamRoutes);
app.use('/datacenters', datacenterRoutes);
app.use('/regions', regionRoutes);

app.use(errorHandler);

app.listen(PORT, () => {
  console.log(`Backend server running on http://localhost:${PORT}`);
});
