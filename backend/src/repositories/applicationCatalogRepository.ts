import prisma from '../prismaClient.js';
import type { ApplicationCatalog, NewApplicationCatalog } from '../models/applicationCatalog.js';

export async function getAll() {
  return prisma.applicationCatalog.findMany();
}

export async function getById(id: number) {
  return prisma.applicationCatalog.findUnique({ where: { id } });
}

export async function create(data: NewApplicationCatalog) {
  return prisma.applicationCatalog.create({ data });
}

export async function update(id: number, data: Partial<NewApplicationCatalog>) {
  return prisma.applicationCatalog.update({ where: { id }, data });
}

export async function remove(id: number) {
  return prisma.applicationCatalog.delete({ where: { id } });
}
