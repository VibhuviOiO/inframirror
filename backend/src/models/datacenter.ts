export interface Datacenter {
  id: number;
  name: string;
  shortName: string;
  publicCIDR?: string | null;
  privateCIDR?: string | null;
}

export type NewDatacenter = Omit<Datacenter, 'id'>;
