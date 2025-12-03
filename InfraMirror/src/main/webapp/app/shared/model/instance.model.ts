import dayjs from 'dayjs';
import { IDatacenter } from 'app/shared/model/datacenter.model';
import { IAgent } from 'app/shared/model/agent.model';

export interface IInstance {
  id?: number;
  name?: string;
  hostname?: string;
  description?: string | null;
  instanceType?: string;
  monitoringType?: string;
  operatingSystem?: string | null;
  platform?: string | null;
  privateIpAddress?: string | null;
  publicIpAddress?: string | null;
  tags?: string | null;
  pingEnabled?: boolean;
  pingInterval?: number;
  pingTimeoutMs?: number;
  pingRetryCount?: number;
  hardwareMonitoringEnabled?: boolean;
  hardwareMonitoringInterval?: number;
  cpuWarningThreshold?: number;
  cpuDangerThreshold?: number;
  memoryWarningThreshold?: number;
  memoryDangerThreshold?: number;
  diskWarningThreshold?: number;
  diskDangerThreshold?: number;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  lastPingAt?: dayjs.Dayjs | null;
  lastHardwareCheckAt?: dayjs.Dayjs | null;
  datacenter?: IDatacenter;
  agent?: IAgent | null;
}

export const defaultValue: Readonly<IInstance> = {
  pingEnabled: false,
  hardwareMonitoringEnabled: false,
};
