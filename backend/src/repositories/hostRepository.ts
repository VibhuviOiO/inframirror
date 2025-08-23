import prisma from '../prismaClient.js';
import type { Host, NewHost } from '../models/host.js';

export async function getAll() {
  return prisma.host.findMany();
}

export async function getById(id: number) {
  return prisma.host.findUnique({ where: { id } });
}


export async function create(data: NewHost) {
  // Ensure tags is valid JSON and nulls are undefined
  const { tags, ...rest } = data;
  return prisma.host.create({
    data: {
      ...rest,
      tags: tags == null ? undefined : JSON.parse(JSON.stringify(tags)),
    },
  });
}

export async function update(id: number, data: Partial<NewHost>) {
  const { tags, ...rest } = data;
  return prisma.host.update({
    where: { id },
    data: {
      ...rest,
      tags: tags == null ? undefined : JSON.parse(JSON.stringify(tags)),
    },
  });
}

export async function remove(id: number) {
  return prisma.host.delete({ where: { id } });
}


