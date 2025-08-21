export interface Service {
  id: number;
  datacenterId: number | null;
  hostId: number;
  catalogId: number | null;
  environmentId: number | null;
  metadata: any;
  clusterId: number | null;
  teamId: number | null;
}

export type NewService = Omit<Service, 'id'>;
