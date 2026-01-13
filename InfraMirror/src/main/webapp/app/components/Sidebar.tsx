import React, { useState } from 'react';
import './Sidebar.css';
import { theme } from 'app/config/theme';
import {
  FaCogs,
  FaServer,
  FaHeartbeat,
  FaSitemap,
  FaPaintBrush,
  FaKey,
  FaHistory,
  FaChartLine,
  FaFileAlt,
  FaBook,
  FaGlobe,
  FaUserSecret,
  FaDesktop,
  FaTasks,
  FaNetworkWired,
  FaEye,
  FaList,
  FaLock,
  FaDatabase,
  FaChartBar,
  FaClipboardList,
  FaRobot,
  FaUserShield,
  FaTools,
  FaBars,
  FaAngleLeft,
  FaTachometerAlt,
} from 'react-icons/fa';
import { useNavigate } from 'react-router-dom';
import { IconType } from 'react-icons';

interface SidebarItem {
  name: string;
  icon: IconType;
  route: string;
}

interface SidebarMenu {
  title: string;
  icon: IconType;
  items: SidebarItem[];
  route?: string;
}

interface SidebarProps {
  isAuthenticated: boolean;
  isAdmin: boolean;
  isCollapsed: boolean;
}

const sidebarMenu: SidebarMenu[] = [
  {
    title: 'Infrastructure',
    icon: FaDatabase,
    items: [
      { name: 'Region', icon: FaGlobe, route: '/region' },
      { name: 'Datacenter', icon: FaServer, route: '/datacenter' },
      { name: 'Instance', icon: FaDesktop, route: '/instance' },
      { name: 'HTTP Monitor', icon: FaNetworkWired, route: '/http-monitor' },
      { name: 'Service', icon: FaEye, route: '/monitored-service' },
    ],
  },
  {
    title: 'Agent',
    icon: FaRobot,
    items: [
      { name: 'Agent', icon: FaUserSecret, route: '/agent' },
      { name: 'Agent Monitor', icon: FaEye, route: '/agent-monitor' },
      { name: 'Agent Lock', icon: FaLock, route: '/agent-lock' },
    ],
  },
  {
    title: 'Status Page',
    icon: FaTachometerAlt,
    route: '/status-page',
    items: [],
  },
  {
    title: 'Monitoring',
    icon: FaChartBar,
    items: [
      { name: 'HTTP Heartbeat', icon: FaHeartbeat, route: '/http-heartbeat' },
      { name: 'Service Heartbeat', icon: FaHeartbeat, route: '/service-heartbeat' },
      { name: 'Instance Heartbeat', icon: FaHeartbeat, route: '/instance-heartbeat' },
    ],
  },
  {
    title: 'Administrator',
    icon: FaUserShield,
    items: [
      { name: 'Branding', icon: FaPaintBrush, route: '/branding' },
      { name: 'API Key', icon: FaKey, route: '/api-key' },
      { name: 'Audit Trail', icon: FaHistory, route: '/audit-trail' },
    ],
  },
  {
    title: 'Integration Control',
    icon: FaSitemap,
    route: '/icc',
    items: [],
  },
  {
    title: 'App Metrics',
    icon: FaTools,
    items: [
      { name: 'Metrics', icon: FaChartLine, route: '/admin/metrics' },
      { name: 'Health', icon: FaHeartbeat, route: '/admin/health' },
      { name: 'Logs', icon: FaFileAlt, route: '/admin/logs' },
      { name: 'API Documentation', icon: FaBook, route: '/admin/docs' },
      { name: 'Configuration', icon: FaCogs, route: '/admin/configuration' },
    ],
  },
];

const Sidebar: React.FC<SidebarProps> = ({ isAuthenticated, isAdmin, isCollapsed }) => {
  const navigate = useNavigate();
  const [expandedSection, setExpandedSection] = useState<string | null>(null);
  const [selectedMenu, setSelectedMenu] = useState<SidebarMenu | null>(null);

  const toggleSection = (section: string) => {
    setExpandedSection(expandedSection === section ? null : section);
  };

  const handleNavigation = (route: string) => {
    navigate(route);
  };

  // Filter menu based on authentication and role
  const filteredMenu = sidebarMenu.filter(menu => {
    if (menu.title === 'App Metrics') {
      return isAuthenticated && isAdmin;
    }
    return isAuthenticated;
  });

  if (!isAuthenticated) {
    return null;
  }

  return (
    <>
      <div className={`sidebar ${isCollapsed ? 'collapsed' : ''}`}>
        {filteredMenu.map((menu, index) => (
          <div key={index}>
            {menu.route ? (
              <div className="sidebar-header" onClick={() => handleNavigation(menu.route)}>
                {React.createElement(menu.icon, { className: 'sidebar-icon' })}
                {!isCollapsed && <span>{menu.title}</span>}
                {isCollapsed && <span className="sidebar-tooltip">{menu.title}</span>}
              </div>
            ) : (
              <>
                <div
                  className="sidebar-header"
                  onClick={() => {
                    if (isCollapsed) {
                      setSelectedMenu(selectedMenu?.title === menu.title ? null : menu);
                    } else {
                      toggleSection(menu.title);
                    }
                  }}
                >
                  {React.createElement(menu.icon, { className: 'sidebar-icon' })}
                  {!isCollapsed && <span>{menu.title}</span>}
                  {isCollapsed && <span className="sidebar-tooltip">{menu.title}</span>}
                </div>
                {!isCollapsed && expandedSection === menu.title && (
                  <div className="sidebar-submenu">
                    {menu.items.map((item, idx) => {
                      const Icon = item.icon;
                      return (
                        <div key={idx} className="sidebar-item" onClick={() => handleNavigation(item.route)}>
                          <Icon className="sidebar-icon" />
                          {item.name}
                        </div>
                      );
                    })}
                  </div>
                )}
              </>
            )}
          </div>
        ))}
      </div>
      {isCollapsed && selectedMenu && (
        <div className="sidebar-secondary">
          <div className="sidebar-secondary-header">{selectedMenu.title}</div>
          {selectedMenu.items.map((item, idx) => {
            const Icon = item.icon;
            return (
              <div
                key={idx}
                className="sidebar-secondary-item"
                onClick={() => {
                  handleNavigation(item.route);
                  setSelectedMenu(null);
                }}
              >
                <Icon className="sidebar-icon" />
                {item.name}
              </div>
            );
          })}
        </div>
      )}
    </>
  );
};

export default Sidebar;
