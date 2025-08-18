import { getAll, getById, create, update, remove } from '../repositories/applicationCatalogRepository.js';
import type { NewApplicationCatalog } from '../models/applicationCatalog.js';

export async function getAllApplicationCatalogs() {
  return getAll();
}

export async function getApplicationCatalogById(id: number) {
  return getById(id);
}

export async function createApplicationCatalog(data: NewApplicationCatalog) {
  return create(data);
}

export async function updateApplicationCatalog(id: number, data: Partial<NewApplicationCatalog>) {
  return update(id, data);
}

export async function deleteApplicationCatalog(id: number) {
  return remove(id);
}
// Ensure this file is always a module
export {};
