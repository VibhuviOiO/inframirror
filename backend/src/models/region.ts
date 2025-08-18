export interface Region {
  id: number;
  name: string;
}

export type NewRegion = Omit<Region, 'id'>;
