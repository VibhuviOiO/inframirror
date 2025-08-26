import { useMemo, useState } from 'react';
import { DashboardLayout } from '@/components/dashboard/DashboardLayout';
import { Edit2, Trash2, Check, X, ChevronUp, ChevronDown, MoreVertical } from 'lucide-react';
import { useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useIntegrationInstances, useCreateIntegrationInstance, useUpdateIntegrationInstance, useDeleteIntegrationInstance } from '../hooks/useIntegrationInstances';
import { useHosts } from '../hooks/useHosts';
import { useDatacenters } from '../hooks/useDatacenters';
import { useRegions } from '../hooks/useRegions';
import { useIntegrations } from '../hooks/useIntegration';
import { toast } from 'sonner';

function Spinner() {
  return <div className="w-5 h-5 border-2 border-gray-200 border-t-blue-600 rounded-full animate-spin" />;
}

function ServicesPage() {
  const navigate = useNavigate();
  const [selectedIds, setSelectedIds] = useState<number[]>([]);
  const [addModalOpen, setAddModalOpen] = useState(false);
  const [addHostId, setAddHostId] = useState<number | ''>('');
  const [addIntegrationId, setAddIntegrationId] = useState<number | ''>('');
  const [addEnabled, setAddEnabled] = useState(true);
  const [addPort, setAddPort] = useState<number | ''>('');
  const [addConfig, setAddConfig] = useState('');
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editHostId, setEditHostId] = useState<number | ''>('');
  const [editIntegrationId, setEditIntegrationId] = useState<number | ''>('');
  const [editEnabled, setEditEnabled] = useState(true);
  const [editPort, setEditPort] = useState<number | ''>('');
  const [editConfig, setEditConfig] = useState('');
  const [search, setSearch] = useState('');

  const { data: instances, isLoading, isError, error } = useIntegrationInstances();
  const { data: hosts } = useHosts();
  const { data: datacenters } = useDatacenters();
  const { data: regions } = useRegions();
  const { data: integrations } = useIntegrations();
  const create = useCreateIntegrationInstance();
  const update = useUpdateIntegrationInstance();
  const del = useDeleteIntegrationInstance();

  // Join all data for display
  const joinedRows = useMemo(() => {
    if (!instances || !hosts || !datacenters || !regions || !integrations) return [];
    return instances.map((inst) => {
      const host = hosts.find(h => h.id === inst.hostId);
      const datacenter = host ? datacenters.find(d => d.id === host.datacenterId) : undefined;
      const region = datacenter ? regions.find(r => r.id === datacenter.regionId) : undefined;
      const integration = integrations.find(i => i.id === inst.integrationId);
      return {
        ...inst,
        hostName: host?.hostname || inst.hostId,
        datacenterName: datacenter?.name || host?.datacenterId || '',
        regionName: region?.name || datacenter?.regionId || '',
        integrationName: integration?.name || inst.integrationId,
      };
    });
  }, [instances, hosts, datacenters, regions, integrations]);

  const filteredRows = useMemo(() => {
    if (!joinedRows) return [];
    if (!search.trim()) return joinedRows;
    return joinedRows.filter(r =>
      String(r.hostName).toLowerCase().includes(search.trim().toLowerCase()) ||
      String(r.integrationName).toLowerCase().includes(search.trim().toLowerCase()) ||
      String(r.datacenterName).toLowerCase().includes(search.trim().toLowerCase()) ||
      String(r.regionName).toLowerCase().includes(search.trim().toLowerCase())
    );
  }, [joinedRows, search]);

  const [sortBy, setSortBy] = useState<'hostName' | 'integrationName' | 'datacenterName' | 'regionName' | null>(null);
  const [sortDir, setSortDir] = useState<'asc' | 'desc'>('asc');

  const sortedRows = useMemo(() => {
    if (!filteredRows) return [];
    if (!sortBy) return filteredRows;
    return [...filteredRows].sort((a, b) => {
      let aVal, bVal;
      if (sortBy === 'hostName') {
        aVal = String(a.hostName).toLowerCase() || '';
        bVal = String(b.hostName).toLowerCase() || '';
      } else if (sortBy === 'integrationName') {
        aVal = String(a.integrationName).toLowerCase() || '';
        bVal = String(b.integrationName).toLowerCase() || '';
      } else if (sortBy === 'datacenterName') {
        aVal = String(a.datacenterName).toLowerCase() || '';
        bVal = String(b.datacenterName).toLowerCase() || '';
      } else if (sortBy === 'regionName') {
        aVal = String(a.regionName).toLowerCase() || '';
        bVal = String(b.regionName).toLowerCase() || '';
      }
      if (aVal < bVal) return sortDir === 'asc' ? -1 : 1;
      if (aVal > bVal) return sortDir === 'asc' ? 1 : -1;
      return 0;
    });
  }, [filteredRows, sortBy, sortDir]);

  const handleSort = (col: 'hostName' | 'integrationName' | 'datacenterName' | 'regionName' ) => {
    if (sortBy === col) {
      setSortDir(sortDir === 'asc' ? 'desc' : 'asc');
    } else {
      setSortBy(col);
      setSortDir('asc');
    }
  };

  const handleAdd = async () => {
    if (!addHostId) return toast.error('Host ID is required');
    if (!addIntegrationId) return toast.error('Integration ID is required');
    try {
      await create.mutateAsync({ hostId: Number(addHostId), integrationId: Number(addIntegrationId), port: addPort ? Number(addPort) : undefined, config: addConfig ? JSON.parse(addConfig) : undefined });
      setAddHostId(''); setAddIntegrationId(''); setAddEnabled(true); setAddPort(''); setAddConfig(''); setAddModalOpen(false);
      toast.success('Integration instance created');
    } catch (err: any) {
      toast.error(err?.message || 'Error');
    }
  };

  const handleEdit = async (id: number) => {
    if (!editHostId) return toast.error('Host ID is required');
    if (!editIntegrationId) return toast.error('Integration ID is required');
    try {
      await update.mutateAsync({ id, data: { hostId: Number(editHostId), integrationId: Number(editIntegrationId), port: editPort ? Number(editPort) : undefined, config: editConfig ? JSON.parse(editConfig) : undefined } });
      setEditingId(null); setEditHostId(''); setEditIntegrationId(''); setEditEnabled(true); setEditPort(''); setEditConfig('');
      toast.success('Integration instance updated');
    } catch (err: any) {
      toast.error(err?.message || 'Error');
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this integration instance?')) return;
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

  // Dropdown options
  const hostOptions = hosts || [];
  const integrationOptions = integrations || [];

  // For menu open state per row
  const [menuOpenId, setMenuOpenId] = useState<number | null>(null);
  const menuRefs = useRef<{ [key: number]: HTMLDivElement | null }>({});

  // Close menu on outside click
  useEffect(() => {
    function handleClick(e: MouseEvent) {
      if (menuOpenId !== null && menuRefs.current[menuOpenId]) {
        if (!(menuRefs.current[menuOpenId]?.contains(e.target as Node))) {
          setMenuOpenId(null);
        }
      }
    }
    if (menuOpenId !== null) {
      document.addEventListener('mousedown', handleClick);
    }
    return () => document.removeEventListener('mousedown', handleClick);
  }, [menuOpenId]);

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center gap-3">
        <div className="flex w-full items-center">
          <input
            type="text"
            value={search}
            onChange={e => setSearch(e.target.value)}
            placeholder="Search services..."
            className="w-full sm:w-64 px-2 py-1 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-indigo-300 text-sm shadow-sm transition"
            style={{ height: 32, fontSize: '0.925rem' }}
          />
          <div className="flex-1" />
          <button
            onClick={async () => {
              if (!selectedIds.length) return;
              if (!window.confirm(`Delete ${selectedIds.length} selected integration instances?`)) return;
              try {
                for (const id of selectedIds) {
                  await del.mutateAsync(id);
                }
                setSelectedIds([]);
                toast.success('Selected integration instances deleted');
              } catch (err: any) {
                toast.error(err?.message || 'Bulk delete failed');
              }
            }}
            className="ml-2 inline-flex items-center justify-center bg-red-600 hover:bg-red-700 text-white rounded shadow-sm transition px-2 py-1 text-sm disabled:opacity-50"
            style={{ height: 32, fontSize: '0.925rem' }}
            title="Delete Selected"
            disabled={!selectedIds.length}
          >
            <Trash2 size={16} className="mr-1" />
            Delete
          </button>
          <button
            onClick={() => setAddModalOpen(true)}
            className="ml-2 inline-flex items-center justify-center bg-gradient-to-r from-blue-600 via-blue-500 to-indigo-500 hover:from-blue-700 hover:via-blue-600 hover:to-indigo-600 text-white rounded shadow-sm transition px-2 py-1 text-sm"
            style={{ height: 32, fontSize: '0.925rem' }}
            title="Add Integration Instance"
          >
            Add Integration Instance
          </button>
        </div>
      </div>
      {/* Add Modal */}
      {addModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
          <div className="bg-white dark:bg-gray-900 rounded-2xl shadow-2xl p-8 w-full max-w-md relative">
            <button onClick={() => setAddModalOpen(false)} className="absolute top-3 right-3 text-gray-400 hover:text-gray-700 dark:hover:text-gray-200 text-2xl">&times;</button>
            <h2 className="text-xl font-bold mb-4 text-blue-900 dark:text-blue-100">Add Integration Instance</h2>
            <div className="mb-4">
              <label className="block text-sm font-medium mb-1">Host</label>
              <select
                value={addHostId}
                onChange={e => setAddHostId(Number(e.target.value))}
                className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm"
              >
                <option value="">Select Host</option>
                {hostOptions.map(h => (
                  <option key={h.id} value={h.id}>{h.hostname}</option>
                ))}
              </select>
            </div>
            <div className="mb-4">
              <label className="block text-sm font-medium mb-1">Integration</label>
              <select
                value={addIntegrationId}
                onChange={e => setAddIntegrationId(Number(e.target.value))}
                className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm"
              >
                <option value="">Select Integration</option>
                {integrationOptions.map(i => (
                  <option key={i.id} value={i.id}>{i.name}</option>
                ))}
              </select>
            </div>
            <div className="mb-4">
              <label className="block text-sm font-medium mb-1">Port</label>
              <input
                value={addPort}
                onChange={e => setAddPort(Number(e.target.value))}
                className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm"
                placeholder="Port (optional)"
                type="number"
              />
            </div>
            <div className="mb-4">
              <label className="block text-sm font-medium mb-1">Config (JSON)</label>
              <input
                value={addConfig}
                onChange={e => setAddConfig(e.target.value)}
                className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm"
                placeholder='{"user":"admin"}'
              />
            </div>
            <div className="flex items-center justify-end gap-2 pt-4">
              <button onClick={() => setAddModalOpen(false)} className="px-4 py-2 rounded border">Cancel</button>
              <button
                className="px-4 py-2 rounded bg-blue-600 text-white font-semibold disabled:opacity-60"
                disabled={!addHostId || !addIntegrationId}
                onClick={handleAdd}
              >
                Add
              </button>
            </div>
          </div>
        </div>
      )}
      {/* Edit Modal (inline in table) */}
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
                  <th className="px-4 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Host</th>
                  <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Datacenter</th>
                  <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Region</th>
                  <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Integration</th>
                  <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Port</th>
                  <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Config</th>
                  <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100 text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                {sortedRows.length === 0 && (
                  <tr>
                    <td colSpan={8} className="px-6 py-8 text-center text-gray-400">No services found.</td>
                  </tr>
                )}
                {sortedRows.map((r) => (
                  <tr key={r.id} className="bg-white border-b dark:bg-gray-800 dark:border-gray-700 border-gray-200">
                    <td className="px-4 py-4">{editingId === r.id ? (
                      <select value={editHostId} onChange={e => setEditHostId(Number(e.target.value))} className="w-full px-2 py-1 border rounded">
                        <option value="">Select Host</option>
                        {hostOptions.map(h => (
                          <option key={h.id} value={h.id}>{h.hostname}</option>
                        ))}
                      </select>
                    ) : r.hostName}</td>
                    <td className="px-6 py-4">{r.datacenterName}</td>
                    <td className="px-6 py-4">{r.regionName}</td>
                    <td className="px-6 py-4">{editingId === r.id ? (
                      <select value={editIntegrationId} onChange={e => setEditIntegrationId(Number(e.target.value))} className="w-full px-2 py-1 border rounded">
                        <option value="">Select Integration</option>
                        {integrationOptions.map(i => (
                          <option key={i.id} value={i.id}>{i.name}</option>
                        ))}
                      </select>
                    ) : r.integrationName}</td>
                    <td className="px-6 py-4">{editingId === r.id ? (
                      <input value={editPort} onChange={e => setEditPort(Number(e.target.value))} className="w-full px-2 py-1 border rounded" type="number" />
                    ) : (
                      r.port ?? ''
                    )}</td>
                    <td className="px-6 py-4">{editingId === r.id ? (
                      <input value={editConfig} onChange={e => setEditConfig(e.target.value)} className="w-full px-2 py-1 border rounded" placeholder='{"user":"admin"}' />
                    ) : (
                      r.config ? JSON.stringify(r.config) : ''
                    )}</td>
                    <td className="px-6 py-4 text-right">
                      {/* Actions */}
                      {editingId === r.id ? (
                        <div className="flex gap-2 justify-end">
                          <button onClick={() => handleEdit(r.id)} className="bg-green-500 hover:bg-green-600 text-white px-2 py-1 rounded"><Check size={16} /></button>
                          <button onClick={() => setEditingId(null)} className="bg-gray-300 hover:bg-gray-400 text-gray-800 px-2 py-1 rounded"><X size={16} /></button>
                        </div>
                      ) : (
                        <div className="relative flex justify-end">
                          <button
                            onClick={() => setMenuOpenId(menuOpenId === r.id ? null : r.id)}
                            className="p-1 text-gray-600 hover:text-blue-700 focus:outline-none"
                            title="More options"
                          >
                            <MoreVertical size={20} />
                          </button>
                          {menuOpenId === r.id && (
                            <div
                              ref={el => (menuRefs.current[r.id] = el)}
                              className="absolute right-0 mt-2 w-40 bg-white border border-gray-200 rounded shadow-lg z-50 animate-fade-in max-h-60 overflow-y-auto"
                              style={{ minWidth: 160 }}
                            >
                              <button
                                onClick={() => {
                                  setEditingId(r.id);
                                  setEditHostId(r.hostId);
                                  setEditIntegrationId(r.integrationId);
                                  setEditPort(r.port ?? '');
                                  setEditConfig(r.config ? JSON.stringify(r.config) : '');
                                  setMenuOpenId(null);
                                }}
                                className="w-full text-left px-4 py-2 hover:bg-blue-50 text-blue-700"
                              >
                                Edit
                              </button>
                              <button
                                onClick={() => { handleDelete(r.id); setMenuOpenId(null); }}
                                className="w-full text-left px-4 py-2 hover:bg-red-50 text-red-600"
                              >
                                Delete
                              </button>
                              {/* Dynamic Ops option */}
                              {(() => {
                                let ops = null;
                                let configObj: any = {};
                                try { configObj = typeof r.config === 'string' ? JSON.parse(r.config) : r.config || {}; } catch {}
                                const integrations = configObj?.integrations || [];
                                if (integrations.includes('Docker')) {
                                  ops = (
                                    <button
                                      onClick={() => {
                                        // Find host IP
                                        const host = hosts?.find(h => h.id === r.hostId);
                                        const ip = host?.privateIP || host?.publicIP || '';
                                        navigate(`/docker-operations?ip=${encodeURIComponent(ip)}&port=${r.port ?? ''}`);
                                        setMenuOpenId(null);
                                      }}
                                      className="w-full text-left px-4 py-2 hover:bg-blue-50 text-blue-900"
                                    >
                                      Docker Ops
                                    </button>
                                  );
                                } else if (integrations.includes('Postgres')) {
                                  ops = (
                                    <button
                                      onClick={() => {
                                        const host = hosts?.find(h => h.id === r.hostId);
                                        const ip = host?.privateIP || host?.publicIP || '';
                                        navigate(`/postgres-ops?ip=${encodeURIComponent(ip)}&port=${r.port ?? ''}`);
                                        setMenuOpenId(null);
                                      }}
                                      className="w-full text-left px-4 py-2 hover:bg-blue-50 text-blue-900"
                                    >
                                      Postgres Ops
                                    </button>
                                  );
                                }
                                return ops;
                              })()}
                            </div>
                          )}
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

export function ServicesPageWithLayout() {
  return (
    <DashboardLayout>
      <ServicesPage />
    </DashboardLayout>
  );
}

export default ServicesPageWithLayout;
