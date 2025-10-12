
import { useState } from 'react';
import { DashboardContent } from './DashboardContent';
import Sidebar from './Sidebar';

export const DashboardLayout = ({ children }: { children?: React.ReactNode }) => {
  // Sidebar expanded/collapsed state
  const [expanded, setExpanded] = useState(true);
  const sidebarWidth = expanded ? 280 : 72; // px

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-950">
      {/* Sidebar */}
      <Sidebar expanded={expanded} setExpanded={setExpanded} />
      {/* Main content, shifted right for sidebar */}
      <div
        className="transition-all duration-300 ease-in-out"
        style={{
          marginLeft: sidebarWidth,
        }}
      >
        <main className="py-8">
          <div className="w-full px-6 lg:px-8">
            {children ? children : <DashboardContent />}
          </div>
        </main>
      </div>
    </div>
  );
};