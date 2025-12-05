import dayjs from 'dayjs';

export interface IBranding {
  id?: number;
  title?: string;
  description?: string | null;
  keywords?: string | null;
  author?: string | null;
  faviconPath?: string | null;
  logoPath?: string | null;
  logoWidth?: number | null;
  logoHeight?: number | null;
  footerTitle?: string | null;
  isActive?: boolean;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<IBranding> = {
  isActive: false,
};
