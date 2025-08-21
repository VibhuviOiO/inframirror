
import { useMemo, useState, lazy, Suspense } from "react";
import { DashboardLayout } from "@/components/dashboard/DashboardLayout";
import { Plus, Edit2, Trash2, Check, X, ChevronUp, ChevronDown } from 'lucide-react';
import {
  useServiceCatalogs,
  useCreateServiceCatalog,
  useUpdateServiceCatalog,
  useDeleteServiceCatalog,
} from '../hooks/useServiceCatalogs';
import { useServiceOrAppTypes } from '../hooks/useServiceOrAppTypes';
import { toast } from 'sonner';
const ApplicationCatalogTable = lazy(() => import("../components/catalog/ApplicationCatalogTable"));

export default function CatalogPage() {
  const [tab, setTab] = useState<'services' | 'applications'>('services');
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

  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this catalog?')) return;
    try {
      await del.mutateAsync(id);
      toast.success('Deleted');
    } catch (err: any) {
      toast.error(err?.message || 'Error');
    }
  };

  const serviceCount = items?.length ?? 0;
  const applicationCount = 10;

  return (
    <DashboardLayout>
      <div className="flex justify-center min-h-screen">
        <div className="w-full max-w-[98vw]">
          
            <div className="flex flex-row items-center pt-8 pb-4">
              <div className="flex gap-0">
                <button
                  className={`flex items-center gap-2 px-5 pt-2 pb-2 font-medium text-base transition focus:outline-none border-0 relative
                    ${tab === "services" ? "text-blue-700 font-bold bg-white" : "text-gray-500 hover:text-blue-700 bg-gray-100"}
                  `}
                  style={{
                    borderBottom: tab === "services" ? "3px solid #2563eb" : "3px solid transparent",
                    zIndex: tab === "services" ? 2 : 1,
                    borderTopLeftRadius: 18,
                    borderTopRightRadius: 0,
                    borderBottomLeftRadius: 0,
                    borderBottomRightRadius: 0,
                    boxShadow: tab === "services" ? "0 4px 24px 0 rgba(37,99,235,0.08)" : undefined,
                  }}
                  onClick={() => setTab("services")}
                >
                  <span>Service Catalog</span>
                  <span className={`ml-2 px-2 py-0.5 rounded-full text-xs font-bold ${tab === "services" ? "bg-blue-100 text-blue-700" : "bg-gray-200 text-gray-500"}`}>{serviceCount}</span>
                </button>
                <div className="-ml-2 w-4 h-8 flex items-end" aria-hidden="true">
                  <svg width="100%" height="100%" viewBox="0 0 16 28" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M0 28L16 0V28H0Z" fill="#f9fafb" />
                  </svg>
                </div>
                <button
                  className={`flex items-center gap-2 px-5 pt-2 pb-2 font-medium text-base transition focus:outline-none border-0 relative
                    ${tab === "applications" ? "text-blue-700 font-bold bg-white" : "text-gray-500 hover:text-blue-700 bg-gray-100"}
                  `}
                  style={{
                    borderBottom: tab === "applications" ? "3px solid #2563eb" : "3px solid transparent",
                    zIndex: tab === "applications" ? 2 : 1,
                    borderTopLeftRadius: 0,
                    borderTopRightRadius: 18,
                    borderBottomLeftRadius: 0,
                    borderBottomRightRadius: 0,
                    boxShadow: tab === "applications" ? "0 4px 24px 0 rgba(37,99,235,0.08)" : undefined,
                  }}
                  onClick={() => setTab("applications")}
                >
                  <span>Application Catalog</span>
                  <span className={`ml-2 px-2 py-0.5 rounded-full text-xs font-bold ${tab === "applications" ? "bg-blue-100 text-blue-700" : "bg-gray-200 text-gray-500"}`}>{applicationCount}</span>
                </button>
              </div>
            </div>
            {/* Line below the tabs for the whole page */}
            <div className="w-full border-b border-gray-200" />
            <div className="relative z-10" style={{marginTop: -24}}>
              {tab === 'services' ? (
                <div className="space-y-0"></div>
              ) : (
                <div className="space-y-0"></div>
              )}
            </div>
          </div>
        </div>
    </DashboardLayout>
  );
}
