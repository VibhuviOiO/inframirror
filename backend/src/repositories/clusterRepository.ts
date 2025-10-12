import prisma from '../prismaClient.js';
import type { Cluster, NewCluster } from '../models/cluster.js';

export async function getAll() {
  return prisma.cluster.findMany();
}

export async function getById(id: number) {
  return prisma.cluster.findUnique({ where: { id } });
}

export async function create(data: NewCluster) {
  return prisma.cluster.create({ data });
}

export async function update(id: number, data: Partial<NewCluster>) {
  return prisma.cluster.update({ where: { id }, data });
}

export async function remove(id: number) {
  return prisma.cluster.delete({ where: { id } });
}
