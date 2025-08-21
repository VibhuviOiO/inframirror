import prisma from '../prismaClient.js';
import type { CatalogType, NewCatalogType } from '../models/catalogType.js';

export async function getAll() {
  return prisma.catalogType.findMany();
}

export async function getById(id: number) {
  return prisma.catalogType.findUnique({ where: { id } });
}

export async function create(data: NewCatalogType) {
  return prisma.catalogType.create({ data });
}

export async function update(id: number, data: Partial<NewCatalogType>) {
  return prisma.catalogType.update({ where: { id }, data });
}

export async function remove(id: number) {
  return prisma.catalogType.delete({ where: { id } });
}
