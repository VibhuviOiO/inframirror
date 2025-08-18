import express from 'express';
import * as service from '../services/applicationCatalogService.js';

const router = express.Router();

router.get('/', async (req, res, next) => {
  try {
    const items = await service.getAllApplicationCatalogs();
    res.json(items);
  } catch (err) {
    next(err);
  }
});

router.get('/:id', async (req, res, next) => {
  try {
    const item = await service.getApplicationCatalogById(Number(req.params.id));
    if (!item) return res.status(404).json({ error: 'ApplicationCatalog not found' });
    res.json(item);
  } catch (err) {
    next(err);
  }
});

router.post('/', async (req, res, next) => {
  try {
    const created = await service.createApplicationCatalog(req.body);
    res.status(201).json(created);
  } catch (err) {
    next(err);
  }
});

router.put('/:id', async (req, res, next) => {
  try {
    const updated = await service.updateApplicationCatalog(Number(req.params.id), req.body);
    res.json(updated);
  } catch (err) {
    next(err);
  }
});

router.delete('/:id', async (req, res, next) => {
  try {
    await service.deleteApplicationCatalog(Number(req.params.id));
    res.status(204).send();
  } catch (err) {
    next(err);
  }
});

export default router;
