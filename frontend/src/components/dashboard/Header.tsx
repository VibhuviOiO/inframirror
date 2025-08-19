import { Bell, Search, Menu, User, Command } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';

interface HeaderProps {
  onMenuClick: () => void;
}

export const Header: React.FC<HeaderProps> = ({ onMenuClick }) => {
  return (
    <div className="sticky top-0 z-40 flex h-16 shrink-0 items-center border-b border-sidebar-border bg-header-bg px-4 shadow-subtle backdrop-blur-sm bg-header-bg/95 lg:pl-0">
      {/* Sidebar menu button for mobile */}
      <Button
        variant="ghost"
        size="sm"
        onClick={onMenuClick}
        className="lg:hidden hover:bg-sidebar-hover mr-2"
      >
        <Menu className="h-5 w-5" />
      </Button>

      {/* Centered Search Bar */}
      <div className="flex-1 flex justify-center">
        <div className="relative w-full max-w-xl">
          <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
            <Search className="h-4 w-4 text-muted-foreground" />
          </div>
          <Input
            id="search-field"
            className="block w-full rounded-full border-0 bg-muted/50 py-2 pl-10 pr-4 text-sm placeholder:text-muted-foreground focus:bg-background focus:ring-2 focus:ring-ring transition-all duration-200"
            placeholder="Search..."
            type="search"
            disabled
          />
        </div>
      </div>

      {/* Right side: Notification and Profile */}
      <div className="flex items-center gap-x-4 ml-4">
        <Button 
          variant="ghost" 
          size="sm" 
          className="relative hover:bg-sidebar-hover rounded-full"
        >
          <Bell className="h-5 w-5" />
          <span className="absolute -top-0.5 -right-0.5 h-2 w-2 rounded-full bg-destructive animate-pulse"></span>
        </Button>
        <Avatar className="h-9 w-9 ring-2 ring-border shadow-subtle">
          <AvatarImage src="/lovable-uploads/d5b984e9-dda3-4cc4-a691-a578c7a0e24a.png" alt="Profile" />
          <AvatarFallback className="bg-gradient-to-br from-primary to-primary/80 text-primary-foreground text-sm font-medium">
            M
          </AvatarFallback>
        </Avatar>
      </div>
    </div>
  );
};