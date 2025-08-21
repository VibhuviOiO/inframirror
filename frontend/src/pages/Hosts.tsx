import { useEffect, useMemo, useState } from 'react';
import {
  useHosts,
  useCreateHost,
  useUpdateHost,
  useDeleteHost,
  Host,
} from '../hooks/useHosts';
import { useDatacenters } from '../hooks/useDatacenters';
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

export default function HostsPage() {
  const { data: items, isLoading, isError, error } = useHosts();
  const { data: datacenters, isLoading: loadingDatacenters } = useDatacenters();
  const create = useCreateHost();
  const update = useUpdateHost();
  const del = useDeleteHost();
  const [open, setOpen] = useState(false);
  const [viewing, setViewing] = useState<Host | null>(null);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [form, setForm] = useState<Partial<Host>>({});
  const [search, setSearch] = useState('');

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
    if (!form.hostname) return toast.error('Hostname is required');
    if (!form.privateIP) return toast.error('Private IP is required');
    if (!form.kind) return toast.error('Kind is required');
    try {
      if (editingId !== null) {
        await update.mutateAsync({ id: editingId, data: form });
        toast.success('Host updated');
        setEditingId(null);
      } else {
        await create.mutateAsync(form);
        toast.success('Host created');
        setOpen(false);
      }
    } catch (err: any) {
      toast.error(err?.message || 'Error');
    }
  }

  async function remove(id: number) {
    if (!confirm('Delete host?')) return;
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

  const filteredRows = useMemo(() => {
    if (!items) return [];
    if (!search.trim()) return items;
    return items.filter((r) => r.hostname.toLowerCase().includes(search.trim().toLowerCase()));
  }, [items, search]);

  return (
    <DashboardLayout>
      <div className="space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center gap-3">
          <div className="flex w-full items-center">
            <input
              type="text"
              value={search}
              onChange={e => setSearch(e.target.value)}
              placeholder="Search hosts..."
              className="w-full sm:w-64 px-2 py-1 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-indigo-300 text-sm shadow-sm transition"
              style={{ height: 32, fontSize: '0.925rem' }}
            />
            <div className="flex-1" />
            <button
              onClick={openCreate}
              className="ml-2 inline-flex items-center justify-center bg-gradient-to-r from-blue-600 via-blue-500 to-indigo-500 hover:from-blue-700 hover:via-blue-600 hover:to-indigo-600 text-white rounded shadow-sm transition px-2 py-1 text-sm"
              style={{ height: 32, fontSize: '0.925rem' }}
              title="Add Host"
            >
              Add Host
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
                  <th className="px-4 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Hostname</th>
                  <th className="px-4 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Private IP</th>
                  <th className="px-4 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Public IP</th>
                  <th className="px-4 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Kind</th>
                  <th className="px-4 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Tags</th>
                  <th className="px-4 py-4 text-base font-bold text-blue-800 dark:text-blue-100 text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredRows.length === 0 && (
                  <tr>
                    <td colSpan={7} className="px-6 py-8 text-center text-gray-400">No hosts found.</td>
                  </tr>
                )}
                {filteredRows.map((r) => (
                  <tr key={r.id} className="border-t border-gray-100 dark:border-gray-700">
                    <td className="px-4 py-3">
                      {editingId === r.id ? (
                        <select
                          value={form.datacenterId || ''}
                          onChange={e => setForm(f => ({ ...f, datacenterId: Number(e.target.value) }))}
                          className="w-full px-2 py-1 border rounded"
                          disabled={loadingDatacenters}
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
                    <td className="px-4 py-3">
                      {editingId === r.id ? (
                        <input
                          value={form.hostname || ''}
                          onChange={e => setForm(f => ({ ...f, hostname: e.target.value }))}
                          className="w-full px-2 py-1 border rounded"
                          placeholder="Hostname"
                        />
                      ) : (
                        r.hostname
                      )}
                    </td>
                    <td className="px-4 py-3">
                      {editingId === r.id ? (
                        <input
                          value={form.privateIP || ''}
                          onChange={e => setForm(f => ({ ...f, privateIP: e.target.value }))}
                          className="w-full px-2 py-1 border rounded"
                          placeholder="Private IP"
                        />
                      ) : (
                        r.privateIP
                      )}
                    </td>
                    <td className="px-4 py-3">
                      {editingId === r.id ? (
                        <input
                          value={form.publicIP || ''}
                          onChange={e => setForm(f => ({ ...f, publicIP: e.target.value }))}
                          className="w-full px-2 py-1 border rounded"
                          placeholder="Public IP"
                        />
                      ) : (
                        r.publicIP ?? ''
                      )}
                    </td>
                    <td className="px-4 py-3">
                      {editingId === r.id ? (
                        <select
                          value={form.kind || ''}
                          onChange={e => setForm(f => ({ ...f, kind: e.target.value as Host['kind'] }))}
                          className="w-full px-2 py-1 border rounded"
                        >
                          <option value="">Select kind</option>
                          <option value="VM">VM</option>
                          <option value="Physical">Physical</option>
                          <option value="BareMetal">BareMetal</option>
                        </select>
                      ) : (
                        r.kind
                      )}
                    </td>
                    <td className="px-4 py-3 text-xs">
                      {editingId === r.id ? (
                        <input
                          value={typeof form.tags === 'string' ? form.tags : JSON.stringify(form.tags || {})}
                          onChange={e => setForm(f => ({ ...f, tags: e.target.value }))}
                          className="w-full px-2 py-1 border rounded"
                          placeholder="Tags (JSON)"
                        />
                      ) : (
                        r.tags ? JSON.stringify(r.tags) : ''
                      )}
                    </td>
                    <td className="px-4 py-3 text-right">
                      {editingId === r.id ? (
                        <div className="flex gap-2 justify-end">
                          <button onClick={save} className="bg-green-500 hover:bg-green-600 text-white px-2 py-1 rounded" title="Save"><Check size={16} /></button>
                          <button onClick={() => setEditingId(null)} className="bg-gray-300 hover:bg-gray-400 text-gray-800 px-2 py-1 rounded" title="Cancel"><X size={16} /></button>
                        </div>
                      ) : (
                        <div className="flex gap-2 justify-end">
                          <button
                            onClick={() => setViewing(r)}
                            className="p-1 text-indigo-600 dark:text-indigo-400 hover:text-indigo-800 dark:hover:text-indigo-200 focus:outline-none"
                            title="View"
                          >
                            <span className="sr-only">View</span>
                            View
                          </button>
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
        <Modal open={open} title={editingId !== null ? 'Edit Host' : 'Create Host'} onClose={() => setOpen(false)}>
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium mb-1">Datacenter</label>
              <select
                value={form.datacenterId || ''}
                onChange={e => setForm(f => ({ ...f, datacenterId: Number(e.target.value) }))}
                className="w-full px-3 py-2 border rounded bg-white dark:bg-gray-900"
                disabled={loadingDatacenters}
              >
                <option value="">Select datacenter</option>
                {datacenters?.map(dc => (
                  <option key={dc.id} value={dc.id}>{dc.name}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Hostname</label>
              <input value={form.hostname || ''} onChange={e => setForm((f) => ({ ...f, hostname: e.target.value }))} className="w-full px-3 py-2 border rounded bg-white dark:bg-gray-900" />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Private IP</label>
              <input value={form.privateIP || ''} onChange={e => setForm((f) => ({ ...f, privateIP: e.target.value }))} className="w-full px-3 py-2 border rounded bg-white dark:bg-gray-900" />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Public IP</label>
              <input value={form.publicIP || ''} onChange={e => setForm((f) => ({ ...f, publicIP: e.target.value }))} className="w-full px-3 py-2 border rounded bg-white dark:bg-gray-900" />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Kind</label>
              <select
                value={form.kind || ''}
                onChange={e => setForm(f => ({ ...f, kind: e.target.value as Host['kind'] }))}
                className="w-full px-3 py-2 border rounded bg-white dark:bg-gray-900"
              >
                <option value="">Select kind</option>
                <option value="VM">VM</option>
                <option value="Physical">Physical</option>
                <option value="BareMetal">BareMetal</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Tags (JSON)</label>
              <input value={typeof form.tags === 'string' ? form.tags : JSON.stringify(form.tags || {})} onChange={e => setForm((f) => ({ ...f, tags: e.target.value }))} className="w-full px-3 py-2 border rounded bg-white dark:bg-gray-900" />
            </div>
            <div className="flex items-center justify-end gap-2 pt-4">
              <button onClick={() => setOpen(false)} className="px-3 py-2 rounded border">Cancel</button>
              <button onClick={save} className="px-3 py-2 rounded bg-indigo-600 text-white">Save</button>
            </div>
          </div>
        </Modal>
        <Modal open={!!viewing} title="Host details" onClose={() => setViewing(null)}>
          {viewing && (
            <div className="space-y-2">
              <div><strong>Datacenter:</strong> {datacenterMap[viewing.datacenterId] ?? '-'}</div>
              <div><strong>Hostname:</strong> {viewing.hostname}</div>
              <div><strong>Private IP:</strong> {viewing.privateIP}</div>
              <div><strong>Public IP:</strong> {viewing.publicIP ?? '-'}</div>
              <div><strong>Kind:</strong> {viewing.kind}</div>
              <div><strong>Tags:</strong> {viewing.tags ? JSON.stringify(viewing.tags) : '-'}</div>
            </div>
          )}
        </Modal>
      </div>
    </DashboardLayout>
  );
}
