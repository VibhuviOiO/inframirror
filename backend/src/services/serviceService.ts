import { getAll, getById, create, update, remove } from '../repositories/serviceRepository.js';
import type { NewService } from '../models/service.js';

export async function getAllServices() {
  return getAll();
}

export async function getServiceById(id: number) {
  return getById(id);
}

export async function createService(data: NewService) {
  return create(data);
}

export async function updateService(id: number, data: Partial<NewService>) {
  return update(id, data);
}

export async function deleteService(id: number) {
  return remove(id);
}
// Ensure this file is always a module
export {};
