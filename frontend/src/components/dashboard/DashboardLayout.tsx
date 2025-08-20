
import { useState } from 'react';
import { DashboardContent } from './DashboardContent';
import Sidebar from './Sidebar';

import Header from './Navbar';
import { usePageTitle } from './usePageTitle';



export const DashboardLayout = ({ children }: { children?: React.ReactNode }) => {
  // Sidebar expanded/collapsed state
  const [expanded, setExpanded] = useState(true);
  const sidebarWidth = expanded ? 288 : 80; // px
  const title = usePageTitle();

  return (
    <div className="min-h-screen bg-white dark:bg-gray-900">
      {/* Sidebar */}
      <Sidebar expanded={expanded} setExpanded={setExpanded} />
      {/* Header (sticky navbar) */}
      <Header expanded={expanded} sidebarWidth={sidebarWidth} title={title} />
      {/* Main content, shifted right and down for sidebar/header */}
      <div
        className="transition-all duration-300"
        style={{
          marginLeft: sidebarWidth,
          paddingTop: 80, // header height in px
        }}
      >
        <main className="py-6">
          <div
            className={expanded ? "mx-auto max-w-7xl px-4 sm:px-6 lg:px-8" : "w-full px-4 sm:px-6 lg:px-8"}
          >
            {children ? children : <DashboardContent />}
          </div>
        </main>
      </div>
    </div>
  );
};