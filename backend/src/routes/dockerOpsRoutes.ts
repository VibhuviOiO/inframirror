import express from 'express';
import Docker from 'dockerode';
import fs from 'fs';

const router = express.Router();

function createDockerClient({ host, port, protocol = 'http', ca, cert, key }: any) {
  const opts: any = { host, port, protocol };
  if (ca) opts.ca = fs.readFileSync(ca);
  if (cert) opts.cert = fs.readFileSync(cert);
  if (key) opts.key = fs.readFileSync(key);
  return new Docker(opts);
}

// GET /api/dockerops/containers
router.get('/containers', async (req, res) => {
  try {
    const { host, port, protocol, ca, cert, key, all, limit, size, filters } = req.query;
    if (!host || !port) {
      return res.status(400).json({ message: 'host and port are required' });
    }
    const docker = createDockerClient({ host, port, protocol, ca, cert, key });
    const opts: any = {};
    if (all !== undefined) opts.all = all === 'true';
    if (limit !== undefined) opts.limit = Number(limit);
    if (size !== undefined) opts.size = size === 'true';
    if (filters !== undefined) {
      try {
        opts.filters = JSON.parse(filters as string);
      } catch (e) {
        return res.status(400).json({ message: 'Invalid filters JSON' });
      }
    }
    docker.listContainers(opts, (err: Error | null, containers?: any[]) => {
      if (err) {
        return res.status(500).json({ message: err.message });
      }
      res.json(containers ?? []);
    });
  } catch (err: any) {
    res.status(500).json({ message: err.message });
  }
});

export default router;
