import { useEffect, useMemo, useState } from 'react'

type Status = 'ACTIVE' | 'INACTIVE' | 'MAINTENANCE'

type Datacenter = {
  id: string
  name: string
  location: string
  description: string
  status: Status
}

function statusOptions(): Status[] {
  return ['ACTIVE', 'INACTIVE', 'MAINTENANCE']
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

export default function DatacentersPage() {
  const [items, setItems] = useState<Datacenter[]>(() => {
    // seed with a couple entries
    return [
      { id: 'dc-1', name: 'US-East', location: 'Virginia, USA', description: 'Primary US-East site', status: 'ACTIVE' },
      { id: 'dc-2', name: 'EU-West', location: 'Dublin, Ireland', description: 'EU region', status: 'MAINTENANCE' },
    ]
  })

  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState<Datacenter | null>(null)

  const [form, setForm] = useState<Partial<Datacenter>>({})

  useEffect(() => {
    if (editing) setForm(editing)
    else setForm({ status: 'ACTIVE' })
  }, [editing])

  function openCreate() {
    setEditing(null)
    setOpen(true)
  }

  function openEdit(item: Datacenter) {
    setEditing(item)
    setOpen(true)
  }

  function save() {
    if (!form.name || !form.location) return alert('Name and location are required')

    if (editing) {
      setItems((s) => s.map((it) => (it.id === editing.id ? { ...(it as Datacenter), ...(form as Datacenter) } : it)))
    } else {
      const id = 'dc-' + Math.random().toString(36).slice(2, 9)
      setItems((s) => [...s, { id, name: form.name as string, location: form.location as string, description: form.description || '', status: (form.status as Status) || 'ACTIVE' }])
    }

    setOpen(false)
  }

  function remove(id: string) {
    if (!confirm('Delete datacenter?')) return
    setItems((s) => s.filter((it) => it.id !== id))
  }

  const rows = useMemo(() => items, [items])

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold">Datacenters</h1>
        <div className="flex items-center gap-2">
          <button onClick={openCreate} className="inline-flex items-center gap-2 bg-indigo-600 hover:bg-indigo-700 text-white px-3 py-2 rounded">
            Create Datacenter
          </button>
        </div>
      </div>

      <div className="overflow-auto bg-white dark:bg-gray-800 rounded-lg shadow-sm">
        <table className="min-w-full table-auto">
          <thead>
            <tr className="text-left">
              <th className="px-4 py-3 text-sm text-gray-500">Name</th>
              <th className="px-4 py-3 text-sm text-gray-500">Location</th>
              <th className="px-4 py-3 text-sm text-gray-500">Description</th>
              <th className="px-4 py-3 text-sm text-gray-500">Status</th>
              <th className="px-4 py-3 text-sm text-gray-500">Actions</th>
            </tr>
          </thead>
          <tbody>
            {rows.map((r) => (
              <tr key={r.id} className="border-t border-gray-100 dark:border-gray-700">
                <td className="px-4 py-3">{r.name}</td>
                <td className="px-4 py-3">{r.location}</td>
                <td className="px-4 py-3 text-sm text-gray-600 dark:text-gray-300">{r.description}</td>
                <td className="px-4 py-3">
                  <span className={`px-2 py-1 rounded text-xs font-medium ${r.status === 'ACTIVE' ? 'bg-green-100 text-green-800' : r.status === 'MAINTENANCE' ? 'bg-yellow-100 text-yellow-800' : 'bg-gray-100 text-gray-800'}`}>
                    {r.status}
                  </span>
                </td>
                <td className="px-4 py-3">
                  <div className="flex items-center gap-2">
                    <button onClick={() => openEdit(r)} className="text-indigo-600 hover:underline">Edit</button>
                    <button onClick={() => remove(r.id)} className="text-red-600 hover:underline">Delete</button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Modal open={open} title={editing ? 'Edit Datacenter' : 'Create Datacenter'} onClose={() => setOpen(false)}>
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1">Name</label>
            <input value={form.name || ''} onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))} className="w-full px-3 py-2 border rounded bg-white dark:bg-gray-900" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Location</label>
            <input value={form.location || ''} onChange={(e) => setForm((f) => ({ ...f, location: e.target.value }))} className="w-full px-3 py-2 border rounded bg-white dark:bg-gray-900" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Description</label>
            <textarea value={form.description || ''} onChange={(e) => setForm((f) => ({ ...f, description: e.target.value }))} className="w-full px-3 py-2 border rounded bg-white dark:bg-gray-900" />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">Status</label>
            <select value={form.status || 'ACTIVE'} onChange={(e) => setForm((f) => ({ ...f, status: e.target.value as Status }))} className="w-full px-3 py-2 border rounded bg-white dark:bg-gray-900">
              {statusOptions().map((s) => (
                <option key={s} value={s}>{s}</option>
              ))}
            </select>
          </div>

          <div className="flex items-center justify-end gap-2 pt-4">
            <button onClick={() => setOpen(false)} className="px-3 py-2 rounded border">Cancel</button>
            <button onClick={save} className="px-3 py-2 rounded bg-indigo-600 text-white">Save</button>
          </div>
        </div>
      </Modal>
    </div>
  )
}
