import { getAll, getById, create, update, remove } from '../repositories/regionRepository.js';
import type { NewRegion } from '../models/region.js';


export async function getAllRegions() {
  return getAll();
}

export async function getRegionById(id: number) {
  return getById(id);
}

export async function createRegion(data: NewRegion) {
  return create(data);
}

export async function updateRegion(id: number, data: Partial<NewRegion>) {
  return update(id, data);
}

export async function deleteRegion(id: number) {
  return remove(id);
}
