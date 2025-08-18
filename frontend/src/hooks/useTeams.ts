import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';

export type Team = {
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

export function useTeams() {
  return useQuery<Team[], Error>({
    queryKey: ['teams'],
    queryFn: async () => request<Team[]>('/teams'),
  });
}

export function useCreateTeam() {
  const qc = useQueryClient();
  return useMutation<Team, Error, Partial<Team>>({
    mutationFn: (payload) => request<Team>('/teams', { method: 'POST', body: JSON.stringify(payload) }),
    onMutate: async (newTeam) => {
      await qc.cancelQueries(['teams'] as any);
      const previous = (qc.getQueryData(['teams'] as any) as Team[]) || [];
      const optimistic: Team = {
        id: Date.now(),
        name: newTeam.name as string,
      };
      qc.setQueryData(['teams'] as any, [...previous, optimistic]);
      return { previous };
    },
    onError: (_err, _newTeam, context: any) => {
      if (context?.previous) qc.setQueryData(['teams'] as any, context.previous);
    },
    onSettled: () => qc.invalidateQueries(['teams'] as any),
  });
}

export function useUpdateTeam() {
  const qc = useQueryClient();
  return useMutation<Team, Error, { id: number; data: Partial<Team> }>({
    mutationFn: ({ id, data }) => request<Team>(`/teams/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    onMutate: async ({ id, data }) => {
      await qc.cancelQueries(['teams'] as any);
      const previous = (qc.getQueryData(['teams'] as any) as Team[]) || [];
      qc.setQueryData(['teams'] as any, previous.map((p) => (p.id === id ? { ...p, ...(data as Team) } : p)));
      return { previous };
    },
    onError: (_err, _vars, context: any) => {
      if (context?.previous) qc.setQueryData(['teams'] as any, context.previous);
    },
    onSettled: () => qc.invalidateQueries(['teams'] as any),
  });
}

export function useDeleteTeam() {
  const qc = useQueryClient();
  return useMutation<void, Error, number>({
    mutationFn: (id) => request<void>(`/teams/${id}`, { method: 'DELETE' }),
    onMutate: async (id) => {
      await qc.cancelQueries(['teams'] as any);
      const previous = (qc.getQueryData(['teams'] as any) as Team[]) || [];
      qc.setQueryData(['teams'] as any, previous.filter((p) => p.id !== id));
      return { previous };
    },
    onError: (_err, _id, context: any) => {
      if (context?.previous) qc.setQueryData(['teams'] as any, context.previous);
    },
    onSettled: () => qc.invalidateQueries(['teams'] as any),
  });
}
