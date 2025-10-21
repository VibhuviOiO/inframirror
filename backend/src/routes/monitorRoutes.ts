import express from 'express';
import * as service from '../services/monitorService.js';
import type { MonitorFilters } from '../models/monitor.js';

const router = express.Router();

// Get all monitors with optional filters
router.get('/', async (req, res, next) => {
  try {
    const filters: MonitorFilters = {
      monitorType: req.query.type as string,
      agentRegion: req.query.region as string,
      success: req.query.success ? req.query.success === 'true' : undefined,
      targetHost: req.query.host as string,
      limit: req.query.limit ? parseInt(req.query.limit as string) : undefined,
      offset: req.query.offset ? parseInt(req.query.offset as string) : undefined,
      sortBy: req.query.sortBy as 'executedAt' | 'responseTime' | 'monitorId',
      sortOrder: req.query.sortOrder as 'asc' | 'desc'
    };

    // Remove undefined values
    Object.keys(filters).forEach(key => {
      if (filters[key as keyof MonitorFilters] === undefined) {
        delete filters[key as keyof MonitorFilters];
      }
    });

    const monitors = await service.getAllMonitors(filters);
    res.json(monitors);
  } catch (err) {
    next(err);
  }
});

// Get monitor statistics
router.get('/stats', async (req, res, next) => {
  try {
    const stats = await service.getMonitorStats();
    res.json(stats);
  } catch (err) {
    next(err);
  }
});

// Get monitor regions
router.get('/regions', async (req, res, next) => {
  try {
    const regions = await service.getMonitorRegions();
    res.json(regions);
  } catch (err) {
    next(err);
  }
});

// Get monitors by type (protocol-specific endpoints)
router.get('/http', async (req, res, next) => {
  try {
    const filters: MonitorFilters = {
      agentRegion: req.query.region as string,
      success: req.query.success ? req.query.success === 'true' : undefined,
      targetHost: req.query.host as string,
      limit: req.query.limit ? parseInt(req.query.limit as string) : undefined,
      offset: req.query.offset ? parseInt(req.query.offset as string) : undefined,
      sortBy: req.query.sortBy as 'executedAt' | 'responseTime' | 'monitorId',
      sortOrder: req.query.sortOrder as 'asc' | 'desc',
      activeOnly: req.query.activeOnly ? req.query.activeOnly === 'true' : undefined,
      maxAge: req.query.maxAge ? parseInt(req.query.maxAge as string) : undefined
    };

    // Remove undefined values
    Object.keys(filters).forEach(key => {
      if (filters[key as keyof MonitorFilters] === undefined) {
        delete filters[key as keyof MonitorFilters];
      }
    });

    const monitors = await service.getHTTPMonitors(filters);
    res.json(monitors);
  } catch (err) {
    next(err);
  }
});

router.get('/tcp', async (req, res, next) => {
  try {
    const filters: MonitorFilters = {
      agentRegion: req.query.region as string,
      success: req.query.success ? req.query.success === 'true' : undefined,
      targetHost: req.query.host as string,
      limit: req.query.limit ? parseInt(req.query.limit as string) : undefined,
      offset: req.query.offset ? parseInt(req.query.offset as string) : undefined,
      sortBy: req.query.sortBy as 'executedAt' | 'responseTime' | 'monitorId',
      sortOrder: req.query.sortOrder as 'asc' | 'desc'
    };

    // Remove undefined values
    Object.keys(filters).forEach(key => {
      if (filters[key as keyof MonitorFilters] === undefined) {
        delete filters[key as keyof MonitorFilters];
      }
    });

    const monitors = await service.getTCPMonitors(filters);
    res.json(monitors);
  } catch (err) {
    next(err);
  }
});

