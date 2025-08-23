export interface Cluster {
  id: number;
  name: string;
  environmentId?: number | null;
  datacenterId?: number | null;
}

export type NewCluster = Omit<Cluster, 'id'>;