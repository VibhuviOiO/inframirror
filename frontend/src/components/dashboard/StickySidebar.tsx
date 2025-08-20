import React from "react";
import { Settings, ChevronLeft, ChevronRight, Database, Layers3, AppWindow, FileText, Building2, Cpu, Hexagon, Home } from "lucide-react";
import clsx from "clsx";

const menuGroups = [
  {
    title: "Main",
    items: [
      { name: "Dashboard", icon: Home, href: "/" },
    ],
  },
  {
    title: "Infrastructure",
    items: [
      { name: "Datacenters", icon: Building2, href: "/datacenters" },
      { name: "Machines", icon: Cpu, href: "/infrastructure/machines" },
    ],
  },
  {
    title: "Catalogs",
    items: [
      { name: "Service Catalog", icon: Layers3, href: "/service-catalogs" },
      { name: "Application Catalog", icon: FileText, href: "/application-catalogs" },
    ],
  },
  {
    title: "Services",
    items: [
      { name: "Marathon", icon: Hexagon, href: "/services/marathon" },
      { name: "Machines", icon: Cpu, href: "/services/machines" },
    ],
  },
  {
    title: "Applications",
    items: [
      { name: "Applications", icon: AppWindow, href: "/applications" },
      { name: "Deployments", icon: Database, href: "/applications/deployments" },
    ],
  },
];

interface StickySidebarProps {
  expanded: boolean;
  setExpanded: (v: boolean) => void;
}

export default function StickySidebar({ expanded, setExpanded }: StickySidebarProps) {
  // Simulate user data
  const user = {
    name: 'Jinna Baalu',
    image: '', // Set to image URL if available
  };
  const initials = user.name.split(' ').map(n => n[0]).join('').toUpperCase();

  return (
  <div className={`fixed top-0 left-0 z-20 h-full bg-white border-r-2 border-gray-100 flex flex-col transition-all duration-300 ${expanded ? 'w-72' : 'w-20'}`}>
      {expanded ? (
        <>
          {/* Expanded: Modern sidebar like Sidebar.tsx */}
          <div className="flex items-center gap-3 mb-8 px-8 pt-8">
            <div className="h-10 w-10 rounded-xl bg-indigo-50 flex items-center justify-center shadow-lg">
              <Database className="h-6 w-6 text-indigo-600" />
            </div>
            <div>
              <span className="text-base font-bold tracking-tight text-gray-800">InfraHub</span>
              <p className="text-xs text-gray-500 tracking-wide mt-1">Management Console</p>
            </div>
          </div>
          <nav className="flex-1 flex flex-col justify-between overflow-y-auto px-4 pb-4">
            <div className="space-y-8">
              {menuGroups.map((group) => (
                <div key={group.title}>
                  <h3 className="px-2 mb-3 text-xs font-semibold uppercase tracking-wider text-gray-500">{group.title}</h3>
                  <ul className="space-y-1">
                    {group.items.map((item) => (
                      <li key={item.name}>
                        <a
                          href={item.href}
                          className={clsx(
                            "group flex items-center gap-x-3 rounded-xl px-3 py-2.5 text-sm font-semibold transition-all duration-200",
                            window.location.pathname === item.href
                              ? "bg-gradient-to-r from-indigo-500 to-blue-500 text-white shadow-lg scale-[1.03]"
                              : "text-gray-700 hover:bg-blue-100 hover:text-blue-700"
                          )}
                        >
                          <item.icon className={clsx("h-6 w-6 shrink-0", window.location.pathname === item.href ? "text-white" : "text-indigo-600 group-hover:text-blue-700")} />
                          <span>{item.name}</span>
                        </a>
                      </li>
                    ))}
                  </ul>
                </div>
              ))}
            </div>
          </nav>
          {/* User info at the bottom with separator */}
          <div className="mt-4 border-t border-gray-200 dark:border-gray-700" />
          <div className="mb-6 flex items-center gap-3 px-8 mt-4">
            {user.image ? (
              <img src={user.image} className="w-11 h-11 rounded-full border-2 border-white shadow" alt={user.name} />
            ) : (
              <div className="w-11 h-11 rounded-full bg-indigo-100 flex items-center justify-center text-indigo-700 font-bold text-lg border-2 border-white shadow">{initials}</div>
            )}
            <span className="font-semibold text-gray-800 text-base whitespace-nowrap">{user.name}</span>
          </div>
        </>
      ) : (
        <>
          {/* Collapsed: Centered icons only, show expand button on logo hover */}
          <div className="flex items-center justify-center h-20 border-b border-gray-100 group relative">
            <div className="h-12 w-12 rounded-2xl bg-indigo-50 flex items-center justify-center shadow">
              <Database className="h-7 w-7 text-indigo-600" />
            </div>
            <button
              onClick={() => setExpanded(true)}
              className="absolute left-full ml-2 p-2 rounded-full bg-gray-100 hover:bg-gray-200 opacity-0 group-hover:opacity-100 transition-opacity"
              style={{ top: '50%', transform: 'translateY(-50%)' }}
              aria-label="Expand sidebar"
            >
              <ChevronRight />
            </button>
          </div>
          <div className="flex-1 flex flex-col gap-2 mt-6 overflow-y-auto">
            {menuGroups.map((group) => (
              <div key={group.title} className="mb-2">
                {group.items.map((item) => {
                  const selected = window.location.pathname === item.href;
                  return (
                    <a
                      key={item.name}
                      href={item.href}
                      className={`relative flex flex-col items-center justify-center gap-3 py-3 rounded-xl transition-colors group ${selected ? 'bg-indigo-50 text-indigo-600 font-bold' : 'text-gray-500 hover:bg-gray-100'}`}
                      style={{ minHeight: 48 }}
                    >
                      <span className="flex items-center justify-center w-full">
                        <item.icon
                          size={24}
                          className={selected ? 'text-indigo-600' : 'text-gray-400 group-hover:text-indigo-400'}
                        />
                      </span>
                      {selected && (
                        <span className="absolute left-0 top-1/2 -translate-y-1/2 w-1.5 h-8 bg-indigo-500 rounded-r-full"></span>
                      )}
                    </a>
                  );
                })}
              </div>
            ))}
          </div>
          {/* User initials only at the bottom with separator */}
          <div className="mt-4 border-t border-gray-200 dark:border-gray-700" />
          <div className="mb-6 flex items-center justify-center mt-4">
            {user.image ? (
              <img src={user.image} className="w-11 h-11 rounded-full border-2 border-white shadow" alt={user.name} />
            ) : (
              <div className="w-11 h-11 rounded-full bg-indigo-100 flex items-center justify-center text-indigo-700 font-bold text-lg border-2 border-white shadow">{initials}</div>
            )}
          </div>
        </>
      )}
      {/* Expand/collapse button for expanded mode only */}
      {expanded && (
        <button
          onClick={() => setExpanded(false)}
          className="mb-6 mx-auto p-2 rounded-full bg-gray-100 hover:bg-gray-200"
          aria-label="Collapse sidebar"
        >
          <ChevronLeft />
        </button>
      )}
    </div>
  );
}
