export interface CatalogType {
  id: number;
  name: string;
  description?: string;
}

export type NewCatalogType = Omit<CatalogType, 'id'>;
