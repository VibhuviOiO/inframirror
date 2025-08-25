import { useState, useMemo } from 'react';
import { DashboardLayout } from '@/components/dashboard/DashboardLayout';
import { useDockerContainers, DockerContainer, DockerListContainersParams } from '../hooks/useDockerOps';

function Spinner() {
  return <div className="w-5 h-5 border-2 border-gray-200 border-t-blue-600 rounded-full animate-spin" />;
}

function DockerOpsPage() {
  const [params] = useState<DockerListContainersParams>({
    host: '192.168.0.103',
    port: 2375,
    protocol: 'http',
    all: true,
  });
  const [search, setSearch] = useState('');
  const { data: containers, isLoading, isError, error } = useDockerContainers(params);

  const filteredRows = useMemo(() => {
    if (!containers) return [];
    if (!search.trim()) return containers;
    return containers.filter(c => c.Names.some(name => name.toLowerCase().includes(search.trim().toLowerCase())) || c.Image.toLowerCase().includes(search.trim().toLowerCase()));
  }, [containers, search]);

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center gap-3">
        <div className="flex w-full items-center">
          <input
            type="text"
            value={search}
            onChange={e => setSearch(e.target.value)}
            placeholder="Search containers..."
            className="w-full sm:w-64 px-2 py-1 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-indigo-300 text-sm shadow-sm transition"
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
                </tr>
              </thead>
              <tbody>
                {filteredRows.length === 0 && (
                  <tr>
                    <td colSpan={6} className="px-6 py-8 text-center text-gray-400">No containers found.</td>
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

export function DockerOpsPageWithLayout() {
  return (
    <DashboardLayout>
      <DockerOpsPage />
    </DashboardLayout>
  );
}

export default DockerOpsPageWithLayout;
