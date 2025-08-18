import prisma from '../prismaClient.js';
import type { Team, NewTeam } from '../models/team.js';

export async function getAll() {
  return prisma.team.findMany();
}

export async function getById(id: number) {
  return prisma.team.findUnique({ where: { id } });
}

export async function create(data: NewTeam) {
  return prisma.team.create({ data });
}

export async function update(id: number, data: Partial<NewTeam>) {
  return prisma.team.update({ where: { id }, data });
}

export async function remove(id: number) {
  return prisma.team.delete({ where: { id } });
}
