import express from 'express';
import * as service from '../services/datacenterService.js';

const router = express.Router();

router.get('/', async (req, res, next) => {
  try {
    const datacenters = await service.getAllDatacenters();
    res.json(datacenters);
  } catch (err) {
    next(err);
  }
});

router.get('/:id', async (req, res, next) => {
  try {
    const datacenter = await service.getDatacenterById(Number(req.params.id));
    if (!datacenter) return res.status(404).json({ error: 'Datacenter not found' });
    res.json(datacenter);
  } catch (err) {
    next(err);
  }
});

router.post('/', async (req, res, next) => {
  try {
    const created = await service.createDatacenter(req.body);
    res.status(201).json(created);
  } catch (err) {
    next(err);
  }
});

router.put('/:id', async (req, res, next) => {
  try {
    const updated = await service.updateDatacenter(Number(req.params.id), req.body);
    res.json(updated);
  } catch (err) {
    next(err);
  }
});

router.delete('/:id', async (req, res, next) => {
  try {
    await service.deleteDatacenter(Number(req.params.id));
    res.status(204).send();
  } catch (err) {
    next(err);
  }
});

export default router;
