import { useEffect, useMemo, useState } from 'react'
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
  const [editing, setEditing] = useState<Datacenter | null>(null)
  const [form, setForm] = useState<Partial<Datacenter>>({})

  useEffect(() => {
    if (editing) setForm(editing)
    else setForm({})
  }, [editing])

  function openCreate() {
    setEditing(null)
    setOpen(true)
  }

  function openEdit(item: Datacenter) {
    setEditing(item)
    setOpen(true)
  }

  async function save() {
    if (!form.name || !form.shortName) return toast.error('Name and Short Name are required')
    if (!form.regionId) return toast.error('Region is required')

    try {
      if (editing) {
        await update.mutateAsync({ id: editing.id, data: form })
        toast.success('Datacenter updated')
      } else {
        await create.mutateAsync(form)
        toast.success('Datacenter created')
      }
      setOpen(false)
    } catch (err: any) {
      toast.error(err?.message || 'Error')
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
  const rows = useMemo(() => items ?? [], [items])

  return (
    <DashboardLayout>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <h1 className="text-2xl font-semibold">Datacenters</h1>
          <div className="flex items-center gap-2">
            <button onClick={openCreate} className="inline-flex items-center gap-2 bg-indigo-600 hover:bg-indigo-700 text-white px-3 py-2 rounded">
              Add Datacenter
            </button>
          </div>
        </div>

        <div className="overflow-auto bg-white dark:bg-gray-800 rounded-lg shadow-sm">
          {isLoading ? (
            <div className="p-6 flex items-center justify-center">
              <Spinner />
            </div>
          ) : isError ? (
            <div className="p-6 text-red-600">Error: {(error as Error)?.message}</div>
          ) : (
            <table className="min-w-full table-auto">
              <thead>
                <tr className="text-left">
                  <th className="px-4 py-3 text-sm text-gray-500">Name</th>
                  <th className="px-4 py-3 text-sm text-gray-500">Short Name</th>
                  <th className="px-4 py-3 text-sm text-gray-500">Region</th>
                  <th className="px-4 py-3 text-sm text-gray-500">Private CIDR</th>
                  <th className="px-4 py-3 text-sm text-gray-500">Public CIDR</th>
                  <th className="px-4 py-3 text-sm text-gray-500">Actions</th>
                </tr>
              </thead>
              <tbody>
                {rows.map((r) => (
                  <tr key={r.id} className="border-t border-gray-100 dark:border-gray-700">
                    <td className="px-4 py-3">{r.name}</td>
                    <td className="px-4 py-3">{r.shortName}</td>
                    <td className="px-4 py-3">{regionMap[r.regionId] ?? '-'}</td>
                    <td className="px-4 py-3 text-sm text-gray-600 dark:text-gray-300">{r.privateCIDR ?? ''}</td>
                    <td className="px-4 py-3 text-sm text-gray-600 dark:text-gray-300">{r.publicCIDR ?? ''}</td>
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-2">
                        <button onClick={() => setViewing(r)} className="text-indigo-600 hover:underline">View</button>
                        <button onClick={() => openEdit(r)} className="text-indigo-600 hover:underline">Edit</button>
                        <button onClick={() => remove(r.id)} className="text-red-600 hover:underline">Delete</button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>

        <Modal open={open} title={editing ? 'Edit Datacenter' : 'Create Datacenter'} onClose={() => setOpen(false)}>
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
