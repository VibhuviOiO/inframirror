import { IRegion } from 'app/shared/model/region.model';
import { IDatacenter } from 'app/shared/model/datacenter.model';

export interface IAgent {
  id?: number;
  name?: string;
  hostname?: string | null;
  ipAddress?: string | null;
  osType?: string | null;
  osVersion?: string | null;
  agentVersion?: string | null;
  lastSeenAt?: string | null;
  status?: string | null;
  tags?: Record<string, unknown> | null;
  region?: IRegion | null;
  datacenter?: IDatacenter | null;
}

export const defaultValue: Readonly<IAgent> = {};
