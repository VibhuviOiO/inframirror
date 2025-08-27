import { useState, useMemo } from 'react';
import { useLocation } from 'react-router-dom';
import { DashboardLayout } from '@/components/dashboard/DashboardLayout';
import { usePostgresTables, PostgresTablesParams } from '../hooks/usePostgresOps';

function Spinner() {
  return <div className="w-5 h-5 border-2 border-gray-200 border-t-blue-600 rounded-full animate-spin" />;
}

function PostgresOpsPage() {
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const initialHost = searchParams.get('ip') || '127.0.0.1';
  const initialPort = Number(searchParams.get('port')) || 5432;
  const initialDatabase = searchParams.get('database') || '';
  const initialUser = searchParams.get('user') || '';
  const initialPassword = searchParams.get('password') || '';

  const [host, setHost] = useState<string>(initialHost);
  const [port, setPort] = useState<number>(initialPort);
  const [database, setDatabase] = useState<string>(initialDatabase);
  const [user, setUser] = useState<string>(initialUser);
  const [password, setPassword] = useState<string>(initialPassword);
  const [search, setSearch] = useState('');
  const [params, setParams] = useState<PostgresTablesParams>({
    host: initialHost,
    port: initialPort,
    database: initialDatabase,
    user: initialUser || undefined,
    password: initialPassword || undefined,
  });

  // Update params when filters change
  function handleFilterChange(newHost: string, newPort: number, newDatabase: string, newUser?: string, newPassword?: string) {
    setParams({
      host: newHost,
      port: newPort,
      database: newDatabase,
      user: newUser || undefined,
      password: newPassword || undefined,
    });
  }

  const onHostChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setHost(e.target.value);
    handleFilterChange(e.target.value, port, database, user, password);
  };
  const onPortChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const val = Number(e.target.value) || 0;
    setPort(val);
    handleFilterChange(host, val, database, user, password);
  };
  const onDatabaseChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setDatabase(e.target.value);
    handleFilterChange(host, port, e.target.value, user, password);
  };
  const onUserChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setUser(e.target.value);
    handleFilterChange(host, port, database, e.target.value, password);
  };
  const onPasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setPassword(e.target.value);
    handleFilterChange(host, port, database, user, e.target.value);
  };

  const { data: tables, isLoading, isError, error } = usePostgresTables(params, !!params.host && !!params.port && !!params.database);

  const filteredTables = useMemo(() => {
    if (!tables) return [];
    if (!search.trim()) return tables;
    return tables.filter(t => t.toLowerCase().includes(search.trim().toLowerCase()));
  }, [tables, search]);

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center gap-3">
        <div className="flex w-full items-center gap-2">
          <input
            type="text"
            value={search}
            onChange={e => setSearch(e.target.value)}
            placeholder="Search tables..."
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
          <input
            type="text"
            value={database}
            onChange={onDatabaseChange}
            placeholder="Database"
            className="w-36 px-2 py-1 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-indigo-300 text-sm shadow-sm transition"
            style={{ height: 32, fontSize: '0.925rem' }}
          />
          <input
            type="text"
            value={user}
            onChange={onUserChange}
            placeholder="User (optional)"
            className="w-32 px-2 py-1 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-indigo-300 text-sm shadow-sm transition"
            style={{ height: 32, fontSize: '0.925rem' }}
          />
          <input
            type="password"
            value={password}
            onChange={onPasswordChange}
            placeholder="Password (optional)"
            className="w-32 px-2 py-1 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-900 focus:ring-2 focus:ring-indigo-300 text-sm shadow-sm transition"
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
                  <th className="px-6 py-4 text-base font-bold text-blue-800 dark:text-blue-100">Table Name</th>
                </tr>
              </thead>
              <tbody>
                {filteredTables.length === 0 && (
                  <tr>
                    <td className="px-6 py-8 text-center text-gray-400">No tables found.</td>
                  </tr>
                )}
                {filteredTables.map((t) => (
                  <tr key={t} className="bg-white border-b dark:bg-gray-800 dark:border-gray-700 border-gray-200">
                    <td className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white">{t}</td>
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

export function PostgresOpsPageWithLayout() {
  return (
    <DashboardLayout>
      <PostgresOpsPage />
    </DashboardLayout>
  );
}

export default PostgresOpsPageWithLayout;
