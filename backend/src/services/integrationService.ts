
import { getAll, getById, create, update, remove } from '../repositories/integrationRepository.js';
import type { NewIntegration } from '../models/integration.js';
import { logInfo, logError } from '../utils/logger.js';


export async function getAllIntegrations() {
  try {
    logInfo('Fetching all integrations');
    return await getAll();
  } catch (err) {
    logError('Service error: getAllIntegrations', err);
    throw err;
  }
}


export async function getIntegrationById(id: number) {
  try {
    logInfo(`Fetching integration by id: ${id}`);
    return await getById(id);
  } catch (err) {
    logError('Service error: getIntegrationById', err);
    throw err;
  }
}


export async function createIntegration(data: NewIntegration) {
  try {
    logInfo('Creating integration', data);
    return await create(data);
  } catch (err) {
    logError('Service error: createIntegration', err);
    throw err;
  }
}


export async function updateIntegration(id: number, data: Partial<NewIntegration>) {
  try {
    logInfo(`Updating integration id: ${id}`, data);
    return await update(id, data);
  } catch (err) {
    logError('Service error: updateIntegration', err);
    throw err;
  }
}


export async function deleteIntegration(id: number) {
  try {
    logInfo(`Deleting integration id: ${id}`);
    return await remove(id);
  } catch (err) {
    logError('Service error: deleteIntegration', err);
    throw err;
  }
}
