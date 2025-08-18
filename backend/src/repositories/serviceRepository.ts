import prisma from '../prismaClient.js';
import type { Service, NewService } from '../models/service.js';

export async function getAll() {
  return prisma.service.findMany();
}

export async function getById(id: number) {
  return prisma.service.findUnique({ where: { id } });
}

export async function create(data: NewService) {
  return prisma.service.create({ data });
}

export async function update(id: number, data: Partial<NewService>) {
  return prisma.service.update({ where: { id }, data });
}

export async function remove(id: number) {
  return prisma.service.delete({ where: { id } });
}
