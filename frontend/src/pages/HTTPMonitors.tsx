import React, { useState, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { useHTTPMonitors, type Monitor } from "../hooks/useMonitors";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { 
  Globe,
  Clock, 
  AlertCircle, 
  CheckCircle, 
  Search, 
  Filter,
  MoreVertical,
  TrendingUp,
  MapPin,
  Zap,
  Timer,
  Lock,
  Unlock,
  FileText,
  Server,
  Eye,
  Activity,
  Settings,
  Pause,
  Play,
  Copy,
  Trash2,
  Download,
  Bell,
  RefreshCw,
  ExternalLink
} from "lucide-react";
import { cn } from "@/lib/utils";
import { DashboardLayout } from "@/components/dashboard/DashboardLayout";

type HTTPMonitor = Monitor;

const HTTPStatusBadge: React.FC<{ 
  status: number | undefined, 
  success: boolean,
  size?: 'sm' | 'default' | 'lg' 
}> = ({ status, success, size = 'default' }) => {
  if (!success || !status) {
    return (
      <Badge variant="destructive" className={cn(
        "animate-pulse",
        size === 'sm' && "text-xs px-2 py-0.5",
        size === 'lg' && "text-sm px-3 py-1"
      )}>
        <AlertCircle className="w-3 h-3 mr-1" />
        {status || 'Down'}
      </Badge>
    );
  }

  if (status >= 200 && status < 300) {
    return (
      <Badge variant="default" className={cn(
        "bg-green-500 hover:bg-green-600 text-white",
        size === 'sm' && "text-xs px-2 py-0.5",
        size === 'lg' && "text-sm px-3 py-1"
      )}>
        <CheckCircle className="w-3 h-3 mr-1" />
        {status}
      </Badge>
    );
  }

  if (status >= 300 && status < 400) {
    return (
      <Badge variant="secondary" className={cn(
        "bg-yellow-500 hover:bg-yellow-600 text-white",
        size === 'sm' && "text-xs px-2 py-0.5",
        size === 'lg' && "text-sm px-3 py-1"
      )}>
        <Activity className="w-3 h-3 mr-1" />
        {status}
      </Badge>
    );
  }

  return (
    <Badge variant="destructive" className={cn(
      size === 'sm' && "text-xs px-2 py-0.5",
      size === 'lg' && "text-sm px-3 py-1"
    )}>
      <AlertCircle className="w-3 h-3 mr-1" />
      {status}
    </Badge>
  );
};

const PerformanceMetrics: React.FC<{ monitor: HTTPMonitor }> = ({ monitor }) => {
  const metrics = [
    { label: 'DNS Lookup', value: monitor.dnsLookupMs, icon: Globe },
    { label: 'TCP Connect', value: monitor.tcpConnectMs, icon: Zap },
    { label: 'TLS Handshake', value: monitor.tlsHandshakeMs, icon: Lock },
    { label: 'TTFB', value: monitor.timeToFirstByteMs, icon: Timer },
  ].filter(m => m.value !== undefined);

  if (metrics.length === 0) return null;

  return (
    <div className="grid grid-cols-2 lg:grid-cols-4 gap-2 mt-3">
      {metrics.map((metric) => {
        const Icon = metric.icon;
        return (
          <div key={metric.label} className="flex items-center gap-1 text-xs text-muted-foreground bg-muted/50 px-2 py-1 rounded">
            <Icon className="w-3 h-3" />
            <span>{metric.label}:</span>
            <span className="font-mono font-medium">{metric.value}ms</span>
          </div>
        );
      })}
    </div>
  );
};

const HTTPMonitorCard: React.FC<{ monitor: HTTPMonitor, onClick: () => void }> = ({ monitor, onClick }) => {
  const lastCheck = new Date(monitor.executedAt);
  const isRecent = Date.now() - lastCheck.getTime() < 5 * 60 * 1000; // 5 minutes
  
  // Build URL exactly as provided in configuration
  let fullUrl = '';
  
  // If targetHost already includes protocol, use it as-is
  if (monitor.targetHost.startsWith('http://') || monitor.targetHost.startsWith('https://')) {
    fullUrl = monitor.targetHost;
    // Add path if not already included
    if (monitor.targetPath && !monitor.targetHost.includes(monitor.targetPath)) {
      fullUrl += monitor.targetPath;
    }
  } else {
    // Build URL based on monitor type or port
    const protocol = monitor.monitorType === 'HTTPS' || monitor.targetPort === 443 ? 'https' : 'http';
    fullUrl = `${protocol}://${monitor.targetHost}`;
    
    // Add port if specified and not default
    if (monitor.targetPort && monitor.targetPort !== 80 && monitor.targetPort !== 443) {
      fullUrl += `:${monitor.targetPort}`;
    }
    
    // Add path
    fullUrl += monitor.targetPath || '/';
  }
  
  // For display purposes, determine if it's secure
  const isSecure = fullUrl.startsWith('https://');

  const handleMenuAction = (action: string, event: React.MouseEvent) => {
    event.preventDefault();
    event.stopPropagation();
    
    switch (action) {
      case 'view-details':
        onClick();
        break;
      case 'export':
        handleExportData();
        break;
      case 'open-url':
        window.open(fullUrl, '_blank');
        break;
      default:
        break;
    }
  };

  const handleExportData = async () => {
    try {
      // Get monitor history data from the last execution time
      const response = await fetch(`/api/monitors/history/${monitor.monitorId}?limit=100`);
      if (!response.ok) throw new Error('Failed to fetch monitor data');
      
      const data = await response.json();
      
      // Create CSV content
      const csvHeader = 'Timestamp,Status Code,Success,Response Time (ms),Agent Region,Target URL\n';
      const csvRows = data.map((record: any) => {
        const timestamp = new Date(record.executedAt).toISOString();
        
        return `${timestamp},${record.responseStatusCode || 'N/A'},${record.success},${record.responseTime || 'N/A'},${record.agentRegion || monitor.agentRegion},${fullUrl}`;
      }).join('\n');
      
      const csvContent = csvHeader + csvRows;
      
      // Create and download file
      const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
      const link = document.createElement('a');
      const url = URL.createObjectURL(blob);
      link.setAttribute('href', url);
      link.setAttribute('download', `monitor-${monitor.monitorName || monitor.monitorId}-${new Date().toISOString().split('T')[0]}.csv`);
      link.style.visibility = 'hidden';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    } catch (error) {
      console.error('Export failed:', error);
      alert('Failed to export data. Please try again.');
    }
  };

  return (
    <Card className={cn(
      "transition-all duration-200 hover:shadow-lg",
      "border-l-4",
      monitor.success && monitor.responseStatusCode && monitor.responseStatusCode < 300 
        ? "border-l-green-500 hover:border-l-green-600" 
        : "border-l-red-500 hover:border-l-red-600"
    )}>
      <CardHeader className="pb-3">
        <div className="flex items-start justify-between">
          <div 
            className="space-y-1 flex-1 min-w-0 cursor-pointer" 
            onClick={onClick}
          >
            <CardTitle className="text-lg font-semibold flex items-center gap-2">
              <div className="flex items-center gap-1">
                {isSecure ? <Lock className="w-4 h-4 text-green-600" /> : <Unlock className="w-4 h-4 text-gray-400" />}
                <Globe className="w-5 h-5 text-blue-500" />
              </div>
              <span className="truncate">{monitor.monitorName || monitor.monitorId}</span>
            </CardTitle>
            <CardDescription className="flex items-center gap-2">
              <MapPin className="w-3 h-3" />
              {monitor.agentRegion}
              <span className="text-muted-foreground">•</span>
              <Badge variant="outline" className="text-xs">
                {monitor.httpMethod || 'GET'}
              </Badge>
            </CardDescription>
          </div>
          <div className="flex items-center gap-2">
            <HTTPStatusBadge 
              status={monitor.responseStatusCode} 
              success={monitor.success} 
              size="sm"
            />
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" size="sm" className="h-8 w-8 p-0">
                  <MoreVertical className="w-4 h-4" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" className="w-48">
                <DropdownMenuItem onClick={(e) => handleMenuAction('view-details', e)}>
                  <ExternalLink className="mr-2 h-4 w-4" />
                  View Details
                </DropdownMenuItem>
                <DropdownMenuItem disabled className="opacity-50">
                  <Settings className="mr-2 h-4 w-4" />
                  Edit Monitor
                </DropdownMenuItem>
                <DropdownMenuItem disabled className="opacity-50">
                  <Copy className="mr-2 h-4 w-4" />
                  Duplicate
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem disabled className="opacity-50">
                  <RefreshCw className="mr-2 h-4 w-4" />
                  Run Test Now
                </DropdownMenuItem>
                <DropdownMenuItem disabled className="opacity-50">
                  <Pause className="mr-2 h-4 w-4" />
                  Pause Monitor
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem disabled className="opacity-50">
                  <Bell className="mr-2 h-4 w-4" />
                  Alert Settings
                </DropdownMenuItem>
                <DropdownMenuItem onClick={(e) => handleMenuAction('export', e)}>
                  <Download className="mr-2 h-4 w-4" />
                  Export Data
                </DropdownMenuItem>
                <DropdownMenuItem onClick={(e) => handleMenuAction('open-url', e)}>
                  <ExternalLink className="mr-2 h-4 w-4" />
                  Open URL
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem disabled className="opacity-50 text-red-600">
                  <Trash2 className="mr-2 h-4 w-4" />
                  Delete Monitor
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        </div>
      </CardHeader>
      <CardContent className="pt-0">
        <div className="space-y-3">
          {/* URL */}
          <div className="flex items-center gap-2 text-sm">
            <span className="font-mono bg-muted px-2 py-1 rounded text-xs break-all">
              {fullUrl}
            </span>
          </div>

          {/* Response Details */}
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2 flex-wrap">
              {monitor.responseTime && (
                <Badge variant="outline" className={cn(
                  "text-xs font-mono",
                  monitor.responseTime < 200 ? "text-green-600" :
                  monitor.responseTime < 500 ? "text-yellow-600" :
                  monitor.responseTime < 1000 ? "text-orange-600" : "text-red-600"
                )}>
                  <Clock className="w-3 h-3 mr-1" />
                  {monitor.responseTime}ms
                </Badge>
              )}

              {/* Activity Status */}
              <Badge variant="outline" className={cn(
                "text-xs font-mono",
                isRecent ? "text-green-600 border-green-200" : "text-amber-600 border-amber-200"
              )}>
                <Activity className="w-3 h-3 mr-1" />
                {isRecent ? 'Active' : `${Math.floor((Date.now() - lastCheck.getTime()) / (1000 * 60))}m ago`}
              </Badge>
              
              {monitor.responseSizeBytes && (
                <Badge variant="outline" className="text-xs">
                  <FileText className="w-3 h-3 mr-1" />
                  {(monitor.responseSizeBytes / 1024).toFixed(1)}KB
                </Badge>
              )}

              {monitor.responseContentType && (
                <Badge variant="outline" className="text-xs">
                  {monitor.responseContentType.split(';')[0]}
                </Badge>
              )}

              {monitor.rawResponseBody && (
                <Badge variant="outline" className="text-xs text-blue-600">
                  <Eye className="w-3 h-3 mr-1" />
                  Body
                </Badge>
              )}
            </div>
            
            <div className="flex items-center gap-1 text-xs text-muted-foreground">
              <Clock className="w-3 h-3" />
              {isRecent ? 'Just now' : lastCheck.toLocaleTimeString()}
            </div>
          </div>

          {/* Server Info */}
          {monitor.responseServer && (
            <div className="flex items-center gap-2 text-xs">
              <Server className="w-3 h-3 text-muted-foreground" />
              <span className="text-muted-foreground">Server:</span>
              <span className="font-mono">{monitor.responseServer}</span>
              {monitor.responseCacheStatus && (
                <Badge variant="outline" className="text-xs ml-auto">
                  Cache: {monitor.responseCacheStatus}
                </Badge>
              )}
            </div>
          )}

          {/* Performance Metrics */}
          <PerformanceMetrics monitor={monitor} />

          {/* Error Message */}
          {monitor.errorMessage && (
            <div className="text-xs text-red-600 dark:text-red-400 bg-red-50 dark:bg-red-900/20 p-2 rounded">
              <span className="font-medium">{monitor.errorType || 'Error'}:</span> {monitor.errorMessage}
            </div>
          )}
        </div>
      </CardContent>
    </Card>
  );
};

const HTTPMonitorsContent: React.FC = () => {
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');
  const [regionFilter, setRegionFilter] = useState('all');
  const [methodFilter, setMethodFilter] = useState('all');
  const [showActiveOnly, setShowActiveOnly] = useState(true);
  const [activeWindow, setActiveWindow] = useState(15); // minutes
  
  // Prepare filters for API call (excluding search - handled client-side for smoother UX)
  const filters = useMemo(() => ({
    ...(regionFilter !== 'all' && { agentRegion: regionFilter }),
    ...(statusFilter === 'success' && { success: true }),
    ...(statusFilter === 'failure' && { success: false }),
    activeOnly: showActiveOnly,
    maxAge: activeWindow,
    limit: 100,
    sortBy: 'executedAt' as const,
    sortOrder: 'desc' as const
  }), [regionFilter, statusFilter, showActiveOnly, activeWindow]);

  const { data: monitors = [], isLoading: loading, error } = useHTTPMonitors(filters);

  // Client-side filtering for all filters including search
  const filteredMonitors = useMemo(() => {
    return monitors.filter(monitor => {
      // Method filter
      if (methodFilter !== 'all' && monitor.httpMethod !== methodFilter) return false;
      
      // Status filters
      if (statusFilter === 'healthy' && (!monitor.success || !monitor.responseStatusCode || monitor.responseStatusCode >= 300)) return false;
      if (statusFilter === 'error' && (monitor.success && monitor.responseStatusCode && monitor.responseStatusCode < 400)) return false;
      if (statusFilter === 'redirect' && (!monitor.responseStatusCode || monitor.responseStatusCode < 300 || monitor.responseStatusCode >= 400)) return false;
      
      // Active monitoring filter - client-side for immediate feedback
      if (showActiveOnly) {
        const monitorTime = new Date(monitor.executedAt).getTime();
        const cutoffTime = Date.now() - (activeWindow * 60 * 1000); // Convert minutes to milliseconds
        if (monitorTime < cutoffTime) return false;
      }
      
      // Search filtering - applied client-side for smooth UX
      if (searchQuery.trim()) {
        const searchLower = searchQuery.toLowerCase().trim();
        
        const matchesHost = monitor.targetHost?.toLowerCase().includes(searchLower);
        const matchesName = monitor.monitorName?.toLowerCase().includes(searchLower);
        const matchesId = monitor.monitorId?.toLowerCase().includes(searchLower);
        const matchesRegion = monitor.agentRegion?.toLowerCase().includes(searchLower);
        const matchesPath = monitor.targetPath?.toLowerCase().includes(searchLower);
        
        if (!matchesHost && !matchesName && !matchesId && !matchesRegion && !matchesPath) {
          return false;
        }
      }
      
      return true;
    });
  }, [monitors, methodFilter, statusFilter, searchQuery, showActiveOnly, activeWindow]);

  const stats = useMemo(() => ({
    total: filteredMonitors.length,
    healthy: filteredMonitors.filter(m => m.success && m.responseStatusCode && m.responseStatusCode < 300).length,
    errors: filteredMonitors.filter(m => !m.success || (m.responseStatusCode && m.responseStatusCode >= 400)).length,
    redirects: filteredMonitors.filter(m => m.responseStatusCode && m.responseStatusCode >= 300 && m.responseStatusCode < 400).length,
    avgResponseTime: Math.round(filteredMonitors.reduce((acc, m) => acc + (m.responseTime || 0), 0) / filteredMonitors.length) || 0
  }), [filteredMonitors]);

  if (loading) {
    return (
      <div className="container mx-auto p-6">
        <div className="space-y-6">
          <div className="flex items-center gap-2">
            <Globe className="w-8 h-8 animate-pulse" />
            <h1 className="text-3xl font-bold">Loading HTTP Monitors...</h1>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            {[...Array(4)].map((_, i) => (
              <Card key={i} className="animate-pulse">
                <CardContent className="p-6">
                  <div className="h-16 bg-muted rounded"></div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container mx-auto p-6">
        <Card className="p-12 text-center">
          <div className="space-y-3">
            <AlertCircle className="w-12 h-12 text-red-500 mx-auto" />
            <h3 className="text-lg font-semibold">Failed to load HTTP monitors</h3>
            <p className="text-muted-foreground">
              {error instanceof Error ? error.message : 'An error occurred while fetching monitor data'}
            </p>
          </div>
        </Card>
      </div>
    );
  }

  return (
    <div className="container mx-auto p-6 space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="space-y-1">
          <h1 className="text-3xl font-bold flex items-center gap-3">
            <Globe className="w-8 h-8 text-blue-500" />
            HTTP(S) Monitors
          </h1>
          <p className="text-muted-foreground">
            HTTP and HTTPS endpoint monitoring with detailed performance metrics
          </p>
        </div>
      </div>

      {/* Stats Overview */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
        <Card>
          <CardContent className="p-4">
            <div className="text-center">
              <p className="text-2xl font-bold">{stats.total}</p>
              <p className="text-xs text-muted-foreground">Total Monitors</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4">
            <div className="text-center">
              <p className="text-2xl font-bold text-green-600">{stats.healthy}</p>
              <p className="text-xs text-muted-foreground">Healthy (2xx)</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4">
            <div className="text-center">
              <p className="text-2xl font-bold text-yellow-600">{stats.redirects}</p>
              <p className="text-xs text-muted-foreground">Redirects (3xx)</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4">
            <div className="text-center">
              <p className="text-2xl font-bold text-red-600">{stats.errors}</p>
              <p className="text-xs text-muted-foreground">Errors (4xx/5xx)</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4">
            <div className="text-center">
              <p className="text-2xl font-bold">{stats.avgResponseTime}ms</p>
              <p className="text-xs text-muted-foreground">Avg Response</p>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Kibana-style Search and Filter Bar */}
      <div className="bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-700 rounded-md mb-4">
        {/* Main Search Line */}
        <div className="flex items-center gap-3 px-4 py-3">
          {/* Add Filter Button */}
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="outline" size="sm" className="h-8 px-3 text-xs flex-shrink-0">
                <Filter className="w-3 h-3 mr-1" />
                Add filter
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="start" className="w-48">
              <DropdownMenuItem onClick={() => setStatusFilter('healthy')}>
                Status: Healthy
              </DropdownMenuItem>
              <DropdownMenuItem onClick={() => setStatusFilter('error')}>
                Status: Error
              </DropdownMenuItem>
              <DropdownMenuItem onClick={() => setMethodFilter('GET')}>
                Method: GET
              </DropdownMenuItem>
              <DropdownMenuItem onClick={() => setMethodFilter('POST')}>
                Method: POST
              </DropdownMenuItem>
              <DropdownMenuItem onClick={() => setRegionFilter('us-east-1')}>
                Region: US East 1
              </DropdownMenuItem>
              <DropdownMenuItem onClick={() => setRegionFilter('us-west-2')}>
                Region: US West 2
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>

          {/* Search Input */}
          <div className="flex-1 relative">
            <div className="flex items-center border border-gray-300 dark:border-gray-600 rounded bg-white dark:bg-gray-800 h-8 px-3">
              <Search className="w-4 h-4 text-gray-400 mr-2 flex-shrink-0" />
              <Input
                placeholder="Search monitors, URLs, paths..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="border-0 p-0 h-auto bg-transparent focus-visible:ring-0 focus-visible:ring-offset-0 placeholder:text-gray-400 text-sm"
              />
            </div>
          </div>

          {/* Time Range */}
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="outline" size="sm" className="h-8 px-3 text-xs flex-shrink-0">
                <Clock className="w-3 h-3 mr-1" />
                {showActiveOnly ? `Last ${activeWindow}m` : 'All time'}
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuItem onClick={() => setShowActiveOnly(false)}>
                All time
              </DropdownMenuItem>
              <DropdownMenuSeparator />
              <DropdownMenuItem onClick={() => { setShowActiveOnly(true); setActiveWindow(5); }}>
                Last 5 minutes
              </DropdownMenuItem>
              <DropdownMenuItem onClick={() => { setShowActiveOnly(true); setActiveWindow(15); }}>
                Last 15 minutes
              </DropdownMenuItem>
              <DropdownMenuItem onClick={() => { setShowActiveOnly(true); setActiveWindow(30); }}>
                Last 30 minutes
              </DropdownMenuItem>
              <DropdownMenuItem onClick={() => { setShowActiveOnly(true); setActiveWindow(60); }}>
                Last 1 hour
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>

          {/* Refresh & Timestamp */}
          <div className="flex items-center gap-2 flex-shrink-0">
            <Button 
              variant="ghost" 
              size="sm" 
              onClick={() => window.location.reload()}
              className="h-6 w-6 p-0 text-gray-500 hover:text-gray-700"
            >
              <RefreshCw className="w-3 h-3" />
            </Button>
            <span className="text-xs text-gray-500 whitespace-nowrap">
              {new Date().toLocaleTimeString()}
            </span>
          </div>
        </div>

        {/* Results Count and Active Filters Section */}
        <div className="px-4 pb-3 border-t border-gray-100 dark:border-gray-800">
          <div className="flex items-center justify-between pt-2">
            <div className="flex items-center gap-2 flex-wrap">
              {(statusFilter !== 'all' || methodFilter !== 'all' || regionFilter !== 'all' || showActiveOnly) && (
                <>
                  <span className="text-xs text-gray-500 mr-1">Active filters:</span>
                  
                  {statusFilter !== 'all' && (
                    <div className="inline-flex items-center gap-1 px-2 py-1 bg-blue-100 dark:bg-blue-900/30 text-blue-800 dark:text-blue-200 rounded text-xs">
                      <span className="font-medium">status:</span>
                      <span>{statusFilter}</span>
                      <button
                        onClick={() => setStatusFilter('all')}
                        className="ml-1 hover:bg-blue-200 dark:hover:bg-blue-800 rounded-sm p-0.5 leading-none"
                      >
                        ×
                      </button>
                    </div>
                  )}
                  
                  {methodFilter !== 'all' && (
                    <div className="inline-flex items-center gap-1 px-2 py-1 bg-green-100 dark:bg-green-900/30 text-green-800 dark:text-green-200 rounded text-xs">
                      <span className="font-medium">method:</span>
                      <span>{methodFilter}</span>
                      <button
                        onClick={() => setMethodFilter('all')}
                        className="ml-1 hover:bg-green-200 dark:hover:bg-green-800 rounded-sm p-0.5 leading-none"
                      >
                        ×
                      </button>
                    </div>
                  )}
                  
                  {regionFilter !== 'all' && (
                    <div className="inline-flex items-center gap-1 px-2 py-1 bg-purple-100 dark:bg-purple-900/30 text-purple-800 dark:text-purple-200 rounded text-xs">
                      <span className="font-medium">region:</span>
                      <span>{regionFilter}</span>
                      <button
                        onClick={() => setRegionFilter('all')}
                        className="ml-1 hover:bg-purple-200 dark:hover:bg-purple-800 rounded-sm p-0.5 leading-none"
                      >
                        ×
                      </button>
                    </div>
                  )}

                  {showActiveOnly && (
                    <div className="inline-flex items-center gap-1 px-2 py-1 bg-orange-100 dark:bg-orange-900/30 text-orange-800 dark:text-orange-200 rounded text-xs">
                      <span className="font-medium">timeRange:</span>
                      <span>last {activeWindow}m</span>
                      <button
                        onClick={() => setShowActiveOnly(false)}
                        className="ml-1 hover:bg-orange-200 dark:hover:bg-orange-800 rounded-sm p-0.5 leading-none"
                      >
                        ×
                      </button>
                    </div>
                  )}

                  {/* Clear All Filters */}
                  <button
                    onClick={() => {
                      setStatusFilter('all');
                      setMethodFilter('all');
                      setRegionFilter('all');
                      setShowActiveOnly(false);
                    }}
                    className="text-xs text-gray-500 hover:text-gray-700 underline ml-2"
                  >
                    Clear all
                  </button>
                </>
              )}
            </div>

            {/* Results Count - Always on Right Side */}
            <div className="text-xs text-gray-500 flex-shrink-0">
              Showing {filteredMonitors.length} of {monitors.length} monitors
            </div>
          </div>
        </div>
      </div>

      {/* Active Monitoring Status */}
      {showActiveOnly && filteredMonitors.length > 0 && (
        <div className="flex items-center gap-2 mb-4 p-3 bg-green-50 dark:bg-green-900/20 rounded-lg border border-green-200 dark:border-green-800">
          <Activity className="w-4 h-4 text-green-600" />
          <span className="text-sm font-medium text-green-800 dark:text-green-200">
            Showing {filteredMonitors.length} active monitors with activity in the last {activeWindow} minutes
          </span>
          <Badge variant="outline" className="ml-auto text-green-700 border-green-300">
            <Clock className="w-3 h-3 mr-1" />
            Updated: {new Date().toLocaleTimeString()}
          </Badge>
        </div>
      )}

      {/* Monitor Grid */}
      <div className="grid grid-cols-1 xl:grid-cols-2 gap-4">
        {filteredMonitors.map(monitor => (
          <HTTPMonitorCard 
            key={monitor.id} 
            monitor={monitor} 
            onClick={() => {
              navigate(`/monitors/${monitor.monitorId}`);
            }}
          />
        ))}
      </div>

      {filteredMonitors.length === 0 && (
        <Card className="p-12 text-center">
          <div className="space-y-3">
            <Search className="w-12 h-12 text-muted-foreground mx-auto" />
            <h3 className="text-lg font-semibold">No HTTP monitors found</h3>
            <p className="text-muted-foreground">
              Try adjusting your filters or search criteria
            </p>
          </div>
        </Card>
      )}
    </div>
  );
};

const HTTPMonitors: React.FC = () => {
  return (
    <DashboardLayout>
      <HTTPMonitorsContent />
    </DashboardLayout>
  );
};

export default HTTPMonitors;