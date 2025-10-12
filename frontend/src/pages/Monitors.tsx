import React, { useState, useEffect } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { 
  Activity, 
  Globe, 
  Clock, 
  AlertCircle, 
  CheckCircle, 
  Search, 
  Filter,
  MoreVertical,
  TrendingUp,
  MapPin,
  Zap
} from "lucide-react";
import { cn } from "@/lib/utils";
import { DashboardLayout } from "@/components/dashboard/DashboardLayout";

interface Monitor {
  id: number;
  monitorId: string;
  monitorName: string;
  monitorType: 'HTTP' | 'TCP' | 'UDP' | 'PING' | 'DNS';
  targetHost: string;
  targetPort?: number;
  targetPath?: string;
  httpMethod?: string;
  agentId: string;
  agentRegion: string;
  executedAt: string;
  success: boolean;
  responseTime: number;
  responseStatusCode?: number;
  errorMessage?: string;
}

const MonitorStatusBadge: React.FC<{ 
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
        Down
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
        Healthy
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
        Redirect
      </Badge>
    );
  }

  return (
    <Badge variant="destructive" className={cn(
      size === 'sm' && "text-xs px-2 py-0.5",
      size === 'lg' && "text-sm px-3 py-1"
    )}>
      <AlertCircle className="w-3 h-3 mr-1" />
      Error
    </Badge>
  );
};

const ResponseTimeBadge: React.FC<{ responseTime: number }> = ({ responseTime }) => {
  const getColor = () => {
    if (responseTime < 200) return "bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200";
    if (responseTime < 500) return "bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200";
    if (responseTime < 1000) return "bg-orange-100 text-orange-800 dark:bg-orange-900 dark:text-orange-200";
    return "bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200";
  };

  return (
    <Badge variant="outline" className={cn("font-mono text-xs", getColor())}>
      <Clock className="w-3 h-3 mr-1" />
      {responseTime}ms
    </Badge>
  );
};

const MonitorCard: React.FC<{ monitor: Monitor, onClick: () => void }> = ({ monitor, onClick }) => {
  const lastCheck = new Date(monitor.executedAt);
  const isRecent = Date.now() - lastCheck.getTime() < 5 * 60 * 1000; // 5 minutes

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
          <div className="space-y-1">
            <CardTitle className="text-lg font-semibold flex items-center gap-2">
              <Globe className="w-5 h-5 text-blue-500" />
              {monitor.monitorName || monitor.monitorId}
            </CardTitle>
            <CardDescription className="flex items-center gap-2">
              <MapPin className="w-3 h-3" />
              {monitor.agentRegion}
              <span className="text-muted-foreground">•</span>
              <Badge variant="outline" className="text-xs">
                {monitor.monitorType}
              </Badge>
            </CardDescription>
          </div>
          <div className="flex items-center gap-2">
            <MonitorStatusBadge 
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
          {/* URL/Endpoint */}
          <div className="flex items-center gap-2 text-sm">
            <span className="font-mono bg-muted px-2 py-1 rounded text-xs">
              {monitor.httpMethod || 'GET'} {monitor.targetHost}
              {monitor.targetPort && monitor.targetPort !== 80 && monitor.targetPort !== 443 ? `:${monitor.targetPort}` : ''}
              {monitor.targetPath || '/'}
            </span>
          </div>

          {/* Metrics */}
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <ResponseTimeBadge responseTime={monitor.responseTime} />
              {monitor.responseStatusCode && (
                <Badge variant="outline" className="text-xs font-mono">
                  {monitor.responseStatusCode}
                </Badge>
              )}
            </div>
            <div className="flex items-center gap-1 text-xs text-muted-foreground">
              <Clock className="w-3 h-3" />
              {isRecent ? 'Just now' : lastCheck.toLocaleTimeString()}
            </div>
          </div>

          {/* Error Message */}
          {monitor.errorMessage && (
            <div className="text-xs text-red-600 dark:text-red-400 bg-red-50 dark:bg-red-900/20 p-2 rounded">
              {monitor.errorMessage}
            </div>
          )}
        </div>
      </CardContent>
    </Card>
  );
};

