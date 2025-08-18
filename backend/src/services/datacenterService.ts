import { getAll, getById, create, update, remove } from '../repositories/datacenterRepository.js';
import type { NewDatacenter } from '../models/datacenter.js';

export async function getAllDatacenters() {
  return getAll();
}

export async function getDatacenterById(id: number) {
  return getById(id);
}

export async function createDatacenter(data: NewDatacenter) {
  return create(data);
}

export async function updateDatacenter(id: number, data: Partial<NewDatacenter>) {
  return update(id, data);
}

export async function deleteDatacenter(id: number) {
  return remove(id);
}
// Ensure this file is always a module
export {};
