import { useMemo, useState } from 'react';
import { Plus, Edit2, Trash2, Check, X } from 'lucide-react';
import {
  useApplicationCatalogs,
  useCreateApplicationCatalog,
  useUpdateApplicationCatalog,
  useDeleteApplicationCatalog,
  ApplicationCatalog,
} from '../hooks/useApplicationCatalogs';
import { useServiceOrAppTypes } from '../hooks/useServiceOrAppTypes';
import { useTeams } from '../hooks/useTeams';
import { toast } from 'sonner';

function Spinner() {
  return <div className="w-5 h-5 border-2 border-gray-200 border-t-blue-600 rounded-full animate-spin" />;
}

export default function ApplicationCatalogPage() {
  const { data: items, isLoading, isError, error } = useApplicationCatalogs();
  const { data: types } = useServiceOrAppTypes();
  const { data: teams } = useTeams();
  const create = useCreateApplicationCatalog();
  const update = useUpdateApplicationCatalog();
  const del = useDeleteApplicationCatalog();
  const [addValue, setAddValue] = useState('');
  const [addUniqueId, setAddUniqueId] = useState('');
  const [addType, setAddType] = useState<number | ''>('');
  const [addTeam, setAddTeam] = useState<number | ''>('');
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editValue, setEditValue] = useState('');
  const [editUniqueId, setEditUniqueId] = useState('');
  const [editType, setEditType] = useState<number | ''>('');
  const [editTeam, setEditTeam] = useState<number | ''>('');
  const [search, setSearch] = useState('');

  const filteredRows = useMemo(() => {
    if (!items) return [];
    if (!search.trim()) return items;
    return items.filter(r => r.name.toLowerCase().includes(search.trim().toLowerCase()));
  }, [items, search]);

  // Inline add
  const handleAdd = async () => {
    if (!addValue.trim()) return toast.error('Name is required');
    if (!addUniqueId.trim()) return toast.error('Unique ID is required');
    if (!addType) return toast.error('App Type is required');
    if (!addTeam) return toast.error('Team is required');
    try {
      await create.mutateAsync({ name: addValue, uniqueId: addUniqueId, appTypeId: addType, teamId: addTeam });
      setAddValue('');
      setAddUniqueId('');
      setAddType('');
      setAddTeam('');
      toast.success('Application Catalog created');
    } catch (err: any) {
      toast.error(err?.message || 'Error');
    }
  };

  // Inline edit
  const handleEdit = async (id: number) => {
    if (!editValue.trim()) return toast.error('Name is required');
    if (!editUniqueId.trim()) return toast.error('Unique ID is required');
    if (!editType) return toast.error('App Type is required');
    if (!editTeam) return toast.error('Team is required');
    try {
      await update.mutateAsync({ id, data: { name: editValue, uniqueId: editUniqueId, appTypeId: editType, teamId: editTeam } });
      setEditingId(null);
      setEditValue('');
      setEditUniqueId('');
      setEditType('');
      setEditTeam('');
      toast.success('Application Catalog updated');
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
        <h1 className="text-3xl font-extrabold text-blue-700 tracking-tight flex items-center gap-2">
          <span className="inline-block bg-blue-100 text-blue-700 rounded-full px-3 py-1 text-lg shadow-sm">Application Catalogs</span>
        </h1>
        <div className="flex gap-2 w-full sm:w-auto">
          <input
            type="text"
            value={search}
            onChange={e => setSearch(e.target.value)}
            placeholder="Search applications..."
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
                    <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Name</th>
                    <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Unique ID</th>
                    <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100">App Type</th>
                    <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Team</th>
                    <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredRows.length === 0 && (
                    <tr>
                      <td colSpan={5} className="px-6 py-8 text-center text-gray-400">No applications found.</td>
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
                        {editingId === r.id ? (
                          <input
                            value={editUniqueId}
                            onChange={e => setEditUniqueId(e.target.value)}
                            className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm"
                          />
                        ) : (
                          <span className="text-blue-700 dark:text-blue-200 font-semibold">{r.uniqueId}</span>
                        )}
                      </td>
                      <td className="px-6 py-4 bg-transparent">
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
                            {types?.find(t => t.id === r.appTypeId)?.name || r.appTypeId}
                          </span>
                        )}
                      </td>
                      <td className="px-6 py-4 bg-transparent">
                        {editingId === r.id ? (
                          <select
                            value={editTeam}
                            onChange={e => setEditTeam(Number(e.target.value))}
                            className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm"
                          >
                            <option value="">Select team...</option>
                            {teams?.map(t => (
                              <option key={t.id} value={t.id}>{t.name}</option>
                            ))}
                          </select>
                        ) : (
                          <span className="text-blue-700 dark:text-blue-200 font-semibold">
                            {teams?.find(t => t.id === r.teamId)?.name || r.teamId}
                          </span>
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
                                onClick={() => { setEditingId(null); setEditValue(''); setEditUniqueId(''); setEditType(''); setEditTeam(''); }}
                                className="inline-flex items-center justify-center bg-gray-300 hover:bg-gray-400 text-gray-700 rounded-full p-2 shadow transition"
                                title="Cancel"
                              >
                                <X size={18} />
                              </button>
                            </>
                          ) : (
                            <>
                              <button
                                onClick={() => { setEditingId(r.id); setEditValue(r.name); setEditUniqueId(r.uniqueId); setEditType(r.appTypeId); setEditTeam(r.teamId); }}
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
                        placeholder="Add new app..."
                        onKeyDown={e => { if (e.key === 'Enter') handleAdd(); }}
                      />
                    </td>
                    <td className="px-6 py-4 bg-transparent">
                      <input
                        value={addUniqueId}
                        onChange={e => setAddUniqueId(e.target.value)}
                        className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm"
                        placeholder="Unique ID..."
                      />
                    </td>
                    <td className="px-6 py-4 bg-transparent">
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
                    <td className="px-6 py-4 bg-transparent">
                      <select
                        value={addTeam}
                        onChange={e => setAddTeam(Number(e.target.value))}
                        className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm"
                      >
                        <option value="">Select team...</option>
                        {teams?.map(t => (
                          <option key={t.id} value={t.id}>{t.name}</option>
                        ))}
                      </select>
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
