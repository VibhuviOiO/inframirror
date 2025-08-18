
import { Settings, Building2, Grid3X3, Layers3, Users } from 'lucide-react';
import { cn } from '@/lib/utils';
import { NavLink, Outlet, useLocation } from 'react-router-dom';
import { DashboardLayout } from '@/components/dashboard/DashboardLayout';

import { Cloud } from 'lucide-react';
const settingsMenu = [
  { name: 'Regions', icon: Building2, href: '/settings/regions' },
  { name: 'Environments', icon: Cloud, href: '/settings/environments' },
  { name: 'SVC/App Types', icon: Layers3, href: '/settings/service-types' },
  { name: 'Service Catalog', icon: Grid3X3, href: '/settings/services-catalog' },
  { name: 'App Catalog', icon: Grid3X3, href: '/settings/application-catalog' },
  { name: 'Teams', icon: Users, href: '/settings/teams' },
];

export default function SettingsPage() {
  const location = useLocation();
  return (
    <DashboardLayout>
      <div className="w-full flex justify-center items-start pt-8 px-4">
        <div className="w-full max-w-6xl rounded-3xl shadow-2xl border border-gray-200 dark:border-gray-800 flex flex-col md:flex-row overflow-hidden bg-white dark:bg-gray-900 p-0 md:p-0" style={{ minHeight: '70vh' }}>
          {/* Settings sidebar */}
          <aside className="w-full md:w-64 border-b md:border-b-0 md:border-r border-gray-200 dark:border-gray-800 bg-gradient-to-b from-white via-blue-50 to-blue-100 dark:from-gray-900 dark:via-gray-950 dark:to-blue-950 p-8 flex-shrink-0">
            <div className="flex items-center gap-2 mb-10">
              <Settings className="h-6 w-6 text-indigo-600" />
              <span className="font-bold text-xl tracking-tight text-gray-800 dark:text-gray-100">Settings</span>
            </div>
            <nav className="flex flex-col gap-2">
              {settingsMenu.map((item) => (
                <NavLink
                  key={item.href}
                  to={item.href}
                  className={({ isActive }) =>
                    cn(
                      'flex items-center gap-3 px-4 py-3 rounded-xl text-base font-semibold transition shadow-sm',
                      isActive || location.pathname === item.href
                        ? 'bg-gradient-to-r from-indigo-500 to-blue-500 text-white shadow-lg scale-[1.03]'
                        : 'text-gray-700 dark:text-gray-200 hover:bg-blue-100 dark:hover:bg-blue-900 hover:text-blue-700 dark:hover:text-blue-200'
                    )
                  }
                  end
                >
                  <item.icon className="h-5 w-5" />
                  {item.name}
                </NavLink>
              ))}
            </nav>
          </aside>
          {/* Settings content */}
          <main className="flex-1 p-10">
            <Outlet />
          </main>
        </div>
      </div>
    </DashboardLayout>
  );
}
