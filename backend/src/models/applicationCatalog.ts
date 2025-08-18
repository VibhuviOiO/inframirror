export interface ApplicationCatalog {
  id: number;
  name: string;
  uniqueId: string;
  defaultPort?: number;
  description?: string;
  appTypeId: number;
  gitRepoUrl?: string;
  teamId: number;
}

export type NewApplicationCatalog = Omit<ApplicationCatalog, 'id'>;
