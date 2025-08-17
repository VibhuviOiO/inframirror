import express from 'express';
import type { Request, Response } from 'express';
import * as service from '../services/serviceTypeService.js';

const router = express.Router();

router.post('/', async (req: Request, res: Response) => {
  try {
    const created = await service.create(req.body);
    res.status(201).json(created);
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

router.get('/', async (req: Request, res: Response) => {
  try {
    const list = await service.list();
    res.json(list);
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
    const updated = await service.update(id, req.body);
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
