import dayjs from 'dayjs';

export interface ITag {
  id?: number;
  key?: string;
  value?: string;
  entityType?: string;
  entityId?: number;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<ITag> = {};
