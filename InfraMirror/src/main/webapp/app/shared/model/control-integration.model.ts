export interface IControlIntegration {
  id?: number;
  code?: string;
  name?: string;
  description?: string | null;
  category?: string | null;
  icon?: string | null;
  supportsMultiDc?: boolean | null;
  supportsWrite?: boolean | null;
  isActive?: boolean | null;
  createdAt?: string | null;
}

export const defaultValue: Readonly<IControlIntegration> = {
  supportsMultiDc: false,
  supportsWrite: true,
  isActive: true,
};
