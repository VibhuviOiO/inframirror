import { useMemo, useState, useRef, useEffect } from 'react';
import { DashboardLayout } from '@/components/dashboard/DashboardLayout';
import { Edit2, Trash2, Check, X, ChevronUp, ChevronDown, Upload, Download } from 'lucide-react';
import {
  useServiceCatalogs,
  useCreateServiceCatalog,
  useUpdateServiceCatalog,
  useDeleteServiceCatalog,
  useBulkDeleteServiceCatalogs,
} from '../hooks/useServiceCatalogs';
import { useServiceOrAppTypes } from '../hooks/useServiceOrAppTypes';
import { toast } from 'sonner';

function Spinner() {
  return <div className="w-5 h-5 border-2 border-gray-200 border-t-blue-600 rounded-full animate-spin" />;
}


const API_BASE = import.meta.env.VITE_API_BASE_URL?.replace(/\/$/, '') || 'http://localhost:8080';
async function fetchJSON(url, options) {
  const fullUrl = url.startsWith('http') ? url : `${API_BASE}${url.startsWith('/') ? '' : '/'}${url}`;
  const res = await fetch(fullUrl, options);
  let text = await res.text();
  let json;
  try { json = JSON.parse(text); } catch { json = undefined; }
  if (!res.ok) {
    const errMsg = json?.message || text || res.statusText;
    throw new Error(`HTTP ${res.status} ${res.statusText}: ${errMsg}`);
  }
  return json;
}

function toInitCap(str) {
  return str
    .trim()
    .split(' ')
    .filter(Boolean)
    .map(w => w.charAt(0).toUpperCase() + w.slice(1).toLowerCase())
    .join(' ');
}

