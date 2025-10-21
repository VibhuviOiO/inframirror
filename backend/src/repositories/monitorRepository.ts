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

export async function getByMonitorId(monitorId: string, filters?: MonitorFilters) {
  const {
    agentRegion,
    success,
    limit = 1000,
    offset = 0,
    sortBy = 'executedAt',
    sortOrder = 'desc',
    startTime,
    endTime
  } = filters || {};

  const where: any = { monitorId };
  
  if (agentRegion) {
    where.agentRegion = agentRegion;
  }
  
  if (success !== undefined) {
    where.success = success;
  }
  
  if (startTime || endTime) {
    where.executedAt = {};
    if (startTime) {
      where.executedAt.gte = startTime;
    }
    if (endTime) {
      where.executedAt.lte = endTime;
    }
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

  // Build the where clause for raw SQL
  const conditions: string[] = [];
  const params: any[] = [];
  let paramIndex = 1;

  if (monitorType) {
    conditions.push(`"monitorType"::text = $${paramIndex}`);
    params.push(monitorType);
    paramIndex++;
  }

  if (agentRegion) {
    conditions.push(`"agentRegion" = $${paramIndex}`);
    params.push(agentRegion);
    paramIndex++;
  }

  if (success !== undefined) {
    conditions.push(`"success" = $${paramIndex}`);
    params.push(success);
    paramIndex++;
  }

  if (targetHost) {
    conditions.push(`"targetHost" ILIKE $${paramIndex}`);
    params.push(`%${targetHost}%`);
    paramIndex++;
  }

  if (activeOnly) {
    const cutoffTime = new Date(Date.now() - (maxAge * 60 * 1000));
    conditions.push(`"executedAt" >= $${paramIndex}`);
    params.push(cutoffTime);
    paramIndex++;
  }

  const whereClause = conditions.length > 0 ? `WHERE ${conditions.join(' AND ')}` : '';

  // Use raw SQL with DISTINCT ON for optimal performance
  // This gets the latest record for each (monitorId, agentRegion) combination in a single query
  const query = `
    SELECT DISTINCT ON ("monitorId", "agentRegion")
      id, "monitorId", "agentRegion", "monitorType", "targetHost", "success",
      "responseTime", "responseStatusCode", "executedAt", "errorMessage", "rawResponseHeaders",
      "rawRequestHeaders", "rawNetworkData"
    FROM monitors
    ${whereClause}
    ORDER BY "monitorId", "agentRegion", "executedAt" DESC
  `;

  const latestMonitors = await prisma.$queryRawUnsafe(query, ...params);

  // Sort by executedAt desc for final ordering
  return (latestMonitors as any[]).sort((a, b) =>
    new Date(b.executedAt).getTime() - new Date(a.executedAt).getTime()
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