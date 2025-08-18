export interface Team {
  id: number;
  name: string;
}

export type NewTeam = Omit<Team, 'id'>;
