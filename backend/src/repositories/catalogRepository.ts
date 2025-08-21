export async function bulkRemove(ids: number[]) {
  return prisma.catalog.deleteMany({ where: { id: { in: ids } } });
}
import prisma from '../prismaClient.js';
import type { Catalog, NewCatalog } from '../models/catalog.js';

export async function getAll() {
  return prisma.catalog.findMany();
}

export async function getById(id: number) {
  return prisma.catalog.findUnique({ where: { id } });
}

export async function create(data: NewCatalog) {
  return prisma.catalog.create({ data });
}

export async function update(id: number, data: Partial<NewCatalog>) {
  return prisma.catalog.update({ where: { id }, data });
}

export async function remove(id: number) {
  return prisma.catalog.delete({ where: { id } });
}
