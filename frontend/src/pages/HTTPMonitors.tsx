import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
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
  Activity
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
  const isSecure = monitor.targetPort === 443 || monitor.targetHost.startsWith('https://');
  
  const fullUrl = `${isSecure ? 'https' : 'http'}://${monitor.targetHost}${
    monitor.targetPort && monitor.targetPort !== 80 && monitor.targetPort !== 443 ? `:${monitor.targetPort}` : ''
  }${monitor.targetPath || '/'}`;

  return (
    <Card className={cn(
      "cursor-pointer transition-all duration-200 hover:shadow-lg hover:scale-[1.02]",
      "border-l-4",
      monitor.success && monitor.responseStatusCode && monitor.responseStatusCode < 300 
        ? "border-l-green-500 hover:border-l-green-600" 
        : "border-l-red-500 hover:border-l-red-600"
    )} onClick={onClick}>
      <CardHeader className="pb-3">
        <div className="flex items-start justify-between">
          <div className="space-y-1 flex-1 min-w-0">
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
            <Button variant="ghost" size="sm" className="h-8 w-8 p-0">
              <MoreVertical className="w-4 h-4" />
            </Button>
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
  
  // Prepare filters for API call
  const filters = {
    ...(regionFilter !== 'all' && { agentRegion: regionFilter }),
    ...(statusFilter === 'success' && { success: true }),
    ...(statusFilter === 'failure' && { success: false }),
    ...(searchQuery && { targetHost: searchQuery }),
    limit: 100,
    sortBy: 'executedAt' as const,
    sortOrder: 'desc' as const
  };

  const { data: monitors = [], isLoading: loading, error } = useHTTPMonitors(filters);

  // Client-side filtering for additional filters not handled by API
  const filteredMonitors = monitors.filter(monitor => {
    if (methodFilter !== 'all' && monitor.httpMethod !== methodFilter) return false;
    if (statusFilter === 'healthy' && (!monitor.success || !monitor.responseStatusCode || monitor.responseStatusCode >= 300)) return false;
    if (statusFilter === 'error' && (monitor.success && monitor.responseStatusCode && monitor.responseStatusCode < 400)) return false;
    if (statusFilter === 'redirect' && (!monitor.responseStatusCode || monitor.responseStatusCode < 300 || monitor.responseStatusCode >= 400)) return false;
    return true;
  });

  const stats = {
    total: monitors.length,
    healthy: monitors.filter(m => m.success && m.responseStatusCode && m.responseStatusCode < 300).length,
    errors: monitors.filter(m => !m.success || (m.responseStatusCode && m.responseStatusCode >= 400)).length,
    redirects: monitors.filter(m => m.responseStatusCode && m.responseStatusCode >= 300 && m.responseStatusCode < 400).length,
    avgResponseTime: Math.round(monitors.reduce((acc, m) => acc + (m.responseTime || 0), 0) / monitors.length) || 0
  };

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

      {/* Filters */}
      <Card>
        <CardContent className="p-4">
          <div className="flex flex-col lg:flex-row gap-4">
            <div className="flex-1">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-4 h-4" />
                <Input
                  placeholder="Search monitors, URLs, regions..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-10"
                />
              </div>
            </div>
            <div className="flex gap-2">
              <Select value={statusFilter} onValueChange={setStatusFilter}>
                <SelectTrigger className="w-32">
                  <Filter className="w-4 h-4 mr-2" />
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">All Status</SelectItem>
                  <SelectItem value="healthy">Healthy</SelectItem>
                  <SelectItem value="redirect">Redirects</SelectItem>
                  <SelectItem value="error">Errors</SelectItem>
                </SelectContent>
              </Select>
              <Select value={methodFilter} onValueChange={setMethodFilter}>
                <SelectTrigger className="w-32">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">All Methods</SelectItem>
                  <SelectItem value="GET">GET</SelectItem>
                  <SelectItem value="POST">POST</SelectItem>
                  <SelectItem value="PUT">PUT</SelectItem>
                  <SelectItem value="DELETE">DELETE</SelectItem>
                </SelectContent>
              </Select>
              <Select value={regionFilter} onValueChange={setRegionFilter}>
                <SelectTrigger className="w-32">
                  <MapPin className="w-4 h-4 mr-2" />
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">All Regions</SelectItem>
                  <SelectItem value="us-east-1">US East 1</SelectItem>
                  <SelectItem value="us-west-2">US West 2</SelectItem>
                  <SelectItem value="eu-west-1">EU West 1</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
        </CardContent>
      </Card>

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