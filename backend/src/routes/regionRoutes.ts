import express from 'express';
import * as service from '../services/regionService.js';

const router = express.Router();

router.get('/', async (req, res, next) => {
  try {
    const regions = await service.getAllRegions();
    res.json(regions);
  } catch (err) {
    next(err);
  }
});

router.get('/:id', async (req, res, next) => {
  try {
    const region = await service.getRegionById(Number(req.params.id));
    if (!region) return res.status(404).json({ error: 'Region not found' });
    res.json(region);
  } catch (err) {
    next(err);
  }
});

router.post('/', async (req, res, next) => {
  try {
    const created = await service.createRegion(req.body);
    res.status(201).json(created);
  } catch (err) {
    next(err);
  }
});

router.put('/:id', async (req, res, next) => {
  try {
    const updated = await service.updateRegion(Number(req.params.id), req.body);
    res.json(updated);
  } catch (err) {
    next(err);
  }
});

router.delete('/:id', async (req, res, next) => {
  try {
    await service.deleteRegion(Number(req.params.id));
    res.status(204).send();
  } catch (err) {
    next(err);
  }
});

export default router;
