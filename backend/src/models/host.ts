export interface Host {
  id: number;
  datacenterId: number;
  hostname: string;
  privateIP: string;
  publicIP?: string | null;
  kind: 'VM' | 'Physical' | 'BareMetal';
  tags?: Record<string, any> | null;
}

export type NewHost = Omit<Host, 'id'>;
