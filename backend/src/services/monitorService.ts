import * as repository from '../repositories/monitorRepository.js';
import type { NewMonitor, MonitorFilters } from '../models/monitor.js';
import { logInfo, logError } from '../utils/logger.js';

export async function getAllMonitors(filters?: MonitorFilters) {
  try {
    logInfo('Fetching all monitors with filters', filters);
    return await repository.getLatestForEachMonitor(filters);
  } catch (err) {
    logError('Service error: getAllMonitors', err);
    throw err;
  }
}

export async function getMonitorById(id: number) {
  try {
    logInfo(`Fetching monitor by id: ${id}`);
    return await repository.getById(id);
  } catch (err) {
    logError('Service error: getMonitorById', err);
    throw err;
  }
}

export async function getMonitorsByType(monitorType: string, filters?: MonitorFilters) {
  try {
    logInfo(`Fetching monitors by type: ${monitorType}`, filters);
    return await repository.getByType(monitorType, filters);
  } catch (err) {
    logError('Service error: getMonitorsByType', err);
    throw err;
  }
}

export async function getMonitorHistory(monitorId: string, filters?: MonitorFilters) {
  try {
    logInfo(`Fetching monitor history for: ${monitorId}`, filters);
    return await repository.getByMonitorId(monitorId, filters);
  } catch (err) {
    logError('Service error: getMonitorHistory', err);
    throw err;
  }
}

export async function getLatestMonitorData(monitorId: string) {
  try {
    logInfo(`Fetching latest monitor data for: ${monitorId}`);
    return await repository.getLatestByMonitorId(monitorId);
  } catch (err) {
    logError('Service error: getLatestMonitorData', err);
    throw err;
  }
}

export async function createMonitor(data: NewMonitor) {
  try {
    logInfo('Creating monitor', data);
    return await repository.create(data);
  } catch (err) {
    logError('Service error: createMonitor', err);
    throw err;
  }
}

export async function updateMonitor(id: number, data: Partial<NewMonitor>) {
  try {
    logInfo(`Updating monitor id: ${id}`, data);
    return await repository.update(id, data);
  } catch (err) {
    logError('Service error: updateMonitor', err);
    throw err;
  }
}

export async function deleteMonitor(id: number) {
  try {
    logInfo(`Deleting monitor id: ${id}`);
    return await repository.remove(id);
  } catch (err) {
    logError('Service error: deleteMonitor', err);
    throw err;
  }
}

export async function getMonitorStats() {
  try {
    logInfo('Fetching monitor statistics');
    return await repository.getStats();
  } catch (err) {
    logError('Service error: getMonitorStats', err);
    throw err;
  }
}

export async function getMonitorRegions() {
  try {
    logInfo('Fetching monitor regions');
    return await repository.getRegions();
  } catch (err) {
    logError('Service error: getMonitorRegions', err);
    throw err;
  }
}

export async function getLatestMonitors(filters?: MonitorFilters) {
  try {
    logInfo('Fetching latest monitors (deduplicated)', filters);
    return await repository.getLatestForEachMonitor(filters);
  } catch (err) {
    logError('Service error: getLatestMonitors', err);
    throw err;
  }
}

// Protocol-specific convenience methods using deduplicated latest data
export async function getHTTPMonitors(filters?: MonitorFilters) {
  // Get both HTTP and HTTPS monitors for the HTTP page
  const httpMonitors = await getLatestMonitors({ ...filters, monitorType: 'HTTP' });
  const httpsMonitors = await getLatestMonitors({ ...filters, monitorType: 'HTTPS' });
  
  // Combine and sort by execution time (most recent first)
  const allMonitors = [...httpMonitors, ...httpsMonitors];
  return allMonitors.sort((a, b) => {
    const timeA = a?.executedAt ? new Date(a.executedAt).getTime() : 0;
    const timeB = b?.executedAt ? new Date(b.executedAt).getTime() : 0;
    return timeB - timeA;
  });
}

export async function getTCPMonitors(filters?: MonitorFilters) {
  return getLatestMonitors({ ...filters, monitorType: 'TCP' });
}

export async function getPINGMonitors(filters?: MonitorFilters) {
  return getLatestMonitors({ ...filters, monitorType: 'PING' });
}

export async function getDNSMonitors(filters?: MonitorFilters) {
  return getLatestMonitors({ ...filters, monitorType: 'DNS' });
}

// Ensure this file is always a module
export {};