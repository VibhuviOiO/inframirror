import prisma from '../prismaClient.js';
import type { Datacenter, NewDatacenter } from '../models/datacenter.js';

export async function getAll() {
  return prisma.datacenter.findMany();
}

export async function getById(id: number) {
  return prisma.datacenter.findUnique({ where: { id } });
}

export async function create(data: NewDatacenter) {
  return prisma.datacenter.create({ data });
}

export async function update(id: number, data: Partial<NewDatacenter>) {
  return prisma.datacenter.update({ where: { id }, data });
}

export async function remove(id: number) {
  return prisma.datacenter.delete({ where: { id } });
}
