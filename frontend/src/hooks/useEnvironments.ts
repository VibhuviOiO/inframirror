import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';

export type Environment = {
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

export function useEnvironments() {
  return useQuery<Environment[], Error>({
    queryKey: ['environments'],
    queryFn: async () => request<Environment[]>('/environments'),
  });
}

export function useCreateEnvironment() {
  const qc = useQueryClient();
  return useMutation<Environment, Error, Partial<Environment>>({
    mutationFn: (payload) => request<Environment>('/environments', { method: 'POST', body: JSON.stringify(payload) }),
    onMutate: async (newEnv) => {
      await qc.cancelQueries(['environments'] as any);
      const previous = (qc.getQueryData(['environments'] as any) as Environment[]) || [];
      const optimistic: Environment = {
        id: Date.now(),
        name: newEnv.name as string,
      };
      qc.setQueryData(['environments'] as any, [...previous, optimistic]);
      return { previous };
    },
    onError: (_err, _newEnv, context: any) => {
      if (context?.previous) qc.setQueryData(['environments'] as any, context.previous);
    },
    onSettled: () => qc.invalidateQueries(['environments'] as any),
  });
}

export function useUpdateEnvironment() {
  const qc = useQueryClient();
  return useMutation<Environment, Error, { id: number; data: Partial<Environment> }>({
    mutationFn: ({ id, data }) => request<Environment>(`/environments/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    onMutate: async ({ id, data }) => {
      await qc.cancelQueries(['environments'] as any);
      const previous = (qc.getQueryData(['environments'] as any) as Environment[]) || [];
      qc.setQueryData(['environments'] as any, previous.map((p) => (p.id === id ? { ...p, ...(data as Environment) } : p)));
      return { previous };
    },
    onError: (_err, _vars, context: any) => {
      if (context?.previous) qc.setQueryData(['environments'] as any, context.previous);
    },
    onSettled: () => qc.invalidateQueries(['environments'] as any),
  });
}

export function useDeleteEnvironment() {
  const qc = useQueryClient();
  return useMutation<void, Error, number>({
    mutationFn: (id) => request<void>(`/environments/${id}`, { method: 'DELETE' }),
    onMutate: async (id) => {
      await qc.cancelQueries(['environments'] as any);
      const previous = (qc.getQueryData(['environments'] as any) as Environment[]) || [];
      qc.setQueryData(['environments'] as any, previous.filter((p) => p.id !== id));
      return { previous };
    },
    onError: (_err, _id, context: any) => {
      if (context?.previous) qc.setQueryData(['environments'] as any, context.previous);
    },
    onSettled: () => qc.invalidateQueries(['environments'] as any),
  });
}
