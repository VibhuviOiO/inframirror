import { useMemo, useState } from 'react';

type IntegrationType = 'Database' | 'KeyValueStore' | 'SearchEngine' | 'Cache' | 'OrchestrationFramework' | 'Container' | 'Gateway';
import { DashboardLayout } from '@/components/dashboard/DashboardLayout';
import { Edit2, Trash2, Check, X, ChevronUp, ChevronDown } from 'lucide-react';
import { useIntegrations, useCreateIntegration, useUpdateIntegration, useDeleteIntegration } from '../hooks/useIntegration';
import { toast } from 'sonner';

function Spinner() {
  return <div className="w-5 h-5 border-2 border-gray-200 border-t-blue-600 rounded-full animate-spin" />;
}

function IntegrationPage() {
  const [selectedIds, setSelectedIds] = useState<number[]>([]);
  const [addModalOpen, setAddModalOpen] = useState(false);
  const [addName, setAddName] = useState('');
  const [addType, setAddType] = useState('Database');
  const [addVersion, setAddVersion] = useState('');
  const [addDescription, setAddDescription] = useState('');
  const [addEnabled, setAddEnabled] = useState(true);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editName, setEditName] = useState('');
  const [editType, setEditType] = useState('Database');
  const [editVersion, setEditVersion] = useState('');
  const [editDescription, setEditDescription] = useState('');
  const [editEnabled, setEditEnabled] = useState(true);
  const [search, setSearch] = useState('');

  const { data: integrations, isLoading, isError, error } = useIntegrations();
  const create = useCreateIntegration();
  const update = useUpdateIntegration();
  const del = useDeleteIntegration();

  const filteredRows = useMemo(() => {
    if (!integrations) return [];
    if (!search.trim()) return integrations;
    return integrations.filter(r => r.name.toLowerCase().includes(search.trim().toLowerCase()));
  }, [integrations, search]);

  const [sortBy, setSortBy] = useState<'name' | 'type' | 'version' | null>(null);
  const [sortDir, setSortDir] = useState<'asc' | 'desc'>('asc');

  const sortedRows = useMemo(() => {
    if (!filteredRows) return [];
    if (!sortBy) return filteredRows;
    return [...filteredRows].sort((a, b) => {
      let aVal, bVal;
      if (sortBy === 'name') {
        aVal = a.name?.toLowerCase() || '';
        bVal = b.name?.toLowerCase() || '';
      } else if (sortBy === 'type') {
        aVal = a.integrationType || '';
        bVal = b.integrationType || '';
      } else if (sortBy === 'version') {
        aVal = a.version || '';
        bVal = b.version || '';
      }
      if (aVal < bVal) return sortDir === 'asc' ? -1 : 1;
      if (aVal > bVal) return sortDir === 'asc' ? 1 : -1;
      return 0;
    });
  }, [filteredRows, sortBy, sortDir]);

  const handleSort = (col: 'name' | 'type' | 'version') => {
    if (sortBy === col) {
      setSortDir(sortDir === 'asc' ? 'desc' : 'asc');
    } else {
      setSortBy(col);
      setSortDir('asc');
    }
  };

  const handleAdd = async () => {
    if (!addName.trim()) return toast.error('Name is required');
    if (!addType) return toast.error('Type is required');
    if (!addVersion.trim()) return toast.error('Version is required');
    try {
  await create.mutateAsync({ name: addName, integrationType: addType as IntegrationType, version: addVersion, description: addDescription, enabled: addEnabled });
      setAddName(''); setAddType('Database'); setAddVersion(''); setAddDescription(''); setAddEnabled(true); setAddModalOpen(false);
      toast.success('Integration created');
    } catch (err: any) {
      toast.error(err?.message || 'Error');
    }
  };

  const handleEdit = async (id: number) => {
    if (!editName.trim()) return toast.error('Name is required');
    if (!editType) return toast.error('Type is required');
    if (!editVersion.trim()) return toast.error('Version is required');
    try {
  await update.mutateAsync({ id, data: { name: editName, integrationType: editType as IntegrationType, version: editVersion, description: editDescription, enabled: editEnabled } });
      setEditingId(null); setEditName(''); setEditType('Database'); setEditVersion(''); setEditDescription(''); setEditEnabled(true);
      toast.success('Integration updated');
    } catch (err: any) {
      toast.error(err?.message || 'Error');
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this integration?')) return;
    try {
      await del.mutateAsync(id);
      toast.success('Deleted');
    } catch (err: any) {
      toast.error(err?.message || 'Error');
    }
  };

  const allVisibleIds = useMemo(() => sortedRows.map(r => r.id), [sortedRows]);
  const allSelected = allVisibleIds.length > 0 && allVisibleIds.every(id => selectedIds.includes(id));
  const toggleSelectAll = () => {
    if (allSelected) setSelectedIds(ids => ids.filter(id => !allVisibleIds.includes(id)));
    else setSelectedIds(ids => Array.from(new Set([...ids, ...allVisibleIds])));
  };
  const toggleSelectOne = (id: number) => {
    setSelectedIds(ids => ids.includes(id) ? ids.filter(i => i !== id) : [...ids, id]);
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center gap-3">
        <div className="flex w-full items-center">
          <input
            type="text"
            value={search}
            onChange={e => setSearch(e.target.value)}
            placeholder="Search integrations..."
            className="w-full sm:w-64 px-2 py-1 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-indigo-300 text-sm shadow-sm transition"
            style={{ height: 32, fontSize: '0.925rem' }}
          />
          <div className="flex-1" />
          <button
            onClick={() => setAddModalOpen(true)}
            className="ml-2 inline-flex items-center justify-center bg-gradient-to-r from-blue-600 via-blue-500 to-indigo-500 hover:from-blue-700 hover:via-blue-600 hover:to-indigo-600 text-white rounded shadow-sm transition px-2 py-1 text-sm"
            style={{ height: 32, fontSize: '0.925rem' }}
            title="Add Integration"
          >
            Add Integration
          </button>
        </div>
      </div>
      {/* Add Integration Modal */}
      {addModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
          <div className="bg-white dark:bg-gray-900 rounded-2xl shadow-2xl p-8 w-full max-w-md relative">
            <button onClick={() => setAddModalOpen(false)} className="absolute top-3 right-3 text-gray-400 hover:text-gray-700 dark:hover:text-gray-200 text-2xl">&times;</button>
            <h2 className="text-xl font-bold mb-4 text-blue-900 dark:text-blue-100">Add Integration</h2>
            <div className="mb-4">
              <label className="block text-sm font-medium mb-1">Name</label>
              <input
                value={addName}
                onChange={e => setAddName(e.target.value)}
                className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm"
                placeholder="Integration Name"
              />
            </div>
            <div className="mb-4">
              <label className="block text-sm font-medium mb-1">Type</label>
              <select
                value={addType}
                onChange={e => setAddType(e.target.value)}
                className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm"
              >
                <option value="Database">Database</option>
                <option value="KeyValueStore">KeyValueStore</option>
                <option value="SearchEngine">SearchEngine</option>
                <option value="Cache">Cache</option>
                <option value="OrchestrationFramework">OrchestrationFramework</option>
                <option value="Container">Container</option>
                <option value="Gateway">Gateway</option>
              </select>
            </div>
            <div className="mb-4">
              <label className="block text-sm font-medium mb-1">Version</label>
              <input
                value={addVersion}
                onChange={e => setAddVersion(e.target.value)}
                className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm"
                placeholder="Version"
              />
            </div>
            <div className="mb-4">
              <label className="block text-sm font-medium mb-1">Description</label>
              <input
                value={addDescription}
                onChange={e => setAddDescription(e.target.value)}
                className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm"
                placeholder="Description (optional)"
              />
            </div>
            <div className="mb-4 flex items-center gap-2">
              <input
                type="checkbox"
                checked={addEnabled}
                onChange={e => setAddEnabled(e.target.checked)}
                id="add-enabled"
              />
              <label htmlFor="add-enabled" className="text-sm font-medium">Enabled</label>
            </div>
            <div className="flex items-center justify-end gap-2 pt-4">
              <button onClick={() => setAddModalOpen(false)} className="px-4 py-2 rounded border">Cancel</button>
              <button
                className="px-4 py-2 rounded bg-blue-600 text-white font-semibold disabled:opacity-60"
                disabled={!addName.trim() || !addType || !addVersion.trim()}
                onClick={handleAdd}
              >
                Add
              </button>
            </div>
          </div>
        </div>
      )}
      <div className="relative mt-2">
        <div className="relative overflow-x-auto shadow-md">
          {isLoading ? (
            <div className="p-8 flex items-center justify-center">
              <Spinner />
            </div>
          ) : isError ? (
            <div className="p-8 text-red-600">Error: {(error as Error)?.message}</div>
          ) : (
            <table className="w-full text-sm text-left text-gray-500 dark:text-gray-400">
              <thead>
                <tr className="text-left bg-blue-100 dark:bg-blue-950 animate-fade-in">
                  <th className="px-4 py-4 text-base font-bold text-blue-800 dark:text-blue-100">
                    <input
                      type="checkbox"
                      checked={allSelected}
                      onChange={toggleSelectAll}
                      aria-label="Select all"
                      disabled
                    />
                  </th>
                  <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100 cursor-pointer select-none" onClick={() => handleSort('name')}>
                    <div className="flex items-center">
                      Name
                      <span className="ml-1 font-bold">
                        {sortBy === 'name' ? (
                          sortDir === 'asc' ? (
                            <ChevronUp size={16} className="inline text-blue-800 dark:text-blue-100 font-bold" />
                          ) : (
                            <ChevronDown size={16} className="inline text-blue-800 dark:text-blue-100 font-bold" />
                          )
                        ) : (
                          <ChevronUp size={16} className="inline text-blue-200 font-bold" />
                        )}
                      </span>
                    </div>
                  </th>
                  <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100 cursor-pointer select-none" onClick={() => handleSort('type')}>Type</th>
                  <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100 cursor-pointer select-none" onClick={() => handleSort('version')}>Version</th>
                  <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Description</th>
                  <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Enabled</th>
                  <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100 text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredRows.length === 0 && (
                  <tr>
                    <td colSpan={7} className="px-6 py-8 text-center text-gray-400">No integrations found.</td>
                  </tr>
                )}
                {sortedRows.map((r, idx) => (
                  <tr key={r.id} className="bg-white border-b dark:bg-gray-800 dark:border-gray-700 border-gray-200">
                    <td className="px-4 py-4">
                      <input
                        type="checkbox"
                        checked={selectedIds.includes(r.id)}
                        onChange={() => toggleSelectOne(r.id)}
                        aria-label={`Select row ${r.name}`}
                        disabled
                      />
                    </td>
                    <td className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white">{editingId === r.id ? (
                      <input
                        value={editName}
                        onChange={e => setEditName(e.target.value)}
                        className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm text-base font-medium text-blue-900 dark:text-blue-100"
                        placeholder="Name"
                      />
                    ) : (
                      r.name
                    )}</td>
                    <td className="px-6 py-4">{editingId === r.id ? (
                      <select value={editType} onChange={e => setEditType(e.target.value)} className="w-full px-2 py-1 border rounded">
                        <option value="Database">Database</option>
                        <option value="KeyValueStore">KeyValueStore</option>
                        <option value="SearchEngine">SearchEngine</option>
                        <option value="Cache">Cache</option>
                        <option value="OrchestrationFramework">OrchestrationFramework</option>
                        <option value="Container">Container</option>
                        <option value="Gateway">Gateway</option>
                      </select>
                    ) : (
                      r.integrationType
                    )}</td>
                    <td className="px-6 py-4">{editingId === r.id ? (
                      <input value={editVersion} onChange={e => setEditVersion(e.target.value)} className="w-full px-2 py-1 border rounded" placeholder="Version" />
                    ) : (
                      r.version
                    )}</td>
                    <td className="px-6 py-4">{editingId === r.id ? (
                      <input value={editDescription || ''} onChange={e => setEditDescription(e.target.value)} className="w-full px-2 py-1 border rounded" placeholder="Description" />
                    ) : (
                      r.description || ''
                    )}</td>
                    <td className="px-6 py-4">{editingId === r.id ? (
                      <input type="checkbox" checked={editEnabled} onChange={e => setEditEnabled(e.target.checked)} />
                    ) : (
                      r.enabled ? 'Yes' : 'No'
                    )}</td>
                    <td className="px-6 py-4 text-right">
                      {/* Actions */}
                      {editingId === r.id ? (
                        <div className="flex gap-2 justify-end">
                          <button onClick={() => handleEdit(r.id)} className="bg-green-500 hover:bg-green-600 text-white px-2 py-1 rounded"><Check size={16} /></button>
                          <button onClick={() => setEditingId(null)} className="bg-gray-300 hover:bg-gray-400 text-gray-800 px-2 py-1 rounded"><X size={16} /></button>
                        </div>
                      ) : (
                        <div className="flex gap-2 justify-end">
                          <button
                            onClick={() => {
                              setEditingId(r.id);
                              setEditName(r.name);
                              setEditType(r.integrationType);
                              setEditVersion(r.version);
                              setEditDescription(r.description || '');
                              setEditEnabled(r.enabled);
                            }}
                            className="p-1 text-blue-600 dark:text-blue-400 hover:text-blue-800 dark:hover:text-blue-200 focus:outline-none"
                            title="Edit"
                          >
                            <Edit2 size={18} />
                          </button>
                          <button
                            onClick={() => handleDelete(r.id)}
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
      </div>
    </div>
  );
}

export function IntegrationPageWithLayout() {
  return (
    <DashboardLayout>
      <IntegrationPage />
    </DashboardLayout>
  );
}

export default IntegrationPageWithLayout;
