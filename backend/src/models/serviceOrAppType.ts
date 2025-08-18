export interface ServiceOrAppType {
  id: number;
  name: string;
}

export type NewServiceOrAppType = Omit<ServiceOrAppType, 'id'>;
