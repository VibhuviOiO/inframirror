

import { getAll, getById, create, update, remove } from '../repositories/clusterRepository.js';
import type { NewCluster } from '../models/cluster.js';
import { logInfo, logError } from '../utils/logger.js';


export async function getAllClusters() {
	try {
		logInfo('Fetching all clusters');
		return await getAll();
	} catch (err) {
		logError('Service error: getAllClusters', err);
		throw err;
	}
}


export async function getClusterById(id: number) {
	try {
		logInfo(`Fetching cluster by id: ${id}`);
		return await getById(id);
	} catch (err) {
		logError('Service error: getClusterById', err);
		throw err;
	}
}


export async function createCluster(data: NewCluster) {
	try {
		logInfo('Creating cluster', data);
		return await create(data);
	} catch (err) {
		logError('Service error: createCluster', err);
		throw err;
	}
}


export async function updateCluster(id: number, data: Partial<NewCluster>) {
	try {
		logInfo(`Updating cluster id: ${id}`, data);
		return await update(id, data);
	} catch (err) {
		logError('Service error: updateCluster', err);
		throw err;
	}
}


export async function deleteCluster(id: number) {
	try {
		logInfo(`Deleting cluster id: ${id}`);
		return await remove(id);
	} catch (err) {
		logError('Service error: deleteCluster', err);
		throw err;
	}
}
// Ensure this file is always a module
export {};