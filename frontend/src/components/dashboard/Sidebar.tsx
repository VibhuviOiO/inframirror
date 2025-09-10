import React from "react";
import {
  Settings,
  Database,
  Building2,
  Home,
  Server,
  Network,
  Container,
  AppWindow,
  Blocks,
} from "lucide-react";
import clsx from "clsx";

const menuGroups = [
  {
    title: "Main",
    items: [{ name: "Dashboard", icon: Home, href: "/" }],
  },
  {
    title: "Infrastructure",
    items: [
      { name: "Datacenters", icon: Building2, href: "/datacenters" },
      { name: "Hosts", icon: Server, href: "/hosts" },
      { name: "Clusters", icon: Network, href: "/clusters" },
      { name: "Service", icon: AppWindow, href: "/services" },
    ],
  },
  {
    title: "Operations",
    items: [
      { name: "Docker Ops", icon: Container, href: "/docker-operations" },
      { name: "Postgres Ops", icon: Container, href: "/postgres-operations" },
    ],
  },
];

interface SidebarProps {
  expanded: boolean;
  setExpanded: (v: boolean) => void;
}

export default function Sidebar({ expanded, setExpanded }: SidebarProps) {
  return (
    <div
      className={`fixed top-0 left-0 z-20 h-full bg-white border-r-2 border-gray-100 flex flex-col transition-all duration-300 ${
        expanded ? "w-72" : "w-20"
      }`}
      onMouseEnter={() => setExpanded(true)}
      onMouseLeave={() => setExpanded(false)}
      style={{ cursor: "pointer" }}
    >
      {expanded ? (
        <>
          <div className="flex items-center gap-3 mb-8 px-8 pt-8">
            <div className="h-12 w-12 flex items-center justify-center overflow-hidden">
              <img src="/inframirror.png" alt="InfraMirror Logo" className="h-11 w-11 object-contain" />
            </div>
            <div>
              <span
                className="text-2xl font-extrabold tracking-tight"
                style={{
                  background: 'linear-gradient(90deg, #4F46E5 0%, #EC4899 100%)',
                  WebkitBackgroundClip: 'text',
                  WebkitTextFillColor: 'transparent',
                  backgroundClip: 'text',
                  color: 'transparent',
                  display: 'inline-block',
                }}
              >
                Infra Mirror
              </span>
            </div>
          </div>
          <nav className="flex-1 flex flex-col justify-between overflow-y-auto px-4 pb-4">
            <div className="space-y-8">
              {menuGroups.map((group) => (
                <div key={group.title}>
                  <h3 className="px-2 mb-3 text-xs font-semibold uppercase tracking-wider text-gray-500">
                    {group.title}
                  </h3>
                  <ul className="space-y-1">
                    {group.items.map((item) => {
                      const selected = window.location.pathname === item.href;
                      return (
                        <li key={item.name}>
                          <a
                            href={item.href}
                            className={clsx(
                              "group flex items-center gap-x-3 rounded-xl px-3 py-2.5 text-sm font-semibold transition-all duration-200",
                              selected
                                ? "text-indigo-600"
                                : "text-gray-700 hover:bg-blue-100 hover:text-blue-700"
                            )}
                          >
                            <span
                              className={clsx(
                                "flex items-center justify-center h-8 w-8",
                                selected
                                  ? "ring-2 ring-indigo-500 rounded-full bg-white"
                                  : ""
                              )}
                            >
                              <item.icon
                                className={clsx(
                                  "h-6 w-6 shrink-0",
                                  selected
                                    ? "text-indigo-600"
                                    : "text-gray-400 group-hover:text-indigo-400"
                                )}
                              />
                            </span>
                            <span>{item.name}</span>
                          </a>
                        </li>
                      );
                    })}
                  </ul>
                </div>
              ))}
            </div>
          </nav>
          <div className="border-t border-gray-200 dark:border-gray-700" />
          <div className="flex items-center px-4 mt-2 mb-2">
            <a
              href="/integrations"
              className={clsx(
                "flex items-center justify-center w-11 h-11 rounded-full bg-indigo-100 border-2 border-white shadow transition-colors",
                window.location.pathname.startsWith("/integrations")
                  ? "ring-2 ring-indigo-500"
                  : "hover:ring-2 hover:ring-indigo-300",
                window.location.pathname.startsWith("/integrations")
                  ? "text-indigo-700"
                  : "text-gray-400 hover:text-indigo-700"
              )}
              title="Integrations"
            >
              <Blocks className="h-6 w-6" />
            </a>
            {expanded && (
              <a href="/integrations">
                <span className="ml-3 font-semibold text-gray-800 text-xs">
                  Integrations
                </span>
              </a>
            )}
          </div>
          <div className="border-t border-gray-200 dark:border-gray-700" />
          <div className="flex items-center px-4 mt-2 mb-2">
            <a
              href="/settings"
              className={clsx(
                "flex items-center justify-center w-11 h-11 rounded-full bg-indigo-100 border-2 border-white shadow transition-colors",
                window.location.pathname.startsWith("/settings")
                  ? "ring-2 ring-indigo-500"
                  : "hover:ring-2 hover:ring-indigo-300",
                window.location.pathname.startsWith("/settings")
                  ? "text-indigo-700"
                  : "text-gray-400 hover:text-indigo-700"
              )}
              title="Settings"
            >
              <Settings className="h-6 w-6" />
            </a>
            {expanded && (
              <a href="/settings">
                <span className="ml-3 font-semibold text-gray-800 text-xs">
                  Settings
                </span>
              </a>
            )}
          </div>
        </>
      ) : (
        <>
          <div className="flex items-center justify-center h-20 border-b border-gray-100 group relative">
            <div className="h-14 w-14 flex items-center justify-center overflow-hidden">
              <img src="/inframirror.png" alt="InfraMirror Logo" className="h-12 w-12 object-contain" />
            </div>
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
                      className={`relative flex flex-col items-center justify-center gap-3 py-3 rounded-xl transition-colors group ${
                        selected
                          ? "text-indigo-600 font-bold"
                          : "text-gray-500 hover:bg-gray-100"
                      }`}
                      style={{ minHeight: 48 }}
                    >
                      <span
                        className={clsx(
                          "flex items-center justify-center h-10 w-10",
                          selected
                            ? "ring-2 ring-indigo-500 rounded-full bg-white"
                            : ""
                        )}
                      >
                        <item.icon
                          size={24}
                          className={
                            selected
                              ? "text-indigo-600"
                              : "text-gray-400 group-hover:text-indigo-400"
                          }
                        />
                      </span>
                    </a>
                  );
                })}
              </div>
            ))}
          </div>
          <div className="border-t border-gray-200 dark:border-gray-700" />
          <div className="flex flex-col items-center">
            <a
              href="/integrations"
              className={clsx(
                "flex flex-col items-center justify-center gap-1 p-2 rounded-xl transition-colors group",
                window.location.pathname.startsWith("/integrations")
                  ? "text-indigo-600 font-bold"
                  : "text-gray-500 hover:bg-gray-100"
              )}
              style={{ minHeight: 48 }}
              title="integrations"
            >
              <span
                className={clsx(
                  "flex items-center justify-center h-10 w-10",
                  window.location.pathname.startsWith("/integrations")
                    ? "ring-2 ring-indigo-500 rounded-full bg-white"
                    : ""
                )}
              >
                <Blocks
                  size={24}
                  className={
                    window.location.pathname.startsWith("/integrations")
                      ? "text-indigo-600"
                      : "text-gray-400 group-hover:text-indigo-400"
                  }
                />
              </span>
            </a>
          </div>
          <div className="border-t border-gray-200 dark:border-gray-700" />
          <div className="flex flex-col items-center">
            <a
              href="/settings"
              className={clsx(
                "flex flex-col items-center justify-center gap-1 p-2 rounded-xl transition-colors group",
                window.location.pathname.startsWith("/settings")
                  ? "text-indigo-600 font-bold"
                  : "text-gray-500 hover:bg-gray-100"
              )}
              style={{ minHeight: 48 }}
              title="Settings"
            >
              <span
                className={clsx(
                  "flex items-center justify-center h-10 w-10",
                  window.location.pathname.startsWith("/settings")
                    ? "ring-2 ring-indigo-500 rounded-full bg-white"
                    : ""
                )}
              >
                <Settings
                  size={24}
                  className={
                    window.location.pathname.startsWith("/settings")
                      ? "text-indigo-600"
                      : "text-gray-400 group-hover:text-indigo-400"
                  }
                />
              </span>
            </a>
          </div>
        </>
      )}
    </div>
  );
}
