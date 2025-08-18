import { getAll, getById, create, update, remove } from '../repositories/applicationRepository.js';
import type { NewApplication } from '../models/application.js';

export async function getAllApplications() {
  return getAll();
}

export async function getApplicationById(id: number) {
  return getById(id);
}

export async function createApplication(data: NewApplication) {
  return create(data);
}

export async function updateApplication(id: number, data: Partial<NewApplication>) {
  return update(id, data);
}

export async function deleteApplication(id: number) {
  return remove(id);
}
// Ensure this file is always a module
export {};
