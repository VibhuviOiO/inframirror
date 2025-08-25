import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import axios from 'axios';

const API_BASE = (import.meta.env.VITE_API_BASE_URL ?? '').replace(/\/$/, '');
function apiUrl(path: string) {
  return `${API_BASE}${path}`;
}

export interface IntegrationInstance {
  id: number;
  hostId: number;
  integrationId: number;
  port?: number | null;
  config?: Record<string, any> | null;
}

export type NewIntegrationInstance = Omit<IntegrationInstance, 'id'>;

export function useIntegrationInstances() {
  return useQuery<IntegrationInstance[]>({
    queryKey: ['integration-instances'],
    queryFn: async () => {
      const { data } = await axios.get(apiUrl('/integration-instances'));
      return Array.isArray(data) ? data : [];
    },
    select: (data) => Array.isArray(data) ? data : [],
  });
}

export function useCreateIntegrationInstance() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (input: NewIntegrationInstance) => {
      const { data } = await axios.post(apiUrl('/integration-instances'), input);
      return data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['integration-instances'] });
    },
  });
}

export function useUpdateIntegrationInstance() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async ({ id, data }: { id: number; data: Partial<NewIntegrationInstance> }) => {
      const res = await axios.put(apiUrl(`/integration-instances/${id}`), data);
      return res.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['integration-instances'] });
    },
  });
}

export function useDeleteIntegrationInstance() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (id: number) => {
      await axios.delete(apiUrl(`/integration-instances/${id}`));
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['integration-instances'] });
    },
  });
}
