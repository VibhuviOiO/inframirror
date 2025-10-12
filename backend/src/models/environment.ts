export interface Environment {
  id: number;
  name: string;
}

export type NewEnvironment = Omit<Environment, 'id'>;