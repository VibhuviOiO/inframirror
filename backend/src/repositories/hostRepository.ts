import prisma from '../prismaClient.js';
import type { Host, NewHost } from '../models/host.js';

export async function getAll() {
  return prisma.host.findMany();
}

export async function getById(id: number) {
  return prisma.host.findUnique({ where: { id } });
}

export async function create(data: NewHost) {
  return prisma.host.create({ data });
}

export async function update(id: number, data: Partial<NewHost>) {
  return prisma.host.update({ where: { id }, data });
}

export async function remove(id: number) {
  return prisma.host.delete({ where: { id } });
}
