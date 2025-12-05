import dayjs from 'dayjs';

export interface IStatusPage {
  id?: number;
  name?: string;
  slug?: string;
  description?: string | null;
  isPublic?: boolean;
  customDomain?: string | null;
  logoUrl?: string | null;
  themeColor?: string | null;
  headerText?: string | null;
  footerText?: string | null;
  showResponseTimes?: boolean | null;
  showUptimePercentage?: boolean | null;
  autoRefreshSeconds?: number | null;
  monitorSelection?: string | null;
  isActive?: boolean | null;
  isHomePage?: boolean | null;
  allowedRoles?: string | null;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs;
}

export const defaultValue: Readonly<IStatusPage> = {
  isPublic: false,
  showResponseTimes: false,
  showUptimePercentage: false,
  isActive: false,
  isHomePage: false,
};
