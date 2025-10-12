import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { 
  Network,
  Clock, 
  AlertCircle, 
  CheckCircle, 
  Search, 
  Filter,
  MoreVertical,
  MapPin,
  Zap,
  Timer,
  Lock,
  Unlock,
  Server,
  Activity
} from "lucide-react";
import { cn } from "@/lib/utils";
import { DashboardLayout } from "@/components/dashboard/DashboardLayout";

interface TCPMonitor {
  id: number;
  monitorId: string;
  monitorName: string;
  monitorType: 'TCP';
  targetHost: string;
  targetPort: number;
  
  // Execution Context
  executedAt: string;
  agentId: string;
  agentRegion: string;
  
  // Response Data
  success: boolean;
  responseTime?: number;
  
  // Network Performance
  dnsLookupMs?: number;
  tcpConnectMs?: number;
  tlsHandshakeMs?: number;
  
  // Error Tracking
  errorMessage?: string;
  errorType?: string;
}

const TCPStatusBadge: React.FC<{ 
  success: boolean,
  size?: 'sm' | 'default' | 'lg' 
}> = ({ success, size = 'default' }) => {
  if (!success) {
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

  return (
    <Badge variant="default" className={cn(
      "bg-green-500 hover:bg-green-600 text-white",
      size === 'sm' && "text-xs px-2 py-0.5",
      size === 'lg' && "text-sm px-3 py-1"
    )}>
      <CheckCircle className="w-3 h-3 mr-1" />
      Connected
    </Badge>
  );
};

const TCPPerformanceMetrics: React.FC<{ monitor: TCPMonitor }> = ({ monitor }) => {
  const metrics = [
    { label: 'DNS Lookup', value: monitor.dnsLookupMs, icon: Network },
    { label: 'TCP Connect', value: monitor.tcpConnectMs, icon: Zap },
    { label: 'TLS Handshake', value: monitor.tlsHandshakeMs, icon: Lock },
  ].filter(m => m.value !== undefined && m.value > 0);

  if (metrics.length === 0) return null;

  return (
    <div className="grid grid-cols-1 lg:grid-cols-3 gap-2 mt-3">
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

const TCPMonitorCard: React.FC<{ monitor: TCPMonitor, onClick: () => void }> = ({ monitor, onClick }) => {
  const lastCheck = new Date(monitor.executedAt);
  const isRecent = Date.now() - lastCheck.getTime() < 5 * 60 * 1000; // 5 minutes
  const isSecure = monitor.tlsHandshakeMs && monitor.tlsHandshakeMs > 0;
  
  // Common secure TCP ports
  const commonSecurePorts = [443, 993, 995, 465, 587, 636, 989, 990];
  const likelySecure = commonSecurePorts.includes(monitor.targetPort);

  return (
    <Card className={cn(
      "cursor-pointer transition-all duration-200 hover:shadow-lg hover:scale-[1.02]",
      "border-l-4",
      monitor.success 
        ? "border-l-green-500 hover:border-l-green-600" 
        : "border-l-red-500 hover:border-l-red-600"
    )} onClick={onClick}>
      <CardHeader className="pb-3">
        <div className="flex items-start justify-between">
          <div className="space-y-1 flex-1 min-w-0">
            <CardTitle className="text-lg font-semibold flex items-center gap-2">
              <div className="flex items-center gap-1">
                {isSecure || likelySecure ? <Lock className="w-4 h-4 text-green-600" /> : <Unlock className="w-4 h-4 text-gray-400" />}
                <Network className="w-5 h-5 text-blue-500" />
              </div>
              <span className="truncate">{monitor.monitorName || monitor.monitorId}</span>
            </CardTitle>
            <CardDescription className="flex items-center gap-2">
              <MapPin className="w-3 h-3" />
              {monitor.agentRegion}
              <span className="text-muted-foreground">•</span>
              <Badge variant="outline" className="text-xs">
                TCP
              </Badge>
            </CardDescription>
          </div>
          <div className="flex items-center gap-2">
            <TCPStatusBadge 
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
          {/* Target */}
          <div className="flex items-center gap-2 text-sm">
            <Server className="w-4 h-4 text-muted-foreground" />
            <span className="font-mono bg-muted px-2 py-1 rounded text-xs">
              {monitor.targetHost}:{monitor.targetPort}
            </span>
            {likelySecure && (
              <Badge variant="outline" className="text-xs">
                SSL/TLS
              </Badge>
            )}
          </div>

          {/* Response Time */}
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              {monitor.responseTime && (
                <Badge variant="outline" className={cn(
                  "text-xs font-mono",
                  monitor.responseTime < 100 ? "text-green-600" :
                  monitor.responseTime < 300 ? "text-yellow-600" :
                  monitor.responseTime < 1000 ? "text-orange-600" : "text-red-600"
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

          {/* Performance Metrics */}
          <TCPPerformanceMetrics monitor={monitor} />

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

const TCPMonitorsContent: React.FC = () => {
  const navigate = useNavigate();
  const [monitors, setMonitors] = useState<TCPMonitor[]>([]);
  const [filteredMonitors, setFilteredMonitors] = useState<TCPMonitor[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');
  const [regionFilter, setRegionFilter] = useState('all');
  const [portFilter, setPortFilter] = useState('all');
  const [loading, setLoading] = useState(true);

  // Mock data - replace with real API calls
  useEffect(() => {
    setTimeout(() => {
      const mockData: TCPMonitor[] = [
        {
          id: 1,
          monitorId: 'us-east-1-database',
          monitorName: 'PostgreSQL Database',
          monitorType: 'TCP',
          targetHost: 'db.example.com',
          targetPort: 5432,
          agentId: 'tcp-monitor-us-east-1@us-east-1-agent-01',
          agentRegion: 'us-east-1',
          executedAt: new Date().toISOString(),
          success: true,
          responseTime: 45,
          dnsLookupMs: 12,
          tcpConnectMs: 33
        },
        {
          id: 2,
          monitorId: 'us-west-2-redis',
          monitorName: 'Redis Cache',
          monitorType: 'TCP',
          targetHost: 'cache.example.com',
          targetPort: 6379,
          agentId: 'tcp-monitor-us-west-2@us-west-2-agent-01',
          agentRegion: 'us-west-2',
          executedAt: new Date(Date.now() - 2 * 60 * 1000).toISOString(),
          success: false,
          responseTime: 5000,
          dnsLookupMs: 25,
          tcpConnectMs: 4975,
          errorMessage: 'Connection refused',
          errorType: 'CONNECTION_REFUSED'
        },
        {
          id: 3,
          monitorId: 'eu-west-1-https',
          monitorName: 'HTTPS Service',
          monitorType: 'TCP',
          targetHost: 'secure.example.com',
          targetPort: 443,
          agentId: 'tcp-monitor-eu-west-1@eu-west-1-agent-01',
          agentRegion: 'eu-west-1',
          executedAt: new Date(Date.now() - 1 * 60 * 1000).toISOString(),
          success: true,
          responseTime: 123,
          dnsLookupMs: 8,
          tcpConnectMs: 45,
          tlsHandshakeMs: 70
        },
        {
          id: 4,
          monitorId: 'us-east-1-smtp',
          monitorName: 'SMTP Server',
          monitorType: 'TCP',
          targetHost: 'mail.example.com',
          targetPort: 587,
          agentId: 'tcp-monitor-us-east-1@us-east-1-agent-02',
          agentRegion: 'us-east-1',
          executedAt: new Date(Date.now() - 3 * 60 * 1000).toISOString(),
          success: true,
          responseTime: 67,
          dnsLookupMs: 15,
          tcpConnectMs: 35,
          tlsHandshakeMs: 17
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
        monitor.agentRegion.toLowerCase().includes(searchQuery.toLowerCase()) ||
        monitor.targetPort.toString().includes(searchQuery)
      );
    }

    if (statusFilter !== 'all') {
      filtered = filtered.filter(monitor => {
        if (statusFilter === 'connected') return monitor.success;
        if (statusFilter === 'failed') return !monitor.success;
        return true;
      });
    }

    if (regionFilter !== 'all') {
      filtered = filtered.filter(monitor => monitor.agentRegion === regionFilter);
    }

    if (portFilter !== 'all') {
      const portRanges: Record<string, (port: number) => boolean> = {
        'common': (port) => [22, 23, 25, 53, 80, 110, 143, 443, 993, 995].includes(port),
        'database': (port) => [3306, 5432, 1521, 1433, 27017, 6379].includes(port),
        'mail': (port) => [25, 110, 143, 465, 587, 993, 995].includes(port),
        'web': (port) => [80, 443, 8080, 8443, 3000, 8000].includes(port),
        'high': (port) => port > 1024
      };
      
      if (portRanges[portFilter]) {
        filtered = filtered.filter(monitor => portRanges[portFilter](monitor.targetPort));
      }
    }

    setFilteredMonitors(filtered);
  }, [monitors, searchQuery, statusFilter, regionFilter, portFilter]);

  const stats = {
    total: monitors.length,
    connected: monitors.filter(m => m.success).length,
    failed: monitors.filter(m => !m.success).length,
    avgResponseTime: Math.round(monitors.reduce((acc, m) => acc + (m.responseTime || 0), 0) / monitors.length) || 0
  };

  if (loading) {
    return (
      <div className="container mx-auto p-6">
        <div className="space-y-6">
          <div className="flex items-center gap-2">
            <Network className="w-8 h-8 animate-pulse" />
            <h1 className="text-3xl font-bold">Loading TCP Monitors...</h1>
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
            <Network className="w-8 h-8 text-blue-500" />
            TCP Monitors
          </h1>
          <p className="text-muted-foreground">
            TCP port connectivity monitoring with connection timing metrics
          </p>
        </div>
      </div>

      {/* Stats Overview */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
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
              <p className="text-2xl font-bold text-green-600">{stats.connected}</p>
              <p className="text-xs text-muted-foreground">Connected</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4">
            <div className="text-center">
              <p className="text-2xl font-bold text-red-600">{stats.failed}</p>
              <p className="text-xs text-muted-foreground">Failed</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4">
            <div className="text-center">
              <p className="text-2xl font-bold">{stats.avgResponseTime}ms</p>
              <p className="text-xs text-muted-foreground">Avg Connect Time</p>
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
                  placeholder="Search monitors, hosts, ports..."
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
                  <SelectItem value="connected">Connected</SelectItem>
                  <SelectItem value="failed">Failed</SelectItem>
                </SelectContent>
              </Select>
              <Select value={portFilter} onValueChange={setPortFilter}>
                <SelectTrigger className="w-32">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">All Ports</SelectItem>
                  <SelectItem value="common">Common</SelectItem>
                  <SelectItem value="web">Web (80,443)</SelectItem>
                  <SelectItem value="database">Database</SelectItem>
                  <SelectItem value="mail">Mail</SelectItem>
                  <SelectItem value="high">High (&gt;1024)</SelectItem>
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
          <TCPMonitorCard 
            key={monitor.id} 
            monitor={monitor} 
            onClick={() => {
              navigate(`/monitors/${monitor.id}`);
            }}
          />
        ))}
      </div>

      {filteredMonitors.length === 0 && (
        <Card className="p-12 text-center">
          <div className="space-y-3">
            <Search className="w-12 h-12 text-muted-foreground mx-auto" />
            <h3 className="text-lg font-semibold">No TCP monitors found</h3>
            <p className="text-muted-foreground">
              Try adjusting your filters or search criteria
            </p>
          </div>
        </Card>
      )}
    </div>
  );
};

const TCPMonitors: React.FC = () => {
  return (
    <DashboardLayout>
      <TCPMonitorsContent />
    </DashboardLayout>
  );
};

export default TCPMonitors;