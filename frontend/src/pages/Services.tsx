import { useEffect, useMemo, useState } from 'react';
import {
  useServices,
  useCreateService,
  useUpdateService,
  useDeleteService,
  useBulkDeleteServices,
  Service,
} from '../hooks/useServices';
import { useDatacenters } from '../hooks/useDatacenters';
import { useHosts } from '../hooks/useHosts';
import { useCatalogs } from '../hooks/useCatalogs';
import { toast } from 'sonner';
import { Edit2, Trash2, Check, X } from 'lucide-react';
import { DashboardLayout } from '@/components/dashboard/DashboardLayout';

function Spinner() {
  return <div className="w-5 h-5 border-2 border-gray-200 border-t-indigo-600 rounded-full animate-spin" />;
}

function Modal({ open, title, children, onClose }: { open: boolean; title: string; children: any; onClose: () => void }) {
  if (!open) return null;
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div className="absolute inset-0 bg-black/40" onClick={onClose} />
      <div className="relative w-full max-w-2xl bg-white dark:bg-gray-800 rounded-lg shadow-lg p-6">
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-lg font-semibold">{title}</h3>
          <button onClick={onClose} aria-label="Close" className="text-gray-500 hover:text-gray-700">✕</button>
        </div>
        <div>{children}</div>
      </div>
    </div>
  );
}

