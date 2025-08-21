
import express from 'express';
import * as service from '../services/clusterService.js';

const router = express.Router();

router.get('/', async (req, res, next) => {
  try {
    const items = await service.getAllClusters();
    res.json(items);
  } catch (err) {
    next(err);
  }
});

router.get('/:id', async (req, res, next) => {
  try {
    const item = await service.getClusterById(Number(req.params.id));
    if (!item) return res.status(404).json({ error: 'Cluster not found' });
    res.json(item);
  } catch (err) {
    next(err);
  }
});

router.post('/', async (req, res, next) => {
  try {
    const created = await service.createCluster(req.body);
    res.status(201).json(created);
  } catch (err) {
    next(err);
  }
});

router.put('/:id', async (req, res, next) => {
  try {
    const updated = await service.updateCluster(Number(req.params.id), req.body);
    res.json(updated);
  } catch (err) {
    next(err);
  }
});

router.delete('/:id', async (req, res, next) => {
  try {
    await service.deleteCluster(Number(req.params.id));
    res.status(204).send();
  } catch (err) {
    next(err);
  }
});

export default router;