const StatCard: React.FC<{ 
  title: string, 
  value: string | number, 
  change?: string, 
  icon: React.ReactNode,
  trend?: 'up' | 'down' | 'stable'
}> = ({ title, value, change, icon, trend }) => {
  return (
    <Card>
      <CardContent className="p-6">
        <div className="flex items-center justify-between">
          <div className="space-y-2">
            <p className="text-sm font-medium text-muted-foreground">{title}</p>
            <p className="text-2xl font-bold">{value}</p>
            {change && (
              <p className={cn(
                "text-xs flex items-center gap-1",
                trend === 'up' ? "text-green-600" : trend === 'down' ? "text-red-600" : "text-muted-foreground"
              )}>
                {trend === 'up' && <TrendingUp className="w-3 h-3" />}
                {change}
              </p>
            )}
          </div>
          <div className="text-muted-foreground">
            {icon}
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

const MonitorsContent: React.FC = () => {
  const [monitors, setMonitors] = useState<Monitor[]>([]);
  const [filteredMonitors, setFilteredMonitors] = useState<Monitor[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');
  const [regionFilter, setRegionFilter] = useState('all');
  const [loading, setLoading] = useState(true);

  // Mock data - replace with real API calls
  useEffect(() => {
    // Simulate API call
    setTimeout(() => {
      const mockData: Monitor[] = [
        {
          id: 1,
          monitorId: 'us-east-1-build-info',
          monitorName: 'Build Info API',
          monitorType: 'HTTP',
          targetHost: 'api.example.com',
          targetPort: 443,
          targetPath: '/v1/build-info',
          httpMethod: 'GET',
          agentId: 'api-monitor-us-east-1@us-east-1-agent-01',
          agentRegion: 'us-east-1',
          executedAt: new Date().toISOString(),
          success: true,
          responseTime: 245,
          responseStatusCode: 200
        },
        {
          id: 2,
          monitorId: 'us-west-2-category-recs',
          monitorName: 'Category Recommendations',
          monitorType: 'HTTP',
          targetHost: 'api.example.com',
          targetPort: 443,
          targetPath: '/v1/recommendations/category',
          httpMethod: 'POST',
          agentId: 'api-monitor-us-west-2@us-west-2-agent-01',
          agentRegion: 'us-west-2',
          executedAt: new Date(Date.now() - 2 * 60 * 1000).toISOString(),
          success: false,
          responseTime: 5000,
          responseStatusCode: 404,
          errorMessage: 'API endpoint not found'
        },
        // Add more mock data as needed
      ];
      setMonitors(mockData);
      setFilteredMonitors(mockData);
      setLoading(false);
    }, 1000);
  }, []);

  // Filter logic
  useEffect(() => {
    let filtered = monitors;

    if (searchQuery) {
      filtered = filtered.filter(monitor => 
        monitor.monitorName?.toLowerCase().includes(searchQuery.toLowerCase()) ||
        monitor.targetHost.toLowerCase().includes(searchQuery.toLowerCase()) ||
        monitor.agentRegion.toLowerCase().includes(searchQuery.toLowerCase())
      );
    }

    if (statusFilter !== 'all') {
      filtered = filtered.filter(monitor => {
        if (statusFilter === 'healthy') return monitor.success && monitor.responseStatusCode && monitor.responseStatusCode < 300;
        if (statusFilter === 'error') return !monitor.success || (monitor.responseStatusCode && monitor.responseStatusCode >= 400);
        if (statusFilter === 'warning') return monitor.responseStatusCode && monitor.responseStatusCode >= 300 && monitor.responseStatusCode < 400;
        return true;
      });
    }

    if (regionFilter !== 'all') {
      filtered = filtered.filter(monitor => monitor.agentRegion === regionFilter);
    }

    setFilteredMonitors(filtered);
  }, [monitors, searchQuery, statusFilter, regionFilter]);

  const stats = {
    total: monitors.length,
    healthy: monitors.filter(m => m.success && m.responseStatusCode && m.responseStatusCode < 300).length,
    errors: monitors.filter(m => !m.success || (m.responseStatusCode && m.responseStatusCode >= 400)).length,
    avgResponseTime: Math.round(monitors.reduce((acc, m) => acc + m.responseTime, 0) / monitors.length) || 0
  };

  if (loading) {
    return (
      <div className="container mx-auto p-6">
        <div className="space-y-6">
          <div className="flex items-center gap-2">
            <Activity className="w-8 h-8 animate-pulse" />
            <h1 className="text-3xl font-bold">Loading Monitors...</h1>
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

  return (
    <div className="container mx-auto p-6 space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="space-y-1">
          <h1 className="text-3xl font-bold flex items-center gap-3">
            <Activity className="w-8 h-8 text-blue-500" />
            HTTP Monitors
          </h1>
          <p className="text-muted-foreground">
            Real-time API endpoint monitoring across multiple regions
          </p>
        </div>
      </div>

      {/* Stats Overview */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Total Monitors"
          value={stats.total}
          icon={<Globe className="w-6 h-6" />}
        />
        <StatCard
          title="Healthy"
          value={stats.healthy}
          change="+2 from last hour"
          trend="up"
          icon={<CheckCircle className="w-6 h-6 text-green-500" />}
        />
        <StatCard
          title="Errors"
          value={stats.errors}
          change={stats.errors > 0 ? "Needs attention" : "All clear"}
          trend={stats.errors > 0 ? "down" : "stable"}
          icon={<AlertCircle className="w-6 h-6 text-red-500" />}
        />
        <StatCard
          title="Avg Response"
          value={`${stats.avgResponseTime}ms`}
          change="12ms faster"
          trend="up"
          icon={<Zap className="w-6 h-6 text-yellow-500" />}
        />
      </div>

      {/* Filters */}
      <Card>
        <CardContent className="p-4">
          <div className="flex flex-col md:flex-row gap-4">
            <div className="flex-1">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-4 h-4" />
                <Input
                  placeholder="Search monitors, endpoints, or regions..."
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
                  <SelectItem value="error">Errors</SelectItem>
                  <SelectItem value="warning">Warnings</SelectItem>
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
      <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-4">
        {filteredMonitors.map(monitor => (
          <MonitorCard 
            key={monitor.id} 
            monitor={monitor} 
            onClick={() => {
              // Navigate to monitor detail page
              console.log('Navigate to monitor:', monitor.id);
            }}
          />
        ))}
      </div>

      {filteredMonitors.length === 0 && (
        <Card className="p-12 text-center">
          <div className="space-y-3">
            <Search className="w-12 h-12 text-muted-foreground mx-auto" />
            <h3 className="text-lg font-semibold">No monitors found</h3>
            <p className="text-muted-foreground">
              Try adjusting your filters or search criteria
            </p>
          </div>
        </Card>
      )}
    </div>
  );
};

const Monitors: React.FC = () => {
  return (
    <DashboardLayout>
      <MonitorsContent />
    </DashboardLayout>
  );
};

export default Monitors;