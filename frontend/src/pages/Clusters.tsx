import { useMemo, useState, useRef, useEffect } from 'react';
import { DashboardLayout } from '@/components/dashboard/DashboardLayout';
import { Edit2, Trash2, Check, X, ChevronUp, ChevronDown } from 'lucide-react';
import { useClusters, useCreateCluster, useUpdateCluster, useDeleteCluster, useBulkDeleteClusters } from '../hooks/useClusters';
import { useDatacenters } from '../hooks/useDatacenters';
import { useEnvironments } from '../hooks/useEnvironments';
import { toast } from 'sonner';

function Spinner() {
  return <div className="w-5 h-5 border-2 border-gray-200 border-t-blue-600 rounded-full animate-spin" />;
}

function ClustersPage() {
  const [selectedIds, setSelectedIds] = useState<number[]>([]);
  const [addModalOpen, setAddModalOpen] = useState(false);
  const [addName, setAddName] = useState('');
  const [addCatalog, setAddCatalog] = useState<number | ''>('');
  const [addDatacenter, setAddDatacenter] = useState<number | ''>('');
  const [addEnvironment, setAddEnvironment] = useState<number | ''>('');
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editName, setEditName] = useState('');
  const [editCatalog, setEditCatalog] = useState<number | ''>('');
  const [editDatacenter, setEditDatacenter] = useState<number | ''>('');
  const [editEnvironment, setEditEnvironment] = useState<number | ''>('');
  const [search, setSearch] = useState('');

  const { data: clusters, isLoading, isError, error, refetch } = useClusters();
  const { data: datacenters } = useDatacenters();
  const { data: environments } = useEnvironments();
  const create = useCreateCluster();
  const update = useUpdateCluster();
  const del = useDeleteCluster();
  const bulkDelete = useBulkDeleteClusters();

  const filteredRows = useMemo(() => {
    if (!clusters) return [];
    if (!search.trim()) return clusters;
    return clusters.filter(r => r.name.toLowerCase().includes(search.trim().toLowerCase()));
  }, [clusters, search]);

  const [sortBy, setSortBy] = useState<'name' | 'datacenter' | 'environment' | null>(null);
  const [sortDir, setSortDir] = useState<'asc' | 'desc'>('asc');

  const sortedRows = useMemo(() => {
    if (!filteredRows) return [];
    if (!sortBy) return filteredRows;
    return [...filteredRows].sort((a, b) => {
      let aVal, bVal;
      if (sortBy === 'name') {
        aVal = a.name?.toLowerCase() || '';
        bVal = b.name?.toLowerCase() || '';
      } else if (sortBy === 'datacenter') {
        aVal = datacenters?.find(d => d.id === a.datacenterId)?.name?.toLowerCase() || '';
        bVal = datacenters?.find(d => d.id === b.datacenterId)?.name?.toLowerCase() || '';
      } else if (sortBy === 'environment') {
        aVal = environments?.find(e => e.id === a.environmentId)?.name?.toLowerCase() || '';
        bVal = environments?.find(e => e.id === b.environmentId)?.name?.toLowerCase() || '';
      }
      if (aVal < bVal) return sortDir === 'asc' ? -1 : 1;
      if (aVal > bVal) return sortDir === 'asc' ? 1 : -1;
      return 0;
    });
  }, [filteredRows, sortBy, sortDir, datacenters, environments]);

  const handleSort = (col: 'name' | 'datacenter' | 'environment') => {
    if (sortBy === col) {
      setSortDir(sortDir === 'asc' ? 'desc' : 'asc');
    } else {
      setSortBy(col);
      setSortDir('asc');
    }
  };

  const handleAdd = async () => {
    if (!addName.trim()) return toast.error('Name is required');
    if (!addCatalog) return toast.error('Catalog is required');
    if (!addDatacenter) return toast.error('Datacenter is required');
    if (!addEnvironment) return toast.error('Environment is required');
    try {
      await create.mutateAsync({ name: addName, datacenterId: addDatacenter, environmentId: addEnvironment });
      setAddName(''); setAddDatacenter(''); setAddEnvironment(''); setAddModalOpen(false);
      toast.success('Cluster created');
    } catch (err: any) {
      toast.error(err?.message || 'Error');
    }
  };

  const handleEdit = async (id: number) => {
    if (!editName.trim()) return toast.error('Name is required');
    if (!editCatalog) return toast.error('Catalog is required');
    if (!editDatacenter) return toast.error('Datacenter is required');
    if (!editEnvironment) return toast.error('Environment is required');
    try {
      await update.mutateAsync({ id, data: { name: editName, datacenterId: editDatacenter, environmentId: editEnvironment } });
      setEditingId(null); setEditName(''); setEditDatacenter(''); setEditEnvironment('');
      toast.success('Cluster updated');
    } catch (err: any) {
      toast.error(err?.message || 'Error');
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this cluster?')) return;
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
            placeholder="Search clusters..."
            className="w-full sm:w-64 px-2 py-1 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-indigo-300 text-sm shadow-sm transition"
            style={{ height: 32, fontSize: '0.925rem' }}
          />
          <div className="flex-1" />
          <button
            onClick={async () => {
              if (!selectedIds.length) return;
              if (!window.confirm(`Delete ${selectedIds.length} selected clusters?`)) return;
              try {
                await bulkDelete.mutateAsync(selectedIds);
                setSelectedIds([]);
                toast.success('Selected clusters deleted');
              } catch (err: any) {
                toast.error(err?.message || 'Bulk delete failed');
              }
            }}
            className="ml-2 inline-flex items-center justify-center bg-red-600 hover:bg-red-700 text-white rounded shadow-sm transition px-2 py-1 text-sm disabled:opacity-50"
            style={{ height: 32, fontSize: '0.925rem' }}
            title="Delete Selected"
            disabled={!selectedIds.length || bulkDelete.isPending}
          >
            <Trash2 size={16} className="mr-1" />
            Delete
          </button>
          <button
            onClick={() => setAddModalOpen(true)}
            className="ml-2 inline-flex items-center justify-center bg-gradient-to-r from-blue-600 via-blue-500 to-indigo-500 hover:from-blue-700 hover:via-blue-600 hover:to-indigo-600 text-white rounded shadow-sm transition px-2 py-1 text-sm"
            style={{ height: 32, fontSize: '0.925rem' }}
            title="Add Cluster"
          >
            Add Cluster
          </button>
        </div>
      </div>
      {/* Add Cluster Modal */}
      {addModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
          <div className="bg-white dark:bg-gray-900 rounded-2xl shadow-2xl p-8 w-full max-w-md relative">
            <button onClick={() => setAddModalOpen(false)} className="absolute top-3 right-3 text-gray-400 hover:text-gray-700 dark:hover:text-gray-200 text-2xl">&times;</button>
            <h2 className="text-xl font-bold mb-4 text-blue-900 dark:text-blue-100">Add Cluster</h2>
            <div className="mb-4">
              <label className="block text-sm font-medium mb-1">Name</label>
              <input
                value={addName}
                onChange={e => setAddName(e.target.value)}
                className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm"
                placeholder="Cluster Name"
              />
            </div>
            <div className="mb-4">
              <label className="block text-sm font-medium mb-1">Datacenter</label>
              <select
                value={addDatacenter}
                onChange={e => setAddDatacenter(Number(e.target.value))}
                className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm"
              >
                <option value="">Select Datacenter</option>
                {datacenters?.map(d => (
                  <option key={d.id} value={d.id}>{d.name}</option>
                ))}
              </select>
            </div>
            <div className="mb-4">
              <label className="block text-sm font-medium mb-1">Environment</label>
              <select
                value={addEnvironment}
                onChange={e => setAddEnvironment(Number(e.target.value))}
                className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm"
              >
                <option value="">Select Environment</option>
                {environments?.map(e => (
                  <option key={e.id} value={e.id}>{e.name}</option>
                ))}
              </select>
            </div>
            <div className="flex items-center justify-end gap-2 pt-4">
              <button onClick={() => setAddModalOpen(false)} className="px-4 py-2 rounded border">Cancel</button>
              <button
                className="px-4 py-2 rounded bg-blue-600 text-white font-semibold disabled:opacity-60"
                disabled={!addName.trim() || !addCatalog || !addDatacenter || !addEnvironment}
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
                  <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100 cursor-pointer select-none" onClick={() => handleSort('datacenter')}>Datacenter</th>
                  <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100 cursor-pointer select-none" onClick={() => handleSort('environment')}>Environment</th>
                  <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100 text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredRows.length === 0 && (
                  <tr>
                    <td colSpan={6} className="px-6 py-8 text-center text-gray-400">No clusters found.</td>
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
                      <select value={editDatacenter} onChange={e => setEditDatacenter(Number(e.target.value))} className="w-full px-2 py-1 border rounded">
                        <option value="">Select Datacenter</option>
                        {datacenters?.map(d => (
                          <option key={d.id} value={d.id}>{d.name}</option>
                        ))}
                      </select>
                    ) : (
                      datacenters?.find(d => d.id === r.datacenterId)?.name || ''
                    )}</td>
                    <td className="px-6 py-4">{editingId === r.id ? (
                      <select value={editEnvironment} onChange={e => setEditEnvironment(Number(e.target.value))} className="w-full px-2 py-1 border rounded">
                        <option value="">Select Environment</option>
                        {environments?.map(e => (
                          <option key={e.id} value={e.id}>{e.name}</option>
                        ))}
                      </select>
                    ) : (
                      environments?.find(e => e.id === r.environmentId)?.name || ''
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
                              setEditDatacenter(r.datacenterId);
                              setEditEnvironment(r.environmentId);
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

export function ClustersPageWithLayout() {
  return (
    <DashboardLayout>
      <ClustersPage />
    </DashboardLayout>
  );
}

export default ClustersPageWithLayout;
