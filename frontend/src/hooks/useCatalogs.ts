export function useBulkDeleteCatalogs() {
  const qc = useQueryClient();
  return useMutation<void, Error, number[]>({
    mutationFn: (ids) => request<void>(`/catalogs/bulk-delete`, { method: 'POST', body: JSON.stringify({ ids }) }),
    onMutate: async (ids) => {
      await qc.cancelQueries(['catalogs'] as any);
      const previous = (qc.getQueryData(['catalogs'] as any) as Catalog[]) || [];
      qc.setQueryData(['catalogs'] as any, previous.filter((p) => !ids.includes(p.id)));
      return { previous };
    },
    onError: (_err, _ids, context: any) => {
      if (context?.previous) qc.setQueryData(['catalogs'] as any, context.previous);
    },
    onSettled: () => qc.invalidateQueries(['catalogs'] as any),
  });
}

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';

export type Catalog = {
  id: number;
  name: string;
  catalogTypeId: number;
  uniqueId: string;
  defaultPort?: number;
  description?: string;
  gitRepoUrl?: string;
  teamId: number;
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

export function useCatalogs() {
  return useQuery<Catalog[], Error>({
    queryKey: ['catalogs'],
    queryFn: async () => request<Catalog[]>('/catalogs'),
  });
}

export function useCreateCatalog() {
  const qc = useQueryClient();
  return useMutation<Catalog, Error, Partial<Catalog>>({
    mutationFn: (payload) => request<Catalog>('/catalogs', { method: 'POST', body: JSON.stringify(payload) }),
    onMutate: async (newCatalog) => {
      await qc.cancelQueries(['catalogs'] as any);
      const previous = (qc.getQueryData(['catalogs'] as any) as Catalog[]) || [];
      const optimistic: Catalog = {
        id: Date.now(),
        name: newCatalog.name as string,
        catalogTypeId: newCatalog.catalogTypeId as number,
        uniqueId: newCatalog.uniqueId as string,
        defaultPort: newCatalog.defaultPort,
        description: newCatalog.description,
        gitRepoUrl: newCatalog.gitRepoUrl,
        teamId: newCatalog.teamId as number,
      };
      qc.setQueryData(['catalogs'] as any, [...previous, optimistic]);
      return { previous };
    },
    onError: (_err, _newCatalog, context: any) => {
      if (context?.previous) qc.setQueryData(['catalogs'] as any, context.previous);
    },
    onSettled: () => qc.invalidateQueries(['catalogs'] as any),
  });
}

export function useUpdateCatalog() {
  const qc = useQueryClient();
  return useMutation<Catalog, Error, { id: number; data: Partial<Catalog> }>({
    mutationFn: ({ id, data }) => request<Catalog>(`/catalogs/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    onMutate: async ({ id, data }) => {
      await qc.cancelQueries(['catalogs'] as any);
      const previous = (qc.getQueryData(['catalogs'] as any) as Catalog[]) || [];
      qc.setQueryData(['catalogs'] as any, previous.map((p) => (p.id === id ? { ...p, ...(data as Catalog) } : p)));
      return { previous };
    },
    onError: (_err, _vars, context: any) => {
      if (context?.previous) qc.setQueryData(['catalogs'] as any, context.previous);
    },
    onSettled: () => qc.invalidateQueries(['catalogs'] as any),
  });
}
export function useDeleteCatalog() {
  const qc = useQueryClient();
  return useMutation<void, Error, number>({
    mutationFn: (id) => request<void>(`/catalogs/${id}`, { method: 'DELETE' }),
    onMutate: async (id) => {
      await qc.cancelQueries(['catalogs'] as any);
      const previous = (qc.getQueryData(['catalogs'] as any) as Catalog[]) || [];
      qc.setQueryData(['catalogs'] as any, previous.filter((p) => p.id !== id));
      return { previous };
    },
    onError: (_err, _id, context: any) => {
      if (context?.previous) qc.setQueryData(['catalogs'] as any, context.previous);
    },
    onSettled: () => qc.invalidateQueries(['catalogs'] as any),
  });
}

