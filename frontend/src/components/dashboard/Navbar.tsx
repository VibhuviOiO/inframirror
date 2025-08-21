import { Bell, Link, Search, Settings } from "lucide-react";

export default function Header({ expanded, sidebarWidth, title }: { expanded: boolean; sidebarWidth: number; title: string }) {
  return (
    <div
      className="fixed top-0 z-30 h-20 bg-white border-b border-gray-200 flex items-center px-10 justify-between transition-all duration-300"
      style={{ left: sidebarWidth, width: `calc(100% - ${sidebarWidth}px)` }}
    >
      <div className="flex items-center gap-4">
        <span className="font-extrabold text-2xl tracking-tight text-gray-900">{title}</span>
      </div>
      <div className="flex items-center gap-6">
        <div className="flex items-center gap-2">
          <div className="relative">
            <input
              className="rounded-full px-5 py-2 bg-gray-100 focus:bg-white focus:ring-2 focus:ring-indigo-200 outline-none w-56 text-base"
              placeholder="Search"
            />
            <Search size={20} className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400" />
          </div>
        </div>
        <div className="h-8 w-px bg-gray-200 mx-4" />
        <div className="flex items-center gap-4">
          <button className="relative p-2 rounded-full hover:bg-gray-100">
            <Bell size={22} />
            <span className="absolute top-1 right-1 w-2 h-2 bg-red-500 rounded-full border-2 border-white"></span>
          </button>
        </div>
      </div>
    </div>
  );
}
