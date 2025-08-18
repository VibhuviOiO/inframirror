
import { Plus, Edit2, Trash2, Check, X } from 'lucide-react';
import { useMemo, useState } from 'react';
import {
  useTeams,
  useCreateTeam,
  useUpdateTeam,
  useDeleteTeam,
  Team,
} from '../hooks/useTeams';
import { toast } from 'sonner';

function Spinner() {
  return <div className="w-5 h-5 border-2 border-gray-200 border-t-blue-600 rounded-full animate-spin" />;
}

export default function TeamsPage() {
  const { data: items, isLoading, isError, error } = useTeams();
  const create = useCreateTeam();
  const update = useUpdateTeam();
  const del = useDeleteTeam();
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
      toast.success('Team created');
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
      toast.success('Team updated');
    } catch (err: any) {
      toast.error(err?.message || 'Error');
    }
  };

  // Inline delete with confirmation
  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this team?')) return;
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
        <h1 className="text-3xl font-extrabold text-blue-700 tracking-tight flex items-center gap-2">
          <span className="inline-block bg-blue-100 text-blue-700 rounded-full px-3 py-1 text-lg shadow-sm">Teams</span>
        </h1>
        <div className="flex gap-2 w-full sm:w-auto">
          <input
            type="text"
            value={search}
            onChange={e => setSearch(e.target.value)}
            placeholder="Search teams..."
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
                    <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Team</th>
                    <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredRows.length === 0 && (
                    <tr>
                      <td colSpan={2} className="px-6 py-8 text-center text-gray-400">No teams found.</td>
                    </tr>
                  )}
                  {filteredRows.map((r) => (
                    <tr key={r.id} className="transition group hover:bg-blue-50 dark:hover:bg-blue-950">
                      <td className="px-6 py-4 bg-transparent">
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
                      <td className="px-6 py-4 bg-transparent">
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
                                onClick={() => { setEditingId(null); setEditValue(''); }}
                                className="inline-flex items-center justify-center bg-gray-300 hover:bg-gray-400 text-gray-700 rounded-full p-2 shadow transition"
                                title="Cancel"
                              >
                                <X size={18} />
                              </button>
                            </>
                          ) : (
                            <>
                              <button
                                onClick={() => { setEditingId(r.id); setEditValue(r.name); }}
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
                    <td className="px-6 py-4 bg-transparent">
                      <input
                        value={addValue}
                        onChange={e => setAddValue(e.target.value)}
                        className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm"
                        placeholder="Add new team..."
                        onKeyDown={e => { if (e.key === 'Enter') handleAdd(); }}
                      />
                    </td>
                    <td className="px-6 py-4 bg-transparent">
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