function ServiceCatalogPage() {
  // Bulk selection state
  const [selectedIds, setSelectedIds] = useState<number[]>([]);
  const [importStatus, setImportStatus] = useState(null); // { total, success, failed, errors: [] }
  const [showImportStatus, setShowImportStatus] = useState(false);
  useEffect(() => {
    if (importStatus) {
      setShowImportStatus(true);
      const timer = setTimeout(() => setShowImportStatus(false), 3000);
      return () => clearTimeout(timer);
    }
  }, [importStatus]);
  const [importFile, setImportFile] = useState<File|null>(null);
  const [importing, setImporting] = useState(false);
  const [addModalOpen, setAddModalOpen] = useState(false);
  const [importModalOpen, setImportModalOpen] = useState(false);
  const [importError, setImportError] = useState('');
  const fileInputRef = useRef<HTMLInputElement>(null);
  const { data: items, isLoading, isError, error, refetch } = useServiceCatalogs();
  const { data: types } = useServiceOrAppTypes();
  const create = useCreateServiceCatalog();
  const update = useUpdateServiceCatalog();
  const del = useDeleteServiceCatalog();
  const bulkDelete = useBulkDeleteServiceCatalogs();
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

  // Bulk selection helpers
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
            placeholder="Search catalogs..."
            className="w-full sm:w-64 px-2 py-1 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-indigo-300 text-sm shadow-sm transition"
            style={{ height: 32, fontSize: '0.925rem' }}
          />
          <div className="flex-1" />
          <button
            onClick={() => setImportModalOpen(true)}
            className="ml-2 inline-flex items-center justify-center bg-blue-600 hover:bg-blue-700 text-white rounded shadow-sm transition px-2 py-1 text-sm"
            style={{ height: 32, fontSize: '0.925rem' }}
            title="Import Service Catalog"
          >
            <Download size={16} className="mr-1" />
            Import
          </button>
          <button
            onClick={() => {
              // Export logic stub
              toast.info('Export not implemented yet');
            }}
            className="ml-2 inline-flex items-center justify-center bg-gray-600 hover:bg-gray-700 text-white rounded shadow-sm transition px-2 py-1 text-sm"
            style={{ height: 32, fontSize: '0.925rem' }}
            title="Export Service Catalog"
          >
            <Upload size={16} className="mr-1" />
            Export
          </button>
          <button
            onClick={async () => {
              if (!selectedIds.length) return;
              if (!window.confirm(`Delete ${selectedIds.length} selected catalogs?`)) return;
              try {
                await bulkDelete.mutateAsync(selectedIds);
                setSelectedIds([]);
                toast.success('Selected catalogs deleted');
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
            title="Add Service Catalog"
          >
            Add Service Catalog
          </button>
          
        </div>
      </div>
      {/* Import Modal */}
      {importModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
          <div className="bg-white dark:bg-gray-900 rounded-2xl shadow-2xl p-8 w-full max-w-lg relative">
            <button onClick={() => setImportModalOpen(false)} className="absolute top-3 right-3 text-gray-400 hover:text-gray-700 dark:hover:text-gray-200 text-2xl">&times;</button>
            <h2 className="text-xl font-bold mb-4 text-blue-900 dark:text-blue-100">Import Service Catalogs</h2>
            <div className="mb-4">
              <label className="block text-sm font-medium mb-1">CSV Format</label>
              <div className="bg-gray-100 dark:bg-gray-800 rounded p-2 text-xs font-mono">
                name,type,defaultPort,description
                <br />Example:<br />MyService,Web,8080,Some description
              </div>
            </div>
            <input
              ref={fileInputRef}
              type="file"
              accept=".csv"
              className="mb-2"
              onChange={e => {
                setImportError('');
                setImportStatus(null);
                setImportFile(e.target.files?.[0] || null);
              }}
            />
            {importError && <div className="text-red-600 text-sm mt-2">{importError}</div>}
            <div className="flex items-center justify-end gap-2 pt-4">
              <button onClick={() => setImportModalOpen(false)} className="px-4 py-2 rounded border">Close</button>
              <button
                className="px-4 py-2 rounded bg-blue-600 text-white font-semibold disabled:opacity-60"
                disabled={!importFile || importing}
                onClick={async () => {
                  if (!importFile) return;
                  setImportError('');
                  setImportStatus(null);
                  setImporting(true);
                  try {
                    const text = await importFile.text();
                    const rows = text.split(/\r?\n/).filter(Boolean).map(line => line.split(','));
                    if (!rows[0] || rows[0][0].toLowerCase() !== 'name' || rows[0][1]?.toLowerCase() !== 'type') {
                      setImportError('CSV must start with: name,type,defaultPort,description');
                      setImporting(false);
                      return;
                    }
                    const existingNames = new Set((items || []).map(i => i.name.toLowerCase()));
                    const typeMap = new Map((types || []).map(t => [t.name.toLowerCase(), t.id]));
                    let success = 0, failed = 0, errors = [];
                    for (let i = 1; i < rows.length; ++i) {
                      let [name, type, defaultPort, description] = rows[i];
                      name = toInitCap(name || '');
                      type = toInitCap(type || '');
                      if (!name) {
                        errors.push(`Row ${i+1}: Name is required.`);
                        failed++;
                        continue;
                      }
                      if (!type) {
                        errors.push(`Row ${i+1}: Type is required.`);
                        failed++;
                        continue;
                      }
                      if (existingNames.has(name.toLowerCase())) {
                        errors.push(`Row ${i+1}: Duplicate name '${name}'.`);
                        failed++;
                        continue;
                      }
                      let serviceTypeId = typeMap.get(type.toLowerCase());
                      if (!serviceTypeId) {
                        try {
                          const newType = await fetchJSON('/service-or-app-types', {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify({ name: type })
                          });
                          serviceTypeId = newType.id;
                          typeMap.set(type.toLowerCase(), serviceTypeId);
                        } catch (err) {
                          errors.push(`Row ${i+1}: Failed to create type '${type}': ${err.message}`);
                          failed++;
                          continue;
                        }
                      }
                      try {
                        await fetchJSON('/service-catalogs', {
                          method: 'POST',
                          headers: { 'Content-Type': 'application/json' },
                          body: JSON.stringify({
                            name,
                            serviceTypeId,
                            defaultPort: defaultPort ? Number(defaultPort) : undefined,
                            description: description || undefined
                          })
                        });
                        existingNames.add(name.toLowerCase());
                        success++;
                      } catch (err) {
                        console.error('Import row error:', {
                          row: i+1,
                          name,
                          serviceTypeId,
                          defaultPort,
                          description,
                          error: err
                        });
                        errors.push(`Row ${i+1}: ${err.message}`);
                        failed++;
                      }
                      setImportStatus({ total: rows.length - 1, success, failed, errors });
                    }
                    setImportStatus({ total: rows.length - 1, success, failed, errors });
                    if (failed === 0) toast.success('All rows imported successfully!');
                    else toast.error('Some rows failed to import.');
                  } catch (err) {
                    setImportError(err.message || 'Import failed');
                  }
                  setImporting(false);
                  setImportModalOpen(false);
                  if (typeof refetch === 'function') refetch();
                }}
              >
                Import
              </button>
            </div>
            {importing && (
              <div className="mt-4 text-blue-700 text-sm">Import in progress...</div>
            )}
          </div>
        </div>
      )}
      {/* Import Status Card */}
      {showImportStatus && (
        <div className="rounded-xl shadow bg-blue-100 border border-blue-400 p-3 mb-4 flex flex-col md:flex-row md:items-center md:gap-6 text-sm relative animate-fade-in">
          <button onClick={() => setShowImportStatus(false)} className="absolute top-2 right-2 text-blue-700 hover:text-blue-900 text-lg font-bold">&times;</button>
          {importing ? (
            <span className="text-blue-700 font-semibold">Import in progress...</span>
          ) : (
            <>
              <span className="font-semibold mr-4">Import Summary:</span>
              <span className="mr-4">Rows processed: <b>{importStatus?.total}</b></span>
              <span className="mr-4 text-green-700">Rows uploaded: <b>{importStatus?.success}</b></span>
              <span className="mr-4 text-red-700">Rows failed: <b>{importStatus?.failed}</b></span>
              {importStatus?.errors && importStatus.errors.length > 0 && (
                <span className="text-red-600">Errors: {importStatus.errors.map((err, i) => <span key={i}>{err}{i < importStatus.errors.length-1 ? ', ' : ''}</span>)}</span>
              )}
            </>
          )}
        </div>
      )}
      {/* Add Service Catalog Modal */}
      {addModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
          <div className="bg-white dark:bg-gray-900 rounded-2xl shadow-2xl p-8 w-full max-w-md relative">
            <button onClick={() => setAddModalOpen(false)} className="absolute top-3 right-3 text-gray-400 hover:text-gray-700 dark:hover:text-gray-200 text-2xl">&times;</button>
            <h2 className="text-xl font-bold mb-4 text-blue-900 dark:text-blue-100">Add Service Catalog</h2>
            <div className="mb-4">
              <label className="block text-sm font-medium mb-1">Name</label>
              <input
                value={addValue}
                onChange={e => setAddValue(e.target.value)}
                className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm"
                placeholder="Service Name"
              />
            </div>
            <div className="mb-4">
              <label className="block text-sm font-medium mb-1">Type</label>
              <select
                value={addType}
                onChange={e => setAddType(Number(e.target.value))}
                className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm"
              >
                <option value="">Select Type</option>
                {types?.map(t => (
                  <option key={t.id} value={t.id}>{t.name}</option>
                ))}
              </select>
            </div>
            <div className="mb-4">
              <label className="block text-sm font-medium mb-1">Port</label>
              <input
                type="number"
                value={addPort}
                onChange={e => setAddPort(e.target.value === '' ? '' : Number(e.target.value))}
                className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm"
                placeholder="Default Port (optional)"
              />
            </div>
            <div className="mb-4">
              <label className="block text-sm font-medium mb-1">Description</label>
              <input
                value={addDesc}
                onChange={e => setAddDesc(e.target.value)}
                className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm"
                placeholder="Description (optional)"
              />
            </div>
            <div className="flex items-center justify-end gap-2 pt-4">
              <button onClick={() => setAddModalOpen(false)} className="px-4 py-2 rounded border">Cancel</button>
              <button
                className="px-4 py-2 rounded bg-blue-600 text-white font-semibold disabled:opacity-60"
                disabled={!addValue.trim() || !addType}
                onClick={handleAdd}
              >
                Add
              </button>
            </div>
          </div>
        </div>
      )}
      <div className="relative mt-2">
        <div className="relative overflow-x-auto shadow-md sm:rounded-lg">
          {isLoading ? (
            <div className="p-8 flex items-center justify-center">
              <Spinner />
            </div>
          ) : isError ? (
            <div className="p-8 text-red-600">Error: {(error as Error)?.message}</div>
          ) : (
            <table className="w-full text-sm text-left text-gray-500 dark:text-gray-400">
              <thead className="text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
                <tr>
                  <th className="px-4 py-3">
                    <input
                      type="checkbox"
                      checked={allSelected}
                      onChange={toggleSelectAll}
                      aria-label="Select all"
                    />
                  </th>
                  <th className="px-6 py-3 cursor-pointer select-none" onClick={() => handleSort('name')}>
                    <div className="flex items-center">
                      Name
                      <span className="ml-1 font-bold">
                        {sortBy === 'name' ? (
                          sortDir === 'asc' ? (
                            <ChevronUp size={16} className="inline text-gray-700 dark:text-gray-200 font-bold" />
                          ) : (
                            <ChevronDown size={16} className="inline text-gray-700 dark:text-gray-200 font-bold" />
                          )
                        ) : (
                          <ChevronUp size={16} className="inline text-gray-400 font-bold" />
                        )}
                      </span>
                    </div>
                  </th>
                  <th className="px-6 py-3 cursor-pointer select-none" onClick={() => handleSort('type')}>
                    <div className="flex items-center">
                      Type
                      <span className="ml-1 font-bold">
                        {sortBy === 'type' ? (
                          sortDir === 'asc' ? (
                            <ChevronUp size={16} className="inline text-gray-700 dark:text-gray-200 font-bold" />
                          ) : (
                            <ChevronDown size={16} className="inline text-gray-700 dark:text-gray-200 font-bold" />
                          )
                        ) : (
                          <ChevronUp size={16} className="inline text-gray-400 font-bold" />
                        )}
                      </span>
                    </div>
                  </th>
                  <th className="px-6 py-3 cursor-pointer select-none" onClick={() => handleSort('port')}>
                    <div className="flex items-center">
                      Port
                      <span className="ml-1 font-bold">
                        {sortBy === 'port' ? (
                          sortDir === 'asc' ? (
                            <ChevronUp size={16} className="inline text-gray-700 dark:text-gray-200 font-bold" />
                          ) : (
                            <ChevronDown size={16} className="inline text-gray-700 dark:text-gray-200 font-bold" />
                          )
                        ) : (
                          <ChevronUp size={16} className="inline text-gray-400 font-bold" />
                        )}
                      </span>
                    </div>
                  </th>
                  <th className="px-6 py-3">Description</th>
                  <th className="px-6 py-3 text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredRows.length === 0 && (
                  <tr>
                    <td colSpan={6} className="px-6 py-8 text-center text-gray-400">No catalogs found.</td>
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
                    <th scope="row" className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white">
                      {editingId === r.id ? (
                        <input
                          value={editValue}
                          onChange={e => setEditValue(e.target.value)}
                          className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm text-base font-medium text-blue-900 dark:text-blue-100"
                          placeholder="Name"
                        />
                      ) : (
                        r.name
                      )}
                    </th>
                    <td className="px-6 py-4">
                      {editingId === r.id ? (
                        <select
                          value={editType}
                          onChange={e => setEditType(Number(e.target.value))}
                          className="w-full px-3 py-2 border-2 border-blue-200 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-blue-300 shadow-sm text-base font-medium text-blue-900 dark:text-blue-100"
                        >
                          <option value="">Select Type</option>
                          {types?.map(t => (
                            <option key={t.id} value={t.id}>{t.name}</option>
                          ))}
                        </select>
                      ) : (
                        types?.find(t => t.id === r.serviceTypeId)?.name || ''
                      )}
                    </td>
                    <td className="px-6 py-4">
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
                    <td className="px-6 py-4">
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
                              setEditValue(r.name);
                              setEditType(r.serviceTypeId);
                              setEditPort(r.defaultPort ?? '');
                              setEditDesc(r.description ?? '');
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



export function ServiceCatalogPageWithLayout() {
  return (
    <DashboardLayout>
      <ServiceCatalogPage />
    </DashboardLayout>
  );
}


