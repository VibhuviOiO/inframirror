import { useState, useMemo, useRef, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { getContainerLogs, useDockerContainers, DockerListContainersParams } from '../hooks/useDockerOps';
import { DashboardLayout } from '@/components/dashboard/DashboardLayout';
import { Play, Pause, RotateCcw, StopCircle, Trash2, FileText, Search, Terminal, FolderOpen, BarChart2, Clipboard, Download, ChevronDown } from 'lucide-react';

import { AnsiUp } from 'ansi_up';
const ansi_up = new AnsiUp();


function DockerDesktopInspiredOperations() {
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
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const { data: containers, isLoading } = useDockerContainers(params);
  const filteredContainers = useMemo(() => {
    if (!containers) return [];
    if (!search.trim()) return containers;
    return containers.filter(c => c.Names.some(name => name.toLowerCase().includes(search.trim().toLowerCase())));
  }, [containers, search]);
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const selectedContainer = containers?.find(c => c.Id === selectedId) || null;

  const tabs = [
    { key: 'Logs', label: 'Logs', icon: <FileText size={16} className="inline-block" /> },
    { key: 'Inspect', label: 'Inspect', icon: <Search size={16} className="inline-block" /> },
    { key: 'Exec', label: 'Exec', icon: <Terminal size={16} className="inline-block" /> },
    { key: 'Files', label: 'Files', icon: <FolderOpen size={16} className="inline-block" /> },
    { key: 'Stats', label: 'Stats', icon: <BarChart2 size={16} className="inline-block" /> },
  ];

  const [activeTab, setActiveTab] = useState('Logs');

  const [logs, setLogs] = useState('');
  const [logsLoading, setLogsLoading] = useState(false);
  const [autoScroll, setAutoScroll] = useState(true);
  const [searchLogs, setSearchLogs] = useState('');


  async function fetchLogs() {
    if (!selectedContainer) return;
    setLogsLoading(true);
    try {
      const result = await getContainerLogs({ host, port, containerId: selectedContainer.Id });
      setLogs(result);
    } catch {
      setLogs('Failed to fetch logs');
    } finally {
      setLogsLoading(false);
    }
  }

  const logsBoxRef = useRef<HTMLDivElement>(null);
  useEffect(() => {
    if (autoScroll && logsBoxRef.current) {
      logsBoxRef.current.scrollTop = logsBoxRef.current.scrollHeight;
    }
  }, [logs, autoScroll]);

  const filteredLogs = useMemo(() => {
    if (!searchLogs.trim()) return logs;
    return logs
      .split('\n')
      .filter(line => line.toLowerCase().includes(searchLogs.trim().toLowerCase()))
      .join('\n');
  }, [logs, searchLogs]);

  useMemo(() => {
    if (selectedContainer && activeTab === 'Logs') fetchLogs();
  }, [selectedContainer, activeTab]);

  function getUptime(created: number) {
    if (!created) return '-';
    const now = Math.floor(Date.now() / 1000);
    const seconds = now - created;
    if (seconds < 60) return `${seconds}s ago`;
    if (seconds < 3600) return `${Math.floor(seconds / 60)}m ago`;
    if (seconds < 86400) return `${Math.floor(seconds / 3600)}h ago`;
    return `${Math.floor(seconds / 86400)}d ago`;
  }

  return (
    <div className="min-h-[calc(100vh-64px)] bg-gray-50 dark:bg-gray-900">
      <main className="flex flex-col">
        <div className="px-8 pt-8 pb-2 flex items-center gap-4">
          <div className="w-full max-w-lg relative">
            <input
              type="text"
              value={search}
              onChange={e => setSearch(e.target.value)}
              onFocus={() => setDropdownOpen(true)}
              onBlur={() => setTimeout(() => setDropdownOpen(false), 150)}
              placeholder="Search containers..."
              className="w-full px-3 py-2 border rounded-lg bg-white dark:bg-gray-900 text-sm shadow"
              style={{ minWidth: 220 }}
            />
            {dropdownOpen && (
              <div className="absolute left-0 right-0 mt-2 z-20 bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-700 rounded-lg shadow-lg max-h-64 overflow-y-auto">
                {isLoading ? (
                  <div className="p-4 text-center text-gray-400"><Spinner /></div>
                ) : filteredContainers.length === 0 ? (
                  <div className="p-4 text-center text-gray-400">No containers found.</div>
                ) : (
                  filteredContainers.map(c => (
                    <div
                      key={c.Id}
                      className={`px-4 py-2 cursor-pointer hover:bg-blue-50 dark:hover:bg-blue-950 transition flex items-center gap-2 ${selectedId === c.Id ? 'bg-blue-100 dark:bg-blue-900' : ''}`}
                      onMouseDown={() => { setSelectedId(c.Id); setSearch(''); setDropdownOpen(false); }}
                    >
                      <span className="w-2 h-2 rounded-full" style={{ background: c.State === 'running' ? '#22c55e' : '#f87171' }} />
                      <span className="font-semibold text-gray-800 dark:text-gray-100">{c.Names[0]}</span>
                      <span className="text-xs text-gray-400">{c.Image}</span>
                    </div>
                  ))
                )}
              </div>
            )}
          </div>
        </div>
        <div className="flex items-center justify-between px-8 py-6 border-b border-gray-200 dark:border-gray-800 bg-white dark:bg-gray-950">
          {selectedContainer ? (
            <>
              <div className="flex flex-col gap-1">
                {/* Container Name (no leading slash) */}
                <span className="font-bold text-2xl text-gray-800 dark:text-gray-100">
                  {selectedContainer.Names[0].replace(/^\//, '')}
                </span>
                {/* Info badges in a single line below name */}
                <div className="flex flex-wrap items-center gap-2 mt-2">
                  {/* Image:tag as single badge with tag icon */}
                  <span className="text-[11px] px-2 py-1 rounded bg-blue-100 dark:bg-blue-900 text-blue-700 dark:text-blue-200 flex items-center gap-1">
                    <svg xmlns="http://www.w3.org/2000/svg" className="inline-block" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M20.59 13.41a2 2 0 0 0 0-2.82l-7.18-7.18a2 2 0 0 0-2.82 0l-5.18 5.18a2 2 0 0 0 0 2.82l7.18 7.18a2 2 0 0 0 2.82 0z"/><path d="M7 7h.01"/></svg>
                    {selectedContainer.Image}
                  </span>
                  {/* Ports */}
                  {selectedContainer.Ports && selectedContainer.Ports.length > 0 ? (
                    Array.from(new Set(selectedContainer.Ports.map(p =>
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
                  {/* Status/Uptime/Started badges, UI-friendly wording */}
                  <span className={`text-[11px] px-2 py-1 rounded flex items-center gap-2 ${selectedContainer.State === 'running' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                    {/* Status: Running/Exited/Paused */}
                    <span className="font-semibold">{selectedContainer.State.charAt(0).toUpperCase() + selectedContainer.State.slice(1)}</span>
                    {/* Uptime with clock icon */}
                    <span className="flex items-center gap-1 text-gray-700 dark:text-gray-200" title="How long the container has been running">
                      <svg xmlns="http://www.w3.org/2000/svg" className="inline-block" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                      Up for {getUptime(selectedContainer.Created)}
                    </span>
                    {/* Started time badge */}
                    <span className="flex items-center gap-1 text-gray-700 dark:text-gray-200" title="When the container was started">
                      <svg xmlns="http://www.w3.org/2000/svg" className="inline-block" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                      Started {new Date(selectedContainer.Created * 1000).toLocaleString('en-US', { dateStyle: 'medium', timeStyle: 'short' })}
                    </span>
                  </span>
                </div>
              </div>
              <div className="flex gap-2 items-center">
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

            </>
          ) : (
            <span className="text-lg text-gray-400">Select a container to view details</span>
          )}
        </div>

        {selectedContainer && (
          <div className="flex gap-6 px-8 pt-4 border-b border-gray-200 dark:border-gray-800 bg-white dark:bg-gray-950">
            {tabs.map(tab => (
              <button
                key={tab.key}
                className={`pb-2 text-sm font-semibold border-b-2 flex items-center gap-2 transition ${activeTab === tab.key ? 'border-blue-600 text-blue-700 dark:text-blue-300' : 'border-transparent text-gray-500 dark:text-gray-400'}`}
                onClick={() => setActiveTab(tab.key)}
              >{tab.icon} {tab.label}</button>
            ))}
          </div>
        )}

        {selectedContainer && activeTab === 'Logs' && (
          <div className="flex flex-col justify-start w-full px-8">
            <div className="flex items-center justify-end mb-2 px-4 gap-2">
              <div className="relative">
                <input
                  type="text"
                  value={searchLogs}
                  onChange={e => setSearchLogs(e.target.value)}
                  placeholder="Search logs..."
                  className="pl-2 pr-2 py-1 text-xs rounded border border-gray-300 dark:border-gray-700 bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-200 focus:outline-none"
                  style={{ width: '180px' }}
                />
              </div>
              <button title="Copy logs" className="p-2 rounded hover:bg-gray-200 dark:hover:bg-gray-700" onClick={() => filteredLogs && navigator.clipboard.writeText(filteredLogs)}>
                <Clipboard size={16} />
              </button>
              <button title="Download logs" className="p-2 rounded hover:bg-gray-200 dark:hover:bg-gray-700" onClick={() => {
                const blob = new Blob([filteredLogs], { type: 'text/plain' });
                const url = URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = `${selectedContainer.Names[0]}-logs.txt`;
                a.click();
                URL.revokeObjectURL(url);
              }}>
                <Download size={16} />
              </button>
              <button title="Auto-scroll" className={`p-2 rounded ${autoScroll ? 'bg-blue-100 dark:bg-blue-900' : ''}`} onClick={() => setAutoScroll(v => !v)}>
                <ChevronDown size={16} />
              </button>
            </div>
            <div className="relative border bg-gray-100 dark:bg-gray-800 w-full text-xs font-mono shadow-inner overflow-x-auto overflow-y-auto flex-1 h-full">
              {logsLoading && (
                <div className="absolute inset-0 bg-black/60 flex items-center justify-center z-10">
                  <Spinner />
                </div>
              )}
              <div
                ref={logsBoxRef}
                className="p-4 whitespace-pre-wrap break-words font-mono text-xs h-full"
                dangerouslySetInnerHTML={{ __html: ansi_up.ansi_to_html(filteredLogs) }}
              />
            </div>
          </div>
        )}
        {selectedContainer && activeTab !== 'Logs' && (
          <div className="text-sm text-gray-700 dark:text-gray-200 flex items-center justify-center h-full">
            <span className="opacity-60">{tabs.find(t => t.key === activeTab)?.label} feature coming soon...</span>
          </div>
        )}
      </main>
    </div>
  );
}

export function DockerOperationsPageWithLayout() {
  return (
    <DashboardLayout>
      <DockerDesktopInspiredOperations />
    </DashboardLayout>
  );
}

export default DockerOperationsPageWithLayout;
