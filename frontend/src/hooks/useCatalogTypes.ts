
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';

const API_BASE = import.meta.env.VITE_API_BASE_URL?.replace(/\/$/, '') || 'http://localhost:8080';

function fetchJSON(url: string, options?: RequestInit) {
  const fullUrl = url.startsWith('http') ? url : `${API_BASE}${url.startsWith('/') ? '' : '/'}${url}`;
  return fetch(fullUrl, options).then(async res => {
    const text = await res.text();
    let json;
    try { json = JSON.parse(text); } catch { json = undefined; }
    if (!res.ok) {
      const errMsg = json?.message || text || res.statusText;
      throw new Error(`HTTP ${res.status} ${res.statusText}: ${errMsg}`);
    }
    return json;
  });
}

// List all catalog types
export function useCatalogTypes() {
  return useQuery({
    queryKey: ['catalog-types'],
    queryFn: () => fetchJSON('/catalog-types'),
  });
}

// Create a new catalog type
export function useCreateCatalogType() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: { name: string; description?: string }) =>
      fetchJSON('/catalog-types', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['catalog-types'] });
    },
  });
}

// Update a catalog type
export function useUpdateCatalogType() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: { name: string; description?: string } }) =>
      fetchJSON(`/catalog-types/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['catalog-types'] });
    },
  });
}

// Delete a catalog type
export function useDeleteCatalogType() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) =>
      fetchJSON(`/catalog-types/${id}`, {
        method: 'DELETE',
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['catalog-types'] });
    },
  });
}
