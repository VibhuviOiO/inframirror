
import { getAll, getById, create, update, remove } from '../repositories/integrationInstanceRepository.js';
import type { NewIntegrationInstance } from '../models/integrationInstance.js';
import { logInfo, logError } from '../utils/logger.js';


export async function getAllIntegrationInstances() {
  try {
    logInfo('Fetching all integration instances');
    return await getAll();
  } catch (err) {
    logError('Service error: getAllIntegrationInstances', err);
    throw err;
  }
}


export async function getIntegrationInstanceById(id: number) {
  try {
    logInfo(`Fetching integration instance by id: ${id}`);
    return await getById(id);
  } catch (err) {
    logError('Service error: getIntegrationInstanceById', err);
    throw err;
  }
}


export async function createIntegrationInstance(data: NewIntegrationInstance) {
  try {
    logInfo('Creating integration instance', data);
    return await create(data);
  } catch (err) {
    logError('Service error: createIntegrationInstance', err);
    throw err;
  }
}


export async function updateIntegrationInstance(id: number, data: Partial<NewIntegrationInstance>) {
  try {
    logInfo(`Updating integration instance id: ${id}`, data);
    return await update(id, data);
  } catch (err) {
    logError('Service error: updateIntegrationInstance', err);
    throw err;
  }
}


export async function deleteIntegrationInstance(id: number) {
  try {
    logInfo(`Deleting integration instance id: ${id}`);
    return await remove(id);
  } catch (err) {
    logError('Service error: deleteIntegrationInstance', err);
    throw err;
  }
}
