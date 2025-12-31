import dayjs from 'dayjs';

export interface IStatusPage {
  id?: number;
  name?: string;
  slug?: string;
  description?: string | null;
  isPublic?: boolean;
  isActive?: boolean | null;
  isHomePage?: boolean | null;
  allowedRoles?: string | null;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs;
  itemCount?: number;
}

export const defaultValue: Readonly<IStatusPage> = {
  isPublic: false,
  isActive: false,
  isHomePage: false,
};
