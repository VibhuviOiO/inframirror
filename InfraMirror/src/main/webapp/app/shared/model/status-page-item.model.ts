import dayjs from 'dayjs';
import { IStatusPage } from 'app/shared/model/status-page.model';

export interface IStatusPageItem {
  id?: number;
  itemType?: string;
  itemId?: number;
  itemName?: string | null;
  displayOrder?: number | null;
  createdAt?: dayjs.Dayjs | null;
  statusPage?: IStatusPage;
}

export const defaultValue: Readonly<IStatusPageItem> = {};
