
import { getAll, getById, create, update, remove } from '../repositories/environmentRepository.js';
import type { NewEnvironment } from '../models/environment.js';
import { logInfo, logError } from '../utils/logger.js';


export async function getAllEnvironments() {
  try {
    logInfo('Fetching all environments');
    return await getAll();
  } catch (err) {
    logError('Service error: getAllEnvironments', err);
    throw err;
  }
}


export async function getEnvironmentById(id: number) {
  try {
    logInfo(`Fetching environment by id: ${id}`);
    return await getById(id);
  } catch (err) {
    logError('Service error: getEnvironmentById', err);
    throw err;
  }
}


export async function createEnvironment(data: NewEnvironment) {
  try {
    logInfo('Creating environment', data);
    return await create(data);
  } catch (err) {
    logError('Service error: createEnvironment', err);
    throw err;
  }
}


export async function updateEnvironment(id: number, data: Partial<NewEnvironment>) {
  try {
    logInfo(`Updating environment id: ${id}`, data);
    return await update(id, data);
  } catch (err) {
    logError('Service error: updateEnvironment', err);
    throw err;
  }
}


export async function deleteEnvironment(id: number) {
  try {
    logInfo(`Deleting environment id: ${id}`);
    return await remove(id);
  } catch (err) {
    logError('Service error: deleteEnvironment', err);
    throw err;
  }
}
// Ensure this file is always a module
export {};
