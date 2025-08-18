export interface Host {
  id: number;
  datacenterId: number;
  hostname: string;
  privateIP: string;
  publicIP?: string;
  kind: 'VM' | 'Physical' | 'BareMetal';
  tags?: any;
}

export type NewHost = Omit<Host, 'id'>;
