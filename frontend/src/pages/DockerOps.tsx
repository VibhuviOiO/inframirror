
import { useState, useMemo } from 'react';
import { getContainerLogs, streamContainerLogs } from '../hooks/useDockerOps';
import AnsiToHtml from 'ansi-to-html';
import { useLocation } from 'react-router-dom';
import { DashboardLayout } from '@/components/dashboard/DashboardLayout';
import { useDockerContainers, DockerContainer, DockerListContainersParams } from '../hooks/useDockerOps';

function Spinner() {
  return <div className="w-5 h-5 border-2 border-gray-200 border-t-blue-600 rounded-full animate-spin" />;
}


function DockerOpsPage() {
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const initialHost = searchParams.get('ip') || '192.168.0.103';
  const initialPort = Number(searchParams.get('port')) || 2375;
  const [host, setHost] = useState<string>(initialHost);
  const [port, setPort] = useState<number>(initialPort);
  const [params, setParams] = useState<DockerListContainersParams>({
    host: initialHost,
    port: initialPort,
    protocol: 'http',
    all: true,
  });
  const [search, setSearch] = useState('');

  // Update params when host or port changes
  function handleFilterChange(newHost: string, newPort: number) {
    setParams({
      host: newHost,
      port: newPort,
      protocol: 'http',
      all: true,
    });
  }

  // When host/port input changes, update state and params
  const onHostChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setHost(e.target.value);
    handleFilterChange(e.target.value, port);
  };
  const onPortChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const val = Number(e.target.value) || 0;
    setPort(val);
    handleFilterChange(host, val);
  };

  const { data: containers, isLoading, isError, error } = useDockerContainers(params);

  const filteredRows = useMemo(() => {
    if (!containers) return [];
    if (!search.trim()) return containers;
    return containers.filter(c => c.Names.some(name => name.toLowerCase().includes(search.trim().toLowerCase())) || c.Image.toLowerCase().includes(search.trim().toLowerCase()));
  }, [containers, search]);

  // Modal state
  const [showLogsModal, setShowLogsModal] = useState(false);
  const [selectedContainer, setSelectedContainer] = useState<DockerContainer | null>(null);
  const [logFilters, setLogFilters] = useState({
    stdout: true,
    stderr: true,
    follow: false,
    tail: '100',
    timestamps: false,
    since: '',
    until: '',
  });
  const [logs, setLogs] = useState('');
  const ansiConverter = new AnsiToHtml({ fg: '#e5e7eb', bg: '#18181b', newline: true, colors: [
    '#000000', '#e06c75', '#98c379', '#e5c07b', '#61afef', '#c678dd', '#56b6c2', '#abb2bf',
    '#545454', '#d19a66', '#b5bd68', '#f0c674', '#81a2be', '#b294bb', '#8abeb7', '#ffffff'
  ] });
  const [logsLoading, setLogsLoading] = useState(false);
  const [logsError, setLogsError] = useState('');

  function openLogsModal(container: DockerContainer) {
    setSelectedContainer(container);
    setShowLogsModal(true);
    setLogs('');
    setLogsError('');
  }
  function closeLogsModal() {
    setShowLogsModal(false);
    setSelectedContainer(null);
    setLogs('');
    setLogsError('');
  }

  async function handleFetchLogs(e: React.FormEvent) {
    e.preventDefault();
    if (!selectedContainer) return;
    setLogs('');
    setLogsError('');
    setLogsLoading(true);
    try {
      const params = {
        host,
        port,
        containerId: selectedContainer.Id,
        ...logFilters,
        since: logFilters.since ? Number(logFilters.since) : undefined,
        until: logFilters.until ? Number(logFilters.until) : undefined,
      };
      if (logFilters.follow) {
        // Live streaming
        setLogs('');
        await streamContainerLogs({
          host,
          port,
          containerId: selectedContainer.Id,
          stdout: logFilters.stdout,
          stderr: logFilters.stderr,
          tail: logFilters.tail,
          timestamps: logFilters.timestamps,
          since: logFilters.since ? Number(logFilters.since) : undefined,
          until: logFilters.until ? Number(logFilters.until) : undefined,
          onChunk: (chunk: string) => setLogs(prev => prev + chunk),
        });
      } else {
        // Static logs
        const result = await getContainerLogs(params);
        setLogs(result);
      }
    } catch (err: any) {
      setLogsError(err?.message || 'Failed to fetch logs');
    } finally {
      setLogsLoading(false);
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center gap-3">
        <div className="flex w-full items-center gap-2">
          <input
            type="text"
            value={search}
            onChange={e => setSearch(e.target.value)}
            placeholder="Search containers..."
            className="w-full sm:w-64 px-2 py-1 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-indigo-300 text-sm shadow-sm transition"
            style={{ height: 32, fontSize: '0.925rem' }}
          />
          <input
            type="text"
            value={host}
            onChange={onHostChange}
            placeholder="Host IP"
            className="w-36 px-2 py-1 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-indigo-300 text-sm shadow-sm transition"
            style={{ height: 32, fontSize: '0.925rem' }}
          />
          <input
            type="number"
            value={port}
            onChange={onPortChange}
            placeholder="Port"
            className="w-24 px-2 py-1 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-indigo-300 text-sm shadow-sm transition"
            style={{ height: 32, fontSize: '0.925rem' }}
          />
        </div>
      </div>
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
                  <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Name(s)</th>
                  <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Image</th>
                  <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Status</th>
                  <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100">State</th>
                  <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Ports</th>
                  <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Created</th>
                  <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredRows.length === 0 && (
                  <tr>
                    <td colSpan={7} className="px-6 py-8 text-center text-gray-400">No containers found.</td>
                  </tr>
                )}
                {filteredRows.map((c: DockerContainer) => (
                  <tr key={c.Id} className="bg-white border-b dark:bg-gray-800 dark:border-gray-700 border-gray-200">
                    <td className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white">{c.Names.join(', ')}</td>
                    <td className="px-6 py-4">{c.Image}</td>
                    <td className="px-6 py-4">{c.Status}</td>
                    <td className="px-6 py-4">{c.State}</td>
                    <td className="px-6 py-4">
                      {c.Ports.map((p, i) => (
                        <div key={i}>
                          {p.IP ? `${p.IP}:` : ''}{p.PublicPort ? `${p.PublicPort}->` : ''}{p.PrivatePort}/{p.Type}
                        </div>
                      ))}
                    </td>
                    <td className="px-6 py-4">{new Date(c.Created * 1000).toLocaleString()}</td>
                    <td className="px-6 py-4">
                      <button
                        className="px-3 py-1 bg-blue-600 text-white rounded hover:bg-blue-700 text-xs font-semibold shadow"
                        onClick={() => openLogsModal(c)}
                      >View Logs</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>

      {/* Logs Modal */}
      {showLogsModal && selectedContainer && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-60">
          <div className="bg-white dark:bg-gray-900 rounded-xl shadow-2xl w-full max-w-[98vw] p-0 relative animate-fade-in" style={{ minHeight: '90vh', maxHeight: '98vh', display: 'flex', flexDirection: 'column' }}>
            <button
              className="absolute top-6 right-10 text-gray-500 hover:text-gray-800 dark:hover:text-white text-3xl z-10"
              onClick={closeLogsModal}
              aria-label="Close"
              style={{ background: 'rgba(0,0,0,0.08)', borderRadius: '50%', width: 40, height: 40, display: 'flex', alignItems: 'center', justifyContent: 'center', border: 'none' }}
            >&times;</button>
            <div className="px-10 pt-10 pb-2 flex items-center justify-between" style={{ borderBottom: '1px solid #e5e7eb' }}>
              <h2 className="text-2xl font-bold text-blue-700 dark:text-blue-200">Container Logs: {selectedContainer.Names.join(', ')}</h2>
              {/* ...existing code... */}
            </div>
            <form className="px-10 py-4 flex flex-wrap gap-6 items-end bg-gray-50 dark:bg-gray-800" onSubmit={handleFetchLogs}>
              <label className="flex items-center gap-2 text-base">
                <input type="checkbox" checked={logFilters.stdout} onChange={e => setLogFilters(f => ({ ...f, stdout: e.target.checked }))} /> Stdout
              </label>
              <label className="flex items-center gap-2 text-base">
                <input type="checkbox" checked={logFilters.stderr} onChange={e => setLogFilters(f => ({ ...f, stderr: e.target.checked }))} /> Stderr
              </label>
              <label className="flex items-center gap-2 text-base">
                <input type="checkbox" checked={logFilters.follow} onChange={e => setLogFilters(f => ({ ...f, follow: e.target.checked }))} /> Follow (Live)
              </label>
              <label className="text-base">Tail
                <input type="text" value={logFilters.tail} onChange={e => setLogFilters(f => ({ ...f, tail: e.target.value }))} className="ml-2 px-2 py-1 border rounded w-20" />
              </label>
              <label className="flex items-center gap-2 text-base">
                <input type="checkbox" checked={logFilters.timestamps} onChange={e => setLogFilters(f => ({ ...f, timestamps: e.target.checked }))} /> Timestamps
              </label>
              <label className="text-base">Since
                <input type="text" value={logFilters.since} onChange={e => setLogFilters(f => ({ ...f, since: e.target.value }))} className="ml-2 px-2 py-1 border rounded w-24" placeholder="Unix time" />
              </label>
              <label className="text-base">Until
                <input type="text" value={logFilters.until} onChange={e => setLogFilters(f => ({ ...f, until: e.target.value }))} className="ml-2 px-2 py-1 border rounded w-24" placeholder="Unix time" />
              </label>
              <button
                type="submit"
                className="px-6 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 text-base font-semibold shadow"
                disabled={logsLoading}
              >{logsLoading ? 'Loading...' : 'Fetch Logs'}</button>
            </form>
            {logsError && <div className="text-red-600 mb-3">{logsError}</div>}
            <div
              className="flex-1 border-2 border-gray-300 dark:border-gray-700 rounded-b-xl px-8 py-4 overflow-auto text-xs font-mono shadow-inner"
              style={{ fontSize: '0.8rem', background: '#18181b', color: '#e5e7eb', whiteSpace: 'pre', minHeight: '50vh', maxHeight: 'calc(80vh - 120px)' }}
            >
              {logsLoading && !logs ? (
                <Spinner />
              ) : logs ? (
                <div
                  style={{ whiteSpace: 'pre-wrap', fontFamily: 'monospace', fontSize: '0.8rem', background: 'transparent', color: '#e5e7eb' }}
                  dangerouslySetInnerHTML={{ __html:
                    ansiConverter
                      .toHtml(logs)
                      .replace(/[\x00-\x1F\x7F-\x9F�]+/g, '')
                  }}
                />
              ) : (
                <span className="text-gray-500">No logs yet.</span>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export function DockerOpsPageWithLayout() {
  return (
    <DashboardLayout>
      <DockerOpsPage />
    </DashboardLayout>
  );
}

export default DockerOpsPageWithLayout;
