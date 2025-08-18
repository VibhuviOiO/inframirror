import prisma from '../prismaClient.js';
import type { Application, NewApplication } from '../models/application.js';

export async function getAll() {
  return prisma.application.findMany();
}

export async function getById(id: number) {
  return prisma.application.findUnique({ where: { id } });
}

export async function create(data: NewApplication) {
  return prisma.application.create({ data });
}

export async function update(id: number, data: Partial<NewApplication>) {
  return prisma.application.update({ where: { id }, data });
}

export async function remove(id: number) {
  return prisma.application.delete({ where: { id } });
}
