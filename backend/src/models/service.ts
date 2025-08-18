export interface Service {
  id: number;
  datacenterId: number;
  hostId: number;
  catalogId: number;
}

export type NewService = Omit<Service, 'id'>;
