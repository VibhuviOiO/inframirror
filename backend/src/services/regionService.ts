
import { getAll, getById, create, update, remove } from '../repositories/regionRepository.js';
import type { NewRegion } from '../models/region.js';
import { logInfo, logError } from '../utils/logger.js';



export async function getAllRegions() {
  try {
    logInfo('Fetching all regions');
    return await getAll();
  } catch (err) {
    logError('Service error: getAllRegions', err);
    throw err;
  }
}


export async function getRegionById(id: number) {
  try {
    logInfo(`Fetching region by id: ${id}`);
    return await getById(id);
  } catch (err) {
    logError('Service error: getRegionById', err);
    throw err;
  }
}


export async function createRegion(data: NewRegion) {
  try {
    logInfo('Creating region', data);
    return await create(data);
  } catch (err) {
    logError('Service error: createRegion', err);
    throw err;
  }
}


export async function updateRegion(id: number, data: Partial<NewRegion>) {
  try {
    logInfo(`Updating region id: ${id}`, data);
    return await update(id, data);
  } catch (err) {
    logError('Service error: updateRegion', err);
    throw err;
  }
}


export async function deleteRegion(id: number) {
  try {
    logInfo(`Deleting region id: ${id}`);
    return await remove(id);
  } catch (err) {
    logError('Service error: deleteRegion', err);
    throw err;
  }
}
