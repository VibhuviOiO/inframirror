import { useState, useMemo, useRef, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { getContainerLogs, useDockerContainers, DockerListContainersParams } from '../hooks/useDockerOps';
import { DashboardLayout } from '@/components/dashboard/DashboardLayout';
import { Play, Pause, RotateCcw, StopCircle, Trash2, FileText, Search, Terminal, FolderOpen, BarChart2, Clipboard, Download, ChevronDown } from 'lucide-react';
import { X } from 'lucide-react';
import { AnsiUp } from 'ansi_up';
const ansi_up = new AnsiUp();

function DockerDesktopInspiredOperations() {
  // Track per-container log order: false = API order, true = latest on top
  const [reverseLogsState, setReverseLogsState] = useState<{ [id: string]: boolean }>({});
  function Spinner() {
    return <div className="w-5 h-5 border-2 border-gray-200 border-t-blue-600 rounded-full animate-spin" />;
  }
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const initialHost = searchParams.get('ip') || '192.168.0.104';
  const initialPort = Number(searchParams.get('port')) || 2375;
  const [host, setHost] = useState(initialHost);
  const [port, setPort] = useState(initialPort);
  const params: DockerListContainersParams = { host, port, protocol: 'http', all: true };

  const [search, setSearch] = useState('');
  const { data: containers, isLoading } = useDockerContainers(params);
  const filteredContainers = useMemo(() => {
    if (!containers) return [];
    if (!search.trim()) return containers;
    return containers.filter(c => c.Names.some(name => name.toLowerCase().includes(search.trim().toLowerCase())));
  }, [containers, search]);

  const tabs = [
    { key: 'Logs', label: 'Logs', icon: <FileText size={16} className="inline-block" /> },
    { key: 'Inspect', label: 'Inspect', icon: <Search size={16} className="inline-block" /> },
    { key: 'Exec', label: 'Exec', icon: <Terminal size={16} className="inline-block" /> },
    { key: 'Files', label: 'Files', icon: <FolderOpen size={16} className="inline-block" /> },
    { key: 'Stats', label: 'Stats', icon: <BarChart2 size={16} className="inline-block" /> },
  ];

  const [activeTabs, setActiveTabs] = useState<{ [id: string]: string | null }>({});
  const [tabMenusOpen, setTabMenusOpen] = useState<{ [id: string]: boolean }>({});
  const [logsState, setLogsState] = useState<{ [id: string]: string }>({});
  const [logsLoadingState, setLogsLoadingState] = useState<{ [id: string]: boolean }>({});
  const [autoScrollState, setAutoScrollState] = useState<{ [id: string]: boolean }>({});
  const [searchLogsState, setSearchLogsState] = useState<{ [id: string]: string }>({});
  const logsBoxRefs = useRef<{ [id: string]: HTMLDivElement | null }>({});

  function getUptime(created: number): string {
    const now = Date.now() / 1000;
    const seconds = Math.max(0, Math.floor(now - created));
    if (seconds < 60) return `${seconds}s`;
    if (seconds < 3600) return `${Math.floor(seconds / 60)}m`;
    if (seconds < 86400) return `${Math.floor(seconds / 3600)}h`;
    return `${Math.floor(seconds / 86400)}d`;
  }

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
      <div className="space-y-6 px-4">
        {/* Top area: search box aligned with cards */}
        <div className="flex flex-col sm:flex-row sm:items-center gap-3 pt-8 pb-2">
          <input
            type="text"
            value={search}
            onChange={e => setSearch(e.target.value)}
            placeholder="Search containers..."
            className="w-full sm:w-96 px-4 py-2 rounded-xl border-2 border-blue-100 focus:ring-2 focus:ring-blue-300 bg-white dark:bg-gray-900 shadow focus:shadow-lg transition"
            style={{ height: 40, fontSize: '1rem' }}
          />
        </div>
        {filteredContainers && filteredContainers.length > 0 ? (
          filteredContainers.map(container => (
            <div key={container.Id} className="rounded-xl bg-white dark:bg-gray-950 shadow p-6 flex flex-col gap-2 border border-blue-100 mb-6 animate-fade-in">
              <div className="flex items-center justify-between">
                {/* Name on the left, info on the right */}
                <span className="font-bold text-2xl text-gray-800 dark:text-gray-100">
                  {container.Names[0].replace(/^\//, '')}
                </span>
                <div className="flex flex-wrap items-center gap-2 ml-6">
                  <span className="text-[11px] px-2 py-1 rounded bg-blue-100 dark:bg-blue-900 text-blue-700 dark:text-blue-200 flex items-center gap-1">
                    <svg xmlns="http://www.w3.org/2000/svg" className="inline-block" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M20.59 13.41a2 2 0 0 0 0-2.82l-7.18-7.18a2 2 0 0 0-2.82 0l-5.18 5.18a2 2 0 0 0 0 2.82l7.18 7.18a2 2 0 0 0 2.82 0z"/><path d="M7 7h.01"/></svg>
                    {container.Image}
                  </span>
                  {container.Ports && container.Ports.length > 0 ? (
                    Array.from(new Set(container.Ports.map(p =>
                      p.PublicPort ? `${p.PublicPort}:${p.PrivatePort}` : `:${p.PrivatePort}`
                    ))).map((portStr, idx) => (
                      <span
                        key={portStr}
                        className="text-[10px] px-1.5 py-0.5 rounded bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-200 border border-gray-300 dark:border-gray-700"
                        style={{ lineHeight: '1.2' }}
                      >
                        {portStr}
                      </span>
                    ))
                  ) : (
                    <span className="text-[10px] px-1.5 py-0.5 rounded bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-200" style={{ lineHeight: '1.2' }}>-</span>
                  )}
                  <span className={`text-[11px] px-2 py-1 rounded flex items-center gap-3 ${container.State === 'running' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
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
              {/* Tabs area for this container */}
              <div className="flex flex-col">
                <div className="flex gap-6 pt-4 border-b border-gray-200 dark:border-gray-800 bg-white dark:bg-gray-950 px-0">
                  {tabs.map(tab => (
                    <button
                      key={tab.key}
                      className={`pb-2 text-sm font-semibold border-b-2 flex items-center gap-2 transition ${activeTabs?.[container.Id] === tab.key ? 'border-blue-600 text-blue-700 dark:text-blue-300' : 'border-transparent text-gray-500 dark:text-gray-400'}`}
                      onClick={() => {
                        setActiveTabs(prev => ({
                          ...prev,
                          [container.Id]: activeTabs?.[container.Id] === tab.key ? null : tab.key // toggle tab
                        }));
                        if (tab.key === 'Logs' && activeTabs?.[container.Id] !== tab.key) fetchLogs(container.Id);
                      }}
                    >{tab.icon} {tab.label}</button>
                  ))}
                </div>
                {activeTabs?.[container.Id] === 'Logs' && (
                  <div className="flex flex-col justify-start w-full px-0">
                    <div className="flex items-center mb-2 gap-2 pt-4">
                      <input
                        type="text"
                        value={searchLogsState[container.Id] || ''}
                        onChange={e => setSearchLogsState(prev => ({ ...prev, [container.Id]: e.target.value }))}
                        placeholder="Search logs..."
                        className="pl-2 pr-2 py-1 text-xs rounded border border-gray-300 dark:border-gray-700 bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-200 focus:outline-none"
                        style={{ width: '180px' }}
                      />
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
                    <div className="relative border bg-gray-100 dark:bg-gray-800 w-full text-xs font-mono shadow-inner overflow-x-auto overflow-y-auto flex-1 h-full">
                      {logsLoadingState[container.Id] && (
                        <div className="absolute inset-0 bg-black/60 flex items-center justify-center z-10">
                          <Spinner />
                        </div>
                      )}
                      <div
                        ref={el => logsBoxRefs.current[container.Id] = el}
                        className="p-4 whitespace-pre-wrap break-words font-mono text-xs h-full"
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
                {activeTabs?.[container.Id] && activeTabs?.[container.Id] !== 'Logs' && (
                  <div className="flex flex-col items-center justify-center h-full px-0">
                    <div className="flex w-full justify-end mb-2">
                      <button
                        className="p-2 rounded hover:bg-gray-300 dark:hover:bg-gray-700"
                        onClick={() => setActiveTabs(prev => ({ ...prev, [container.Id]: null }))}
                        title="Close tab"
                      >
                        <X size={18} className="text-red-600 dark:text-red-400" />
                      </button>
                    </div>
                    <span className="opacity-60 text-sm text-gray-700 dark:text-gray-200">{tabs.find(t => t.key === activeTabs?.[container.Id])?.label} feature coming soon...</span>
                  </div>
                )}
              </div>
            </div>
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

export default DockerOperationsPageWithLayout;