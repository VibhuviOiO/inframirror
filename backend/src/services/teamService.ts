import { getAll, getById, create, update, remove } from '../repositories/teamRepository.js';
import type { NewTeam } from '../models/team.js';

export async function getAllTeams() {
  return getAll();
}

export async function getTeamById(id: number) {
  return getById(id);
}

export async function createTeam(data: NewTeam) {
  return create(data);
}

export async function updateTeam(id: number, data: Partial<NewTeam>) {
  return update(id, data);
}

export async function deleteTeam(id: number) {
  return remove(id);
}
// Ensure this file is always a module
export {};
