import * as repo from '../repositories/serviceCatalogRepository.js';

export const create = (data: any) => repo.createServiceCatalog(data);
export const list = () => repo.listServiceCatalogs();
export const getById = (id: number) => repo.getServiceCatalogById(id);
export const update = (id: number, data: any) => repo.updateServiceCatalog(id, data);
export const remove = (id: number) => repo.deleteServiceCatalog(id);
