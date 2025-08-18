import { getAll, getById, create, update, remove } from '../repositories/serviceCatalogRepository.js';
import type { NewServiceCatalog } from '../models/serviceCatalog.js';

export async function getAllServiceCatalogs() {
  return getAll();
}

export async function getServiceCatalogById(id: number) {
  return getById(id);
}

export async function createServiceCatalog(data: NewServiceCatalog) {
  return create(data);
}

export async function updateServiceCatalog(id: number, data: Partial<NewServiceCatalog>) {
  return update(id, data);
}

export async function deleteServiceCatalog(id: number) {
  return remove(id);
}
// Ensure this file is always a module
export {};
