import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'

export type Datacenter = {
  id: number
  name: string
  shortName: string
  regionId: number
  privateCIDR?: string | null
  publicCIDR?: string | null
}

const API_BASE = (import.meta.env.VITE_API_BASE_URL ?? '').replace(/\/$/, '')

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...init,
  })

  if (!res.ok) {
    const text = await res.text().catch(() => res.statusText)
    throw new Error(text || res.statusText)
  }

  if (res.status === 204) return null as unknown as T
  return (await res.json()) as T
}

export function useDatacenters() {
  return useQuery<Datacenter[], Error>({
    queryKey: ['datacenters'],
    queryFn: async () => request<Datacenter[]>('/datacenters'),
  })
}

export function useDatacenter(id?: number) {
  return useQuery<Datacenter, Error>({
    queryKey: ['datacenter', id],
    queryFn: async () => request<Datacenter>(`/datacenters/${id}`),
    enabled: typeof id === 'number',
  })
}

export function useCreateDatacenter() {
  const qc = useQueryClient()
  return useMutation<Datacenter, Error, Partial<Datacenter>>({
    mutationFn: (payload) => request<Datacenter>('/datacenters', { method: 'POST', body: JSON.stringify(payload) }),
    onMutate: async (newDc) => {
      await qc.cancelQueries(['datacenters'] as any)
      const previous = (qc.getQueryData(['datacenters'] as any) as Datacenter[]) || []
      const optimistic: Datacenter = {
        id: Date.now(),
        name: newDc.name as string,
        shortName: newDc.shortName as string,
        regionId: Number(newDc.regionId),
        privateCIDR: newDc.privateCIDR ?? null,
        publicCIDR: newDc.publicCIDR ?? null,
      }
      qc.setQueryData(['datacenters'] as any, [...previous, optimistic])
      return { previous }
    },
    onError: (_err, _newDc, context: any) => {
      if (context?.previous) qc.setQueryData(['datacenters'] as any, context.previous)
    },
    onSettled: () => qc.invalidateQueries(['datacenters'] as any),
  })
}

export function useUpdateDatacenter() {
  const qc = useQueryClient()
  return useMutation<Datacenter, Error, { id: number; data: Partial<Datacenter> }>({
    mutationFn: ({ id, data }) => request<Datacenter>(`/datacenters/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    onMutate: async ({ id, data }) => {
      await qc.cancelQueries(['datacenters'] as any)
      const previous = (qc.getQueryData(['datacenters'] as any) as Datacenter[]) || []
      qc.setQueryData(['datacenters'] as any, previous.map((p) => (p.id === id ? { ...p, ...(data as Datacenter) } : p)))
      return { previous }
    },
    onError: (_err, _vars, context: any) => {
      if (context?.previous) qc.setQueryData(['datacenters'] as any, context.previous)
    },
    onSettled: () => qc.invalidateQueries(['datacenters'] as any),
  })
}

export function useDeleteDatacenter() {
  const qc = useQueryClient()
  return useMutation<void, Error, number>({
    mutationFn: (id) => request<void>(`/datacenters/${id}`, { method: 'DELETE' }),
    onMutate: async (id) => {
      await qc.cancelQueries(['datacenters'] as any)
      const previous = (qc.getQueryData(['datacenters'] as any) as Datacenter[]) || []
      qc.setQueryData(['datacenters'] as any, previous.filter((p) => p.id !== id))
      return { previous }
    },
    onError: (_err, _id, context: any) => {
      if (context?.previous) qc.setQueryData(['datacenters'] as any, context.previous)
    },
    onSettled: () => qc.invalidateQueries(['datacenters'] as any),
  })
}
