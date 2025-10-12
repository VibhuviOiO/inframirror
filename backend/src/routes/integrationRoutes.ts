import express from 'express';
import * as service from '../services/integrationService.js';

const router = express.Router();

router.get('/', async (req, res, next) => {
  try {
    const integrations = await service.getAllIntegrations();
    res.json(integrations);
  } catch (err) {
    next(err);
  }
});

router.get('/:id', async (req, res, next) => {
  try {
    const integration = await service.getIntegrationById(Number(req.params.id));
    if (!integration) return res.status(404).json({ error: 'Integration not found' });
    res.json(integration);
  } catch (err) {
    next(err);
  }
});

router.post('/', async (req, res, next) => {
  try {
    const created = await service.createIntegration(req.body);
    res.status(201).json(created);
  } catch (err) {
    next(err);
  }
});

router.put('/:id', async (req, res, next) => {
  try {
    const updated = await service.updateIntegration(Number(req.params.id), req.body);
    res.json(updated);
  } catch (err) {
    next(err);
  }
});

router.delete('/:id', async (req, res, next) => {
  try {
    await service.deleteIntegration(Number(req.params.id));
    res.status(204).send();
  } catch (err) {
    next(err);
  }
});

export default router;
