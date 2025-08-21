export interface Catalog {
  id: number;
  name: string;
  uniqueId?: string;
  defaultPort?: number;
  description?: string;
  gitRepoUrl?: string;
  teamId?: number;
  catalogTypeId: number;
}

export type NewCatalog = Omit<Catalog, 'id'>;
