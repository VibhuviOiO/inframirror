import dayjs from 'dayjs';
import { IAgent } from 'app/shared/model/agent.model';
import { IMonitoredService } from 'app/shared/model/monitored-service.model';
import { IServiceInstance } from 'app/shared/model/service-instance.model';

export interface IServiceHeartbeat {
  id?: number;
  executedAt?: dayjs.Dayjs;
  success?: boolean;
  status?: string;
  responseTimeMs?: number | null;
  errorMessage?: string | null;
  metadata?: string | null;
  agent?: IAgent | null;
  monitoredService?: IMonitoredService;
  serviceInstance?: IServiceInstance | null;
}

export const defaultValue: Readonly<IServiceHeartbeat> = {
  success: false,
};
