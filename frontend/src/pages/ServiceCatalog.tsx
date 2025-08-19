
import { useMemo, useState } from 'react';
import { DashboardLayout } from '@/components/dashboard/DashboardLayout';
import { Plus, Edit2, Trash2, Check, X } from 'lucide-react';
import {
  useServiceCatalogs,
  useCreateServiceCatalog,
  useUpdateServiceCatalog,
  useDeleteServiceCatalog,
  ServiceCatalog,
} from '../hooks/useServiceCatalogs';
import { useServiceOrAppTypes } from '../hooks/useServiceOrAppTypes';
import { toast } from 'sonner';

function Spinner() {
  return <div className="w-5 h-5 border-2 border-gray-200 border-t-blue-600 rounded-full animate-spin" />;
}

function ServiceCatalogPage() {
  const { data: items, isLoading, isError, error } = useServiceCatalogs();
  const { data: types } = useServiceOrAppTypes();
  const create = useCreateServiceCatalog();
  const update = useUpdateServiceCatalog();
  const del = useDeleteServiceCatalog();
  const [addValue, setAddValue] = useState('');
  const [addType, setAddType] = useState<number | ''>('');
  const [addPort, setAddPort] = useState<number | ''>('');
  const [addDesc, setAddDesc] = useState('');
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editValue, setEditValue] = useState('');
  const [editType, setEditType] = useState<number | ''>('');
  const [editPort, setEditPort] = useState<number | ''>('');
  const [editDesc, setEditDesc] = useState('');
  const [search, setSearch] = useState('');

  const filteredRows = useMemo(() => {
    if (!items) return [];
    if (!search.trim()) return items;
    return items.filter(r => r.name.toLowerCase().includes(search.trim().toLowerCase()));
  }, [items, search]);

  // Inline add
  const handleAdd = async () => {
    if (!addValue.trim()) return toast.error('Name is required');
    if (!addType) return toast.error('Service Type is required');
    let port = addPort === '' ? undefined : Number(addPort);
    try {
      await create.mutateAsync({ name: addValue, serviceTypeId: addType, defaultPort: port, description: addDesc || undefined });
      setAddValue('');
      setAddType('');
      setAddPort('');
      setAddDesc('');
      toast.success('Service Catalog created');
    } catch (err: any) {
      toast.error(err?.message || 'Error');
    }
  };

  // Inline edit
  const handleEdit = async (id: number) => {
    if (!editValue.trim()) return toast.error('Name is required');
    if (!editType) return toast.error('Service Type is required');
    let port = editPort === '' ? undefined : Number(editPort);
    try {
      await update.mutateAsync({ id, data: { name: editValue, serviceTypeId: editType, defaultPort: port, description: editDesc || undefined } });
      setEditingId(null);
      setEditValue('');
      setEditType('');
      setEditPort('');
      setEditDesc('');
      toast.success('Service Catalog updated');
    } catch (err: any) {
      toast.error(err?.message || 'Error');
    }
  };

  // Inline delete with confirmation
  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this catalog?')) return;
    try {
      await del.mutateAsync(id);
      toast.success('Deleted');
    } catch (err: any) {
      toast.error(err?.message || 'Error');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <h1 className="text-3xl font-extrabold text-blue-700 tracking-tight">Service Catalogs</h1>
        <div className="flex gap-2 w-full sm:w-auto">
          <input
            type="text"
            value={search}
            onChange={e => setSearch(e.target.value)}
            placeholder="Search catalogs..."
            className="w-full sm:w-72 px-4 py-2 rounded-full border-2 border-blue-200 focus:ring-2 focus:ring-blue-300 bg-white dark:bg-gray-900 shadow focus:shadow-lg transition"
          />
        </div>
      </div>
      <div className="relative mt-4">
        <div className="overflow-x-auto">
          <div className="rounded-2xl shadow-xl bg-white/80 dark:bg-gray-900/80 backdrop-blur-md">
            {isLoading ? (
              <div className="p-8 flex items-center justify-center">
                <Spinner />
              </div>
            ) : isError ? (
              <div className="p-8 text-red-600">Error: {(error as Error)?.message}</div>
            ) : (
              <table className="min-w-full table-auto">
                <thead>
                  <tr className="text-left bg-blue-100 dark:bg-blue-950 animate-fade-in">
                    <th className="px-4 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Name</th>
                    <th className="px-4 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Service Type</th>
                    <th className="px-4 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Default Port</th>
                    <th className="px-4 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Description</th>
                    <th className="px-4 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredRows.length === 0 && (
                    <tr>
                      <td colSpan={5} className="px-6 py-8 text-center text-gray-400">No catalogs found.</td>
                    </tr>
                  )}
                  {filteredRows.map((r) => (
                    <tr key={r.id} className="transition group hover:bg-blue-50 dark:hover:bg-blue-950">
                      <td className="px-4 py-4 bg-transparent">
                        {editingId === r.id ? (
                          <input
                            value={editValue}
                            onChange={e => setEditValue(e.target.value)}
                            className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm"
                            onKeyDown={e => { if (e.key === 'Enter') handleEdit(r.id); }}
                            autoFocus
                          />
                        ) : (
                          <span className="text-lg font-medium text-blue-900 dark:text-blue-100">{r.name}</span>
                        )}
                      </td>
                      <td className="px-4 py-4 bg-transparent">
                        {editingId === r.id ? (
                          <select
                            value={editType}
                            onChange={e => setEditType(Number(e.target.value))}
                            className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm"
                          >
                            <option value="">Select type...</option>
                            {types?.map(t => (
                              <option key={t.id} value={t.id}>{t.name}</option>
                            ))}
                          </select>
                        ) : (
                          <span className="text-blue-700 dark:text-blue-200 font-semibold">
                            {types?.find(t => t.id === r.serviceTypeId)?.name || r.serviceTypeId}
                          </span>
                        )}
                      </td>
                      <td className="px-4 py-4 bg-transparent">
                        {editingId === r.id ? (
                          <input
                            type="number"
                            value={editPort}
                            onChange={e => {
                              const val = e.target.value;
                              setEditPort(val === '' ? '' : Number(val));
                            }}
                            className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm"
                            placeholder="Port"
                          />
                        ) : (
                          <span className="text-blue-900 dark:text-blue-100">{r.defaultPort ?? ''}</span>
                        )}
                      </td>
                      <td className="px-4 py-4 bg-transparent">
                        {editingId === r.id ? (
                          <input
                            value={editDesc}
                            onChange={e => setEditDesc(e.target.value)}
                            className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm"
                            placeholder="Description"
                          />
                        ) : (
                          <span className="text-blue-900 dark:text-blue-100">{r.description ?? ''}</span>
                        )}
                      </td>
                      <td className="px-4 py-4 bg-transparent">
                        <div className="flex items-center gap-2">
                          {editingId === r.id ? (
                            <>
                              <button
                                onClick={() => handleEdit(r.id)}
                                className="inline-flex items-center justify-center bg-green-500 hover:bg-green-600 text-white rounded-full p-2 shadow transition"
                                title="Save"
                              >
                                <Check size={18} />
                              </button>
                              <button
                                onClick={() => { setEditingId(null); setEditValue(''); setEditType(''); setEditPort(''); setEditDesc(''); }}
                                className="inline-flex items-center justify-center bg-gray-300 hover:bg-gray-400 text-gray-700 rounded-full p-2 shadow transition"
                                title="Cancel"
                              >
                                <X size={18} />
                              </button>
                            </>
                          ) : (
                            <>
                              <button
                                onClick={() => { setEditingId(r.id); setEditValue(r.name); setEditType(r.serviceTypeId); setEditPort(r.defaultPort ?? ''); setEditDesc(r.description ?? ''); }}
                                className="inline-flex items-center justify-center bg-blue-600 hover:bg-blue-800 text-white rounded-full p-2 shadow transition"
                                title="Edit"
                              >
                                <Edit2 size={18} />
                              </button>
                              <button
                                onClick={() => handleDelete(r.id)}
                                className="inline-flex items-center justify-center bg-red-500 hover:bg-red-700 text-white rounded-full p-2 shadow transition"
                                title="Delete"
                              >
                                <Trash2 size={18} />
                              </button>
                            </>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                  {/* Add row at the bottom */}
                  <tr className="bg-blue-50 dark:bg-blue-950 animate-fade-in">
                    <td className="px-4 py-4 bg-transparent">
                      <input
                        value={addValue}
                        onChange={e => setAddValue(e.target.value)}
                        className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm"
                        placeholder="Add new catalog..."
                        onKeyDown={e => { if (e.key === 'Enter') handleAdd(); }}
                      />
                    </td>
                    <td className="px-4 py-4 bg-transparent">
                      <select
                        value={addType}
                        onChange={e => setAddType(Number(e.target.value))}
                        className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm"
                      >
                        <option value="">Select type...</option>
                        {types?.map(t => (
                          <option key={t.id} value={t.id}>{t.name}</option>
                        ))}
                      </select>
                    </td>
                    <td className="px-4 py-4 bg-transparent">
                      <input
                        type="number"
                        value={addPort}
                        onChange={e => {
                          const val = e.target.value;
                          setAddPort(val === '' ? '' : Number(val));
                        }}
                        className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm"
                        placeholder="Port"
                      />
                    </td>
                    <td className="px-4 py-4 bg-transparent">
                      <input
                        value={addDesc}
                        onChange={e => setAddDesc(e.target.value)}
                        className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm"
                        placeholder="Description"
                      />
                    </td>
                    <td className="px-4 py-4 bg-transparent">
                      <button
                        onClick={handleAdd}
                        className="inline-flex items-center justify-center bg-blue-600 hover:bg-blue-800 text-white rounded-full px-5 py-2 font-bold shadow-md transition"
                        title="Add"
                        style={{ minWidth: 40 }}
                      >
                        <Plus size={20} />
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default function ServiceCatalogPageWithLayout() {
  return (
    <DashboardLayout>
      <ServiceCatalogPage />
    </DashboardLayout>
  );
}