router.get('/ping', async (req, res, next) => {
  try {
    const filters: MonitorFilters = {
      agentRegion: req.query.region as string,
      success: req.query.success ? req.query.success === 'true' : undefined,
      targetHost: req.query.host as string,
      limit: req.query.limit ? parseInt(req.query.limit as string) : undefined,
      offset: req.query.offset ? parseInt(req.query.offset as string) : undefined,
      sortBy: req.query.sortBy as 'executedAt' | 'responseTime' | 'monitorId',
      sortOrder: req.query.sortOrder as 'asc' | 'desc'
    };

    // Remove undefined values
    Object.keys(filters).forEach(key => {
      if (filters[key as keyof MonitorFilters] === undefined) {
        delete filters[key as keyof MonitorFilters];
      }
    });

    const monitors = await service.getPINGMonitors(filters);
    res.json(monitors);
  } catch (err) {
    next(err);
  }
});

router.get('/dns', async (req, res, next) => {
  try {
    const filters: MonitorFilters = {
      agentRegion: req.query.region as string,
      success: req.query.success ? req.query.success === 'true' : undefined,
      targetHost: req.query.host as string,
      limit: req.query.limit ? parseInt(req.query.limit as string) : undefined,
      offset: req.query.offset ? parseInt(req.query.offset as string) : undefined,
      sortBy: req.query.sortBy as 'executedAt' | 'responseTime' | 'monitorId',
      sortOrder: req.query.sortOrder as 'asc' | 'desc'
    };

    // Remove undefined values
    Object.keys(filters).forEach(key => {
      if (filters[key as keyof MonitorFilters] === undefined) {
        delete filters[key as keyof MonitorFilters];
      }
    });

    const monitors = await service.getDNSMonitors(filters);
    res.json(monitors);
  } catch (err) {
    next(err);
  }
});

// Get monitor history by monitorId
router.get('/history/:monitorId', async (req, res, next) => {
  try {
    const filters: MonitorFilters = {
      agentRegion: req.query.region as string,
      limit: req.query.limit ? parseInt(req.query.limit as string) : undefined,
      offset: req.query.offset ? parseInt(req.query.offset as string) : undefined,
      sortBy: req.query.sortBy as 'executedAt' | 'responseTime' | 'monitorId',
      sortOrder: req.query.sortOrder as 'asc' | 'desc',
      activeOnly: req.query.activeOnly ? req.query.activeOnly === 'true' : undefined,
      maxAge: req.query.maxAge ? parseInt(req.query.maxAge as string) : undefined
    };

    // Add time range filtering
    if (req.query.startTime) {
      filters.startTime = new Date(req.query.startTime as string);
    }
    if (req.query.endTime) {
      filters.endTime = new Date(req.query.endTime as string);
    }

    // Remove undefined values
    Object.keys(filters).forEach(key => {
      if (filters[key as keyof MonitorFilters] === undefined) {
        delete filters[key as keyof MonitorFilters];
      }
    });

    const history = await service.getMonitorHistory(req.params.monitorId, filters);
    res.json(history);
  } catch (err) {
    next(err);
  }
});

// Get latest monitor data by monitorId
router.get('/latest/:monitorId', async (req, res, next) => {
  try {
    const monitor = await service.getLatestMonitorData(req.params.monitorId);
    if (!monitor) return res.status(404).json({ error: 'Monitor not found' });
    res.json(monitor);
  } catch (err) {
    next(err);
  }
});

// Get monitor by id
router.get('/:id', async (req, res, next) => {
  try {
    const monitor = await service.getMonitorById(Number(req.params.id));
    if (!monitor) return res.status(404).json({ error: 'Monitor not found' });
    res.json(monitor);
  } catch (err) {
    next(err);
  }
});

// Create new monitor
router.post('/', async (req, res, next) => {
  try {
    const created = await service.createMonitor(req.body);
    res.status(201).json(created);
  } catch (err) {
    next(err);
  }
});

// Update monitor
router.put('/:id', async (req, res, next) => {
  try {
    const updated = await service.updateMonitor(Number(req.params.id), req.body);
    res.json(updated);
  } catch (err) {
    next(err);
  }
});

// Delete monitor
router.delete('/:id', async (req, res, next) => {
  try {
    await service.deleteMonitor(Number(req.params.id));
    res.status(204).send();
  } catch (err) {
    next(err);
  }
});

export default router;