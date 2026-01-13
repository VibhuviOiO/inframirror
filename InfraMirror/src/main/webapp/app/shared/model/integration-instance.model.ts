export interface IIntegrationInstance {
  id?: number;
  controlIntegrationId?: number;
  controlIntegrationName?: string | null;
  name?: string;
  instanceType?: string;
  monitoredServiceId?: number | null;
  monitoredServiceName?: string | null;
  httpMonitorId?: number | null;
  httpMonitorName?: string | null;
  baseUrl?: string | null;
  authType?: string | null;
  authConfig?: Record<string, unknown> | null;
  environment?: string | null;
  datacenterId?: number | null;
  datacenterName?: string | null;
  timeoutMs?: number | null;
  isActive?: boolean | null;
  createdAt?: string | null;
}

export const defaultValue: Readonly<IIntegrationInstance> = {
  isActive: true,
  timeoutMs: 5000,
};
