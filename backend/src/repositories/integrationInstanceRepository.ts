import prisma from '../prismaClient.js';
import type { IntegrationInstance, NewIntegrationInstance } from '../models/integrationInstance.js';

export async function getAll() {
  return prisma.integrationInstance.findMany();
}

export async function getById(id: number) {
  return prisma.integrationInstance.findUnique({ where: { id } });
}

export async function create(data: NewIntegrationInstance) {
  // Ensure config is valid JSON and nulls are undefined for Prisma
  const { config, datacenterId, clusterId, environmentId, port, ...rest } = data;
  return prisma.integrationInstance.create({
    data: {
      ...rest,
      config: config == null ? undefined : JSON.parse(JSON.stringify(config)),
      datacenterId: datacenterId == null ? undefined : datacenterId,
      clusterId: clusterId == null ? undefined : clusterId,
      environmentId: environmentId == null ? undefined : environmentId,
      port: port == null ? undefined : port,
    },
  });
}

export async function update(id: number, data: Partial<NewIntegrationInstance>) {
  const { config, datacenterId, clusterId, environmentId, port, ...rest } = data;
  return prisma.integrationInstance.update({
    where: { id },
    data: {
      ...rest,
      config: config == null ? undefined : JSON.parse(JSON.stringify(config)),
      datacenterId: datacenterId == null ? undefined : datacenterId,
      clusterId: clusterId == null ? undefined : clusterId,
      environmentId: environmentId == null ? undefined : environmentId,
      port: port == null ? undefined : port,
    },
  });
}

export async function remove(id: number) {
  return prisma.integrationInstance.delete({ where: { id } });
}
