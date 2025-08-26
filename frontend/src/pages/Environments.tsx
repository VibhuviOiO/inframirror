
import { Plus, Edit2, Trash2, Check, X } from 'lucide-react';
import { useMemo, useState } from 'react';
import {
  useEnvironments,
  useCreateEnvironment,
  useUpdateEnvironment,
  useDeleteEnvironment,
  Environment,
} from '../hooks/useEnvironments';
import { toast } from 'sonner';

function Spinner() {
  return <div className="w-5 h-5 border-2 border-gray-200 border-t-indigo-600 rounded-full animate-spin" />;
}

export default function EnvironmentsPage() {
  const { data: items, isLoading, isError, error } = useEnvironments();
  const create = useCreateEnvironment();
  const update = useUpdateEnvironment();
  const del = useDeleteEnvironment();
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
      toast.success('Environment created');
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
      toast.success('Environment updated');
    } catch (err: any) {
      toast.error(err?.message || 'Error');
    }
  };

  // Inline delete with confirmation
  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this environment?')) return;
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
        <div className="w-full">
          <input
            type="text"
            value={search}
            onChange={e => setSearch(e.target.value)}
            placeholder="Search environments..."
            className="w-full px-4 py-2 rounded-full border-2 border-blue-200 focus:ring-2 focus:ring-blue-300 bg-white dark:bg-gray-900 shadow focus:shadow-lg transition"
          />
        </div>
      </div>
      <div className="relative mt-4">
        <div className="flex flex-col gap-4">
          {/* Environment rows */}
          {filteredRows.length === 0 && (
            <div className="text-center text-gray-400 py-8">No environments found.</div>
          )}
          {filteredRows.map((r) => (
            <div key={r.id} className="rounded-xl bg-white shadow p-4 flex items-center gap-4 border border-blue-100 animate-fade-in">
              {editingId === r.id ? (
                <input
                  value={editValue}
                  onChange={e => setEditValue(e.target.value)}
                  className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white focus:ring-2 focus:ring-blue-300 shadow-sm"
                  onKeyDown={e => { if (e.key === 'Enter') handleEdit(r.id); }}
                  autoFocus
                />
              ) : (
                <span className="text-base font-medium text-blue-900">{r.name}</span>
              )}
              <div className="flex items-center gap-2 ml-auto">
                {editingId === r.id ? (
                  <>
                    <button
                      onClick={() => handleEdit(r.id)}
                      className="px-2 py-1 text-green-600 hover:text-green-800 transition"
                      title="Save"
                    >
                      <Check size={18} />
                    </button>
                    <button
                      onClick={() => { setEditingId(null); setEditValue(''); }}
                      className="px-2 py-1 text-gray-500 hover:text-gray-700 transition"
                      title="Cancel"
                    >
                      <X size={18} />
                    </button>
                  </>
                ) : (
                  <>
                    <button
                      onClick={() => { setEditingId(r.id); setEditValue(r.name); }}
                      className="px-2 py-1 text-blue-600 hover:text-blue-800 transition"
                      title="Edit"
                    >
                      <Edit2 size={18} />
                    </button>
                    <button
                      onClick={() => handleDelete(r.id)}
                      className="px-2 py-1 text-red-600 hover:text-red-800 transition"
                      title="Delete"
                    >
                      <Trash2 size={18} />
                    </button>
                  </>
                )}
              </div>
            </div>
          ))}
          {/* Add new environment row at the bottom */}
          <div className="rounded-xl bg-white shadow p-4 flex items-center gap-4 border border-blue-100">
            <span className="inline-flex items-center justify-center h-10 w-10 rounded-full bg-blue-100 text-blue-500 font-bold text-xl shadow-sm">
              <Plus size={24} />
            </span>
            <input
              value={addValue}
              onChange={e => setAddValue(e.target.value)}
              className="w-48 px-3 py-2 border-2 border-blue-200 rounded-lg bg-white focus:ring-2 focus:ring-blue-300 shadow-sm"
              placeholder="Add new environment..."
              onKeyDown={e => { if (e.key === 'Enter') handleAdd(); }}
            />
            <button
              onClick={handleAdd}
              className="inline-flex items-center justify-center bg-blue-600 hover:bg-blue-800 text-white rounded-full px-5 py-2 font-bold shadow-md transition"
              title="Add"
              style={{ minWidth: 40 }}
            >
              <Plus size={20} />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
