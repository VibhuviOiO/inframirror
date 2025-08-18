
export interface Application {
  id: number;
  datacenterId: number;
  hostId?: number;
  catalogId: number;
  teamId?: number;
  environmentId?: number;
}

export type NewApplication = Omit<Application, 'id'>;
