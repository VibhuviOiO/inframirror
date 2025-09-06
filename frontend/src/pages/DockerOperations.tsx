import { useState, useMemo, useRef, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { getContainerLogs, useDockerContainers, DockerListContainersParams } from '../hooks/useDockerOps';
import { DashboardLayout } from '@/components/dashboard/DashboardLayout';
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
    { key: 'Logs', label: 'Logs', icon: '📄' },
    { key: 'Inspect', label: 'Inspect', icon: '🔍' },
    { key: 'Exec', label: 'Exec', icon: '💻' },
    { key: 'Files', label: 'Files', icon: '🗂️' },
    { key: 'Stats', label: 'Stats', icon: '📊' },
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
                <div className="flex items-center gap-4">
                  <span className="font-bold text-xl text-gray-800 dark:text-gray-100">{selectedContainer.Names[0]}</span>
                  <span className="text-xs px-2 py-1 rounded bg-blue-100 dark:bg-blue-900 text-blue-700 dark:text-blue-200">{selectedContainer.Image}</span>
                  <span className="text-xs px-2 py-1 rounded bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-200">ID: {selectedContainer.Id.slice(0, 12)}</span>
                  {selectedContainer.Ports && selectedContainer.Ports.length > 0 ? (
                    Array.from(new Set(selectedContainer.Ports.map(p =>
                      p.PublicPort ? `${p.PublicPort}:${p.PrivatePort}` : `:${p.PrivatePort}`
                    ))).map((portStr, idx) => (
                      <span
                        key={portStr}
                        className="text-xs px-2 py-1 rounded bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-200 border border-gray-300 dark:border-gray-700 mr-1"
                      >
                        {portStr}
                      </span>
                    ))
                  ) : (
                    <span className="text-xs px-2 py-1 rounded bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-200">-</span>
                  )}
                  <span className="text-xs px-2 py-1 rounded bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-200">Status: {selectedContainer.Status}</span>
                  <span className={`text-xs px-2 py-1 rounded ${selectedContainer.State === 'running' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>{selectedContainer.State}</span>
                  <span className="text-xs px-2 py-1 rounded bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-200">Uptime: {getUptime(selectedContainer.Created)}</span>
                </div>
              </div>
              <div className="flex gap-2 items-center">
                <button title="Start" className="p-2 rounded hover:bg-blue-100 dark:hover:bg-blue-900">
                  <span role="img" aria-label="Start">▶️</span>
                </button>
                <button title="Pause" className="p-2 rounded hover:bg-yellow-100 dark:hover:bg-yellow-900">
                  <span role="img" aria-label="Pause">⏸️</span>
                </button>
                <button title="Restart" className="p-2 rounded hover:bg-gray-200 dark:hover:bg-gray-800">
                  <span role="img" aria-label="Restart">🔄</span>
                </button>
                <button title="Stop" className="p-2 rounded hover:bg-red-100 dark:hover:bg-red-900">
                  <span role="img" aria-label="Stop">⏹️</span>
                </button>
                <button title="Delete" className="p-2 rounded hover:bg-gray-300 dark:hover:bg-gray-700">
                  <span role="img" aria-label="Delete">🗑️</span>
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

  <div className="px-8 py-6 bg-gray-50 dark:bg-gray-900">
          {selectedContainer && activeTab === 'Logs' && (
            <div className="flex flex-col justify-start w-full">
              <div className="w-full">
                <div className="flex items-center justify-end mb-2 px-2 gap-2">
                  <div className="relative">
                    <input
                      type="text"
                      value={searchLogs}
                      onChange={e => setSearchLogs(e.target.value)}
                      placeholder="Search logs..."
                      className="pl-8 pr-2 py-1 text-xs rounded border border-gray-300 dark:border-gray-700 bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-200 focus:outline-none"
                      style={{ width: '180px' }}
                    />
                    <span className="absolute left-2 top-1.5 text-gray-400">
                      <svg width="14" height="14" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
                    </span>
                  </div>
                  <button title="Copy logs" className="p-2 rounded hover:bg-gray-200 dark:hover:bg-gray-700" onClick={() => filteredLogs && navigator.clipboard.writeText(filteredLogs)}>
                    <span role="img" aria-label="Copy">📋</span>
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
                    <span role="img" aria-label="Download">⬇️</span>
                  </button>
                  <button title="Auto-scroll" className={`p-2 rounded ${autoScroll ? 'bg-blue-100 dark:bg-blue-900' : ''}`} onClick={() => setAutoScroll(v => !v)}>
                    <span role="img" aria-label="Auto-scroll">🔽</span>
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
            </div>
          )}
          {selectedContainer && activeTab !== 'Logs' && (
            <div className="text-sm text-gray-700 dark:text-gray-200 flex items-center justify-center h-full">
              <span className="opacity-60">{tabs.find(t => t.key === activeTab)?.label} feature coming soon...</span>
            </div>
          )}
        </div>
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
