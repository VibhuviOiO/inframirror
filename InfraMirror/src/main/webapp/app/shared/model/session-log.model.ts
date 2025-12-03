import dayjs from 'dayjs';
import { IInstance } from 'app/shared/model/instance.model';
import { IAgent } from 'app/shared/model/agent.model';
import { IUser } from 'app/shared/model/user.model';

export interface ISessionLog {
  id?: number;
  sessionType?: string;
  startTime?: dayjs.Dayjs;
  endTime?: dayjs.Dayjs | null;
  duration?: number | null;
  sourceIpAddress?: string;
  status?: string;
  terminationReason?: string | null;
  commandsExecuted?: number | null;
  bytesTransferred?: number | null;
  sessionId?: string;
  metadata?: string | null;
  instance?: IInstance;
  agent?: IAgent | null;
  user?: IUser;
}

export const defaultValue: Readonly<ISessionLog> = {};
