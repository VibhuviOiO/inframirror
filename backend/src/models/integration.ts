export interface Integration {
  id: number;
  name: string;
  integrationType: 'Database' | 'KeyValueStore' | 'SearchEngine' | 'Cache' | 'OrchestrationFramework' | 'Container' | 'Gateway';
  version: string;
  description?: string | null;
  updatedAt: Date;
  enabled: boolean;
}

export type NewIntegration = Omit<Integration, 'id' | 'updatedAt'>;
