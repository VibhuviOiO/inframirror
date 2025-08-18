import { getAll, getById, create, update, remove } from '../repositories/serviceOrAppTypeRepository.js';
import type { NewServiceOrAppType } from '../models/serviceOrAppType.js';

export async function getAllServiceOrAppTypes() {
  return getAll();
}

export async function getServiceOrAppTypeById(id: number) {
  return getById(id);
}

export async function createServiceOrAppType(data: NewServiceOrAppType) {
  return create(data);
}

export async function updateServiceOrAppType(id: number, data: Partial<NewServiceOrAppType>) {
  return update(id, data);
}

export async function deleteServiceOrAppType(id: number) {
  return remove(id);
}
// Ensure this file is always a module
export {};
