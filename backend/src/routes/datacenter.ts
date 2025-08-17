import express from 'express';
import type { Request, Response } from 'express';
import * as service from '../services/datacenterService.js';

const router = express.Router();

// Ensure table exists on startup
service.init().catch((err) => console.error('Failed to initialize datacenter table', err));

router.post('/', async (req: Request, res: Response) => {
  try {
    const { name, shortName, publicCIDR, privateCIDR } = req.body;
    if (!name || !shortName) return res.status(400).json({ error: 'name and shortName are required' });
    const created = await service.create({ name, shortName, publicCIDR, privateCIDR });
    res.status(201).json(created);
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

router.get('/', async (req: Request, res: Response) => {
  try {
    const items = await service.list();
    res.json(items);
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

router.get('/:id', async (req: Request, res: Response) => {
  try {
    const id = parseInt(req.params.id);
    const item = await service.getById(id);
    if (!item) return res.status(404).json({ error: 'not found' });
    res.json(item);
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

router.put('/:id', async (req: Request, res: Response) => {
  try {
    const id = parseInt(req.params.id);
    const { name, shortName, publicCIDR, privateCIDR } = req.body;
    const updated = await service.update(id, { name, shortName, publicCIDR, privateCIDR });
    if (!updated) return res.status(404).json({ error: 'not found' });
    res.json(updated);
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

router.delete('/:id', async (req: Request, res: Response) => {
  try {
    const id = parseInt(req.params.id);
    const ok = await service.remove(id);
    if (!ok) return res.status(404).json({ error: 'not found' });
    res.status(204).send();
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

export default router;
