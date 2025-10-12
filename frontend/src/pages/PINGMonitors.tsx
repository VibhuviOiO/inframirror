import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { 
  Zap,
  Clock, 
  AlertCircle, 
  CheckCircle, 
  Search, 
  Filter,
  MoreVertical,
  MapPin,
  Timer,
  TrendingUp,
  TrendingDown,
  Activity,
  Signal
} from "lucide-react";
import { cn } from "@/lib/utils";
import { DashboardLayout } from "@/components/dashboard/DashboardLayout";

interface PINGMonitor {
  id: number;
  monitorId: string;
  monitorName: string;
  monitorType: 'PING';
  targetHost: string;
  
  // Execution Context
  executedAt: string;
  agentId: string;
  agentRegion: string;
  
  // Response Data
  success: boolean;
  responseTime?: number;
  
  // Network Performance
  dnsLookupMs?: number;
  
  // PING-specific metrics
  packetLoss?: number;
  jitterMs?: number;
  
  // Error Tracking
  errorMessage?: string;
  errorType?: string;
}

const PINGStatusBadge: React.FC<{ 
  success: boolean,
  packetLoss?: number,
  size?: 'sm' | 'default' | 'lg' 
}> = ({ success, packetLoss, size = 'default' }) => {
  if (!success) {
    return (
      <Badge variant="destructive" className={cn(
        "animate-pulse",
        size === 'sm' && "text-xs px-2 py-0.5",
        size === 'lg' && "text-sm px-3 py-1"
      )}>
        <AlertCircle className="w-3 h-3 mr-1" />
        Unreachable
      </Badge>
    );
  }

  if (packetLoss !== undefined) {
    if (packetLoss === 0) {
      return (
        <Badge variant="default" className={cn(
          "bg-green-500 hover:bg-green-600 text-white",
          size === 'sm' && "text-xs px-2 py-0.5",
          size === 'lg' && "text-sm px-3 py-1"
        )}>
          <CheckCircle className="w-3 h-3 mr-1" />
          Perfect
        </Badge>
      );
    } else if (packetLoss < 5) {
      return (
        <Badge variant="secondary" className={cn(
          "bg-yellow-500 hover:bg-yellow-600 text-white",
          size === 'sm' && "text-xs px-2 py-0.5",
          size === 'lg' && "text-sm px-3 py-1"
        )}>
          <Activity className="w-3 h-3 mr-1" />
          {packetLoss}% Loss
        </Badge>
      );
    } else {
      return (
        <Badge variant="destructive" className={cn(
          size === 'sm' && "text-xs px-2 py-0.5",
          size === 'lg' && "text-sm px-3 py-1"
        )}>
          <AlertCircle className="w-3 h-3 mr-1" />
          {packetLoss}% Loss
        </Badge>
      );
    }
  }

  return (
    <Badge variant="default" className={cn(
      "bg-green-500 hover:bg-green-600 text-white",
      size === 'sm' && "text-xs px-2 py-0.5",
      size === 'lg' && "text-sm px-3 py-1"
    )}>
      <CheckCircle className="w-3 h-3 mr-1" />
      Reachable
    </Badge>
  );
};

const PINGMetrics: React.FC<{ monitor: PINGMonitor }> = ({ monitor }) => {
  const metrics = [
    { 
      label: 'DNS Lookup', 
      value: monitor.dnsLookupMs, 
      icon: Signal,
      unit: 'ms'
    },
    { 
      label: 'Packet Loss', 
      value: monitor.packetLoss, 
      icon: TrendingDown,
      unit: '%',
      color: monitor.packetLoss === 0 ? 'text-green-600' : 
             monitor.packetLoss && monitor.packetLoss < 5 ? 'text-yellow-600' : 'text-red-600'
    },
    { 
      label: 'Jitter', 
      value: monitor.jitterMs, 
      icon: Activity,
      unit: 'ms',
      color: monitor.jitterMs && monitor.jitterMs < 10 ? 'text-green-600' : 
             monitor.jitterMs && monitor.jitterMs < 30 ? 'text-yellow-600' : 'text-red-600'
    },
  ].filter(m => m.value !== undefined);

  if (metrics.length === 0) return null;

  return (
    <div className="grid grid-cols-1 lg:grid-cols-3 gap-2 mt-3">
      {metrics.map((metric) => {
        const Icon = metric.icon;
        return (
          <div key={metric.label} className="flex items-center gap-1 text-xs bg-muted/50 px-2 py-1 rounded">
            <Icon className="w-3 h-3" />
            <span className="text-muted-foreground">{metric.label}:</span>
            <span className={cn("font-mono font-medium", metric.color || "text-foreground")}>
              {metric.value}{metric.unit}
            </span>
          </div>
        );
      })}
    </div>
  );
};

