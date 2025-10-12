import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import axios from 'axios';

const API_BASE = (import.meta.env.VITE_API_BASE_URL ?? '').replace(/\/$/, '');
function apiUrl(path: string) {
  return `${API_BASE}${path}`;
}

export interface Integration {
  id: number;
  name: string;
  integrationType: 'Database' | 'KeyValueStore' | 'SearchEngine' | 'Cache' | 'OrchestrationFramework' | 'Container' | 'Gateway';
  version: string;
  description?: string | null;
  updatedAt: string; // ISO string for frontend
  enabled: boolean;
}

export type NewIntegration = Omit<Integration, 'id' | 'updatedAt'>;

export function useIntegrations() {
  return useQuery<Integration[]>({
    queryKey: ['integrations'],
    queryFn: async () => {
      const { data } = await axios.get(apiUrl('/integrations'));
      return Array.isArray(data) ? data : [];
    },
    select: (data) => Array.isArray(data) ? data : [],
  });
}

export function useCreateIntegration() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (input: NewIntegration) => {
      const { data } = await axios.post(apiUrl('/integrations'), input);
      return data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['integrations'] });
    },
  });
}

export function useUpdateIntegration() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async ({ id, data }: { id: number; data: NewIntegration }) => {
      const res = await axios.put(apiUrl(`/integrations/${id}`), data);
      return res.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['integrations'] });
    },
  });
}

export function useDeleteIntegration() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (id: number) => {
      await axios.delete(apiUrl(`/integrations/${id}`));
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['integrations'] });
    },
  });
}
