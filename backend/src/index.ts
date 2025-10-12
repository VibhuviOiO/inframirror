
import path from 'path';
import { fileURLToPath } from 'url';
import express from 'express';
import type { Request, Response } from 'express';
import prisma from './prismaClient.js';
import cors from 'cors';
import { errorHandler } from './middleware/errorHandler.js';

import environmentRoutes from './routes/environmentRoutes.js';
import hostRoutes from './routes/hostRoutes.js';
import datacenterRoutes from './routes/datacenterRoutes.js';
import regionRoutes from './routes/regionRoutes.js';
import clusterRoutes from './routes/clusterRoutes.js';
import integrationRoutes from './routes/integrationRoutes.js';
import integrationInstanceRoutes from './routes/integrationInstanceRoutes.js';
import dockerOpsRoutes from './routes/dockerOpsRoutes.js';
import postgresOpsRoutes from './routes/postgresOpsRoutes.js';
import monitorRoutes from './routes/monitorRoutes.js';




const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();
const PORT = 8080;


// Serve static frontend files
app.use(express.static(path.join(__dirname, '../public')));

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



app.use('/api/environments', environmentRoutes);
app.use('/api/hosts', hostRoutes);
app.use('/api/datacenters', datacenterRoutes);
app.use('/api/regions', regionRoutes);
app.use('/api/clusters', clusterRoutes);
app.use('/api/integrations', integrationRoutes);
app.use('/api/integration-instances', integrationInstanceRoutes);
app.use('/api/dockerops', dockerOpsRoutes);
app.use('/api/postgresops', postgresOpsRoutes);
app.use('/api/monitors', monitorRoutes);


app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, '../public/index.html'));
});

app.use(errorHandler);

app.listen(PORT, () => {
  console.log(`Backend server running on http://localhost:${PORT}`);
});
