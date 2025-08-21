import { bulkRemove } from '../repositories/catalogRepository.js';
export async function bulkDeleteCatalogs(ids: number[]) {
  return bulkRemove(ids);
}
import { getAll, getById, create, update, remove } from '../repositories/catalogRepository.js';
import type { NewCatalog } from '../models/catalog.js';

export async function getAllCatalogs() {
  return getAll();
}

export async function getCatalogById(id: number) {
  return getById(id);
}

export async function createCatalog(data: NewCatalog) {
  return create(data);
}

export async function updateCatalog(id: number, data: Partial<NewCatalog>) {
  return update(id, data);
}

export async function deleteCatalog(id: number) {
  return remove(id);
}
