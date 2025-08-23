import prisma from '../prismaClient.js';
import type { Integration, NewIntegration } from '../models/integration.js';

export async function getAll() {
  return prisma.integration.findMany();
}

export async function getById(id: number) {
  return prisma.integration.findUnique({ where: { id } });
}

export async function create(data: NewIntegration) {
  return prisma.integration.create({ data });
}

export async function update(id: number, data: Partial<NewIntegration>) {
  return prisma.integration.update({ where: { id }, data });
}

export async function remove(id: number) {
  return prisma.integration.delete({ where: { id } });
}
