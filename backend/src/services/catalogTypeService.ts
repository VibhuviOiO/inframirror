import { getAll, getById, create, update, remove } from '../repositories/catalogTypeRepository.js';
import type { NewCatalogType } from '../models/catalogType.js';

export async function getAllCatalogTypes() {
  return getAll();
}

export async function getCatalogTypeById(id: number) {
  return getById(id);
}

export async function createCatalogType(data: NewCatalogType) {
  return create(data);
}

export async function updateCatalogType(id: number, data: Partial<NewCatalogType>) {
  return update(id, data);
}

export async function deleteCatalogType(id: number) {
  return remove(id);
}
