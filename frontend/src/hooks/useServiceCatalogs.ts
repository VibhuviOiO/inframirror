export function useBulkDeleteServiceCatalogs() {
  const qc = useQueryClient();
  return useMutation<void, Error, number[]>({
    mutationFn: (ids) => request<void>(`/service-catalogs/bulk`, { method: 'DELETE', body: JSON.stringify({ ids }) }),
    onMutate: async (ids) => {
      await qc.cancelQueries(['serviceCatalogs'] as any);
      const previous = (qc.getQueryData(['serviceCatalogs'] as any) as ServiceCatalog[]) || [];
      qc.setQueryData(['serviceCatalogs'] as any, previous.filter((p) => !ids.includes(p.id)));
      return { previous };
    },
    onError: (_err, _ids, context: any) => {
      if (context?.previous) qc.setQueryData(['serviceCatalogs'] as any, context.previous);
    },
    onSettled: () => qc.invalidateQueries(['serviceCatalogs'] as any),
  });
}
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';

export type ServiceCatalog = {
  id: number;
  name: string;
  defaultPort?: number;
  description?: string;
  serviceTypeId: number;
};

const API_BASE = (import.meta.env.VITE_API_BASE_URL ?? '').replace(/\/$/, '');

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...init,
  });
  if (!res.ok) {
    const text = await res.text().catch(() => res.statusText);
    throw new Error(text || res.statusText);
  }
  if (res.status === 204) return null as unknown as T;
  return (await res.json()) as T;
}

export function useServiceCatalogs() {
  return useQuery<ServiceCatalog[], Error>({
    queryKey: ['serviceCatalogs'],
    queryFn: async () => request<ServiceCatalog[]>('/service-catalogs'),
  });
}

export function useCreateServiceCatalog() {
  const qc = useQueryClient();
  return useMutation<ServiceCatalog, Error, Partial<ServiceCatalog>>({
    mutationFn: (payload) => request<ServiceCatalog>('/service-catalogs', { method: 'POST', body: JSON.stringify(payload) }),
    onMutate: async (newCatalog) => {
      await qc.cancelQueries(['serviceCatalogs'] as any);
      const previous = (qc.getQueryData(['serviceCatalogs'] as any) as ServiceCatalog[]) || [];
      const optimistic: ServiceCatalog = {
        id: Date.now(),
        name: newCatalog.name as string,
        defaultPort: newCatalog.defaultPort,
        description: newCatalog.description,
        serviceTypeId: newCatalog.serviceTypeId as number,
      };
      qc.setQueryData(['serviceCatalogs'] as any, [...previous, optimistic]);
      return { previous };
    },
    onError: (_err, _newCatalog, context: any) => {
      if (context?.previous) qc.setQueryData(['serviceCatalogs'] as any, context.previous);
    },
    onSettled: () => qc.invalidateQueries(['serviceCatalogs'] as any),
  });
}

export function useUpdateServiceCatalog() {
  const qc = useQueryClient();
  return useMutation<ServiceCatalog, Error, { id: number; data: Partial<ServiceCatalog> }>({
    mutationFn: ({ id, data }) => request<ServiceCatalog>(`/service-catalogs/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    onMutate: async ({ id, data }) => {
      await qc.cancelQueries(['serviceCatalogs'] as any);
      const previous = (qc.getQueryData(['serviceCatalogs'] as any) as ServiceCatalog[]) || [];
      qc.setQueryData(['serviceCatalogs'] as any, previous.map((p) => (p.id === id ? { ...p, ...(data as ServiceCatalog) } : p)));
      return { previous };
    },
    onError: (_err, _vars, context: any) => {
      if (context?.previous) qc.setQueryData(['serviceCatalogs'] as any, context.previous);
    },
    onSettled: () => qc.invalidateQueries(['serviceCatalogs'] as any),
  });
}

export function useDeleteServiceCatalog() {
  const qc = useQueryClient();
  return useMutation<void, Error, number>({
    mutationFn: (id) => request<void>(`/service-catalogs/${id}`, { method: 'DELETE' }),
    onMutate: async (id) => {
      await qc.cancelQueries(['serviceCatalogs'] as any);
      const previous = (qc.getQueryData(['serviceCatalogs'] as any) as ServiceCatalog[]) || [];
      qc.setQueryData(['serviceCatalogs'] as any, previous.filter((p) => p.id !== id));
      return { previous };
    },
    onError: (_err, _id, context: any) => {
      if (context?.previous) qc.setQueryData(['serviceCatalogs'] as any, context.previous);
    },
    onSettled: () => qc.invalidateQueries(['serviceCatalogs'] as any),
  });
}