const PINGMonitorCard: React.FC<{ monitor: PINGMonitor, onClick: () => void }> = ({ monitor, onClick }) => {
  const lastCheck = new Date(monitor.executedAt);
  const isRecent = Date.now() - lastCheck.getTime() < 5 * 60 * 1000; // 5 minutes

  return (
    <Card className={cn(
      "cursor-pointer transition-all duration-200 hover:shadow-lg hover:scale-[1.02]",
      "border-l-4",
      monitor.success && (!monitor.packetLoss || monitor.packetLoss < 5)
        ? "border-l-green-500 hover:border-l-green-600" 
        : "border-l-red-500 hover:border-l-red-600"
    )} onClick={onClick}>
      <CardHeader className="pb-3">
        <div className="flex items-start justify-between">
          <div className="space-y-1 flex-1 min-w-0">
            <CardTitle className="text-lg font-semibold flex items-center gap-2">
              <Zap className="w-5 h-5 text-orange-500" />
              <span className="truncate">{monitor.monitorName || monitor.monitorId}</span>
            </CardTitle>
            <CardDescription className="flex items-center gap-2">
              <MapPin className="w-3 h-3" />
              {monitor.agentRegion}
              <span className="text-muted-foreground">•</span>
              <Badge variant="outline" className="text-xs">
                ICMP
              </Badge>
            </CardDescription>
          </div>
          <div className="flex items-center gap-2">
            <PINGStatusBadge 
              success={monitor.success} 
              packetLoss={monitor.packetLoss}
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
          {/* Target Host */}
          <div className="flex items-center gap-2 text-sm">
            <Signal className="w-4 h-4 text-muted-foreground" />
            <span className="font-mono bg-muted px-2 py-1 rounded text-xs">
              {monitor.targetHost}
            </span>
          </div>

          {/* Response Time */}
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              {monitor.responseTime && (
                <Badge variant="outline" className={cn(
                  "text-xs font-mono",
                  monitor.responseTime < 50 ? "text-green-600" :
                  monitor.responseTime < 100 ? "text-yellow-600" :
                  monitor.responseTime < 200 ? "text-orange-600" : "text-red-600"
                )}>
                  <Clock className="w-3 h-3 mr-1" />
                  {monitor.responseTime}ms
                </Badge>
              )}
            </div>
            
            <div className="flex items-center gap-1 text-xs text-muted-foreground">
              <Clock className="w-3 h-3" />
              {isRecent ? 'Just now' : lastCheck.toLocaleTimeString()}
            </div>
          </div>

          {/* PING Metrics */}
          <PINGMetrics monitor={monitor} />

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

const PINGMonitorsContent: React.FC = () => {
  const navigate = useNavigate();
  const [monitors, setMonitors] = useState<PINGMonitor[]>([]);
  const [filteredMonitors, setFilteredMonitors] = useState<PINGMonitor[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');
  const [regionFilter, setRegionFilter] = useState('all');
  const [performanceFilter, setPerformanceFilter] = useState('all');
  const [loading, setLoading] = useState(true);

  // Mock data - replace with real API calls
  useEffect(() => {
    setTimeout(() => {
      const mockData: PINGMonitor[] = [
        {
          id: 1,
          monitorId: 'us-east-1-gateway',
          monitorName: 'Default Gateway',
          monitorType: 'PING',
          targetHost: '8.8.8.8',
          agentId: 'ping-monitor-us-east-1@us-east-1-agent-01',
          agentRegion: 'us-east-1',
          executedAt: new Date().toISOString(),
          success: true,
          responseTime: 23,
          dnsLookupMs: 5,
          packetLoss: 0,
          jitterMs: 2
        },
        {
          id: 2,
          monitorId: 'us-west-2-dns',
          monitorName: 'Cloudflare DNS',
          monitorType: 'PING',
          targetHost: '1.1.1.1',
          agentId: 'ping-monitor-us-west-2@us-west-2-agent-01',
          agentRegion: 'us-west-2',
          executedAt: new Date(Date.now() - 1 * 60 * 1000).toISOString(),
          success: true,
          responseTime: 45,
          dnsLookupMs: 8,
          packetLoss: 0,
          jitterMs: 5
        },
        {
          id: 3,
          monitorId: 'eu-west-1-server',
          monitorName: 'Application Server',
          monitorType: 'PING',
          targetHost: 'app.example.com',
          agentId: 'ping-monitor-eu-west-1@eu-west-1-agent-01',
          agentRegion: 'eu-west-1',
          executedAt: new Date(Date.now() - 2 * 60 * 1000).toISOString(),
          success: true,
          responseTime: 156,
          dnsLookupMs: 15,
          packetLoss: 2.5,
          jitterMs: 25
        },
        {
          id: 4,
          monitorId: 'us-east-1-unreachable',
          monitorName: 'Unreachable Host',
          monitorType: 'PING',
          targetHost: '192.168.255.255',
          agentId: 'ping-monitor-us-east-1@us-east-1-agent-02',
          agentRegion: 'us-east-1',
          executedAt: new Date(Date.now() - 3 * 60 * 1000).toISOString(),
          success: false,
          dnsLookupMs: 12,
          packetLoss: 100,
          errorMessage: 'Destination host unreachable',
          errorType: 'HOST_UNREACHABLE'
        }
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
        if (statusFilter === 'reachable') return monitor.success && (!monitor.packetLoss || monitor.packetLoss < 5);
        if (statusFilter === 'degraded') return monitor.success && monitor.packetLoss && monitor.packetLoss >= 5;
        if (statusFilter === 'unreachable') return !monitor.success || (monitor.packetLoss === 100);
        return true;
      });
    }

    if (regionFilter !== 'all') {
      filtered = filtered.filter(monitor => monitor.agentRegion === regionFilter);
    }

    if (performanceFilter !== 'all') {
      filtered = filtered.filter(monitor => {
        if (!monitor.responseTime) return false;
        if (performanceFilter === 'excellent') return monitor.responseTime < 50;
        if (performanceFilter === 'good') return monitor.responseTime >= 50 && monitor.responseTime < 100;
        if (performanceFilter === 'fair') return monitor.responseTime >= 100 && monitor.responseTime < 200;
        if (performanceFilter === 'poor') return monitor.responseTime >= 200;
        return true;
      });
    }

    setFilteredMonitors(filtered);
  }, [monitors, searchQuery, statusFilter, regionFilter, performanceFilter]);

  const stats = {
    total: monitors.length,
    reachable: monitors.filter(m => m.success && (!m.packetLoss || m.packetLoss < 5)).length,
    degraded: monitors.filter(m => m.success && m.packetLoss && m.packetLoss >= 5).length,
    unreachable: monitors.filter(m => !m.success || m.packetLoss === 100).length,
    avgResponseTime: Math.round(monitors.filter(m => m.responseTime).reduce((acc, m) => acc + (m.responseTime || 0), 0) / monitors.filter(m => m.responseTime).length) || 0,
    avgPacketLoss: Math.round((monitors.filter(m => m.packetLoss !== undefined).reduce((acc, m) => acc + (m.packetLoss || 0), 0) / monitors.filter(m => m.packetLoss !== undefined).length) * 10) / 10 || 0
  };

  if (loading) {
    return (
      <div className="container mx-auto p-6">
        <div className="space-y-6">
          <div className="flex items-center gap-2">
            <Zap className="w-8 h-8 animate-pulse" />
            <h1 className="text-3xl font-bold">Loading PING Monitors...</h1>
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
            <Zap className="w-8 h-8 text-orange-500" />
            PING Monitors
          </h1>
          <p className="text-muted-foreground">
            ICMP ping monitoring with packet loss and jitter analysis
          </p>
        </div>
      </div>

      {/* Stats Overview */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-6 gap-4">
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
              <p className="text-2xl font-bold text-green-600">{stats.reachable}</p>
              <p className="text-xs text-muted-foreground">Reachable</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4">
            <div className="text-center">
              <p className="text-2xl font-bold text-yellow-600">{stats.degraded}</p>
              <p className="text-xs text-muted-foreground">Degraded</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4">
            <div className="text-center">
              <p className="text-2xl font-bold text-red-600">{stats.unreachable}</p>
              <p className="text-xs text-muted-foreground">Unreachable</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4">
            <div className="text-center">
              <p className="text-2xl font-bold">{stats.avgResponseTime}ms</p>
              <p className="text-xs text-muted-foreground">Avg RTT</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4">
            <div className="text-center">
              <p className="text-2xl font-bold">{stats.avgPacketLoss}%</p>
              <p className="text-xs text-muted-foreground">Avg Loss</p>
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
                  placeholder="Search monitors, hosts, regions..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-10"
                />
              </div>
            </div>
            <div className="flex gap-2">
              <Select value={statusFilter} onValueChange={setStatusFilter}>
                <SelectTrigger className="w-36">
                  <Filter className="w-4 h-4 mr-2" />
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">All Status</SelectItem>
                  <SelectItem value="reachable">Reachable</SelectItem>
                  <SelectItem value="degraded">Degraded</SelectItem>
                  <SelectItem value="unreachable">Unreachable</SelectItem>
                </SelectContent>
              </Select>
              <Select value={performanceFilter} onValueChange={setPerformanceFilter}>
                <SelectTrigger className="w-36">
                  <Timer className="w-4 h-4 mr-2" />
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">All Performance</SelectItem>
                  <SelectItem value="excellent">&lt;50ms</SelectItem>
                  <SelectItem value="good">50-100ms</SelectItem>
                  <SelectItem value="fair">100-200ms</SelectItem>
                  <SelectItem value="poor">&gt;200ms</SelectItem>
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
          <PINGMonitorCard 
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
            <h3 className="text-lg font-semibold">No PING monitors found</h3>
            <p className="text-muted-foreground">
              Try adjusting your filters or search criteria
            </p>
          </div>
        </Card>
      )}
    </div>
  );
};

const PINGMonitors: React.FC = () => {
  return (
    <DashboardLayout>
      <PINGMonitorsContent />
    </DashboardLayout>
  );
};

export default PINGMonitors;