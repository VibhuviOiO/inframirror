export interface Cluster {
	id: number;
	name: string;
	catalogId: number;
	environmentId?: number | null;
	datacenterId?: number | null;
}

export type NewCluster = Omit<Cluster, 'id'>;
