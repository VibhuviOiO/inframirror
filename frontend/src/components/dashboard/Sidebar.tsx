import { useState } from 'react';
import { cn } from '@/lib/utils';
import { 
  Home,
  Database,
  Settings,
  Layers3,
  AppWindow,
  ChevronRight,
  Grid3X3,
  Plus,
  X,
  Building2,
  Cpu,
  Shield,
  Users,
  FileText,
  Hexagon
} from 'lucide-react';

interface SidebarProps {
  isOpen: boolean;
  onClose: () => void;
}

interface MenuItem {
  name: string;
  icon: any;
  href?: string;
  subItems?: { name: string; href: string; icon: any }[];
}

interface MenuGroup {
  title: string;
  items: MenuItem[];
}

const menuGroups: MenuGroup[] = [
  {
    title: "Main",
    items: [
      {
        name: 'Dashboard',
        icon: Home,
        href: '/'
      }
    ]
  },
  {
    title: "Infrastructure",
    items: [
      {
        name: 'Datacenters',
        icon: Building2,
        href: '/datacenters'
      },
      {
        name: 'Machines',
        icon: Cpu,
        href: '/infrastructure/machines'
      },
    ]
  },
  {
    title: "Catalogs",
    items: [
      {
        name: 'Service Catalog',
        icon: Layers3,
        href: '/service-catalogs'
      },
      {
        name: 'Application Catalog',
        icon: FileText,
        href: '/application-catalogs'
      },
    ]
  },
  {
    title: "Services",
    items: [
      {
        name: 'Marathon',
        icon: Hexagon,
        href: '/services/marathon'
      },
      {
        name: 'Machines',
        icon: Cpu,
        href: '/services/machines'
      },
    ]
  },
  {
    title: "Applications",
    items: [
      {
        name: 'Applications',
        icon: AppWindow,
        href: '/applications'
      },
      {
        name: 'Deployments',
        icon: Database,
        href: '/applications/deployments'
      }
    ]
  }
];

