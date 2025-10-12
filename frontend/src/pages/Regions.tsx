import { useMemo, useState } from 'react';
import { Plus, Edit2, Trash2, Check, X } from 'lucide-react';
import {
  useRegions,
  useCreateRegion,
  useUpdateRegion,
  useDeleteRegion,
  Region,
} from '../hooks/useRegions';
import { toast } from 'sonner';

function Spinner() {
  return <div className="w-5 h-5 border-2 border-gray-200 border-t-blue-600 rounded-full animate-spin" />;
}

export default function RegionsPage() {
  const { data: items, isLoading, isError, error } = useRegions();
  const create = useCreateRegion();
  const update = useUpdateRegion();
  const del = useDeleteRegion();
  const [addValue, setAddValue] = useState('');
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editValue, setEditValue] = useState('');
  const [search, setSearch] = useState('');

  const filteredRows = useMemo(() => {
    if (!items) return [];
    if (!search.trim()) return items;
    return items.filter(r => r.name.toLowerCase().includes(search.trim().toLowerCase()));
  }, [items, search]);

  // Inline add
  const handleAdd = async () => {
    if (!addValue.trim()) return toast.error('Name is required');
    try {
      await create.mutateAsync({ name: addValue });
      setAddValue('');
      toast.success('Region created');
    } catch (err: any) {
      toast.error(err?.message || 'Error');
    }
  };

  // Inline edit
  const handleEdit = async (id: number) => {
    if (!editValue.trim()) return toast.error('Name is required');
    try {
      await update.mutateAsync({ id, data: { name: editValue } });
      setEditingId(null);
      setEditValue('');
      toast.success('Region updated');
    } catch (err: any) {
      toast.error(err?.message || 'Error');
    }
  };

  // Inline delete with confirmation
  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this region?')) return;
    try {
      await del.mutateAsync(id);
      toast.success('Deleted');
    } catch (err: any) {
      toast.error(err?.message || 'Error');
    }
  };

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="border-b border-gray-200 dark:border-gray-800 pb-6">
        <h1 className="text-2xl font-semibold text-gray-900 dark:text-white">Regions</h1>
        <p className="mt-2 text-sm text-gray-600 dark:text-gray-400">
          Manage geographic regions for infrastructure deployment
        </p>
      </div>

      {/* Search Bar */}
      <div className="flex flex-col gap-4">
        <input
          type="text"
          value={search}
          onChange={e => setSearch(e.target.value)}
          placeholder="Search regions..."
          className="w-full max-w-md px-4 py-2.5 rounded-lg border border-gray-300 dark:border-gray-600 focus:ring-2 focus:ring-indigo-500 focus:border-transparent bg-white dark:bg-gray-800 text-gray-900 dark:text-white placeholder-gray-500 dark:placeholder-gray-400 transition-all duration-200"
        />
      </div>
      <div className="relative mt-4">
        {isLoading ? (
          <div className="p-8 flex items-center justify-center">
            <Spinner />
          </div>
        ) : isError ? (
          <div className="p-8 text-red-600">Error: {(error as Error)?.message}</div>
        ) : (
          <div className="flex flex-col gap-4">
            {/* Region rows */}
            {filteredRows.length === 0 && (
              <div className="text-center text-gray-400 py-8">No regions found.</div>
            )}
            {filteredRows.map((r) => (
              <div key={r.id} className="group rounded-lg bg-white dark:bg-gray-800 shadow-sm hover:shadow-md border border-gray-200 dark:border-gray-700 p-4 flex items-center gap-4 transition-all duration-200">
                {editingId === r.id ? (
                  <input
                    value={editValue}
                    onChange={e => setEditValue(e.target.value)}
                    className="flex-1 px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all duration-200"
                    onKeyDown={e => { if (e.key === 'Enter') handleEdit(r.id); }}
                    autoFocus
                  />
                ) : (
                  <div className="flex-1">
                    <span className="text-sm font-medium text-gray-900 dark:text-white">{r.name}</span>
                  </div>
                )}
                <div className="flex items-center gap-1">
                  {editingId === r.id ? (
                    <>
                      <button
                        onClick={() => handleEdit(r.id)}
                        className="p-2 text-emerald-600 hover:text-emerald-700 hover:bg-emerald-50 dark:hover:bg-emerald-900/20 rounded-lg transition-all duration-200"
                        title="Save"
                      >
                        <Check className="h-4 w-4" />
                      </button>
                      <button
                        onClick={() => { setEditingId(null); setEditValue(''); }}
                        className="p-2 text-gray-500 hover:text-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700 rounded-lg transition-all duration-200"
                        title="Cancel"
                      >
                        <X className="h-4 w-4" />
                      </button>
                    </>
                  ) : (
                    <>
                      <button
                        onClick={() => { setEditingId(r.id); setEditValue(r.name); }}
                        className="p-2 text-gray-400 hover:text-indigo-600 hover:bg-indigo-50 dark:hover:bg-indigo-900/20 rounded-lg transition-all duration-200 opacity-0 group-hover:opacity-100"
                        title="Edit"
                      >
                        <Edit2 className="h-4 w-4" />
                      </button>
                      <button
                        onClick={() => handleDelete(r.id)}
                        className="p-2 text-gray-400 hover:text-red-600 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-lg transition-all duration-200 opacity-0 group-hover:opacity-100"
                        title="Delete"
                      >
                        <Trash2 className="h-4 w-4" />
                      </button>
                    </>
                  )}
                </div>
              </div>
            ))}
            {/* Add new region row at the bottom */}
            <div className="rounded-lg bg-gray-50 dark:bg-gray-800/50 border-2 border-dashed border-gray-300 dark:border-gray-600 p-4 flex items-center gap-4 hover:border-indigo-400 hover:bg-indigo-50/50 dark:hover:bg-indigo-900/10 transition-all duration-200">
              <div className="flex items-center justify-center h-10 w-10 rounded-lg bg-indigo-100 dark:bg-indigo-900/30 text-indigo-600 dark:text-indigo-400">
                <Plus className="h-5 w-5" />
              </div>
              <input
                value={addValue}
                onChange={e => setAddValue(e.target.value)}
                className="flex-1 px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-white placeholder-gray-500 dark:placeholder-gray-400 focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all duration-200"
                placeholder="Enter region name..."
                onKeyDown={e => { if (e.key === 'Enter') handleAdd(); }}
              />
              <button
                onClick={handleAdd}
                disabled={!addValue.trim()}
                className="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 disabled:bg-gray-300 disabled:cursor-not-allowed text-white rounded-lg font-medium transition-all duration-200 flex items-center gap-2"
                title="Add Region"
              >
                <Plus className="h-4 w-4" />
                Add
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
