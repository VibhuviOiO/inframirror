import express from 'express';
import Docker from 'dockerode';
import fs from 'fs';
import { Readable } from 'stream';

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

// GET /api/dockerops/containers/:id/logs
router.get('/containers/:id/logs', (req, res) => {
  const { host, port, protocol, ca, cert, key, follow, stdout, stderr, since, until, timestamps, tail } = req.query;
  const { id } = req.params;
  if (!host || !port || !id) {
    return res.status(400).json({ message: 'host, port, and container id are required' });
  }
  res.setHeader('Cache-Control', 'no-store');
  const docker = createDockerClient({ host, port, protocol, ca, cert, key });
  const container = docker.getContainer(id);

  // Build log options
  const logOpts: any = {
    follow: follow === 'true',
    stdout: stdout === 'true',
    stderr: stderr === 'true',
    since: since ? Number(since) : 0,
    until: until ? Number(until) : 0,
    timestamps: timestamps === 'true',
    tail: tail ?? 'all',
  };

  container.logs({ ...logOpts, stream: true }, (err, streamOrBuffer) => {
    if (err) {
      return res.status(500).json({ message: err.message });
    }
    // If follow=true, streamOrBuffer is a stream; else it's a Buffer
    if (logOpts.follow && streamOrBuffer && typeof (streamOrBuffer as any).on === 'function') {
      res.setHeader('Content-Type', 'application/octet-stream');
      res.setHeader('Connection', 'keep-alive');
      if (logOpts.follow && streamOrBuffer instanceof Readable) {
        const logStream = streamOrBuffer;
        logStream.on('data', (chunk: Buffer) => res.write(chunk));
        logStream.on('end', () => res.end());
        logStream.on('error', (err: Error) => {
          res.status(500).json({ message: err.message });
        });
        req.on('close', () => {
          if (typeof logStream.destroy === 'function') logStream.destroy();
        });
      } else if (Buffer.isBuffer(streamOrBuffer)) {
        // Non-streaming: streamOrBuffer is Buffer
        const logs = streamOrBuffer;
        res.status(200).json({ logs: logs ? logs.toString() : '' });
      } else {
        res.status(500).json({ message: 'Unknown log stream type' });
      }
    } else {
      // Non-streaming: streamOrBuffer is Buffer
      const logs = streamOrBuffer as Buffer;
      res.status(200).json({ logs: logs ? logs.toString() : '' });
    }
  });
});

export default router;
