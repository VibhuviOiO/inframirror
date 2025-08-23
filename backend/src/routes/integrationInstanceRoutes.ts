import express from 'express';
import * as service from '../services/integrationInstanceService.js';

const router = express.Router();

router.get('/', async (req, res, next) => {
  try {
    const instances = await service.getAllIntegrationInstances();
    res.json(instances);
  } catch (err) {
    next(err);
  }
});

router.get('/:id', async (req, res, next) => {
  try {
    const instance = await service.getIntegrationInstanceById(Number(req.params.id));
    if (!instance) return res.status(404).json({ error: 'IntegrationInstance not found' });
    res.json(instance);
  } catch (err) {
    next(err);
  }
});

router.post('/', async (req, res, next) => {
  try {
    const created = await service.createIntegrationInstance(req.body);
    res.status(201).json(created);
  } catch (err) {
    next(err);
  }
});

router.put('/:id', async (req, res, next) => {
  try {
    const updated = await service.updateIntegrationInstance(Number(req.params.id), req.body);
    res.json(updated);
  } catch (err) {
    next(err);
  }
});

router.delete('/:id', async (req, res, next) => {
  try {
    await service.deleteIntegrationInstance(Number(req.params.id));
    res.status(204).send();
  } catch (err) {
    next(err);
  }
});

export default router;
