import { useState, useMemo, useRef, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { getContainerLogs, useDockerContainers, DockerListContainersParams } from '../hooks/useDockerOps';
import { useIntegrationInstances } from '../hooks/useIntegrationInstances';
import { useHosts } from '../hooks/useHosts';
import { DashboardLayout } from '@/components/dashboard/DashboardLayout';
import { Play, Pause, RotateCcw, StopCircle, Trash2, FileText, Search, Terminal, FolderOpen, BarChart2, Clipboard, Download, ChevronDown, X } from 'lucide-react';
import { AnsiUp } from 'ansi_up';
const ansi_up = new AnsiUp();
import { ContainerFilter } from '@/components/container/ContainerFilter';
import { DockerStatsSummary } from '../components/container/DockerStatsDashboard/DockerStatsSummary';
import { useDockerStats } from '../hooks/useDockerOps';

function DockerDesktopInspiredOperations() {
  // Track per-container log order: false = API order, true = latest on top
  const [reverseLogsState, setReverseLogsState] = useState<{ [id: string]: boolean }>({});
  function Spinner() {
    return <div className="w-5 h-5 border-2 border-gray-200 border-t-blue-600 rounded-full animate-spin" />;
  }

  // Host/node dropdown logic
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const initialHost = searchParams.get('ip') || '192.168.0.104';
  const initialPort = Number(searchParams.get('port')) || 2375;
  const [host, setHost] = useState(initialHost);
  const [port, setPort] = useState(initialPort);

  // Fetch integration instances and hosts
  const { data: instances } = useIntegrationInstances();
  const { data: hosts } = useHosts();

  // Build host options: only those with config.integrations including 'Docker'
  const hostOptions = useMemo(() => {
    if (!instances || !hosts) return [];
    return instances
      .filter(inst => {
        let configObj = {};
        try { configObj = typeof inst.config === 'string' ? JSON.parse(inst.config) : inst.config || {}; } catch {}
        const integrationsArr = Array.isArray(configObj['integrations']) ? configObj['integrations'] : [];
        return integrationsArr.includes('Docker');
      })
      .map(inst => {
        const hostObj = hosts.find(h => h.id === inst.hostId);
        return {
          label: hostObj?.hostname || `Host ${inst.hostId}`,
          ip: hostObj?.privateIP || hostObj?.publicIP || '',
          port: inst.port || 2375,
        };
      })
      .filter(opt => opt.ip); // Only hosts with IP
  }, [instances, hosts]);

  // When host dropdown changes, update host/port
  function handleHostChange(e) {
    const idx = Number(e.target.value);
    const selected = hostOptions[idx];
    if (selected) {
      setHost(selected.ip);
      setPort(selected.port);
      // Update query params in URL for consistency and reload
      const params = new URLSearchParams(window.location.search);
      params.set('ip', selected.ip);
      params.set('port', String(selected.port));
      window.history.replaceState({}, '', `${window.location.pathname}?${params.toString()}`);
    }
  }

  const params: DockerListContainersParams = { host, port, protocol: 'http', all: true };


  // Filter and search states
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('All');
  const [imageFilter, setImageFilter] = useState('All');

  const { data: containers, isLoading } = useDockerContainers(params);

  // Get unique images for dropdown
  const imageOptions = useMemo(() => {
    if (!containers) return ['All'];
    const images = Array.from(new Set(containers.map(c => c.Image))).filter(Boolean);
    return ['All', ...images];
  }, [containers]);

  const statusOptions = ['All', 'running', 'exited', 'paused'];

  // Combined filter logic
  const filteredContainers = useMemo(() => {
    if (!containers) return [];
    return containers.filter(c => {
      // Search by name
      const matchesSearch = !search.trim() || c.Names.some(name => name.toLowerCase().includes(search.trim().toLowerCase()));
      // Filter by status
      const matchesStatus = statusFilter === 'All' || c.State === statusFilter;
      // Filter by image
      const matchesImage = imageFilter === 'All' || c.Image === imageFilter;
      return matchesSearch && matchesStatus && matchesImage;
    });
  }, [containers, search, statusFilter, imageFilter]);

  const [activeTabs, setActiveTabs] = useState<{ [id: string]: string | null }>({});
  const [tabMenusOpen, setTabMenusOpen] = useState<{ [id: string]: boolean }>({});
  const [logsState, setLogsState] = useState<{ [id: string]: string }>({});
  const [logsLoadingState, setLogsLoadingState] = useState<{ [id: string]: boolean }>({});
  const [autoScrollState, setAutoScrollState] = useState<{ [id: string]: boolean }>({});
  const [searchLogsState, setSearchLogsState] = useState<{ [id: string]: string }>({});
  const logsBoxRefs = useRef<{ [id: string]: HTMLDivElement | null }>({});

  async function fetchLogs(containerId: string) {
    setLogsLoadingState(prev => ({ ...prev, [containerId]: true }));
    try {
      const result = await getContainerLogs({ host, port, containerId });
      setLogsState(prev => ({ ...prev, [containerId]: result }));
    } catch {
      setLogsState(prev => ({ ...prev, [containerId]: 'Failed to fetch logs' }));
    } finally {
      setLogsLoadingState(prev => ({ ...prev, [containerId]: false }));
    }
  }

  useEffect(() => {
    Object.keys(autoScrollState).forEach(containerId => {
      const el = logsBoxRefs.current[containerId];
      if (el && autoScrollState[containerId]) {
        el.scrollTop = el.scrollHeight;
      }
    });
  }, [logsState, autoScrollState]);

  return (
    <div className="min-h-[calc(100vh-64px)] w-full">
      <div className="space-y-3 px-2">
        {/* Title left, filters/search right */}
        <div className="flex flex-row items-center justify-between pt-2 pb-1">
          <div className="font-bold text-base text-gray-800 dark:text-gray-100">Container Ops</div>
          <div className="flex flex-row items-center gap-1">
            <select
              onChange={handleHostChange}
              value={hostOptions.findIndex(opt => opt.ip === host && opt.port === port)}
              className="px-2 py-1 rounded border border-blue-200 bg-white dark:bg-gray-900 text-gray-700 dark:text-gray-200 text-sm"
              style={{ minWidth: 220, height: 32 }}
            >
              <option value="" disabled>Select Node/Host</option>
              {hostOptions.map((opt, idx) => (
                <option key={opt.ip + opt.port} value={idx}>{opt.label} ({opt.ip}:{opt.port})</option>
              ))}
            </select>
            <input
              type="text"
              value={search}
              onChange={e => setSearch(e.target.value)}
              placeholder="Search containers..."
              className="px-2 py-1 rounded border border-blue-200 bg-white dark:bg-gray-900 text-sm"
              style={{ minWidth: 220, height: 32 }}
            />
            <select
              value={statusFilter}
              onChange={e => setStatusFilter(e.target.value)}
              className="px-2 py-1 rounded border border-blue-200 bg-white dark:bg-gray-900 text-gray-700 dark:text-gray-200 text-sm"
              style={{ minWidth: 100, height: 32 }}
            >
              {statusOptions.map(opt => (
                <option key={opt} value={opt}>{opt === 'All' ? 'All Statuses' : opt.charAt(0).toUpperCase() + opt.slice(1)}</option>
              ))}
            </select>
            <select
              value={imageFilter}
              onChange={e => setImageFilter(e.target.value)}
              className="px-2 py-1 rounded border border-blue-200 bg-white dark:bg-gray-900 text-gray-700 dark:text-gray-200 text-sm"
              style={{ minWidth: 100, height: 32 }}
            >
              {imageOptions.map(opt => (
                <option key={opt} value={opt}>{opt === 'All' ? 'All Images' : opt}</option>
              ))}
            </select>
          </div>
        </div>
        {filteredContainers && filteredContainers.length > 0 ? (
          filteredContainers.map(container => (
            <ContainerCard
              key={container.Id}
              container={container}
              host={host}
              port={port}
              activeTab={activeTabs[container.Id]}
              setActiveTabs={setActiveTabs}
              containerId={container.Id}
              reverseLogsState={reverseLogsState}
              setReverseLogsState={setReverseLogsState}
              tabMenusOpen={tabMenusOpen}
              setTabMenusOpen={setTabMenusOpen}
              logsState={logsState}
              setLogsState={setLogsState}
              logsLoadingState={logsLoadingState}
              setLogsLoadingState={setLogsLoadingState}
              autoScrollState={autoScrollState}
              setAutoScrollState={setAutoScrollState}
              searchLogsState={searchLogsState}
              setSearchLogsState={setSearchLogsState}
              logsBoxRefs={logsBoxRefs}
              fetchLogs={fetchLogs}
            />
          ))
        ) : (
          <span className="text-lg text-gray-400 px-8">No containers found.</span>
        )}
      </div>
    </div>
  );
}

function DockerOperationsPageWithLayout() {
  return (
    <DashboardLayout>
      <DockerDesktopInspiredOperations />
    </DashboardLayout>
  );
}

// Inline InspectTabUI component
import { useDockerInspect } from '../hooks/useDockerOps';
// Inline Spinner component
function Spinner() {
  return (
    <svg className="animate-spin h-5 w-5 text-blue-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 0 0 8-8v8z"></path>
    </svg>
  );
}

function InspectTabUI({ container, host, port, onClose }) {
  const [search, setSearch] = useState('');
  const {
    data: inspect,
    isLoading,
    error,
  } = useDockerInspect({
    host,
    port,
    id: container.Id,
    protocol: container.protocol,
    size: true,
  }, !!container.Id);

  // Helper to pretty-print JSON
  function pretty(obj) {
    return JSON.stringify(obj, null, 2);
  }

  // Filter JSON by key or value
  function filterJson(obj, searchText) {
    if (!searchText.trim()) return obj;
    const lower = searchText.trim().toLowerCase();
    function match(val) {
      if (val == null) return false;
      if (typeof val === 'object') return Object.values(val).some(match);
      return String(val).toLowerCase().includes(lower);
    }
    function filter(obj) {
      if (typeof obj !== 'object' || obj == null) return obj;
      if (Array.isArray(obj)) {
        return obj.filter(item => match(item) || filter(item));
      }
      const result = {};
      for (const [k, v] of Object.entries(obj)) {
        if (k.toLowerCase().includes(lower) || match(v)) {
          result[k] = v;
        } else if (typeof v === 'object' && v != null) {
          const filtered = filter(v);
          if (filtered && (Array.isArray(filtered) ? filtered.length : Object.keys(filtered).length)) {
            result[k] = filtered;
          }
        }
      }
      return result;
    }
    return filter(obj);
  }

  return (
    <div className="w-full flex flex-col gap-2 pt-4">
      <div className="flex items-center mb-2 gap-2 w-full justify-between">
        <input
          type="text"
          value={search}
          onChange={e => setSearch(e.target.value)}
          placeholder="Search JSON..."
          className="pl-2 pr-2 py-1 text-xs rounded border border-gray-300 dark:border-gray-700 bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-200 focus:outline-none"
          style={{ width: '220px' }}
        />
        <button
          className="p-2 rounded hover:bg-gray-300 dark:hover:bg-gray-700"
          onClick={onClose}
          title="Close tab"
        >
          <X size={18} className="text-red-600 dark:text-red-400" />
        </button>
      </div>
      <div className="relative border bg-gray-100 dark:bg-gray-800 w-full text-xs font-mono shadow-inner overflow-x-auto overflow-y-auto flex-1 h-full min-h-[300px] max-h-[500px]">
        {isLoading && (
          <div className="absolute inset-0 bg-black/60 flex items-center justify-center z-10">
            <Spinner />
          </div>
        )}
        {error && (
          <div className="p-4 text-red-600">Error: {error.message}</div>
        )}
        {inspect && (
          <pre className="p-4 whitespace-pre-wrap break-words font-mono text-xs h-full">
            {pretty(filterJson(inspect, search))}
          </pre>
        )}
      </div>
    </div>
  );
}

function ContainerCard({
  container,
  host,
  port,
  activeTab,
  setActiveTabs,
  containerId,
  reverseLogsState,
  setReverseLogsState,
  tabMenusOpen,
  setTabMenusOpen,
  logsState,
  setLogsState,
  logsLoadingState,
  setLogsLoadingState,
  autoScrollState,
  setAutoScrollState,
  searchLogsState,
  setSearchLogsState,
  logsBoxRefs,
  fetchLogs,
}) {
  const statsQuery = useDockerStats({ host, port, id: container.Id }, activeTab === 'Stats' && !!container.Id);
  // Tab switch handler
  const handleTabClick = (tabKey) => {
    setActiveTabs(prev => ({
      ...prev,
      [containerId]: activeTab === tabKey ? null : tabKey
    }));
  };
  function Spinner() {
    return <div className="w-5 h-5 border-2 border-gray-200 border-t-blue-600 rounded-full animate-spin" />;
  }

  function getUptime(created: number): string {
    const now = Date.now() / 1000;
    const seconds = Math.max(0, Math.floor(now - created));
    if (seconds < 60) return `${seconds}s`;
    if (seconds < 3600) return `${Math.floor(seconds / 60)}m`;
    if (seconds < 86400) return `${Math.floor(seconds / 3600)}h`;
    return `${Math.floor(seconds / 86400)}d`;
  }

  // Log range state per container
  const [logRangeState, setLogRangeState] = useState<{ [id: string]: string }>({});

  // Helper to get 'since' timestamp for hours
  function getSince(hoursAgo: number) {
    return Math.floor(Date.now() / 1000) - hoursAgo * 3600;
  }

  async function fetchLogsContainer(containerId: string, range?: string) {
    setLogsLoadingState(prev => ({ ...prev, [containerId]: true }));
    try {
      let params: any = { host, port, containerId };
      const selected = range || logRangeState[containerId] || '100';
      if (selected.endsWith('h')) {
        params.since = getSince(Number(selected.replace('h', '')));
      } else {
        params.tail = Number(selected);
      }
      const result = await getContainerLogs(params);
      setLogsState(prev => ({ ...prev, [containerId]: result }));
    } catch {
      setLogsState(prev => ({ ...prev, [containerId]: 'Failed to fetch logs' }));
    } finally {
      setLogsLoadingState(prev => ({ ...prev, [containerId]: false }));
    }
  }

  useEffect(() => {
    Object.keys(autoScrollState).forEach(containerId => {
      const el = logsBoxRefs.current[containerId];
      if (el && autoScrollState[containerId]) {
        el.scrollTop = el.scrollHeight;
      }
    });
  }, [logsState, autoScrollState]);

  const tabs = [
    { key: 'Logs', label: 'Logs', icon: <FileText size={16} className="inline-block" /> },
    { key: 'Inspect', label: 'Inspect', icon: <Search size={16} className="inline-block" /> },
    { key: 'Stats', label: 'Stats', icon: <BarChart2 size={16} className="inline-block" /> },
  ];

  return (
    <div key={container.Id} className="bg-white dark:bg-gray-950 shadow p-3 flex flex-col gap-1 border border-blue-100 mb-3 animate-fade-in text-sm" style={{ borderRadius: 0 }}>
      <div className="flex items-center justify-between">
        {/* Name on the left, info on the right */}
        <span className="font-bold text-base text-gray-800 dark:text-gray-100">
          {container.Names[0].replace(/^\//, '')}
        </span>
        <div className="flex flex-wrap items-center gap-1 ml-2">
          <span className="text-[10px] px-1.5 py-0.5 rounded bg-blue-100 dark:bg-blue-900 text-blue-700 dark:text-blue-200 flex items-center gap-1">
            <svg xmlns="http://www.w3.org/2000/svg" className="inline-block" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M20.59 13.41a2 2 0 0 0 0-2.82l-7.18-7.18a2 2 0 0 0-2.82 0l-5.18 5.18a2 2 0 0 0 0 2.82l7.18 7.18a2 2 0 0 0 2.82 0z"/><path d="M7 7h.01"/></svg>
            {container.Image}
          </span>
          {container.Ports && container.Ports.length > 0 ? (
            Array.from(new Set(container.Ports.map(p =>
              p.PublicPort ? `${p.PublicPort}:${p.PrivatePort}` : `:${p.PrivatePort}`
            ))).map((portStr, idx) => (
              <span
                key={String(portStr)}
                className="text-[9px] px-1 py-0.5 rounded bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-200 border border-gray-300 dark:border-gray-700"
                style={{ lineHeight: '1.1' }}
              >
                {String(portStr)}
              </span>
            ))
          ) : (
            <span className="text-[9px] px-1 py-0.5 rounded bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-200" style={{ lineHeight: '1.1' }}>-</span>
          )}
          <span className={`text-[10px] px-1.5 py-0.5 rounded flex items-center gap-2 ${container.State === 'running' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
            <span className="flex items-center gap-1 text-gray-500 dark:text-gray-400" title="Started on">
              <svg xmlns="http://www.w3.org/2000/svg" className="inline-block" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
              Started on {new Date(container.Created * 1000).toLocaleString('en-US', { dateStyle: 'medium', timeStyle: 'short' })}
            </span>
            <span className="flex items-center gap-1 text-gray-700 dark:text-gray-200" title="Container uptime">
              Up {container.Status.replace(/^Up /, '')}
            </span>
            <span className="font-semibold" title="Container state">{container.State.charAt(0).toUpperCase() + container.State.slice(1)}</span>
          </span>
        </div>
        {/* Log order toggle 
        <div className="flex gap-2 items-center relative">
          <button title="Start" className="p-2 rounded hover:bg-blue-100 dark:hover:bg-blue-900">
            <Play size={18} className="text-blue-600 dark:text-blue-400" />
          </button>
          <button title="Pause" className="p-2 rounded hover:bg-yellow-100 dark:hover:bg-yellow-900">
            <Pause size={18} className="text-yellow-600 dark:text-yellow-400" />
          </button>
          <button title="Restart" className="p-2 rounded hover:bg-gray-200 dark:hover:bg-gray-800">
            <RotateCcw size={18} className="text-gray-600 dark:text-gray-400" />
          </button>
          <button title="Stop" className="p-2 rounded hover:bg-red-100 dark:bg-red-900">
            <StopCircle size={18} className="text-red-600 dark:text-red-400" />
          </button>
          <button title="Delete" className="p-2 rounded hover:bg-gray-300 dark:hover:bg-gray-700">
            <Trash2 size={18} className="text-gray-600 dark:text-gray-400" />
          </button>
        </div>
        */}
      </div>
      {/* Line separator between container row and tabs */}
      <div className="w-full border-b border-gray-300 dark:border-gray-700 my-1" />
      {/* Tabs area for this container */}
      <div className="flex flex-col">
        <div className="flex gap-2 pt-1 border-b border-gray-200 dark:border-gray-800 text-[11px] w-full" style={{ background: 'linear-gradient(90deg, #e3f0ff 0%, #f8fafd 100%)', paddingLeft: 0, paddingRight: 0 }}>
          {tabs.map(tab => (
            <button
              key={tab.key}
              className={`pb-1 text-[11px] font-semibold border-b-2 flex items-center gap-1 transition ${activeTab === tab.key ? 'border-blue-600 text-blue-700 dark:text-blue-300' : 'border-transparent text-gray-500 dark:text-gray-400'}`}
              onClick={() => {
                handleTabClick(tab.key);
                if (tab.key === 'Logs' && activeTab !== tab.key) fetchLogsContainer(container.Id);
              }}
            >{tab.icon} {tab.label}</button>
          ))}
        </div>
        {activeTab === 'Logs' && (
          <div className="flex flex-col justify-start w-full px-0">
            <div className="flex items-center mb-1 gap-1 pt-1 text-[11px]">
              <input
                type="text"
                value={searchLogsState[container.Id] || ''}
                onChange={e => setSearchLogsState(prev => ({ ...prev, [container.Id]: e.target.value }))}
                placeholder="Search logs..."
                className="pl-2 pr-2 py-1 text-xs rounded border border-gray-300 dark:border-gray-700 bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-200 focus:outline-none"
                style={{ width: '140px' }}
              />
              {/* Log range selector */}
              <select
                value={logRangeState[container.Id] || '100'}
                onChange={e => {
                  setLogRangeState(prev => ({ ...prev, [container.Id]: e.target.value }));
                  fetchLogsContainer(container.Id, e.target.value);
                }}
                className="px-2 py-1 rounded border border-blue-200 bg-white dark:bg-gray-900 text-xs text-gray-700 dark:text-gray-200"
                style={{ minWidth: 90, height: 28 }}
              >
                <option value="100">Last 100</option>
                <option value="500">Last 500</option>
                <option value="1000">Last 1000</option>
                <option value="1h">Last 1 hour</option>
                <option value="6h">Last 6 hours</option>
                <option value="24h">Last 24 hours</option>
              </select>
              {/* Switch to toggle log order */}
              <div className="flex items-center gap-2 ml-2">
                <label className="flex items-center gap-1 text-xs text-gray-700 dark:text-gray-200">
                  <input
                    type="checkbox"
                    checked={!!reverseLogsState[container.Id]}
                    onChange={e => setReverseLogsState(prev => ({ ...prev, [container.Id]: e.target.checked }))}
                    className="accent-blue-600 h-4 w-4"
                  />
                  <span>
                    Show latest logs on top
                  </span>
                </label>
              </div>
              {/* Icon-only buttons, styled like card icons */}
              <button title="Copy logs" className="p-2 rounded hover:bg-blue-100 dark:hover:bg-blue-900" onClick={() => {
                const logs = logsState[container.Id] || '';
                const search = searchLogsState[container.Id] || '';
                const filtered = !search.trim() ? logs : logs.split('\n').filter(line => line.toLowerCase().includes(search.trim().toLowerCase())).join('\n');
                filtered && navigator.clipboard.writeText(filtered);
              }}>
                <Clipboard size={18} className="text-blue-600 dark:text-blue-400" />
              </button>
              <button title="Download logs" className="p-2 rounded hover:bg-gray-200 dark:hover:bg-gray-800" onClick={() => {
                const logs = logsState[container.Id] || '';
                const search = searchLogsState[container.Id] || '';
                const filtered = !search.trim() ? logs : logs.split('\n').filter(line => line.toLowerCase().includes(search.trim().toLowerCase())).join('\n');
                const blob = new Blob([filtered], { type: 'text/plain' });
                const url = URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = `${container.Names[0]}-logs.txt`;
                a.click();
                URL.revokeObjectURL(url);
              }}>
                <Download size={18} className="text-gray-600 dark:text-gray-400" />
              </button>
              {/* Close button at the extreme right, icon-only, styled like card icons */}
              <div className="flex-1 flex justify-end">
                <button
                  className="p-2 rounded hover:bg-gray-300 dark:hover:bg-gray-700"
                  onClick={() => setActiveTabs(prev => ({ ...prev, [container.Id]: null }))}
                  title="Close tab"
                >
                  <X size={18} className="text-red-600 dark:text-red-400" />
                </button>
              </div>
            </div>
            <div className="relative border bg-gray-100 dark:bg-gray-800 w-full text-[11px] font-mono shadow-inner overflow-x-auto overflow-y-auto flex-1 h-full min-h-[220px] max-h-[350px]">
              {logsLoadingState[container.Id] && (
                <div className="absolute inset-0 bg-black/60 flex items-center justify-center z-10">
                  <Spinner />
                </div>
              )}
              <div
                ref={el => logsBoxRefs.current[container.Id] = el}
                className="p-4 whitespace-pre-wrap break-words font-mono text-xs h-full"
                style={{ minHeight: '300px', maxHeight: '500px', overflowY: 'auto', overflowX: 'auto' }}
                dangerouslySetInnerHTML={{ __html: ansi_up.ansi_to_html((() => {
                  const logs = logsState[container.Id] || '';
                  const search = searchLogsState[container.Id] || '';
                  let lines = !search.trim() ? logs.split('\n') : logs.split('\n').filter(line => line.toLowerCase().includes(search.trim().toLowerCase()));
                  if (reverseLogsState[container.Id]) lines = lines.reverse();
                  return lines.join('\n');
                })()) }}
              />
            </div>
          </div>
        )}
        {activeTab && activeTab !== 'Logs' && (
            activeTab === 'Inspect' ? (
              <InspectTabUI
                container={container}
                host={host}
                port={port}
                onClose={() => setActiveTabs(prev => ({ ...prev, [container.Id]: null }))}
              />
            ) : (
              <div className="flex flex-col items-center justify-center h-full px-0">
                <div className="flex w-full justify-end mb-2">
                  <button
                    className="p-2 rounded hover:bg-gray-300 dark:text-gray-700"
                    onClick={() => setActiveTabs(prev => ({ ...prev, [container.Id]: null }))}
                    title="Close tab"
                  >
                    <X size={18} className="text-red-600 dark:text-red-400" />
                  </button>
                </div>
                {/* Removed 'feature coming soon...' placeholder */}
              </div>
            )
        )}
        {activeTab === 'Stats' && (
          <div className="flex flex-col gap-4 p-2">
            {/* Fetch stats for this container */}
            {statsQuery.isLoading && <div className="text-xs text-gray-400">Loading stats...</div>}
            {statsQuery.isError && <div className="text-xs text-red-500">Error loading stats</div>}
            {statsQuery.data && (
              <div className="relative">
                {/* Only one close button remains, duplicate removed */}
                <DockerStatsSummary stats={statsQuery.data} />
              </div>
            )}
            {!statsQuery.data && !statsQuery.isLoading && !statsQuery.isError && (
              <div className="text-xs text-gray-400">No stats available</div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

export default DockerOperationsPageWithLayout;