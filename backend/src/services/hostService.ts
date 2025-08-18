import { getAll, getById, create, update, remove } from '../repositories/hostRepository.js';
import type { NewHost } from '../models/host.js';

export async function getAllHosts() {
  return getAll();
}

export async function getHostById(id: number) {
  return getById(id);
}

export async function createHost(data: NewHost) {
  return create(data);
}

export async function updateHost(id: number, data: Partial<NewHost>) {
  return update(id, data);
}

export async function deleteHost(id: number) {
  return remove(id);
}
// Ensure this file is always a module
export {};
