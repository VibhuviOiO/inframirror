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

export default function ServicesPage() {
  const { data: items, isLoading, isError, error } = useServices();
  const { data: datacenters } = useDatacenters();
  const { data: hosts } = useHosts();
  const { data: catalogs } = useCatalogs();
  const create = useCreateService();
  const update = useUpdateService();
  const del = useDeleteService();
  const bulkDelete = useBulkDeleteServices();
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
      <div className="space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center gap-3">
          <div className="flex w-full items-center">
            <span className="font-semibold text-lg text-blue-900 dark:text-blue-100 mr-4">Services</span>
            <div className="flex-1" />
            <button
              onClick={() => setEditingId(-1)}
              className="ml-2 inline-flex items-center justify-center bg-gradient-to-r from-blue-600 via-blue-500 to-indigo-500 hover:from-blue-700 hover:via-blue-600 hover:to-indigo-600 text-white rounded shadow-sm transition px-2 py-1 text-sm"
              style={{ height: 32, fontSize: '0.925rem' }}
              title="Add Service"
            >
              Add Service
            </button>
          </div>
        </div>
        <div className="overflow-auto bg-white dark:bg-gray-800 shadow-sm">
          {isLoading ? (
            <div className="p-6 flex items-center justify-center">
              <Spinner />
            </div>
          ) : isError ? (
            <div className="p-6 text-red-600">Error: {(error as Error)?.message}</div>
          ) : (
            <table className="min-w-full table-auto">
              <thead>
                <tr className="text-left bg-blue-100 dark:bg-blue-950 animate-fade-in">
                  <th className="px-4 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Datacenter</th>
                  <th className="px-4 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Host</th>
                  <th className="px-4 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Catalog</th>
                  <th className="px-4 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Custom Port</th>
                  <th className="px-4 py-4 text-base font-bold text-blue-800 dark:text-blue-100 text-right">Actions</th>
                </tr>
                <tr className="bg-blue-50 dark:bg-blue-900/40">
                  <th className="px-4 py-1">
                    <select
                      className="w-full px-2 py-1 border border-gray-200 dark:border-gray-700 rounded text-xs bg-white dark:bg-gray-900 focus:ring-1 focus:ring-blue-300"
                      value={filterDatacenter}
                      onChange={e => setFilterDatacenter(e.target.value)}
                      style={{ minWidth: 80 }}
                    >
                      <option value="">All</option>
                      {datacenters?.map(dc => (
                        <option key={dc.id} value={dc.id}>{dc.name}</option>
                      ))}
                    </select>
                  </th>
                  <th className="px-4 py-1">
                    <select
                      className="w-full px-2 py-1 border border-gray-200 dark:border-gray-700 rounded text-xs bg-white dark:bg-gray-900 focus:ring-1 focus:ring-blue-300"
                      value={filterHost}
                      onChange={e => setFilterHost(e.target.value)}
                      style={{ minWidth: 80 }}
                    >
                      <option value="">All</option>
                      {hosts?.map(h => (
                        <option key={h.id} value={h.id}>{h.hostname}</option>
                      ))}
                    </select>
                  </th>
                  <th className="px-4 py-1">
                    <select
                      className="w-full px-2 py-1 border border-gray-200 dark:border-gray-700 rounded text-xs bg-white dark:bg-gray-900 focus:ring-1 focus:ring-blue-300"
                      value={filterCatalog}
                      onChange={e => setFilterCatalog(e.target.value)}
                      style={{ minWidth: 80 }}
                    >
                      <option value="">All</option>
                      {catalogs?.map(c => (
                        <option key={c.id} value={c.id}>{c.name}</option>
                      ))}
                    </select>
                  </th>
                </tr>
              </thead>
              <tbody>
                {filteredRows.length === 0 && (
                  <tr>
                    <td colSpan={5} className="px-6 py-8 text-center text-gray-400">No services found.</td>
                  </tr>
                )}
                {filteredRows.map((r) => (
                  <tr key={r.id} className="border-t border-gray-100 dark:border-gray-700">
                    {/* Datacenter */}
                    <td className="px-4 py-3">
                      {editingId === r.id ? (
                        <select
                          value={form.datacenterId || ''}
                          onChange={e => setForm(f => ({ ...f, datacenterId: Number(e.target.value) }))}
                          className="w-full px-2 py-1 border rounded"
                        >
                          <option value="">Select datacenter</option>
                          {datacenters?.map(dc => (
                            <option key={dc.id} value={dc.id}>{dc.name}</option>
                          ))}
                        </select>
                      ) : (
                        datacenterMap[r.datacenterId] ?? '-'
                      )}
                    </td>
                    {/* Host */}
                    <td className="px-4 py-3">
                      {editingId === r.id ? (
                        <select
                          value={form.hostId || ''}
                          onChange={e => setForm(f => ({ ...f, hostId: Number(e.target.value) }))}
                          className="w-full px-2 py-1 border rounded"
                        >
                          <option value="">Select host</option>
                          {hosts?.map(h => (
                            <option key={h.id} value={h.id}>{h.hostname}</option>
                          ))}
                        </select>
                      ) : (
                        hostMap[r.hostId] ?? '-'
                      )}
                    </td>
                    {/* Catalog */}
                    <td className="px-4 py-3">
                      {editingId === r.id ? (
                        <select
                          value={form.catalogId || ''}
                          onChange={e => setForm(f => ({ ...f, catalogId: Number(e.target.value) }))}
                          className="w-full px-2 py-1 border rounded"
                        >
                          <option value="">Select catalog</option>
                          {catalogs?.map(c => (
                            <option key={c.id} value={c.id}>{c.name}</option>
                          ))}
                        </select>
                      ) : (
                        catalogMap[r.catalogId] ?? '-'
                      )}
                    </td>
                    {/* Custom Port */}
                    <td className="px-4 py-3">
                      {editingId === r.id ? (
                        <input
                          type="number"
                          className="w-full px-2 py-1 border rounded"
                          value={form.customPort ?? ''}
                          onChange={e => setForm(f => ({ ...f, customPort: e.target.value === '' ? null : Number(e.target.value) }))}
                          placeholder="Custom Port"
                        />
                      ) : (
                        r.customPort ?? '-'
                      )}
                    </td>
                    {/* Actions */}
                    <td className="px-4 py-3 text-right">
                      {editingId === r.id ? (
                        <div className="flex gap-2 justify-end">
                          <button onClick={save} className="bg-green-500 hover:bg-green-600 text-white px-2 py-1 rounded" title="Save"><Check size={16} /></button>
                          <button onClick={() => setEditingId(null)} className="bg-gray-300 hover:bg-gray-400 text-gray-800 px-2 py-1 rounded" title="Cancel"><X size={16} /></button>
                        </div>
                      ) : (
                        <div className="flex gap-2 justify-end">
                          <button
                            onClick={() => setEditingId(r.id)}
                            className="p-1 text-blue-600 dark:text-blue-400 hover:text-blue-800 dark:hover:text-blue-200 focus:outline-none"
                            title="Edit"
                          >
                            <Edit2 size={18} />
                          </button>
                          <button
                            onClick={() => remove(r.id)}
                            className="p-1 text-red-500 hover:text-red-700 focus:outline-none"
                            title="Delete"
                          >
                            <Trash2 size={18} />
                          </button>
                        </div>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </DashboardLayout>
  );
}
