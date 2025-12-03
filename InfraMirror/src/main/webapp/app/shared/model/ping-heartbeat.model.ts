import dayjs from 'dayjs';
import { IInstance } from 'app/shared/model/instance.model';
import { IAgent } from 'app/shared/model/agent.model';

export interface IPingHeartbeat {
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
  instance?: IInstance;
  agent?: IAgent | null;
}

export const defaultValue: Readonly<IPingHeartbeat> = {
  success: false,
};
