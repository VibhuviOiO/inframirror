import React, { useState, useCallback } from 'react';
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Badge } from "@/components/ui/badge";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { 
  Search, 
  Filter, 
  Clock, 
  RefreshCw, 
  Grid3X3, 
  List,
  X
} from 'lucide-react';
import { FilterState, ViewMode } from '../types';
import { useDebounce } from '../hooks';

interface MonitorFiltersProps {
  filters: FilterState;
  onFiltersChange: (filters: Partial<FilterState>) => void;
  uniqueRegions: string[];
  uniqueMethods: string[];
  viewMode: ViewMode;
  onViewModeChange: (mode: ViewMode) => void;
  onRefresh: () => void;
  className?: string;
}

export const MonitorFilters: React.FC<MonitorFiltersProps> = ({
  filters,
  onFiltersChange,
  uniqueRegions,
  uniqueMethods,
  viewMode,
  onViewModeChange,
  onRefresh,
  className = ""
}) => {
  const [searchInput, setSearchInput] = useState(filters.search);
  
  // Debounce search to improve performance
  const [debouncedSearch] = useDebounce({ value: searchInput, delay: 300 });
  
  React.useEffect(() => {
    onFiltersChange({ search: debouncedSearch });
  }, [debouncedSearch, onFiltersChange]);

  const handleFilterChange = useCallback((key: keyof FilterState, value: any) => {
    onFiltersChange({ [key]: value });
  }, [onFiltersChange]);

  const clearFilter = useCallback((filterKey: keyof FilterState) => {
    const resetValues: Record<string, any> = {
      search: '',
      method: 'all',
      status: 'all',
      region: 'all',
      responseTime: 'all',
      showActiveOnly: false
    };
    
    if (filterKey in resetValues) {
      if (filterKey === 'search') {
        setSearchInput('');
      }
      handleFilterChange(filterKey, resetValues[filterKey]);
    }
  }, [handleFilterChange]);

  const clearAllFilters = useCallback(() => {
    setSearchInput('');
    onFiltersChange({
      search: '',
      method: 'all',
      status: 'all',
      region: 'all',
      responseTime: 'all',
      showActiveOnly: false,
      activeWindow: 5
    });
  }, [onFiltersChange]);

  const hasActiveFilters = filters.method !== 'all' || 
                          filters.status !== 'all' || 
                          filters.region !== 'all' || 
                          filters.responseTime !== 'all' || 
                          filters.showActiveOnly ||
                          filters.search.trim() !== '';

  return (
    <div className={`bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-700 rounded-md mb-4 ${className}`}>
      {/* Main Filter Bar */}
      <div className="flex items-center gap-3 px-4 py-3">
        {/* Add Filter Dropdown */}
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="outline" size="sm" className="h-8 px-3 text-xs flex-shrink-0">
              <Filter className="w-3 h-3 mr-1" />
              Add filter
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="start" className="w-52">
            <DropdownMenuLabel>Status Filters</DropdownMenuLabel>
            <DropdownMenuItem onClick={() => handleFilterChange('status', 'healthy')}>
              🟢 Status: Healthy
            </DropdownMenuItem>
            <DropdownMenuItem onClick={() => handleFilterChange('status', 'error')}>
              🔴 Status: Error
            </DropdownMenuItem>
            <DropdownMenuSeparator />
            
            <DropdownMenuLabel>Response Time Status</DropdownMenuLabel>
            <DropdownMenuItem onClick={() => handleFilterChange('responseTime', 'healthy')}>
              🟢 Fast Response
            </DropdownMenuItem>
            <DropdownMenuItem onClick={() => handleFilterChange('responseTime', 'warning')}>
              🟡 Slow Response
            </DropdownMenuItem>
            <DropdownMenuItem onClick={() => handleFilterChange('responseTime', 'critical')}>
              🔴 Very Slow Response
            </DropdownMenuItem>
            <DropdownMenuItem onClick={() => handleFilterChange('responseTime', 'failed')}>
              ⚫ Failed Response
            </DropdownMenuItem>

          </DropdownMenuContent>
        </DropdownMenu>

        {/* Search Input */}
        <div className="flex-1 relative">
          <div className="flex items-center border border-gray-300 dark:border-gray-600 rounded bg-white dark:bg-gray-800 h-8 px-3">
            <Search className="w-4 h-4 text-gray-400 mr-2 flex-shrink-0" />
            <Input
              placeholder="Search monitors, URLs, paths..."
              value={searchInput}
              onChange={(e) => setSearchInput(e.target.value)}
              className="border-0 p-0 h-auto bg-transparent focus-visible:ring-0 focus-visible:ring-offset-0 placeholder:text-gray-400 text-sm"
            />
            {searchInput && (
              <Button
                variant="ghost"
                size="sm"
                className="h-4 w-4 p-0 ml-2"
                onClick={() => {
                  setSearchInput('');
                  clearFilter('search');
                }}
              >
                <X className="w-3 h-3" />
              </Button>
            )}
          </div>
        </div>

        {/* Quick Filters */}
        <div className="flex items-center gap-2">
          {/* Method Filter */}
          <Select value={filters.method} onValueChange={(value) => handleFilterChange('method', value)}>
            <SelectTrigger className="w-24 h-8 text-xs">
              <SelectValue placeholder="Method" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All Methods</SelectItem>
              {uniqueMethods.map(method => (
                <SelectItem key={method} value={method}>{method}</SelectItem>
              ))}
            </SelectContent>
          </Select>

          {/* Region Filter */}
          <Select value={filters.region} onValueChange={(value) => handleFilterChange('region', value)}>
            <SelectTrigger className="w-36 h-8 text-xs">
              <SelectValue placeholder="Datacenter" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All Datacenters</SelectItem>
              {uniqueRegions.map(region => (
                <SelectItem key={region} value={region}>{region}</SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        {/* Time Range */}
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="outline" size="sm" className="h-8 px-3 text-xs flex-shrink-0">
              <Clock className="w-3 h-3 mr-1" />
              {filters.showActiveOnly ? `Last ${filters.activeWindow}m` : 'All time'}
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            <DropdownMenuItem onClick={() => onFiltersChange({ showActiveOnly: false })}>
              All time
            </DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem onClick={() => onFiltersChange({ showActiveOnly: true, activeWindow: 5 })}>
              Last 5 minutes
            </DropdownMenuItem>
            <DropdownMenuItem onClick={() => onFiltersChange({ showActiveOnly: true, activeWindow: 15 })}>
              Last 15 minutes
            </DropdownMenuItem>
            <DropdownMenuItem onClick={() => onFiltersChange({ showActiveOnly: true, activeWindow: 30 })}>
              Last 30 minutes
            </DropdownMenuItem>
            <DropdownMenuItem onClick={() => onFiltersChange({ showActiveOnly: true, activeWindow: 60 })}>
              Last 1 hour
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>

        {/* Refresh & View Toggle */}
        <div className="flex items-center gap-2 flex-shrink-0">
          <Button 
            variant="ghost" 
            size="sm" 
            onClick={onRefresh}
            className="h-6 w-6 p-0 text-gray-500 hover:text-gray-700"
          >
            <RefreshCw className="w-3 h-3" />
          </Button>
          
          <span className="text-xs text-gray-500 whitespace-nowrap">
            {new Date().toLocaleTimeString()}
          </span>

          {/* View Toggle */}
          <div className="flex items-center gap-1 bg-gray-100 dark:bg-gray-700 rounded p-1">
            <Button
              variant={viewMode === 'grid' ? 'default' : 'ghost'}
              size="sm"
              onClick={() => onViewModeChange('grid')}
              className="h-6 px-2 text-xs"
            >
              <Grid3X3 className="w-3 h-3 mr-1" />
              Grid
            </Button>
            <Button
              variant={viewMode === 'table' ? 'default' : 'ghost'}
              size="sm"
              onClick={() => onViewModeChange('table')}
              className="h-6 px-2 text-xs"
            >
              <List className="w-3 h-3 mr-1" />
              Table
            </Button>
          </div>
        </div>
      </div>

      {/* Active Filters Display */}
      {hasActiveFilters && (
        <div className="px-4 pb-3 border-t border-gray-100 dark:border-gray-800">
          <div className="flex items-center justify-between pt-2">
            <div className="flex items-center gap-2 flex-wrap">
              <span className="text-xs text-gray-500 mr-1">Active filters:</span>
              
              {filters.method !== 'all' && (
                <Badge variant="secondary" className="text-xs">
                  method: {filters.method}
                  <Button
                    variant="ghost"
                    size="sm"
                    className="h-3 w-3 p-0 ml-1 hover:bg-gray-200"
                    onClick={() => clearFilter('method')}
                  >
                    <X className="w-2 h-2" />
                  </Button>
                </Badge>
              )}
              
              {filters.region !== 'all' && (
                <Badge variant="secondary" className="text-xs">
                  region: {filters.region}
                  <Button
                    variant="ghost"
                    size="sm"
                    className="h-3 w-3 p-0 ml-1 hover:bg-gray-200"
                    onClick={() => clearFilter('region')}
                  >
                    <X className="w-2 h-2" />
                  </Button>
                </Badge>
              )}
              
              {filters.status !== 'all' && (
                <Badge variant="secondary" className="text-xs">
                  status: {filters.status}
                  <Button
                    variant="ghost"
                    size="sm"
                    className="h-3 w-3 p-0 ml-1 hover:bg-gray-200"
                    onClick={() => clearFilter('status')}
                  >
                    <X className="w-2 h-2" />
                  </Button>
                </Badge>
              )}
              
              {filters.responseTime !== 'all' && (
                <Badge variant="secondary" className="text-xs">
                  responseTime: {
                    filters.responseTime === 'healthy' ? 'fast' :
                    filters.responseTime === 'warning' ? 'slow' :
                    filters.responseTime === 'critical' ? 'very slow' : 'failed'
                  }
                  <Button
                    variant="ghost"
                    size="sm"
                    className="h-3 w-3 p-0 ml-1 hover:bg-gray-200"
                    onClick={() => clearFilter('responseTime')}
                  >
                    <X className="w-2 h-2" />
                  </Button>
                </Badge>
              )}
              
              {filters.showActiveOnly && (
                <Badge variant="secondary" className="text-xs">
                  timeRange: last {filters.activeWindow}m
                  <Button
                    variant="ghost"
                    size="sm"
                    className="h-3 w-3 p-0 ml-1 hover:bg-gray-200"
                    onClick={() => clearFilter('showActiveOnly')}
                  >
                    <X className="w-2 h-2" />
                  </Button>
                </Badge>
              )}

              {filters.search.trim() && (
                <Badge variant="secondary" className="text-xs">
                  search: "{filters.search.trim()}"
                  <Button
                    variant="ghost"
                    size="sm"
                    className="h-3 w-3 p-0 ml-1 hover:bg-gray-200"
                    onClick={() => clearFilter('search')}
                  >
                    <X className="w-2 h-2" />
                  </Button>
                </Badge>
              )}

              <Button
                variant="ghost"
                size="sm"
                onClick={clearAllFilters}
                className="text-xs text-gray-500 hover:text-gray-700 underline ml-2"
              >
                Clear all
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};