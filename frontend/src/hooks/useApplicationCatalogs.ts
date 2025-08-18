import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';

export type ApplicationCatalog = {
  id: number;
  name: string;
  uniqueId: string;
  appTypeId: number;
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

export function useApplicationCatalogs() {
  return useQuery<ApplicationCatalog[], Error>({
    queryKey: ['applicationCatalogs'],
    queryFn: async () => request<ApplicationCatalog[]>('/application-catalogs'),
  });
}

export function useCreateApplicationCatalog() {
  const qc = useQueryClient();
  return useMutation<ApplicationCatalog, Error, Partial<ApplicationCatalog>>({
    mutationFn: (payload) => request<ApplicationCatalog>('/application-catalogs', { method: 'POST', body: JSON.stringify(payload) }),
    onMutate: async (newCatalog) => {
      await qc.cancelQueries(['applicationCatalogs'] as any);
      const previous = (qc.getQueryData(['applicationCatalogs'] as any) as ApplicationCatalog[]) || [];
      const optimistic: ApplicationCatalog = {
        id: Date.now(),
        name: newCatalog.name as string,
        uniqueId: newCatalog.uniqueId as string,
        appTypeId: newCatalog.appTypeId as number,
        teamId: newCatalog.teamId as number,
      };
      qc.setQueryData(['applicationCatalogs'] as any, [...previous, optimistic]);
      return { previous };
    },
    onError: (_err, _newCatalog, context: any) => {
      if (context?.previous) qc.setQueryData(['applicationCatalogs'] as any, context.previous);
    },
    onSettled: () => qc.invalidateQueries(['applicationCatalogs'] as any),
  });
}

export function useUpdateApplicationCatalog() {
  const qc = useQueryClient();
  return useMutation<ApplicationCatalog, Error, { id: number; data: Partial<ApplicationCatalog> }>({
    mutationFn: ({ id, data }) => request<ApplicationCatalog>(`/application-catalogs/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    onMutate: async ({ id, data }) => {
      await qc.cancelQueries(['applicationCatalogs'] as any);
      const previous = (qc.getQueryData(['applicationCatalogs'] as any) as ApplicationCatalog[]) || [];
      qc.setQueryData(['applicationCatalogs'] as any, previous.map((p) => (p.id === id ? { ...p, ...(data as ApplicationCatalog) } : p)));
      return { previous };
    },
    onError: (_err, _vars, context: any) => {
      if (context?.previous) qc.setQueryData(['applicationCatalogs'] as any, context.previous);
    },
    onSettled: () => qc.invalidateQueries(['applicationCatalogs'] as any),
  });
}

export function useDeleteApplicationCatalog() {
  const qc = useQueryClient();
  return useMutation<void, Error, number>({
    mutationFn: (id) => request<void>(`/application-catalogs/${id}`, { method: 'DELETE' }),
    onMutate: async (id) => {
      await qc.cancelQueries(['applicationCatalogs'] as any);
      const previous = (qc.getQueryData(['applicationCatalogs'] as any) as ApplicationCatalog[]) || [];
      qc.setQueryData(['applicationCatalogs'] as any, previous.filter((p) => p.id !== id));
      return { previous };
    },
    onError: (_err, _id, context: any) => {
      if (context?.previous) qc.setQueryData(['applicationCatalogs'] as any, context.previous);
    },
    onSettled: () => qc.invalidateQueries(['applicationCatalogs'] as any),
  });
}
