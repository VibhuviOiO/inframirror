import { useQuery } from '@tanstack/react-query';
import axios from 'axios';

const API_BASE = (import.meta.env.VITE_API_BASE_URL ?? '').replace(/\/$/, '');
function apiUrl(path: string) {
  return `${API_BASE}${path}`;
}

export interface PostgresTablesParams {
  host: string;
  port: number | string;
  database: string;
  user?: string;
  password?: string;
}

export function usePostgresTables(params: PostgresTablesParams, enabled = true) {
  return useQuery<string[]>({
    queryKey: ['postgres-tables', params],
    queryFn: async () => {
      const { data } = await axios.post(apiUrl('/postgresops/tables'), params);
      return Array.isArray(data.tables) ? data.tables : [];
    },
    enabled,
    select: (data) => Array.isArray(data) ? data : [],
  });
}
