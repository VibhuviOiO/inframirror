import prisma from '../prismaClient.js';
import type { Region, NewRegion } from '../models/region.js';

export async function getAll() {
  return prisma.region.findMany();
}

export async function getById(id: number) {
  return prisma.region.findUnique({ where: { id } });
}

export async function create(data: NewRegion) {
  return prisma.region.create({ data });
}

export async function update(id: number, data: Partial<NewRegion>) {
  return prisma.region.update({ where: { id }, data });
}

export async function remove(id: number) {
  return prisma.region.delete({ where: { id } });
}
