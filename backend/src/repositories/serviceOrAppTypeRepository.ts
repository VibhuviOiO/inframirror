import prisma from '../prismaClient.js';
import type { ServiceOrAppType, NewServiceOrAppType } from '../models/serviceOrAppType.js';

export async function getAll() {
  return prisma.serviceOrAppType.findMany();
}

export async function getById(id: number) {
  return prisma.serviceOrAppType.findUnique({ where: { id } });
}

export async function create(data: NewServiceOrAppType) {
  return prisma.serviceOrAppType.create({ data });
}

export async function update(id: number, data: Partial<NewServiceOrAppType>) {
  return prisma.serviceOrAppType.update({ where: { id }, data });
}

export async function remove(id: number) {
  return prisma.serviceOrAppType.delete({ where: { id } });
}
