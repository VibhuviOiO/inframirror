import dayjs from 'dayjs';
import { IAgent } from 'app/shared/model/agent.model';

export interface IAgentMonitor {
  id?: number;
  active?: boolean;
  createdBy?: string;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  monitorType?: string;
  monitorId?: number;
  monitorName?: string;
  agent?: IAgent;
}

export const defaultValue: Readonly<IAgentMonitor> = {
  active: false,
};
