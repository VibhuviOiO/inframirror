import prisma from '../prismaClient.js';
import type { Environment, NewEnvironment } from '../models/environment.js';

export async function getAll() {
  return prisma.environment.findMany();
}

export async function getById(id: number) {
  return prisma.environment.findUnique({ where: { id } });
}

export async function create(data: NewEnvironment) {
  return prisma.environment.create({ data });
}

export async function update(id: number, data: Partial<NewEnvironment>) {
  return prisma.environment.update({ where: { id }, data });
}

export async function remove(id: number) {
  return prisma.environment.delete({ where: { id } });
}
