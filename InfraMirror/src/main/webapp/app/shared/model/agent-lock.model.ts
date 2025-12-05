import dayjs from 'dayjs';

export interface IAgentLock {
  id?: number;
  agentId?: number;
  acquiredAt?: dayjs.Dayjs;
  expiresAt?: dayjs.Dayjs;
}

export const defaultValue: Readonly<IAgentLock> = {};
