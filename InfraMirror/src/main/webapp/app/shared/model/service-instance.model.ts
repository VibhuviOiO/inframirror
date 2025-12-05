import dayjs from 'dayjs';
import { IInstance } from 'app/shared/model/instance.model';
import { IMonitoredService } from 'app/shared/model/monitored-service.model';

export interface IServiceInstance {
  id?: number;
  port?: number;
  isActive?: boolean | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  instance?: IInstance;
  monitoredService?: IMonitoredService;
}

export const defaultValue: Readonly<IServiceInstance> = {
  isActive: false,
};
