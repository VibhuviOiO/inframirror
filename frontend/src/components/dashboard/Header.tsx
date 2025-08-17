import { Bell, Search, Menu, User, Command } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';

interface HeaderProps {
  onMenuClick: () => void;
}

export const Header: React.FC<HeaderProps> = ({ onMenuClick }) => {
  return (
    <div className="sticky top-0 z-40 flex h-16 shrink-0 items-center gap-x-4 border-b border-sidebar-border bg-header-bg px-4 shadow-subtle backdrop-blur-sm bg-header-bg/95 sm:gap-x-6 sm:px-6 lg:px-8">
      <Button
        variant="ghost"
        size="sm"
        onClick={onMenuClick}
        className="lg:hidden hover:bg-sidebar-hover"
      >
        <Menu className="h-5 w-5" />
      </Button>

      {/* Separator */}
      <div className="h-6 w-px bg-border lg:hidden" />

      <div className="flex flex-1 gap-x-4 self-stretch lg:gap-x-6">
        {/* Enhanced Search */}
        <div className="relative flex flex-1 max-w-lg">
          <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
            <Search className="h-4 w-4 text-muted-foreground" />
          </div>
          <Input
            id="search-field"
            className="block w-full rounded-lg border-0 bg-muted/50 py-2 pl-10 pr-12 text-sm placeholder:text-muted-foreground focus:bg-background focus:ring-2 focus:ring-ring transition-all duration-200"
            placeholder="Search or type command..."
            type="search"
          />
          <div className="pointer-events-none absolute inset-y-0 right-0 flex items-center pr-3">
            <kbd className="hidden sm:inline-flex h-5 select-none items-center gap-1 rounded border bg-muted px-1.5 font-mono text-xs text-muted-foreground">
              <Command className="h-3 w-3" />
              K
            </kbd>
          </div>
        </div>

        <div className="flex items-center gap-x-3 lg:gap-x-4">
          {/* Notifications with modern styling */}
          <Button 
            variant="ghost" 
            size="sm" 
            className="relative hover:bg-sidebar-hover rounded-lg"
          >
            <Bell className="h-5 w-5" />
            <span className="absolute -top-0.5 -right-0.5 h-2 w-2 rounded-full bg-destructive animate-pulse"></span>
          </Button>

          {/* Separator */}
          <div className="hidden lg:block lg:h-6 lg:w-px lg:bg-border" />

          {/* Enhanced Profile section */}
          <div className="flex items-center gap-x-3 hover:bg-sidebar-hover rounded-lg px-2 py-1.5 transition-colors cursor-pointer">
            <div className="hidden lg:flex lg:flex-col lg:items-end lg:text-sm">
              <p className="font-medium text-foreground">Musharof</p>
              <p className="text-xs text-muted-foreground">Administrator</p>
            </div>
            <Avatar className="h-9 w-9 ring-2 ring-border shadow-subtle">
              <AvatarImage src="/lovable-uploads/d5b984e9-dda3-4cc4-a691-a578c7a0e24a.png" alt="Profile" />
              <AvatarFallback className="bg-gradient-to-br from-primary to-primary/80 text-primary-foreground text-sm font-medium">
                M
              </AvatarFallback>
            </Avatar>
          </div>
        </div>
      </div>
    </div>
  );
};