
import { Settings, Building2, Cloud } from 'lucide-react';
import { cn } from '@/lib/utils';
import { NavLink, Outlet, useLocation } from 'react-router-dom';
import { DashboardLayout } from '@/components/dashboard/DashboardLayout';

const settingsMenu = [
  { name: 'Regions', icon: Building2, href: '/settings/regions' },
  { name: 'Environments', icon: Cloud, href: '/settings/environments' }
];

export default function SettingsPage() {
  const location = useLocation();
  return (
    <DashboardLayout>
      {/* Page Header */}
      <div className="mb-8">
        <div className="flex items-center gap-3 mb-2">
          <div className="p-2 bg-indigo-50 dark:bg-indigo-900/20 rounded-lg">
            <Settings className="h-6 w-6 text-indigo-600 dark:text-indigo-400" />
          </div>
          <div>
            <h1 className="text-2xl font-semibold text-gray-900 dark:text-white">Settings</h1>
            <p className="text-sm text-gray-600 dark:text-gray-400">System configuration and management</p>
          </div>
        </div>
      </div>

      {/* Settings Card */}
      <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
        <div className="flex">
          {/* Settings Navigation */}
          <div className="w-64 bg-gray-50 dark:bg-gray-800/50 border-r border-gray-200 dark:border-gray-700 p-6">
            <nav className="space-y-2">
              {settingsMenu.map((item, idx) => {
                // Special case: highlight Regions if on /settings or /settings/regions
                const isRegions = idx === 0;
                const selected = isRegions
                  ? location.pathname === '/settings' || location.pathname === item.href
                  : location.pathname === item.href;
                return (
                  <NavLink
                    key={item.href}
                    to={item.href}
                    className={cn(
                      'group flex items-center gap-x-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-all duration-200 relative',
                      selected
                        ? 'bg-indigo-50 dark:bg-indigo-900/20 text-indigo-600 dark:text-indigo-400 shadow-sm ring-1 ring-indigo-100 dark:ring-indigo-800/50'
                        : 'text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 hover:text-gray-900 dark:hover:text-white'
                    )}
                  >
                    <div className="flex items-center justify-center w-5 h-5">
                      <item.icon
                        className={cn(
                          "h-5 w-5 shrink-0 transition-colors",
                          selected
                            ? "text-indigo-600 dark:text-indigo-400"
                            : "text-gray-400 group-hover:text-gray-600 dark:group-hover:text-gray-300"
                        )}
                      />
                    </div>
                    <span className="truncate ml-1">{item.name}</span>
                    {selected && (
                      <div className="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-6 bg-indigo-600 dark:bg-indigo-400 rounded-r-full" />
                    )}
                  </NavLink>
                );
              })}
            </nav>
          </div>

          {/* Settings Content */}
          <div className="flex-1 p-8 min-h-[600px]">
            <Outlet />
          </div>
        </div>
      </div>
    </DashboardLayout>
  );
}
