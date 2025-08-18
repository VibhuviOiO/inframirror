import prisma from '../prismaClient.js';
import type { ServiceCatalog, NewServiceCatalog } from '../models/serviceCatalog.js';

export async function getAll() {
  return prisma.serviceCatalog.findMany();
}

export async function getById(id: number) {
  return prisma.serviceCatalog.findUnique({ where: { id } });
}

export async function create(data: NewServiceCatalog) {
  return prisma.serviceCatalog.create({ data });
}

export async function update(id: number, data: Partial<NewServiceCatalog>) {
  return prisma.serviceCatalog.update({ where: { id }, data });
}

export async function remove(id: number) {
  return prisma.serviceCatalog.delete({ where: { id } });
}
