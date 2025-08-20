import { useMemo, useState } from 'react';
import { DashboardLayout } from '@/components/dashboard/DashboardLayout';
import { Plus, Edit2, Trash2, Check, X, ChevronUp, ChevronDown } from 'lucide-react';
import {
  useServiceCatalogs,
  useCreateServiceCatalog,
  useUpdateServiceCatalog,
  useDeleteServiceCatalog,
} from '../hooks/useServiceCatalogs';
import { useServiceOrAppTypes } from '../hooks/useServiceOrAppTypes';
import { toast } from 'sonner';

function Spinner() {
  return <div className="w-5 h-5 border-2 border-gray-200 border-t-blue-600 rounded-full animate-spin" />;
}

function Badge({ children, color }: { children: React.ReactNode; color?: string }) {
  return (
    <span className={`inline-block px-3 py-1 rounded-full text-xs font-semibold ${color || 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-100'}`}>{children}</span>
  );
}

function ServiceCatalogPage() {
  const [addModalOpen, setAddModalOpen] = useState(false);
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

  const [sortBy, setSortBy] = useState<'name' | 'type' | 'port' | null>(null);
  const [sortDir, setSortDir] = useState<'asc' | 'desc'>('asc');

  // Sorting logic
  // (keep only this instance)
  const sortedRows = useMemo(() => {
    if (!filteredRows) return [];
    if (!sortBy) return filteredRows;
    return [...filteredRows].sort((a, b) => {
      let aVal, bVal;
      if (sortBy === 'name') {
        aVal = a.name?.toLowerCase() || '';
        bVal = b.name?.toLowerCase() || '';
      } else if (sortBy === 'type') {
        aVal = types?.find(t => t.id === a.serviceTypeId)?.name?.toLowerCase() || '';
        bVal = types?.find(t => t.id === b.serviceTypeId)?.name?.toLowerCase() || '';
      } else if (sortBy === 'port') {
        aVal = a.defaultPort ?? 0;
        bVal = b.defaultPort ?? 0;
      }
      if (aVal < bVal) return sortDir === 'asc' ? -1 : 1;
      if (aVal > bVal) return sortDir === 'asc' ? 1 : -1;
      return 0;
    });
  }, [filteredRows, sortBy, sortDir, types]);

  const handleSort = (col) => {
    if (sortBy === col) {
      setSortDir(sortDir === 'asc' ? 'desc' : 'asc');
    } else {
      setSortBy(col);
      setSortDir('asc');
    }
  };

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
      setAddModalOpen(false);
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
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
        <h1 className="text-xl font-semibold text-gray-800 dark:text-gray-100 tracking-tight">Service Catalogs</h1>
        <div className="flex gap-2 w-full sm:w-auto items-center justify-end">
          <input
            type="text"
            value={search}
            onChange={e => setSearch(e.target.value)}
            placeholder="Search catalogs..."
            className="w-full sm:w-64 px-3 py-1.5 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-indigo-300 text-sm shadow-sm transition"
          />
          <button
            onClick={() => setAddModalOpen(true)}
            className="inline-flex items-center justify-center bg-gradient-to-r from-indigo-500 to-blue-500 hover:from-indigo-600 hover:to-blue-600 text-white rounded-full shadow-sm transition w-9 h-9"
            title="Add Service Catalog"
          >
            <Plus size={18} />
          </button>
        </div>
      </div>
      <div className="relative mt-2">
        <div className="overflow-x-auto">
            {isLoading ? (
              <div className="p-8 flex items-center justify-center">
                <Spinner />
              </div>
            ) : isError ? (
              <div className="p-8 text-red-600">Error: {(error as Error)?.message}</div>
            ) : (
              <table className="min-w-full table-auto">
                <thead className="sticky top-0 z-10">
                  <tr className="text-left bg-blue-50 dark:bg-gray-900 border-b border-gray-200 dark:border-gray-700 animate-fade-in">
                    <th className="px-3 py-4 text-xs font-bold tracking-wide text-gray-700 dark:text-gray-200 cursor-pointer select-none" onClick={() => handleSort('name')}>Name
                      <span className="inline-flex flex-col justify-center items-center ml-1 min-h-[20px]">
                        <ChevronUp size={14} className={sortBy==='name' ? (sortDir==='asc' ? 'text-blue-600' : 'text-gray-700 dark:text-gray-200') : 'text-gray-700 dark:text-gray-200'} style={{marginBottom: '-2px'}} />
                        <ChevronDown size={14} className={sortBy==='name' ? (sortDir==='desc' ? 'text-blue-600' : 'text-gray-700 dark:text-gray-200') : 'text-gray-700 dark:text-gray-200'} style={{marginTop: '-2px'}} />
                      </span>
                    </th>
                    <th className="px-3 py-4 text-xs font-bold tracking-wide text-gray-700 dark:text-gray-200 cursor-pointer select-none" onClick={() => handleSort('type')}>Type
                      <span className="inline-flex flex-col justify-center items-center ml-1 min-h-[20px]">
                        <ChevronUp size={14} className={sortBy==='type' ? (sortDir==='asc' ? 'text-blue-600' : 'text-gray-700 dark:text-gray-200') : 'text-gray-700 dark:text-gray-200'} style={{marginBottom: '-2px'}} />
                        <ChevronDown size={14} className={sortBy==='type' ? (sortDir==='desc' ? 'text-blue-600' : 'text-gray-700 dark:text-gray-200') : 'text-gray-700 dark:text-gray-200'} style={{marginTop: '-2px'}} />
                      </span>
                    </th>
                    <th className="px-3 py-4 text-xs font-bold tracking-wide text-gray-700 dark:text-gray-200 cursor-pointer select-none" onClick={() => handleSort('port')}>Port
                      <span className="inline-flex flex-col justify-center items-center ml-1 min-h-[20px]">
                        <ChevronUp size={14} className={sortBy==='port' ? (sortDir==='asc' ? 'text-blue-600' : 'text-gray-700 dark:text-gray-200') : 'text-gray-700 dark:text-gray-200'} style={{marginBottom: '-2px'}} />
                        <ChevronDown size={14} className={sortBy==='port' ? (sortDir==='desc' ? 'text-blue-600' : 'text-gray-700 dark:text-gray-200') : 'text-gray-700 dark:text-gray-200'} style={{marginTop: '-2px'}} />
                      </span>
                    </th>
                    <th className="px-3 py-4 text-xs font-bold tracking-wide text-gray-700 dark:text-gray-200">Description</th>
                    <th className="px-3 py-4 text-xs font-bold tracking-wide text-gray-700 dark:text-gray-200">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredRows.length === 0 && (
                    <tr>
                      <td colSpan={5} className="px-6 py-8 text-center text-gray-400">No catalogs found.</td>
                    </tr>
                  )}
                  {sortedRows.map((r, idx) => (
                    <tr
                      key={r.id}
                      className={`transition group ${idx % 2 === 0 ? 'bg-white dark:bg-gray-900' : 'bg-blue-50 dark:bg-gray-800'} hover:bg-blue-100 dark:hover:bg-gray-700 ${idx !== filteredRows.length - 1 ? 'border-b border-gray-200 dark:border-gray-800' : ''}`}
                    >
                      <td className="px-3 py-3 bg-transparent align-middle text-sm text-gray-800 dark:text-gray-100">
                        {editingId === r.id ? (
                          <input
                            value={editValue}
                            onChange={e => setEditValue(e.target.value)}
                            className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm text-base font-medium text-blue-900 dark:text-blue-100"
                            onKeyDown={e => { if (e.key === 'Enter') handleEdit(r.id); }}
                            autoFocus
                          />
                        ) : (
                          <span className="text-xs font-medium">{r.name}</span>
                        )}
                      </td>
                      <td className="px-3 py-3 bg-transparent align-middle text-sm text-gray-800 dark:text-gray-100">
                        {editingId === r.id ? (
                          <select
                            value={editType}
                            onChange={e => setEditType(Number(e.target.value))}
                            className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm text-base font-medium text-blue-900 dark:text-blue-100"
                          >
                            <option value="">Select type...</option>
                            {types?.map(t => (
                              <option key={t.id} value={t.id}>{t.name}</option>
                            ))}
                          </select>
                        ) : (
                          <span className="text-xs font-medium">{types?.find(t => t.id === r.serviceTypeId)?.name || r.serviceTypeId}</span>
                        )}
                      </td>
                      <td className="px-3 py-3 bg-transparent align-middle text-sm text-gray-800 dark:text-gray-100">
                        {editingId === r.id ? (
                          <input
                            type="number"
                            value={editPort}
                            onChange={e => {
                              const val = e.target.value;
                              setEditPort(val === '' ? '' : Number(val));
                            }}
                            className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm text-base font-medium text-blue-900 dark:text-blue-100"
                            placeholder="Port"
                          />
                        ) : (
                          <span className="text-xs font-medium">{r.defaultPort ?? ''}</span>
                        )}
                      </td>
                      <td className="px-3 py-3 bg-transparent align-middle text-sm text-gray-800 dark:text-gray-100">
                        {editingId === r.id ? (
                          <input
                            value={editDesc}
                            onChange={e => setEditDesc(e.target.value)}
                            className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm text-base font-medium text-blue-900 dark:text-blue-100"
                            placeholder="Description"
                          />
                        ) : (
                          <span className="text-xs font-medium">{r.description ?? ''}</span>
                        )}
                      </td>
                      <td className="px-4 py-4 bg-transparent">
                        <div className="flex items-center gap-2">
                          {editingId === r.id ? (
                            <>
                              <button
                                onClick={() => handleEdit(r.id)}
                                className="inline-flex items-center justify-center text-green-600 hover:text-green-700 p-1 transition"
                                title="Save"
                              >
                                <Check size={14} />
                              </button>
                              <button
                                onClick={() => { setEditingId(null); setEditValue(''); setEditType(''); setEditPort(''); setEditDesc(''); }}
                                className="inline-flex items-center justify-center text-gray-400 hover:text-gray-700 p-1 transition"
                                title="Cancel"
                              >
                                <X size={14} />
                              </button>
                            </>
                          ) : (
                            <>
                              <button
                                onClick={() => { setEditingId(r.id); setEditValue(r.name); setEditType(r.serviceTypeId); setEditPort(r.defaultPort ?? ''); setEditDesc(r.description ?? ''); }}
                                className="inline-flex items-center justify-center text-blue-600 hover:text-blue-800 p-1 transition"
                                title="Edit"
                              >
                                <Edit2 size={14} />
                              </button>
                              <button
                                onClick={() => handleDelete(r.id)}
                                className="inline-flex items-center justify-center text-red-500 hover:text-red-700 p-1 transition"
                                title="Delete"
                              >
                                <Trash2 size={14} />
                              </button>
                            </>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                  {/* Add Modal */}
                  {addModalOpen && (
                    <tr>
                      <td colSpan={5} className="p-0">
                        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
                          <div className="bg-white dark:bg-gray-900 rounded-2xl shadow-2xl p-8 w-full max-w-2xl relative">
                            <button onClick={() => setAddModalOpen(false)} className="absolute top-3 right-3 text-gray-400 hover:text-gray-700 dark:hover:text-gray-200 text-2xl">&times;</button>
                            <h2 className="text-2xl font-bold mb-6 text-blue-900 dark:text-blue-100">Add Service Catalog</h2>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                              <div>
                                <label className="block text-sm font-medium mb-1">Name</label>
                                <input value={addValue} onChange={e => setAddValue(e.target.value)} className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm" />
                              </div>
                              <div>
                                <label className="block text-sm font-medium mb-1">Service Type</label>
                                <select value={addType} onChange={e => setAddType(Number(e.target.value))} className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm" >
                                  <option value="">Select type...</option>
                                  {types?.map(t => (
                                    <option key={t.id} value={t.id}>{t.name}</option>
                                  ))}
                                </select>
                              </div>
                              <div>
                                <label className="block text-sm font-medium mb-1">Default Port</label>
                                <input type="number" value={addPort} onChange={e => setAddPort(e.target.value === '' ? '' : Number(e.target.value))} className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm" />
                              </div>
                              <div>
                                <label className="block text-sm font-medium mb-1">Description</label>
                                <input value={addDesc} onChange={e => setAddDesc(e.target.value)} className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm" />
                              </div>
                            </div>
                            <div className="flex items-center justify-end gap-2 pt-8">
                              <button onClick={() => setAddModalOpen(false)} className="px-4 py-2 rounded border">Cancel</button>
                              <button onClick={handleAdd} className="px-4 py-2 rounded bg-blue-600 text-white font-semibold">Add</button>
                            </div>
                          </div>
                        </div>
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            )}
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


