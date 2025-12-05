import dayjs from 'dayjs';
import { IInstance } from 'app/shared/model/instance.model';
import { IService } from 'app/shared/model/service.model';

export interface IServiceInstance {
  id?: number;
  port?: number;
  isActive?: boolean | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  instance?: IInstance;
  service?: IService;
}

export const defaultValue: Readonly<IServiceInstance> = {
  isActive: false,
};
