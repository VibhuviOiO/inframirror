
import { getAll, getById, create, update, remove } from '../repositories/datacenterRepository.js';
import type { NewDatacenter } from '../models/datacenter.js';
import { logInfo, logError } from '../utils/logger.js';


export async function getAllDatacenters() {
  try {
    logInfo('Fetching all datacenters');
    return await getAll();
  } catch (err) {
    logError('Service error: getAllDatacenters', err);
    throw err;
  }
}


export async function getDatacenterById(id: number) {
  try {
    logInfo(`Fetching datacenter by id: ${id}`);
    return await getById(id);
  } catch (err) {
    logError('Service error: getDatacenterById', err);
    throw err;
  }
}


export async function createDatacenter(data: NewDatacenter) {
  try {
    logInfo('Creating datacenter', data);
    return await create(data);
  } catch (err) {
    logError('Service error: createDatacenter', err);
    throw err;
  }
}


export async function updateDatacenter(id: number, data: Partial<NewDatacenter>) {
  try {
    logInfo(`Updating datacenter id: ${id}`, data);
    return await update(id, data);
  } catch (err) {
    logError('Service error: updateDatacenter', err);
    throw err;
  }
}


export async function deleteDatacenter(id: number) {
  try {
    logInfo(`Deleting datacenter id: ${id}`);
    return await remove(id);
  } catch (err) {
    logError('Service error: deleteDatacenter', err);
    throw err;
  }
}
// Ensure this file is always a module
export {};
