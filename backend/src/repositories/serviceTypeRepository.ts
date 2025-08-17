import prisma from '../prismaClient.js';

export const createServiceType = async (data: { name: string; description?: string }) => prisma.serviceType.create({ data });
export const listServiceTypes = async () => prisma.serviceType.findMany({ orderBy: { id: 'asc' } });
export const getServiceTypeById = async (id: number) => prisma.serviceType.findUnique({ where: { id } });
export const updateServiceType = async (id: number, data: any) => {
  try {
    return await prisma.serviceType.update({ where: { id }, data });
  } catch (err: any) {
    if (err.code === 'P2025') return null;
    throw err;
  }
};
export const deleteServiceType = async (id: number) => {
  try {
    await prisma.serviceType.delete({ where: { id } });
    return true;
  } catch (err: any) {
    if (err.code === 'P2025') return false;
    throw err;
  }
};
