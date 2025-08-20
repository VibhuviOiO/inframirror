import React, { useState } from "react";
import Sidebar from "../components/dashboard/Sidebar";
import Header from "../components/dashboard/Header";

const SIDEBAR_WIDTH_EXPANDED = 224;
const SIDEBAR_WIDTH_COLLAPSED = 80;

export default function StickyLayoutDemo({ children }) {
  const [expanded, setExpanded] = useState(true);
  const sidebarWidth = expanded ? SIDEBAR_WIDTH_EXPANDED : SIDEBAR_WIDTH_COLLAPSED;
  return (
    <div className="min-h-screen bg-gray-50">
      <Sidebar expanded={expanded} setExpanded={setExpanded} />
      <Header expanded={expanded} sidebarWidth={sidebarWidth} />
      <div
        className="transition-all duration-300"
        style={{ marginLeft: sidebarWidth }}
      >
        <div className="pt-20">
          {children ? (
            <div className="bg-white rounded-2xl shadow-xl p-8 min-h-[400px]">
              {children}
            </div>
          ) : (
            <div className="bg-white rounded-2xl shadow-xl p-8 min-h-[400px] flex items-center justify-center text-gray-400 text-2xl">
              Main content area
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