export default function ServicesPage() {
  const { data: items, isLoading, isError, error } = useServices();
  const { data: datacenters } = useDatacenters();
  const { data: hosts } = useHosts();
  const { data: catalogs } = useCatalogs();
  const create = useCreateService();
  const update = useUpdateService();
  const del = useDeleteService();
  const bulkDelete = useBulkDeleteServices();
  const [open, setOpen] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [form, setForm] = useState<Partial<Service>>({});
  // Per-column filters
  const [filterDatacenter, setFilterDatacenter] = useState('');
  const [filterHost, setFilterHost] = useState('');
  const [filterCatalog, setFilterCatalog] = useState('');

  useEffect(() => {
    if (editingId !== null) {
      const row = items?.find((r) => r.id === editingId);
      if (row) setForm(row);
    } else {
      setForm({});
    }
  }, [editingId, items]);

  function openCreate() {
    setEditingId(null);
    setOpen(true);
  }

  async function save() {
    if (!form.datacenterId) return toast.error('Datacenter is required');
    if (!form.hostId) return toast.error('Host is required');
    if (!form.catalogId) return toast.error('Catalog is required');
    try {
      if (editingId !== null) {
  await update.mutateAsync({ id: editingId, data: form as Omit<Service, 'id'> });
        toast.success('Service updated');
        setEditingId(null);
      } else {
  await create.mutateAsync(form as Omit<Service, 'id'>);
        toast.success('Service created');
        setOpen(false);
      }
    } catch (err: any) {
      toast.error(err?.message || 'Error');
    }
  }

  async function remove(id: number) {
    if (!confirm('Delete service?')) return;
    try {
      await del.mutateAsync(id);
      toast.success('Deleted');
    } catch (err: any) {
      toast.error(err?.message || 'Error');
    }
  }

  const datacenterMap = useMemo(() => {
    const map: Record<number, string> = {};
    datacenters?.forEach((d) => {
      map[d.id] = d.name;
    });
    return map;
  }, [datacenters]);

  const hostMap = useMemo(() => {
    const map: Record<number, string> = {};
    hosts?.forEach((h) => {
      map[h.id] = h.hostname;
    });
    return map;
  }, [hosts]);

  const catalogMap = useMemo(() => {
    const map: Record<number, string> = {};
    catalogs?.forEach((c) => {
      map[c.id] = c.name;
    });
    return map;
  }, [catalogs]);

  const filteredRows = useMemo(() => {
    if (!items) return [];
    return items.filter((r) => {
      if (filterDatacenter && String(r.datacenterId) !== filterDatacenter) return false;
      if (filterHost && String(r.hostId) !== filterHost) return false;
      if (filterCatalog && String(r.catalogId) !== filterCatalog) return false;
      return true;
    });
  }, [items, filterDatacenter, filterHost, filterCatalog]);

  return (
    <DashboardLayout>
      <div className="flex flex-col gap-4">
        <div className="flex flex-wrap gap-2 items-center">
          <button
            className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded shadow"
            onClick={openCreate}
          >
            Add Service
          </button>
          <button
            className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded shadow disabled:opacity-50"
            disabled={bulkDelete.isPending}
            onClick={async () => {
              const ids = filteredRows.map((r) => r.id);
              if (!ids.length) return;
              if (!window.confirm(`Delete ${ids.length} filtered services?`)) return;
              try {
                await bulkDelete.mutateAsync(ids);
                toast.success('Deleted');
              } catch (err: any) {
                toast.error(err?.message || 'Bulk delete failed');
              }
            }}
          >
            Delete Filtered
          </button>
        </div>
        <div className="overflow-x-auto bg-white dark:bg-gray-900 rounded shadow">
          <table className="min-w-full text-sm">
            <thead>
              <tr>
                <th className="px-4 py-2">Datacenter
                  <select
                    className="block w-full mt-1 border rounded"
                    value={filterDatacenter}
                    onChange={e => setFilterDatacenter(e.target.value)}
                  >
                    <option value="">All</option>
                    {datacenters?.map((d) => (
                      <option key={d.id} value={d.id}>{d.name}</option>
                    ))}
                  </select>
                </th>
                <th className="px-4 py-2">Host
                  <select
                    className="block w-full mt-1 border rounded"
                    value={filterHost}
                    onChange={e => setFilterHost(e.target.value)}
                  >
                    <option value="">All</option>
                    {hosts?.map((h) => (
                      <option key={h.id} value={h.id}>{h.hostname}</option>
                    ))}
                  </select>
                </th>
                <th className="px-4 py-2">Catalog
                  <select
                    className="block w-full mt-1 border rounded"
                    value={filterCatalog}
                    onChange={e => setFilterCatalog(e.target.value)}
                  >
                    <option value="">All</option>
                    {catalogs?.map((c) => (
                      <option key={c.id} value={c.id}>{c.name}</option>
                    ))}
                  </select>
                </th>
                <th className="px-4 py-2 text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredRows.length === 0 && (
                <tr>
                  <td colSpan={4} className="text-center py-8 text-gray-400">No services found.</td>
                </tr>
              )}
              {filteredRows.map((r) => (
                <tr key={r.id} className="border-b">
                  <td className="px-4 py-2">{datacenterMap[r.datacenterId] || r.datacenterId}</td>
                  <td className="px-4 py-2">{hostMap[r.hostId] || r.hostId}</td>
                  <td className="px-4 py-2">{catalogMap[r.catalogId] || r.catalogId}</td>
                  <td className="px-4 py-2 text-right">
                    <button
                      className="inline-flex items-center text-blue-600 hover:text-blue-900 mr-2"
                      onClick={() => {
                        setEditingId(r.id);
                        setOpen(true);
                      }}
                    >
                      <Edit2 size={16} />
                    </button>
                    <button
                      className="inline-flex items-center text-red-600 hover:text-red-900"
                      onClick={() => remove(r.id)}
                    >
                      <Trash2 size={16} />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
      <Modal open={open} title={editingId !== null ? 'Edit Service' : 'Add Service'} onClose={() => { setOpen(false); setEditingId(null); }}>
        <div className="flex flex-col gap-4">
          <div>
            <label className="block text-sm font-medium mb-1">Datacenter</label>
            <select
              className="w-full border rounded px-2 py-1"
              value={form.datacenterId || ''}
              onChange={e => setForm(f => ({ ...f, datacenterId: Number(e.target.value) }))}
            >
              <option value="">Select Datacenter</option>
              {datacenters?.map((d) => (
                <option key={d.id} value={d.id}>{d.name}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Host</label>
            <select
              className="w-full border rounded px-2 py-1"
              value={form.hostId || ''}
              onChange={e => setForm(f => ({ ...f, hostId: Number(e.target.value) }))}
            >
              <option value="">Select Host</option>
              {hosts?.map((h) => (
                <option key={h.id} value={h.id}>{h.hostname}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Catalog</label>
            <select
              className="w-full border rounded px-2 py-1"
              value={form.catalogId || ''}
              onChange={e => setForm(f => ({ ...f, catalogId: Number(e.target.value) }))}
            >
              <option value="">Select Catalog</option>
              {catalogs?.map((c) => (
                <option key={c.id} value={c.id}>{c.name}</option>
              ))}
            </select>
          </div>
        </div>
        <div className="flex justify-end gap-2 mt-6">
          <button className="px-4 py-2 rounded border" onClick={() => { setOpen(false); setEditingId(null); }}>Cancel</button>
          <button
            className="px-4 py-2 rounded bg-indigo-600 text-white font-semibold disabled:opacity-60"
            disabled={!form.datacenterId || !form.hostId || !form.catalogId}
            onClick={save}
          >
            {editingId !== null ? 'Update' : 'Add'}
          </button>
        </div>
      </Modal>
    </DashboardLayout>
  );
}
