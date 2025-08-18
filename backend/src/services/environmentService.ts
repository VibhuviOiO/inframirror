import { getAll, getById, create, update, remove } from '../repositories/environmentRepository.js';
import type { NewEnvironment } from '../models/environment.js';

export async function getAllEnvironments() {
  return getAll();
}

export async function getEnvironmentById(id: number) {
  return getById(id);
}

export async function createEnvironment(data: NewEnvironment) {
  return create(data);
}

export async function updateEnvironment(id: number, data: Partial<NewEnvironment>) {
  return update(id, data);
}

export async function deleteEnvironment(id: number) {
  return remove(id);
}
// Ensure this file is always a module
export {};
