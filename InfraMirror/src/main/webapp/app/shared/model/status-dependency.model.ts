import dayjs from 'dayjs';
import { IStatusPage } from 'app/shared/model/status-page.model';

export interface IStatusDependency {
  id?: number;
  parentType?: string;
  parentId?: number;
  parentName?: string;
  childType?: string;
  childId?: number;
  childName?: string;
  metadata?: string | null;
  createdAt?: dayjs.Dayjs;
  statusPage?: IStatusPage | null;
}

export const defaultValue: Readonly<IStatusDependency> = {};
