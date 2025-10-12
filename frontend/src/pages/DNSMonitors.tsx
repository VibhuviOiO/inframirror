import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { 
  Globe,
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
  Signal,
  Hash
} from "lucide-react";
import { cn } from "@/lib/utils";
import { DashboardLayout } from "@/components/dashboard/DashboardLayout";

interface DNSMonitor {
  id: number;
  monitorId: string;
  monitorName: string;
  monitorType: 'DNS';
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
  
  // DNS-specific fields
  dnsQueryType?: string;
  dnsExpectedResponse?: string;
  dnsResponseValue?: string;
  
  // Error Tracking
  errorMessage?: string;
  errorType?: string;
}

const DNSStatusBadge: React.FC<{ 
  success: boolean,
  expected?: string,
  actual?: string,
  size?: 'sm' | 'default' | 'lg' 
}> = ({ success, expected, actual, size = 'default' }) => {
  if (!success) {
    return (
      <Badge variant="destructive" className={cn(
        "animate-pulse",
        size === 'sm' && "text-xs px-2 py-0.5",
        size === 'lg' && "text-sm px-3 py-1"
      )}>
        <AlertCircle className="w-3 h-3 mr-1" />
        Failed
      </Badge>
    );
  }

  // Check if response matches expected
  if (expected && actual) {
    const matches = expected.toLowerCase() === actual.toLowerCase();
    return (
      <Badge variant={matches ? "default" : "secondary"} className={cn(
        matches ? "bg-green-500 hover:bg-green-600 text-white" : "bg-yellow-500 hover:bg-yellow-600 text-white",
        size === 'sm' && "text-xs px-2 py-0.5",
        size === 'lg' && "text-sm px-3 py-1"
      )}>
        <CheckCircle className="w-3 h-3 mr-1" />
        {matches ? 'Matched' : 'Unexpected'}
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
      Resolved
    </Badge>
  );
};

const DNSMetrics: React.FC<{ monitor: DNSMonitor }> = ({ monitor }) => {
  const metrics = [
    { 
      label: 'Query Type', 
      value: monitor.dnsQueryType, 
      icon: Hash,
      unit: '',
      color: 'text-blue-600'
    },
    { 
      label: 'DNS Lookup', 
      value: monitor.dnsLookupMs, 
      icon: Signal,
      unit: 'ms',
      color: monitor.dnsLookupMs && monitor.dnsLookupMs < 50 ? 'text-green-600' : 
             monitor.dnsLookupMs && monitor.dnsLookupMs < 100 ? 'text-yellow-600' : 'text-red-600'
    },
  ].filter(m => m.value !== undefined);

  if (metrics.length === 0) return null;

  return (
    <div className="grid grid-cols-1 lg:grid-cols-2 gap-2 mt-3">
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

const DNSMonitorCard: React.FC<{ monitor: DNSMonitor, onClick: () => void }> = ({ monitor, onClick }) => {
  const lastCheck = new Date(monitor.executedAt);
  const isRecent = Date.now() - lastCheck.getTime() < 5 * 60 * 1000; // 5 minutes
  const responseMatches = monitor.dnsExpectedResponse && monitor.dnsResponseValue && 
    monitor.dnsExpectedResponse.toLowerCase() === monitor.dnsResponseValue.toLowerCase();

  return (
    <Card className={cn(
      "cursor-pointer transition-all duration-200 hover:shadow-lg hover:scale-[1.02]",
      "border-l-4",
      monitor.success && (!monitor.dnsExpectedResponse || responseMatches)
        ? "border-l-green-500 hover:border-l-green-600" 
        : monitor.success && monitor.dnsExpectedResponse && !responseMatches
        ? "border-l-yellow-500 hover:border-l-yellow-600"
        : "border-l-red-500 hover:border-l-red-600"
    )} onClick={onClick}>
      <CardHeader className="pb-3">
        <div className="flex items-start justify-between">
          <div className="space-y-1 flex-1 min-w-0">
            <CardTitle className="text-lg font-semibold flex items-center gap-2">
              <Globe className="w-5 h-5 text-blue-500" />
              <span className="truncate">{monitor.monitorName || monitor.monitorId}</span>
            </CardTitle>
            <CardDescription className="flex items-center gap-2">
              <MapPin className="w-3 h-3" />
              {monitor.agentRegion}
              <span className="text-muted-foreground">•</span>
              <Badge variant="outline" className="text-xs">
                {monitor.dnsQueryType || 'DNS'}
              </Badge>
            </CardDescription>
          </div>
          <div className="flex items-center gap-2">
            <DNSStatusBadge 
              success={monitor.success} 
              expected={monitor.dnsExpectedResponse}
              actual={monitor.dnsResponseValue}
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

          {/* DNS Response */}
          {monitor.dnsResponseValue && (
            <div className="space-y-2">
              <div className="text-xs text-muted-foreground">Response:</div>
              <div className="font-mono text-xs bg-muted px-2 py-1 rounded break-all">
                {monitor.dnsResponseValue}
              </div>
              {monitor.dnsExpectedResponse && (
                <div>
                  <div className="text-xs text-muted-foreground">Expected:</div>
                  <div className="font-mono text-xs bg-muted/50 px-2 py-1 rounded break-all">
                    {monitor.dnsExpectedResponse}
                  </div>
                </div>
              )}
            </div>
          )}

          {/* DNS Metrics */}
          <DNSMetrics monitor={monitor} />

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

const DNSMonitorsContent: React.FC = () => {
  const navigate = useNavigate();
  const [monitors, setMonitors] = useState<DNSMonitor[]>([]);
  const [filteredMonitors, setFilteredMonitors] = useState<DNSMonitor[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');
  const [regionFilter, setRegionFilter] = useState('all');
  const [queryTypeFilter, setQueryTypeFilter] = useState('all');
  const [performanceFilter, setPerformanceFilter] = useState('all');
  const [loading, setLoading] = useState(true);

  // Mock data - replace with real API calls
  useEffect(() => {
    setTimeout(() => {
      const mockData: DNSMonitor[] = [
        {
          id: 1,
          monitorId: 'us-east-1-google-dns',
          monitorName: 'Google DNS A Record',
          monitorType: 'DNS',
          targetHost: 'google.com',
          agentId: 'dns-monitor-us-east-1@us-east-1-agent-01',
          agentRegion: 'us-east-1',
          executedAt: new Date().toISOString(),
          success: true,
          responseTime: 35,
          dnsLookupMs: 23,
          dnsQueryType: 'A',
          dnsExpectedResponse: '142.250.191.46',
          dnsResponseValue: '142.250.191.46'
        },
        {
          id: 2,
          monitorId: 'us-west-2-mx-record',
          monitorName: 'Email MX Record',
          monitorType: 'DNS',
          targetHost: 'example.com',
          agentId: 'dns-monitor-us-west-2@us-west-2-agent-01',
          agentRegion: 'us-west-2',
          executedAt: new Date(Date.now() - 1 * 60 * 1000).toISOString(),
          success: true,
          responseTime: 52,
          dnsLookupMs: 45,
          dnsQueryType: 'MX',
          dnsExpectedResponse: 'mail.example.com',
          dnsResponseValue: 'mail.example.com'
        },
        {
          id: 3,
          monitorId: 'eu-west-1-txt-record',
          monitorName: 'SPF TXT Record',
          monitorType: 'DNS',
          targetHost: 'example.org',
          agentId: 'dns-monitor-eu-west-1@eu-west-1-agent-01',
          agentRegion: 'eu-west-1',
          executedAt: new Date(Date.now() - 2 * 60 * 1000).toISOString(),
          success: true,
          responseTime: 78,
          dnsLookupMs: 62,
          dnsQueryType: 'TXT',
          dnsExpectedResponse: 'v=spf1 include:_spf.google.com ~all',
          dnsResponseValue: 'v=spf1 include:_spf.example.org ~all'
        },
        {
          id: 4,
          monitorId: 'us-east-1-cname-record',
          monitorName: 'CDN CNAME',
          monitorType: 'DNS',
          targetHost: 'cdn.example.com',
          agentId: 'dns-monitor-us-east-1@us-east-1-agent-02',
          agentRegion: 'us-east-1',
          executedAt: new Date(Date.now() - 3 * 60 * 1000).toISOString(),
          success: true,
          responseTime: 125,
          dnsLookupMs: 98,
          dnsQueryType: 'CNAME',
          dnsExpectedResponse: 'cdn-provider.example.net',
          dnsResponseValue: 'cdn-provider.example.net'
        },
        {
          id: 5,
          monitorId: 'us-west-2-failed-dns',
          monitorName: 'Failed DNS Lookup',
          monitorType: 'DNS',
          targetHost: 'nonexistent-domain.invalid',
          agentId: 'dns-monitor-us-west-2@us-west-2-agent-02',
          agentRegion: 'us-west-2',
          executedAt: new Date(Date.now() - 4 * 60 * 1000).toISOString(),
          success: false,
          dnsLookupMs: 5000,
          dnsQueryType: 'A',
          errorMessage: 'Name or service not known',
          errorType: 'NXDOMAIN'
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
        monitor.dnsQueryType?.toLowerCase().includes(searchQuery.toLowerCase())
      );
    }

    if (statusFilter !== 'all') {
      filtered = filtered.filter(monitor => {
        if (statusFilter === 'resolved') return monitor.success;
        if (statusFilter === 'matched') return monitor.success && monitor.dnsExpectedResponse && monitor.dnsResponseValue && 
          monitor.dnsExpectedResponse.toLowerCase() === monitor.dnsResponseValue.toLowerCase();
        if (statusFilter === 'unmatched') return monitor.success && monitor.dnsExpectedResponse && monitor.dnsResponseValue && 
          monitor.dnsExpectedResponse.toLowerCase() !== monitor.dnsResponseValue.toLowerCase();
        if (statusFilter === 'failed') return !monitor.success;
        return true;
      });
    }

    if (regionFilter !== 'all') {
      filtered = filtered.filter(monitor => monitor.agentRegion === regionFilter);
    }

    if (queryTypeFilter !== 'all') {
      filtered = filtered.filter(monitor => monitor.dnsQueryType === queryTypeFilter);
    }

    if (performanceFilter !== 'all') {
      filtered = filtered.filter(monitor => {
        if (!monitor.dnsLookupMs) return false;
        if (performanceFilter === 'excellent') return monitor.dnsLookupMs < 50;
        if (performanceFilter === 'good') return monitor.dnsLookupMs >= 50 && monitor.dnsLookupMs < 100;
        if (performanceFilter === 'fair') return monitor.dnsLookupMs >= 100 && monitor.dnsLookupMs < 200;
        if (performanceFilter === 'poor') return monitor.dnsLookupMs >= 200;
        return true;
      });
    }

    setFilteredMonitors(filtered);
  }, [monitors, searchQuery, statusFilter, regionFilter, queryTypeFilter, performanceFilter]);

  const stats = {
    total: monitors.length,
    resolved: monitors.filter(m => m.success).length,
    matched: monitors.filter(m => m.success && m.dnsExpectedResponse && m.dnsResponseValue && 
      m.dnsExpectedResponse.toLowerCase() === m.dnsResponseValue.toLowerCase()).length,
    failed: monitors.filter(m => !m.success).length,
    avgResponseTime: Math.round(monitors.filter(m => m.responseTime).reduce((acc, m) => acc + (m.responseTime || 0), 0) / monitors.filter(m => m.responseTime).length) || 0,
    avgDnsLookup: Math.round(monitors.filter(m => m.dnsLookupMs).reduce((acc, m) => acc + (m.dnsLookupMs || 0), 0) / monitors.filter(m => m.dnsLookupMs).length) || 0
  };

  if (loading) {
    return (
      <div className="container mx-auto p-6">
        <div className="space-y-6">
          <div className="flex items-center gap-2">
            <Globe className="w-8 h-8 animate-pulse" />
            <h1 className="text-3xl font-bold">Loading DNS Monitors...</h1>
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
            <Globe className="w-8 h-8 text-blue-500" />
            DNS Monitors
          </h1>
          <p className="text-muted-foreground">
            DNS resolution monitoring with record validation
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
              <p className="text-2xl font-bold text-green-600">{stats.resolved}</p>
              <p className="text-xs text-muted-foreground">Resolved</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4">
            <div className="text-center">
              <p className="text-2xl font-bold text-blue-600">{stats.matched}</p>
              <p className="text-xs text-muted-foreground">Matched</p>
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
              <p className="text-xs text-muted-foreground">Avg Response</p>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4">
            <div className="text-center">
              <p className="text-2xl font-bold">{stats.avgDnsLookup}ms</p>
              <p className="text-xs text-muted-foreground">Avg DNS</p>
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
                  placeholder="Search monitors, domains, record types..."
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
                  <SelectItem value="resolved">Resolved</SelectItem>
                  <SelectItem value="matched">Matched</SelectItem>
                  <SelectItem value="unmatched">Unmatched</SelectItem>
                  <SelectItem value="failed">Failed</SelectItem>
                </SelectContent>
              </Select>
              <Select value={queryTypeFilter} onValueChange={setQueryTypeFilter}>
                <SelectTrigger className="w-32">
                  <Hash className="w-4 h-4 mr-2" />
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">All Types</SelectItem>
                  <SelectItem value="A">A Record</SelectItem>
                  <SelectItem value="AAAA">AAAA Record</SelectItem>
                  <SelectItem value="CNAME">CNAME</SelectItem>
                  <SelectItem value="MX">MX Record</SelectItem>
                  <SelectItem value="TXT">TXT Record</SelectItem>
                  <SelectItem value="NS">NS Record</SelectItem>
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
          <DNSMonitorCard 
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
            <h3 className="text-lg font-semibold">No DNS monitors found</h3>
            <p className="text-muted-foreground">
              Try adjusting your filters or search criteria
            </p>
          </div>
        </Card>
      )}
    </div>
  );
};

const DNSMonitors: React.FC = () => {
  return (
    <DashboardLayout>
      <DNSMonitorsContent />
    </DashboardLayout>
  );
};

export default DNSMonitors;