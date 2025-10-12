import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';

export type Region = {
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

export function useRegions() {
  return useQuery<Region[], Error>({
    queryKey: ['regions'],
    queryFn: async () => request<Region[]>('/regions'),
  });
}

export function useCreateRegion() {
  const qc = useQueryClient();
  return useMutation<Region, Error, Partial<Region>>({
    mutationFn: (payload) => request<Region>('/regions', { method: 'POST', body: JSON.stringify(payload) }),
    onMutate: async (newRegion) => {
      await qc.cancelQueries(['regions'] as any);
      const previous = (qc.getQueryData(['regions'] as any) as Region[]) || [];
      const optimistic: Region = {
        id: Date.now(),
        name: newRegion.name as string,
      };
      qc.setQueryData(['regions'] as any, [...previous, optimistic]);
      return { previous };
    },
    onError: (_err, _newRegion, context: any) => {
      if (context?.previous) qc.setQueryData(['regions'] as any, context.previous);
    },
    onSettled: () => qc.invalidateQueries(['regions'] as any),
  });
}

export function useUpdateRegion() {
  const qc = useQueryClient();
  return useMutation<Region, Error, { id: number; data: Partial<Region> }>({
    mutationFn: ({ id, data }) => request<Region>(`/regions/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    onMutate: async ({ id, data }) => {
      await qc.cancelQueries(['regions'] as any);
      const previous = (qc.getQueryData(['regions'] as any) as Region[]) || [];
      qc.setQueryData(['regions'] as any, previous.map((p) => (p.id === id ? { ...p, ...(data as Region) } : p)));
      return { previous };
    },
    onError: (_err, _vars, context: any) => {
      if (context?.previous) qc.setQueryData(['regions'] as any, context.previous);
    },
    onSettled: () => qc.invalidateQueries(['regions'] as any),
  });
}

export function useDeleteRegion() {
  const qc = useQueryClient();
  return useMutation<void, Error, number>({
    mutationFn: (id) => request<void>(`/regions/${id}`, { method: 'DELETE' }),
    onMutate: async (id) => {
      await qc.cancelQueries(['regions'] as any);
      const previous = (qc.getQueryData(['regions'] as any) as Region[]) || [];
      qc.setQueryData(['regions'] as any, previous.filter((p) => p.id !== id));
      return { previous };
    },
    onError: (_err, _id, context: any) => {
      if (context?.previous) qc.setQueryData(['regions'] as any, context.previous);
    },
    onSettled: () => qc.invalidateQueries(['regions'] as any),
  });
}
