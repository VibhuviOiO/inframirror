
import { getAll, getById, create, update, remove } from '../repositories/clusterRepository.js';
import type { NewCluster } from '../models/cluster.js';

export async function getAllClusters() {
	return getAll();
}

export async function getClusterById(id: number) {
	return getById(id);
}

export async function createCluster(data: NewCluster) {
	return create(data);
}

export async function updateCluster(id: number, data: Partial<NewCluster>) {
	return update(id, data);
}

export async function deleteCluster(id: number) {
	return remove(id);
}
// Ensure this file is always a module
export {};