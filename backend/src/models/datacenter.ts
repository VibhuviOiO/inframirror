export interface Datacenter {
  id: number;
  name: string;
  shortName: string;
  privateCIDR?: string;
  publicCIDR?: string;
  regionId: number;
}

export type NewDatacenter = Omit<Datacenter, 'id'>;
