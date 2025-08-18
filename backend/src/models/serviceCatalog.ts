export interface ServiceCatalog {
  id: number;
  name: string;
  defaultPort?: number;
  description?: string;
  serviceTypeId: number;
}

export type NewServiceCatalog = Omit<ServiceCatalog, 'id'>;
