import dayjs from 'dayjs';
import { IAgent } from 'app/shared/model/agent.model';
import { IHttpMonitor } from 'app/shared/model/http-monitor.model';

export interface IAgentMonitor {
  id?: number;
  active?: boolean;
  createdBy?: string;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  agent?: IAgent;
  monitor?: IHttpMonitor;
}

export const defaultValue: Readonly<IAgentMonitor> = {
  active: false,
};
