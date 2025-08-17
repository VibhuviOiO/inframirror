import prisma from '../prismaClient.js';

export const createServiceCatalog = async (data: { name: string; description?: string; defaultTypeId?: number }) => {
  return prisma.serviceCatalog.create({ data });
};

export const listServiceCatalogs = async () => prisma.serviceCatalog.findMany({ orderBy: { id: 'asc' } });

export const getServiceCatalogById = async (id: number) => prisma.serviceCatalog.findUnique({ where: { id } });

export const updateServiceCatalog = async (id: number, data: any) => {
  try {
    return await prisma.serviceCatalog.update({ where: { id }, data });
  } catch (err: any) {
    if (err.code === 'P2025') return null;
    throw err;
  }
};

export const deleteServiceCatalog = async (id: number) => {
  try {
    await prisma.serviceCatalog.delete({ where: { id } });
    return true;
  } catch (err: any) {
    if (err.code === 'P2025') return false;
    throw err;
  }
};
