import prisma from '../prismaClient.js';
import type { Monitor, NewMonitor, MonitorFilters } from '../models/monitor.js';

export async function getAll(filters?: MonitorFilters) {
  const {
    monitorType,
    agentRegion,
    success,
    targetHost,
    limit = 100,
    offset = 0,
    sortBy = 'executedAt',
    sortOrder = 'desc'
  } = filters || {};

  const where: any = {};
  
  if (monitorType) {
    where.monitorType = monitorType;
  }
  
  if (agentRegion) {
    where.agentRegion = agentRegion;
  }
  
  if (success !== undefined) {
    where.success = success;
  }
  
  if (targetHost) {
    where.targetHost = {
      contains: targetHost,
      mode: 'insensitive'
    };
  }

  return prisma.monitors.findMany({
    where,
    orderBy: {
      [sortBy]: sortOrder
    },
    take: limit,
    skip: offset
  });
}

export async function getById(id: number) {
  return prisma.monitors.findUnique({ 
    where: { id }
  });
}

export async function getByMonitorId(monitorId: string) {
  return prisma.monitors.findMany({ 
    where: { monitorId },
    orderBy: { executedAt: 'desc' }
  });
}

export async function getByType(monitorType: string, filters?: MonitorFilters) {
  return getAll({ ...filters, monitorType });
}

export async function getLatestByMonitorId(monitorId: string) {
  return prisma.monitors.findFirst({
    where: { monitorId },
    orderBy: { executedAt: 'desc' }
  });
}

export async function getLatestForEachMonitor(filters?: MonitorFilters) {
  const {
    monitorType,
    agentRegion,
    success,
    targetHost,
    activeOnly,
    maxAge = 15 // Default to 15 minutes
  } = filters || {};

  // Build the where clause
  const whereClause: any = {};
  if (monitorType) whereClause.monitorType = monitorType as any;
  if (agentRegion) whereClause.agentRegion = agentRegion;
  if (success !== undefined) whereClause.success = success;
  if (targetHost) {
    whereClause.targetHost = {
      contains: targetHost,
      mode: 'insensitive'
    };
  }

  // Add activeOnly filter - only show monitors with recent activity
  if (activeOnly) {
    const cutoffTime = new Date(Date.now() - (maxAge * 60 * 1000)); // Convert minutes to milliseconds
    whereClause.executedAt = {
      gte: cutoffTime
    };
  }

  // Get all unique combinations of monitorId and agentRegion
  const uniqueMonitorRegions = await prisma.monitors.groupBy({
    by: ['monitorId', 'agentRegion'],
    where: whereClause
  });

  // Get the latest record for each monitorId + agentRegion combination
  const latestMonitors = await Promise.all(
    uniqueMonitorRegions.map(async ({ monitorId, agentRegion: region }) => {
      const monitorWhere: any = { monitorId };
      if (region !== null) monitorWhere.agentRegion = region;
      if (monitorType) monitorWhere.monitorType = monitorType as any;
      if (success !== undefined) monitorWhere.success = success;
      if (targetHost) {
        monitorWhere.targetHost = {
          contains: targetHost,
          mode: 'insensitive'
        };
      }

      // Add activeOnly filter for individual monitor lookup
      if (activeOnly) {
        const cutoffTime = new Date(Date.now() - (maxAge * 60 * 1000));
        monitorWhere.executedAt = {
          gte: cutoffTime
        };
      }

      return prisma.monitors.findFirst({
        where: monitorWhere,
        orderBy: { executedAt: 'desc' }
      });
    })
  );

  return latestMonitors.filter(Boolean).sort((a, b) => 
    new Date(b!.executedAt).getTime() - new Date(a!.executedAt).getTime()
  );
}

export async function create(data: NewMonitor) {
  // Handle JSON fields properly
  const {
    rawResponseHeaders,
    rawRequestHeaders,
    rawNetworkData,
    ...rest
  } = data;

  return prisma.monitors.create({
    data: {
      ...rest,
      rawResponseHeaders: rawResponseHeaders ? JSON.parse(JSON.stringify(rawResponseHeaders)) : undefined,
      rawRequestHeaders: rawRequestHeaders ? JSON.parse(JSON.stringify(rawRequestHeaders)) : undefined,
      rawNetworkData: rawNetworkData ? JSON.parse(JSON.stringify(rawNetworkData)) : undefined,
    },
  });
}

export async function update(id: number, data: Partial<NewMonitor>) {
  const {
    rawResponseHeaders,
    rawRequestHeaders,
    rawNetworkData,
    ...rest
  } = data;

  return prisma.monitors.update({
    where: { id },
    data: {
      ...rest,
      rawResponseHeaders: rawResponseHeaders ? JSON.parse(JSON.stringify(rawResponseHeaders)) : undefined,
      rawRequestHeaders: rawRequestHeaders ? JSON.parse(JSON.stringify(rawRequestHeaders)) : undefined,
      rawNetworkData: rawNetworkData ? JSON.parse(JSON.stringify(rawNetworkData)) : undefined,
    },
  });
}

export async function remove(id: number) {
  return prisma.monitors.delete({ where: { id } });
}

export async function getStats() {
  const total = await prisma.monitors.count();
  const successful = await prisma.monitors.count({ where: { success: true } });
  const failed = await prisma.monitors.count({ where: { success: false } });
  
  const avgResponseTime = await prisma.monitors.aggregate({
    _avg: { responseTime: true },
    where: { responseTime: { not: null } }
  });

  const recentChecks = await prisma.monitors.count({
    where: {
      executedAt: {
        gte: new Date(Date.now() - 5 * 60 * 1000) // Last 5 minutes
      }
    }
  });

  return {
    total,
    successful,
    failed,
    avgResponseTime: Math.round(avgResponseTime._avg.responseTime || 0),
    recentChecks
  };
}

export async function getRegions() {
  const result = await prisma.monitors.groupBy({
    by: ['agentRegion'],
    _count: true,
    where: {
      agentRegion: { not: null }
    }
  });

  return result.map(item => ({
    region: item.agentRegion,
    count: item._count
  }));
}