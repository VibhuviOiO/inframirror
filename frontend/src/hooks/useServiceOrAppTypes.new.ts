import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';

export type ServiceOrAppType = {
  id: number;
  name: string;
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

export function useServiceOrAppTypes() {
  return useQuery<ServiceOrAppType[], Error>({
    queryKey: ['serviceOrAppTypes'],
    queryFn: async () => request<ServiceOrAppType[]>('/service-or-app-types'),
  });
}

export function useCreateServiceOrAppType() {
  const qc = useQueryClient();
  return useMutation<ServiceOrAppType, Error, Partial<ServiceOrAppType>>({
    mutationFn: (payload) => request<ServiceOrAppType>('/service-or-app-types', { method: 'POST', body: JSON.stringify(payload) }),
    onMutate: async (newType) => {
      await qc.cancelQueries(['serviceOrAppTypes'] as any);
      const previous = (qc.getQueryData(['serviceOrAppTypes'] as any) as ServiceOrAppType[]) || [];
      const optimistic: ServiceOrAppType = {
        id: Date.now(),
        name: newType.name as string,
      };
      qc.setQueryData(['serviceOrAppTypes'] as any, [...previous, optimistic]);
      return { previous };
    },
    onError: (_err, _newType, context: any) => {
      if (context?.previous) qc.setQueryData(['serviceOrAppTypes'] as any, context.previous);
    },
    onSettled: () => qc.invalidateQueries(['serviceOrAppTypes'] as any),
  });
}

export function useUpdateServiceOrAppType() {
  const qc = useQueryClient();
  return useMutation<ServiceOrAppType, Error, { id: number; data: Partial<ServiceOrAppType> }>({
    mutationFn: ({ id, data }) => request<ServiceOrAppType>(`/service-or-app-types/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    onMutate: async ({ id, data }) => {
      await qc.cancelQueries(['serviceOrAppTypes'] as any);
      const previous = (qc.getQueryData(['serviceOrAppTypes'] as any) as ServiceOrAppType[]) || [];
      qc.setQueryData(['serviceOrAppTypes'] as any, previous.map((p) => (p.id === id ? { ...p, ...(data as ServiceOrAppType) } : p)));
      return { previous };
    },
    onError: (_err, _vars, context: any) => {
      if (context?.previous) qc.setQueryData(['serviceOrAppTypes'] as any, context.previous);
    },
    onSettled: () => qc.invalidateQueries(['serviceOrAppTypes'] as any),
  });
}

export function useDeleteServiceOrAppType() {
  const qc = useQueryClient();
  return useMutation<void, Error, number>({
    mutationFn: (id) => request<void>(`/service-or-app-types/${id}`, { method: 'DELETE' }),
    onMutate: async (id) => {
      await qc.cancelQueries(['serviceOrAppTypes'] as any);
      const previous = (qc.getQueryData(['serviceOrAppTypes'] as any) as ServiceOrAppType[]) || [];
      qc.setQueryData(['serviceOrAppTypes'] as any, previous.filter((p) => p.id !== id));
      return { previous };
    },
    onError: (_err, _id, context: any) => {
      if (context?.previous) qc.setQueryData(['serviceOrAppTypes'] as any, context.previous);
    },
    onSettled: () => qc.invalidateQueries(['serviceOrAppTypes'] as any),
  });
}
