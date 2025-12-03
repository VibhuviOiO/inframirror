import dayjs from 'dayjs';

export interface IApiKey {
  id?: number;
  name?: string;
  description?: string | null;
  keyHash?: string;
  active?: boolean;
  lastUsedDate?: dayjs.Dayjs | null;
  expiresAt?: dayjs.Dayjs | null;
  createdBy?: string;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<IApiKey> = {
  active: false,
};