export const Sidebar: React.FC<SidebarProps> = ({ isOpen, onClose }) => {
  const [expandedItems, setExpandedItems] = useState<string[]>([]);

  const toggleExpanded = (itemName: string) => {
    setExpandedItems(prev => 
      prev.includes(itemName) 
        ? prev.filter(name => name !== itemName)
        : [...prev, itemName]
    );
  };

  return (
    <>
  {/* Modern sidebar for desktop, flush with left edge */}
  <div className="hidden lg:fixed lg:inset-y-0 lg:left-0 lg:z-50 lg:flex lg:w-72 lg:flex-col">
  <div className="flex grow flex-col overflow-y-auto bg-gradient-to-b from-blue-800 via-gray-900 to-gray-800 dark:from-gray-900 dark:via-gray-800 dark:to-gray-900 rounded-none shadow-2xl border-r border-blue-900/40 dark:border-gray-800 p-4">
          {/* Logo */}
          <div className="flex items-center gap-3 mb-8 px-2">
            <div className="h-10 w-10 rounded-xl bg-gradient-to-br from-gray-200 via-blue-200 to-blue-400 flex items-center justify-center shadow-lg">
              <Database className="h-6 w-6 text-blue-900" />
            </div>
            <div>
              <span className="text-2xl font-extrabold tracking-widest bg-gradient-to-r from-blue-200 via-white to-blue-400 bg-clip-text text-transparent drop-shadow-lg shadow-blue-900/30 uppercase">InfraHub</span>
              <p className="text-xs text-blue-100/80 tracking-wide mt-1">Management Console</p>
            </div>
          </div>
          {/* Navigation */}
          <nav className="flex-1 flex flex-col justify-between">
            <div className="space-y-8">
              {menuGroups.map((group) => (
                <div key={group.title}>
                  <h3 className="px-2 mb-3 text-xs font-semibold uppercase tracking-wider text-blue-200/80">
                    {group.title}
                  </h3>
                  <ul className="space-y-1">
                    {group.items.map((item) => (
                      <li key={item.name}>
                        <a
                          href={item.href}
                          className={cn(
                            "group flex items-center gap-x-3 rounded-xl px-3 py-2.5 text-base font-semibold transition-all duration-200",
                            window.location.pathname === item.href
                              ? "bg-white/10 text-white shadow-lg ring-2 ring-white/30"
                              : "text-blue-100 hover:bg-white/10 hover:text-white"
                          )}
                        >
                          <item.icon className={cn("h-6 w-6 shrink-0", window.location.pathname === item.href ? "text-white" : "text-blue-200 group-hover:text-white")} />
                          <span>{item.name}</span>
                        </a>
                      </li>
                    ))}
                  </ul>
                </div>
              ))}
            </div>
            {/* Settings at the bottom */}
            <div className="mb-2 mt-8">
              <a
                href="/settings"
                className={cn(
                  "group flex items-center gap-x-3 rounded-xl px-3 py-2.5 text-base font-semibold transition-all duration-200",
                  window.location.pathname.startsWith('/settings')
                    ? "bg-white/10 text-white shadow-lg ring-2 ring-white/30"
                    : "text-blue-100 hover:bg-white/10 hover:text-white"
                )}
              >
                <Settings className={cn("h-6 w-6 shrink-0", window.location.pathname.startsWith('/settings') ? "text-white" : "text-blue-200 group-hover:text-white")} />
                <span>Settings</span>
              </a>
            </div>
          </nav>
        </div>
      </div>

      {/* Mobile sidebar */}
      <div className={cn(
        "relative z-50 lg:hidden",
        isOpen ? "fixed inset-0" : "hidden"
      )}>
        <div className="fixed inset-y-0 left-0 z-50 w-72 bg-sidebar-bg border-r border-sidebar-border overflow-y-auto shadow-moderate">
          {/* Mobile Logo */}
          <div className="flex h-16 shrink-0 items-center justify-between px-6 border-b border-sidebar-border">
            <div className="flex items-center gap-3">
              <div className="h-9 w-9 rounded-xl bg-gradient-to-br from-primary to-primary/80 flex items-center justify-center shadow-moderate">
                <Database className="h-5 w-5 text-primary-foreground" />
              </div>
              <div>
                <span className="text-lg font-semibold text-foreground">InfraHub</span>
                <p className="text-xs text-muted-foreground">Management Console</p>
              </div>
            </div>
            <button
              onClick={onClose}
              className="rounded-lg p-2 hover:bg-sidebar-hover transition-colors"
            >
              <X className="h-5 w-5" />
            </button>
          </div>
          
          {/* Mobile Navigation */}
          <nav className="flex-1 px-4 py-6">
            <div className="space-y-8">
              {menuGroups.map((group) => (
                <div key={group.title}>
                  <h3 className="px-2 mb-3 text-xs font-medium uppercase tracking-wider text-nav-group">
                    {group.title}
                  </h3>
                  <ul className="space-y-1">
                    {group.items.map((item) => (
                      <li key={item.name}>
                        {item.subItems ? (
                          <>
                            <button
                              onClick={() => toggleExpanded(item.name)}
                              className={cn(
                                "group flex w-full items-center gap-x-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-all duration-200",
                                expandedItems.includes(item.name) 
                                  ? "bg-sidebar-active-bg text-sidebar-active" 
                                  : "text-muted-foreground hover:bg-sidebar-hover hover:text-foreground"
                              )}
                            >
                              <item.icon className="h-5 w-5 shrink-0" />
                              <span className="flex-1 text-left">{item.name}</span>
                              <ChevronRight 
                                className={cn(
                                  "h-4 w-4 transition-transform duration-200",
                                  expandedItems.includes(item.name) && "rotate-90"
                                )}
                              />
                            </button>
                            {expandedItems.includes(item.name) && (
                              <ul className="mt-1 space-y-1 pl-8 animate-fade-in">
                                {item.subItems.map((subItem) => (
                                  <li key={subItem.name}>
                                    <a
                                      href={subItem.href}
                                      className="group flex items-center gap-x-3 rounded-lg px-3 py-2 text-sm text-muted-foreground hover:bg-sidebar-hover hover:text-foreground transition-all duration-200"
                                      onClick={onClose}
                                    >
                                      <subItem.icon className="h-4 w-4 shrink-0" />
                                      {subItem.name}
                                    </a>
                                  </li>
                                ))}
                              </ul>
                            )}
                          </>
                        ) : (
                          <a
                            href={item.href}
                            className={cn(
                              "group flex items-center gap-x-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-all duration-200",
                              item.href === '/' 
                                ? "bg-sidebar-active-bg text-sidebar-active shadow-subtle" 
                                : "text-muted-foreground hover:bg-sidebar-hover hover:text-foreground"
                            )}
                            onClick={onClose}
                          >
                            <item.icon className="h-5 w-5 shrink-0" />
                            {item.name}
                          </a>
                        )}
                      </li>
                    ))}
                  </ul>
                </div>
              ))}
            </div>
          </nav>
        </div>
      </div>
    </>
  );
};