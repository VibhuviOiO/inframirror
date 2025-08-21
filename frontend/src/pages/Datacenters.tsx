import { useEffect, useMemo, useState } from 'react'
import { Edit2, Trash2, Check, X } from 'lucide-react';
import {
  useDatacenters,
  useCreateDatacenter,
  useUpdateDatacenter,
  useDeleteDatacenter,
  Datacenter,
} from '../hooks/useDatacenters'
import {
  useRegions,
  Region,
} from '../hooks/useRegions'
import { toast } from 'sonner'

function Spinner() {
  return <div className="w-5 h-5 border-2 border-gray-200 border-t-indigo-600 rounded-full animate-spin" />
}

function Modal({ open, title, children, onClose }: { open: boolean; title: string; children: any; onClose: () => void }) {
  if (!open) return null
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
  )
}

import { DashboardLayout } from "@/components/dashboard/DashboardLayout";

export default function DatacentersPage() {
  // ...existing code...
  const { data: items, isLoading, isError, error } = useDatacenters()
  const { data: regions, isLoading: loadingRegions } = useRegions()
  const create = useCreateDatacenter()
  const update = useUpdateDatacenter()
  const del = useDeleteDatacenter()
  const [open, setOpen] = useState(false)
  const [viewing, setViewing] = useState<Datacenter | null>(null)
  const [editingId, setEditingId] = useState<number | null>(null)
  const [form, setForm] = useState<Partial<Datacenter>>({})
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
    if (!form.name || !form.shortName) return toast.error('Name and Short Name are required');
    if (!form.regionId) return toast.error('Region is required');

    try {
      if (editingId !== null) {
        await update.mutateAsync({ id: editingId, data: form });
        toast.success('Datacenter updated');
        setEditingId(null);
      } else {
        await create.mutateAsync(form);
        toast.success('Datacenter created');
        setOpen(false);
      }
    } catch (err: any) {
      toast.error(err?.message || 'Error');
    }
  }

  async function remove(id: number) {
    if (!confirm('Delete datacenter?')) return
    try {
      await del.mutateAsync(id)
      toast.success('Deleted')
    } catch (err: any) {
      toast.error(err?.message || 'Error')
    }
  }

  const regionMap = useMemo(() => {
    const map: Record<number, string> = {}
    regions?.forEach(r => { map[r.id] = r.name })
    return map
  }, [regions])

  const filteredRows = useMemo(() => {
    if (!items) return [];
    if (!search.trim()) return items;
    return items.filter(r => r.name.toLowerCase().includes(search.trim().toLowerCase()));
  }, [items, search]);


  return (
    <DashboardLayout>
      <div className="space-y-6">
        {/* Top area: search left, add button right */}
        <div className="flex flex-col sm:flex-row sm:items-center gap-3">
          <div className="flex w-full items-center">
            <input
              type="text"
              value={search}
              onChange={e => setSearch(e.target.value)}
              placeholder="Search datacenters..."
              className="w-full sm:w-64 px-2 py-1 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-indigo-300 text-sm shadow-sm transition"
              style={{ height: 32, fontSize: '0.925rem' }}
            />
            <div className="flex-1" />
            <button
              onClick={openCreate}
              className="ml-2 inline-flex items-center justify-center bg-gradient-to-r from-blue-600 via-blue-500 to-indigo-500 hover:from-blue-700 hover:via-blue-600 hover:to-indigo-600 text-white rounded shadow-sm transition px-2 py-1 text-sm"
              style={{ height: 32, fontSize: '0.925rem' }}
              title="Add Datacenter"
            >
              Add Datacenter
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
                  <th className="px-4 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Name</th>
                  <th className="px-4 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Short Name</th>
                  <th className="px-4 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Region</th>
                  <th className="px-4 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Private CIDR</th>
                  <th className="px-4 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Public CIDR</th>
                  <th className="px-4 py-4 text-base font-bold text-blue-800 dark:text-blue-100 text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredRows.length === 0 && (
                  <tr>
                    <td colSpan={6} className="px-6 py-8 text-center text-gray-400">No datacenters found.</td>
                  </tr>
                )}
                {filteredRows.map((r) => (
                  <tr key={r.id} className="border-t border-gray-100 dark:border-gray-700">
                    <td className="px-4 py-3">
                      {editingId === r.id ? (
                        <input
                          value={form.name || ''}
                          onChange={e => setForm(f => ({ ...f, name: e.target.value }))}
                          className="w-full px-2 py-1 border rounded"
                          placeholder="Name"
                        />
                      ) : (
                        r.name
                      )}
                    </td>
                    <td className="px-4 py-3">
                      {editingId === r.id ? (
                        <input
                          value={form.shortName || ''}
                          onChange={e => setForm(f => ({ ...f, shortName: e.target.value }))}
                          className="w-full px-2 py-1 border rounded"
                          placeholder="Short Name"
                        />
                      ) : (
                        r.shortName
                      )}
                    </td>
                    <td className="px-4 py-3">
                      {editingId === r.id ? (
                        <select
                          value={form.regionId || ''}
                          onChange={e => setForm(f => ({ ...f, regionId: Number(e.target.value) }))}
                          className="w-full px-2 py-1 border rounded"
                        >
                          <option value="">Select region</option>
                          {regions?.map(rg => (
                            <option key={rg.id} value={rg.id}>{rg.name}</option>
                          ))}
                        </select>
                      ) : (
                        regionMap[r.regionId] ?? '-'
                      )}
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-600 dark:text-gray-300">
                      {editingId === r.id ? (
                        <input
                          value={form.privateCIDR || ''}
                          onChange={e => setForm(f => ({ ...f, privateCIDR: e.target.value }))}
                          className="w-full px-2 py-1 border rounded"
                          placeholder="Private CIDR"
                        />
                      ) : (
                        r.privateCIDR ?? ''
                      )}
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-600 dark:text-gray-300">
                      {editingId === r.id ? (
                        <input
                          value={form.publicCIDR || ''}
                          onChange={e => setForm(f => ({ ...f, publicCIDR: e.target.value }))}
                          className="w-full px-2 py-1 border rounded"
                          placeholder="Public CIDR"
                        />
                      ) : (
                        r.publicCIDR ?? ''
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
                            {/* You can use an icon here if desired */}
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

        <Modal open={open} title={editingId !== null ? 'Edit Datacenter' : 'Create Datacenter'} onClose={() => setOpen(false)}>
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium mb-1">Name</label>
              <input value={form.name || ''} onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))} className="w-full px-3 py-2 border rounded bg-white dark:bg-gray-900" />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Short Name</label>
              <input value={form.shortName || ''} onChange={(e) => setForm((f) => ({ ...f, shortName: e.target.value }))} className="w-full px-3 py-2 border rounded bg-white dark:bg-gray-900" />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Region</label>
              <select
                value={form.regionId || ''}
                onChange={e => setForm(f => ({ ...f, regionId: Number(e.target.value) }))}
                className="w-full px-3 py-2 border rounded bg-white dark:bg-gray-900"
                disabled={loadingRegions}
              >
                <option value="">Select region</option>
                {regions?.map(r => (
                  <option key={r.id} value={r.id}>{r.name}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Private CIDR</label>
              <input value={form.privateCIDR || ''} onChange={(e) => setForm((f) => ({ ...f, privateCIDR: e.target.value }))} className="w-full px-3 py-2 border rounded bg-white dark:bg-gray-900" />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Public CIDR</label>
              <input value={form.publicCIDR || ''} onChange={(e) => setForm((f) => ({ ...f, publicCIDR: e.target.value }))} className="w-full px-3 py-2 border rounded bg-white dark:bg-gray-900" />
            </div>

            <div className="flex items-center justify-end gap-2 pt-4">
              <button onClick={() => setOpen(false)} className="px-3 py-2 rounded border">Cancel</button>
              <button onClick={save} className="px-3 py-2 rounded bg-indigo-600 text-white">Save</button>
            </div>
          </div>
        </Modal>

        <Modal open={!!viewing} title="Datacenter details" onClose={() => setViewing(null)}>
          {viewing && (
            <div className="space-y-2">
              <div><strong>Name:</strong> {viewing.name}</div>
              <div><strong>Short Name:</strong> {viewing.shortName}</div>
              <div><strong>Region:</strong> {regionMap[viewing.regionId] ?? '-'}</div>
              <div><strong>Private CIDR:</strong> {viewing.privateCIDR ?? '-'}</div>
              <div><strong>Public CIDR:</strong> {viewing.publicCIDR ?? '-'}</div>
            </div>
          )}
        </Modal>
      </div>
    </DashboardLayout>
  )
}
