import dayjs from 'dayjs';
import { IStatusPage } from 'app/shared/model/status-page.model';

export interface IStatusDependency {
  id?: number;
  parentType?: string;
  parentId?: number;
  childType?: string;
  childId?: number;
  metadata?: string | null;
  createdAt?: dayjs.Dayjs;
  statusPage?: IStatusPage | null;
}

export const defaultValue: Readonly<IStatusDependency> = {};
