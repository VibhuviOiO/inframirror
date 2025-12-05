import dayjs from 'dayjs';
import { IDatacenter } from 'app/shared/model/datacenter.model';

export interface IMonitoredService {
  id?: number;
  name?: string;
  description?: string | null;
  serviceType?: string;
  environment?: string;
  monitoringEnabled?: boolean | null;
  clusterMonitoringEnabled?: boolean | null;
  intervalSeconds?: number;
  timeoutMs?: number;
  retryCount?: number;
  latencyWarningMs?: number | null;
  latencyCriticalMs?: number | null;
  advancedConfig?: string | null;
  isActive?: boolean | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  datacenter?: IDatacenter | null;
}

export const defaultValue: Readonly<IMonitoredService> = {
  monitoringEnabled: false,
  clusterMonitoringEnabled: false,
  isActive: false,
};
