export interface IntegrationInstance {
  id: number;
  datacenterId?: number | null;
  hostId: number;
  clusterId?: number | null;
  environmentId?: number | null;
  integrationId: number;
  port?: number | null;
  config?: Record<string, any> | null;
}

export type NewIntegrationInstance = Omit<IntegrationInstance, 'id' | 'enabled'>;