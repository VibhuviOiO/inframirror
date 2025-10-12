import express from 'express';
import Docker from 'dockerode';
import fs from 'fs';
import { Readable } from 'stream';

const router = express.Router();

const ANSI_REGEX = /\u001b\[[0-9;]*m/g;
function demuxDockerStream(buffer: Buffer): string {
  let cursor = 0;
  let result = '';

  while (cursor + 8 <= buffer.length) {
    const length = buffer.readUInt32BE(cursor + 4);
    const start = cursor + 8;
    const end = start + length;

    if (end > buffer.length) break; // incomplete frame

    result += buffer.slice(start, end).toString('utf8');
    cursor = end;
  }

  return result;
}


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
    if (err) return res.status(500).json({ message: err.message });

    if (logOpts.follow && streamOrBuffer instanceof Readable) {
      res.setHeader('Content-Type', 'text/plain; charset=utf-8');
      const logStream = streamOrBuffer;
      logStream.on('data', (chunk: Buffer) => {
        res.write(demuxDockerStream(chunk));
      });
      logStream.on('end', () => res.end());
      logStream.on('error', (err: Error) => {
        res.status(500).json({ message: err.message });
      });
      req.on('close', () => {
        if (typeof logStream.destroy === 'function') logStream.destroy();
      });
    } else {
      // Buffer mode (non-follow)
      const logs = streamOrBuffer as Buffer;
      const cleanLogs = demuxDockerStream(logs);
      res.status(200).json({ logs: cleanLogs });
    }
  });
});

// GET /api/dockerops/containers/:id/inspect
router.get('/containers/:id/inspect', async (req, res) => {
  const { host, port, protocol, ca, cert, key, size } = req.query;
  const { id } = req.params;
  if (!host || !port || !id) {
    return res.status(400).json({ message: 'host, port, and container id are required' });
  }
  try {
    const docker = createDockerClient({ host, port, protocol, ca, cert, key });
    const container = docker.getContainer(id);
    // dockerode: inspect([options], callback) or inspect(options)
    const opts: any = {};
    if (size !== undefined) opts.size = size === 'true';
    const info = await container.inspect(opts);
    res.status(200).json(info);
  } catch (err: any) {
    if (err.statusCode === 404) {
      return res.status(404).json({ message: 'No such container' });
    }
    res.status(500).json({ message: err.message });
  }
});

// GET /api/dockerops/containers/:id/stats
router.get('/containers/:id/stats', async (req, res) => {
  const { host, port, protocol, ca, cert, key, stream } = req.query;
  const { id } = req.params;
  if (!host || !port || !id) {
    return res.status(400).json({ message: 'host, port, and container id are required' });
  }
  try {
    const docker = createDockerClient({ host, port, protocol, ca, cert, key });
    const container = docker.getContainer(id);
    // dockerode: container.stats([options], callback)
    const opts: any = { stream: stream === 'true' };
    container.stats(opts, (err, stats) => {
      if (err) return res.status(500).json({ message: err.message });
      if (opts.stream && stats instanceof Readable) {
        res.setHeader('Content-Type', 'application/json');
        stats.pipe(res);
        req.on('close', () => {
          if (typeof stats.destroy === 'function') stats.destroy();
        });
      } else {
        res.status(200).json(stats);
      }
    });
  } catch (err: any) {
    res.status(500).json({ message: err.message });
  }
});
export default router;
