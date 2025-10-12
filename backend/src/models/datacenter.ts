export interface Datacenter {
  id: number;
  name: string;
  shortName: string;
  privateCIDR?: string | null;
  publicCIDR?: string | null;
  regionId: number;
}

export type NewDatacenter = Omit<Datacenter, 'id'>;