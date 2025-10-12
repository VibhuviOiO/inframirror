
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import axios from 'axios';

const API_BASE = (import.meta.env.VITE_API_BASE_URL ?? '').replace(/\/$/, '');

function apiUrl(path: string) {
  return `${API_BASE}${path}`;
}

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

export function useHosts() {
  return useQuery<Host[]>({
    queryKey: ['hosts'],
    queryFn: async () => {
      const res = await axios.get(apiUrl('/hosts'));
      if (Array.isArray(res.data)) return res.data;
      if (!res.data) return [];
      return [res.data];
    },
    select: (data) => Array.isArray(data) ? data : [],
  });
}

export function useCreateHost() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (data: Partial<NewHost>) => {
      const res = await axios.post(apiUrl('/hosts'), data);
      return res.data;
    },
    onSuccess: () => queryClient.invalidateQueries(['hosts']),
  });
}

export function useUpdateHost() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async ({ id, data }: { id: number; data: Partial<NewHost> }) => {
      const res = await axios.put(apiUrl(`/hosts/${id}`), data);
      return res.data;
    },
    onSuccess: () => queryClient.invalidateQueries(['hosts']),
  });
}

export function useDeleteHost() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (id: number) => {
      await axios.delete(apiUrl(`/hosts/${id}`));
    },
    onSuccess: () => queryClient.invalidateQueries(['hosts']),
  });
}
