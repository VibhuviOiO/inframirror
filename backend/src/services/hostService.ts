
import { getAll, getById, create, update, remove } from '../repositories/hostRepository.js';
import type { NewHost } from '../models/host.js';
import { logInfo, logError } from '../utils/logger.js';


export async function getAllHosts() {
  try {
    logInfo('Fetching all hosts');
    return await getAll();
  } catch (err) {
    logError('Service error: getAllHosts', err);
    throw err;
  }
}


export async function getHostById(id: number) {
  try {
    logInfo(`Fetching host by id: ${id}`);
    return await getById(id);
  } catch (err) {
    logError('Service error: getHostById', err);
    throw err;
  }
}


export async function createHost(data: NewHost) {
  try {
    logInfo('Creating host', data);
    return await create(data);
  } catch (err) {
    logError('Service error: createHost', err);
    throw err;
  }
}


export async function updateHost(id: number, data: Partial<NewHost>) {
  try {
    logInfo(`Updating host id: ${id}`, data);
    return await update(id, data);
  } catch (err) {
    logError('Service error: updateHost', err);
    throw err;
  }
}


export async function deleteHost(id: number) {
  try {
    logInfo(`Deleting host id: ${id}`);
    return await remove(id);
  } catch (err) {
    logError('Service error: deleteHost', err);
    throw err;
  }
}
// Ensure this file is always a module
export {};
