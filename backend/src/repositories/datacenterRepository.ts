import prisma from '../prismaClient.js';
import type { Datacenter, NewDatacenter } from '../models/datacenter.js';

export const createTableIfNotExists = async () => {
  // Prisma manages schema via migrations or `prisma db push`.
  return;
};

export const createDatacenter = async (dc: NewDatacenter): Promise<Datacenter> => {
  const created = await prisma.datacenter.create({
    data: {
      name: dc.name,
  shortName: dc.shortName,
  publicCIDR: (dc as any).publicCIDR,
  privateCIDR: (dc as any).privateCIDR,
    },
  });
  return mapPrisma(created);
};

export const getAllDatacenters = async (): Promise<Datacenter[]> => {
  const list = await prisma.datacenter.findMany({ orderBy: { id: 'asc' } });
  return list.map(mapPrisma);
};

export const getDatacenterById = async (id: number): Promise<Datacenter | null> => {
  const item = await prisma.datacenter.findUnique({ where: { id } });
  return item ? mapPrisma(item) : null;
};

export const updateDatacenter = async (id: number, dc: Partial<NewDatacenter>): Promise<Datacenter | null> => {
  try {
    const updated = await prisma.datacenter.update({
      where: { id },
      data: { name: dc.name, shortName: dc.shortName, publicCIDR: (dc as any).publicCIDR, privateCIDR: (dc as any).privateCIDR },
    });
    return mapPrisma(updated);
  } catch (err: any) {
    // P2025: record not found
    if (err.code === 'P2025') return null;
    throw err;
  }
};

export const deleteDatacenter = async (id: number): Promise<boolean> => {
  try {
    await prisma.datacenter.delete({ where: { id } });
    return true;
  } catch (err: any) {
    if (err.code === 'P2025') return false;
    throw err;
  }
};

const mapPrisma = (p: any): Datacenter => ({
  id: p.id,
  name: p.name,
  shortName: p.shortName,
  publicCIDR: p.publicCIDR ?? null,
  privateCIDR: p.privateCIDR ?? null,
});
