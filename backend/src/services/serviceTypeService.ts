import * as repo from '../repositories/serviceTypeRepository.js';

export const create = (data: any) => repo.createServiceType(data);
export const list = () => repo.listServiceTypes();
export const getById = (id: number) => repo.getServiceTypeById(id);
export const update = (id: number, data: any) => repo.updateServiceType(id, data);
export const remove = (id: number) => repo.deleteServiceType(id);
