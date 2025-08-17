import type { Datacenter, NewDatacenter } from '../models/datacenter.js';
import * as repo from '../repositories/datacenterRepository.js';

export const init = async () => {
  await repo.createTableIfNotExists();
};

export const create = async (dc: NewDatacenter) => repo.createDatacenter(dc);
export const list = async () => repo.getAllDatacenters();
export const getById = async (id: number) => repo.getDatacenterById(id);
export const update = async (id: number, dc: Partial<NewDatacenter>) => repo.updateDatacenter(id, dc);
export const remove = async (id: number) => repo.deleteDatacenter(id);
