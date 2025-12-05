import dayjs from 'dayjs';

export interface IAuditTrail {
  id?: number;
  action?: string;
  entityName?: string;
  entityId?: number;
  oldValue?: string | null;
  newValue?: string | null;
  timestamp?: dayjs.Dayjs;
  ipAddress?: string | null;
  userAgent?: string | null;
}

export const defaultValue: Readonly<IAuditTrail> = {};
