import dayjs from 'dayjs';
import { IAgent } from 'app/shared/model/agent.model';
import { IInstance } from 'app/shared/model/instance.model';

export interface IInstanceHeartbeat {
  id?: number;
  executedAt?: dayjs.Dayjs;
  heartbeatType?: string;
  success?: boolean;
  responseTimeMs?: number | null;
  packetLoss?: number | null;
  jitterMs?: number | null;
  cpuUsage?: number | null;
  memoryUsage?: number | null;
  diskUsage?: number | null;
  loadAverage?: number | null;
  processCount?: number | null;
  networkRxBytes?: number | null;
  networkTxBytes?: number | null;
  uptimeSeconds?: number | null;
  status?: string;
  errorMessage?: string | null;
  errorType?: string | null;
  metadata?: string | null;
  agent?: IAgent | null;
  instance?: IInstance;
}

export const defaultValue: Readonly<IInstanceHeartbeat> = {
  success: false,
};
