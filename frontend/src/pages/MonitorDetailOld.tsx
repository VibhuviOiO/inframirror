import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { 
  ArrowLeft, 
  Activity, 
  Clock, 
  Globe, 
  MapPin, 
  Calendar,
  TrendingUp,
  TrendingDown,
  AlertCircle,
  CheckCircle,
  BarChart3,
  LineChart,
  Zap,
  Database,
  Timer,
  RefreshCw,
  Filter,
  Search,
  Download,
  Share2,
  AlertTriangle,
  Network,
  Server,
  Eye,
  EyeOff,
  Settings,
  Info,
  Wifi,
  WifiOff,
  Monitor as MonitorIcon
} from "lucide-react";
import { cn } from "@/lib/utils";
import { DashboardLayout } from "@/components/dashboard/DashboardLayout";
import {
  LineChart as RechartsLineChart,
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Line,
  BarChart,
  Bar,
  ComposedChart,
  Scatter,
  ScatterChart,
  PieChart,
  Pie,
  Cell,
  Legend
} from 'recharts';

interface MonitorHistory {
  id: number;
  executedAt: string;
  success: boolean;
  responseTime: number;
  responseStatusCode?: number;
  errorMessage?: string;
  errorType?: string;
  rawResponseBody?: string;
  responseSizeBytes?: number;
  responseContentType?: string;
  responseServer?: string;
  responseCacheStatus?: string;
  dnsLookupMs?: number;
  tcpConnectMs?: number;
  tlsHandshakeMs?: number;
  timeToFirstByteMs?: number;
  warningThresholdMs?: number;
  criticalThresholdMs?: number;
  rawResponseHeaders?: any;
  rawNetworkData?: any;
}

interface MonitorDetails {
  id: number;
  monitorId: string;
  monitorName: string;
  monitorType: 'HTTP' | 'HTTPS' | 'TCP' | 'UDP' | 'PING' | 'DNS';
  targetHost: string;
  targetPort?: number;
  targetPath?: string;
  httpMethod?: string;
  agentId: string;
  agentRegion: string;
  expectedStatusCode?: number;
  createdAt: string;
}

interface MonitorStats {
  uptime: number;
  avgResponseTime: number;
  totalChecks: number;
  successRate: number;
  incidents: number;
  lastIncident?: string;
  p95ResponseTime: number;
  p99ResponseTime: number;
  avgDnsTime: number;
  avgConnectTime: number;
  avgTlsTime: number;
  errorsByType: Record<string, number>;
  hourlyStats: Array<{
    hour: string;
    checks: number;
    successRate: number;
    avgResponseTime: number;
  }>;
}

const StatusIndicator: React.FC<{ status: number | undefined, success: boolean }> = ({ status, success }) => {
  if (!success || !status) {
    return (
      <div className="flex items-center gap-2 text-red-600">
        <div className="w-2 h-2 bg-red-500 rounded-full animate-pulse" />
        <span className="font-medium">Down</span>
        <Badge variant="destructive" className="text-xs">
          {status || 'No Response'}
        </Badge>
      </div>
    );
  }

  if (status >= 200 && status < 300) {
    return (
      <div className="flex items-center gap-2 text-green-600">
        <div className="w-2 h-2 bg-green-500 rounded-full" />
        <span className="font-medium">Operational</span>
        <Badge variant="default" className="bg-green-500 text-xs">
          {status}
        </Badge>
      </div>
    );
  }

  return (
    <div className="flex items-center gap-2 text-yellow-600">
      <div className="w-2 h-2 bg-yellow-500 rounded-full animate-pulse" />
      <span className="font-medium">Degraded</span>
      <Badge variant="secondary" className="bg-yellow-500 text-white text-xs">
        {status}
      </Badge>
    </div>
  );
};

const MetricCard: React.FC<{
  title: string;
  value: string | number;
  change?: string;
  trend?: 'up' | 'down' | 'stable';
  icon: React.ReactNode;
  suffix?: string;
}> = ({ title, value, change, trend, icon, suffix }) => {
  return (
    <Card>
      <CardContent className="p-6">
        <div className="flex items-center justify-between">
          <div className="space-y-2">
            <p className="text-sm font-medium text-muted-foreground flex items-center gap-2">
              {icon}
              {title}
            </p>
            <div className="space-y-1">
              <p className="text-2xl font-bold">
                {value}
                {suffix && <span className="text-lg text-muted-foreground ml-1">{suffix}</span>}
              </p>
              {change && (
                <p className={cn(
                  "text-xs flex items-center gap-1",
                  trend === 'up' ? "text-green-600" : trend === 'down' ? "text-red-600" : "text-muted-foreground"
                )}>
                  {trend === 'up' && <TrendingUp className="w-3 h-3" />}
                  {trend === 'down' && <TrendingDown className="w-3 h-3" />}
                  {change}
                </p>
              )}
            </div>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

// Enhanced comprehensive charts with multiple visualizations
const ResponseTimeChart: React.FC<{ 
  data: MonitorHistory[], 
  timeRange: string,
  showDetails: boolean 
}> = ({ data, timeRange, showDetails }) => {
  const [viewType, setViewType] = useState<'combined' | 'breakdown'>('combined');
  
  // Filter data based on time range
  const filteredData = React.useMemo(() => {
    const now = new Date();
    let cutoff = new Date();
    
    switch (timeRange) {
      case '1h':
        cutoff.setHours(now.getHours() - 1);
        break;
      case '24h':
        cutoff.setDate(now.getDate() - 1);
        break;
      case '7d':
        cutoff.setDate(now.getDate() - 7);
        break;
      case '30d':
        cutoff.setDate(now.getDate() - 30);
        break;
      default:
        return data.slice(-100);
    }
    
    return data.filter(item => new Date(item.executedAt) > cutoff);
  }, [data, timeRange]);

  const chartData = filteredData.map(item => {
    const date = new Date(item.executedAt);
    return {
      time: timeRange === '1h' 
        ? date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
        : timeRange === '24h'
        ? date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
        : date.toLocaleDateString([], { month: 'short', day: 'numeric' }),
      fullTime: date.toLocaleString(),
      responseTime: item.responseTime || 0,
      dnsTime: item.dnsLookupMs || 0,
      connectTime: item.tcpConnectMs || 0,
      tlsTime: item.tlsHandshakeMs || 0,
      ttfbTime: item.timeToFirstByteMs || item.responseTime || 0,
      success: item.success,
      statusCode: item.responseStatusCode,
      warningThreshold: item.warningThresholdMs || 500,
      criticalThreshold: item.criticalThresholdMs || 1000,
      size: item.responseSizeBytes || 0
    };
  });

  return (
    <Card className="col-span-2">
      <CardHeader>
        <div className="flex items-center justify-between">
          <div>
            <CardTitle className="flex items-center gap-2">
              <LineChart className="w-5 h-5" />
              Response Time Analysis
            </CardTitle>
            <CardDescription>
              Performance metrics over time • {chartData.length} data points
            </CardDescription>
          </div>
          <div className="flex items-center gap-2">
            <Select value={viewType} onValueChange={(v: any) => setViewType(v)}>
              <SelectTrigger className="w-40">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="combined">Combined View</SelectItem>
                <SelectItem value="breakdown">Timing Breakdown</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>
      </CardHeader>
      <CardContent>
        <div className="h-80">
          <ResponsiveContainer width="100%" height="100%">
            {viewType === 'combined' ? (
              <ComposedChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" className="opacity-30" />
                <XAxis 
                  dataKey="time" 
                  fontSize={11}
                  tickLine={false}
                  axisLine={false}
                />
                <YAxis 
                  yAxisId="time"
                  fontSize={11}
                  tickLine={false}
                  axisLine={false}
                  tickFormatter={(value) => `${value}ms`}
                />
                <YAxis 
                  yAxisId="size"
                  orientation="right"
                  fontSize={11}
                  tickLine={false}
                  axisLine={false}
                  tickFormatter={(value) => `${(value/1024).toFixed(0)}KB`}
                />
                <Tooltip 
                  contentStyle={{
                    backgroundColor: 'hsl(var(--background))',
                    border: '1px solid hsl(var(--border))',
                    borderRadius: '8px',
                    fontSize: '12px'
                  }}
                  formatter={(value: any, name: string) => {
                    if (name === 'size') return [`${(value/1024).toFixed(1)}KB`, 'Response Size'];
                    if (name === 'responseTime') return [`${value}ms`, 'Response Time'];
                    return [value, name];
                  }}
                  labelFormatter={(label, payload) => {
                    if (payload?.[0]) {
                      return `${payload[0].payload.fullTime}`;
                    }
                    return label;
                  }}
                />
                
                {/* Threshold lines */}
                <Line 
                  yAxisId="time"
                  type="monotone" 
                  dataKey="warningThreshold" 
                  stroke="#f59e0b" 
                  strokeDasharray="5 5"
                  strokeWidth={1}
                  dot={false}
                  name="Warning Threshold"
                />
                <Line 
                  yAxisId="time"
                  type="monotone" 
                  dataKey="criticalThreshold" 
                  stroke="#ef4444" 
                  strokeDasharray="5 5"
                  strokeWidth={1}
                  dot={false}
                  name="Critical Threshold"
                />
                
                {/* Response size bars */}
                <Bar
                  yAxisId="size"
                  dataKey="size"
                  fill="#94a3b8"
                  fillOpacity={0.3}
                  name="size"
                />
                
                {/* Response time line */}
                <Area
                  yAxisId="time"
                  type="monotone"
                  dataKey="responseTime"
                  stroke="#3b82f6"
                  strokeWidth={2}
                  fill="url(#colorResponseTime)"
                  fillOpacity={0.2}
                  name="responseTime"
                />
                
                {/* Error indicators */}
                <Scatter
                  yAxisId="time"
                  dataKey="responseTime"
                  fill={(entry: any) => entry.success ? 'transparent' : '#ef4444'}
                  shape={(props: any) => {
                    if (!props.payload?.success) {
                      return <AlertTriangle x={props.cx - 4} y={props.cy - 4} width={8} height={8} fill="#ef4444" />;
                    }
                    return null;
                  }}
                />
                
                <defs>
                  <linearGradient id="colorResponseTime" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#3b82f6" stopOpacity={0.3}/>
                    <stop offset="95%" stopColor="#3b82f6" stopOpacity={0}/>
                  </linearGradient>
                </defs>
              </ComposedChart>
            ) : (
              <AreaChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" className="opacity-30" />
                <XAxis 
                  dataKey="time" 
                  fontSize={11}
                  tickLine={false}
                  axisLine={false}
                />
                <YAxis 
                  fontSize={11}
                  tickLine={false}
                  axisLine={false}
                  tickFormatter={(value) => `${value}ms`}
                />
                <Tooltip 
                  contentStyle={{
                    backgroundColor: 'hsl(var(--background))',
                    border: '1px solid hsl(var(--border))',
                    borderRadius: '8px',
                    fontSize: '12px'
                  }}
                />
                <Area
                  type="monotone"
                  dataKey="dnsTime"
                  stackId="1"
                  stroke="#8b5cf6"
                  fill="#8b5cf6"
                  fillOpacity={0.8}
                  name="DNS Lookup"
                />
                <Area
                  type="monotone"
                  dataKey="connectTime"
                  stackId="1"
                  stroke="#06b6d4"
                  fill="#06b6d4"
                  fillOpacity={0.8}
                  name="TCP Connect"
                />
                <Area
                  type="monotone"
                  dataKey="tlsTime"
                  stackId="1"
                  stroke="#10b981"
                  fill="#10b981"
                  fillOpacity={0.8}
                  name="TLS Handshake"
                />
                <Area
                  type="monotone"
                  dataKey="ttfbTime"
                  stackId="1"
                  stroke="#3b82f6"
                  fill="#3b82f6"
                  fillOpacity={0.8}
                  name="Time to First Byte"
                />
              </AreaChart>
            )}
          </ResponsiveContainer>
        </div>
      </CardContent>
    </Card>
  );
};

const UptimeChart: React.FC<{ data: MonitorHistory[], timeRange: string, availabilityTimeRange: string, onTimeRangeChange: (range: string) => void, customStartDate: string, customEndDate: string, onCustomStartChange: (date: string) => void, onCustomEndChange: (date: string) => void }> = ({ 
  data, 
  timeRange, 
  availabilityTimeRange, 
  onTimeRangeChange, 
  customStartDate, 
  customEndDate, 
  onCustomStartChange, 
  onCustomEndChange 
}) => {
  // Filter data based on availability time range or custom dates
  const filteredData = React.useMemo(() => {
    const now = new Date();
    let startTime = new Date();
    let endTime = new Date();

    if (availabilityTimeRange === 'custom' && customStartDate && customEndDate) {
      startTime = new Date(customStartDate);
      endTime = new Date(customEndDate);
    } else {
      switch (availabilityTimeRange) {
        case '1h':
          startTime = new Date(now.getTime() - 1 * 60 * 60 * 1000);
          break;
        case '6h':
          startTime = new Date(now.getTime() - 6 * 60 * 60 * 1000);
          break;
        case '24h':
          startTime = new Date(now.getTime() - 24 * 60 * 60 * 1000);
          break;
        case '7d':
          startTime = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
          break;
        case '30d':
          startTime = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
          break;
        default:
          startTime = new Date(now.getTime() - 24 * 60 * 60 * 1000);
      }
    }

    return data.filter(item => {
      const itemDate = new Date(item.executedAt);
      return itemDate >= startTime && itemDate <= endTime;
    });
  }, [data, availabilityTimeRange, customStartDate, customEndDate]);

  const groupingInterval = availabilityTimeRange === '1h' ? 5 : availabilityTimeRange === '6h' ? 15 : availabilityTimeRange === '24h' ? 60 : availabilityTimeRange === '7d' ? 720 : 1440; // minutes
  
  // Group data by time intervals (Kibana-style)
  const groupedData = filteredData.reduce((acc: Record<string, { total: number, successful: number, errors: number, responseTime: number[] }>, item) => {
    const date = new Date(item.executedAt);
    let key: string;
    
    if (groupingInterval === 5) {
      // 5-minute intervals
      const minutes = Math.floor(date.getMinutes() / 5) * 5;
      key = new Date(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), minutes).toISOString();
    } else if (groupingInterval === 15) {
      // 15-minute intervals
      const minutes = Math.floor(date.getMinutes() / 15) * 15;
      key = new Date(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), minutes).toISOString();
    } else if (groupingInterval === 60) {
      // Hourly intervals
      key = new Date(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours()).toISOString();
    } else {
      // Daily intervals
      key = new Date(date.getFullYear(), date.getMonth(), date.getDate()).toISOString();
    }
    
    if (!acc[key]) {
      acc[key] = { total: 0, successful: 0, errors: 0, responseTime: [] };
    }
    acc[key].total++;
    if (item.success) {
      acc[key].successful++;
    } else {
      acc[key].errors++;
    }
    if (item.responseTime) {
      acc[key].responseTime.push(item.responseTime);
    }
    return acc;
  }, {});

  const chartData = Object.entries(groupedData)
    .sort(([a], [b]) => new Date(a).getTime() - new Date(b).getTime())
    .map(([timestamp, stats]) => {
      const date = new Date(timestamp);
      const avgResponseTime = stats.responseTime.length > 0 
        ? stats.responseTime.reduce((sum, rt) => sum + rt, 0) / stats.responseTime.length 
        : 0;
      
      const successRate = (stats.successful / stats.total) * 100;
      let barColor = '#10b981'; // Green for 100% success
      
      if (successRate < 100) {
        if (successRate >= 95) {
          barColor = '#f59e0b'; // Yellow for 95%+ success
        } else {
          barColor = '#ef4444'; // Red for <95% success
        }
      }
      
      return {
        time: groupingInterval <= 15 
          ? date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
          : groupingInterval === 60
          ? date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
          : date.toLocaleDateString([], { month: 'short', day: 'numeric' }),
        fullTime: date.toLocaleString(),
        timestamp: date.getTime(),
        count: stats.total,
        successful: stats.successful,
        errors: stats.errors,
        successRate,
        errorRate: (stats.errors / stats.total) * 100,
        avgResponseTime,
        fill: barColor
      };
    });

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <BarChart3 className="w-5 h-5" />
          Availability Overview
          <div className="ml-auto flex items-center gap-2">
            <Filter className="w-4 h-4" />
            <Select value={availabilityTimeRange} onValueChange={onTimeRangeChange}>
              <SelectTrigger className="w-32 h-8">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="1h">Last 1 hour</SelectItem>
                <SelectItem value="6h">Last 6 hours</SelectItem>
                <SelectItem value="24h">Last 24 hours</SelectItem>
                <SelectItem value="7d">Last 7 days</SelectItem>
                <SelectItem value="30d">Last 30 days</SelectItem>
                <SelectItem value="custom">Custom Range</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </CardTitle>
        {availabilityTimeRange === 'custom' && (
          <div className="flex items-center gap-4 mt-2">
            <div className="flex items-center gap-2">
              <Label htmlFor="start-date" className="text-sm">From:</Label>
              <Input
                id="start-date"
                type="datetime-local"
                value={customStartDate}
                onChange={(e) => onCustomStartChange(e.target.value)}
                className="w-48 h-8"
              />
            </div>
            <div className="flex items-center gap-2">
              <Label htmlFor="end-date" className="text-sm">To:</Label>
              <Input
                id="end-date"
                type="datetime-local"
                value={customEndDate}
                onChange={(e) => onCustomEndChange(e.target.value)}
                className="w-48 h-8"
              />
            </div>
          </div>
        )}
        <CardDescription>
          Checks per {groupingInterval < 60 ? `${groupingInterval}min` : groupingInterval === 60 ? 'hour' : 'day'} • {chartData.length} intervals • {filteredData.length} total checks
        </CardDescription>
      </CardHeader>
      <CardContent>
        {/* Availability Statistics - Checkly Style */}
        <div className="flex items-center gap-8 mb-6 pb-4 border-b">
          <div className="flex items-center gap-4">
            <div className="text-center">
              <div className="text-sm text-muted-foreground mb-1">Availability</div>
              <div className="flex items-baseline gap-1">
                <span className="text-3xl font-light text-green-600">
                  {filteredData.length > 0 
                    ? ((filteredData.filter(item => item.success).length / filteredData.length) * 100).toFixed(1)
                    : '100'
                  }
                </span>
                <span className="text-sm text-muted-foreground">%</span>
              </div>
            </div>
            <div className="text-center">
              <div className="text-sm text-muted-foreground mb-1">Failure Alerts</div>
              <div className="flex items-baseline gap-1">
                <span className="text-3xl font-light text-red-500">
                  {filteredData.filter(item => !item.success).length}
                </span>
              </div>
            </div>
          </div>
        </div>
        
        <div className="h-64">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart 
              data={chartData} 
              margin={{ top: 20, right: 5, left: 5, bottom: 20 }}
              barCategoryGap="10%"
            >
              <XAxis 
                dataKey="time" 
                fontSize={12}
                tickLine={false}
                axisLine={{ stroke: '#e5e7eb', strokeWidth: 1 }}
                tick={{ fill: '#6b7280' }}
                interval="preserveStartEnd"
              />
              <YAxis 
                fontSize={12}
                tickLine={false}
                axisLine={{ stroke: '#e5e7eb', strokeWidth: 1 }}
                tick={{ fill: '#6b7280' }}
                domain={[0, 'dataMax + 1']}
              />
              <Tooltip 
                contentStyle={{
                  backgroundColor: 'white',
                  border: '1px solid #e5e7eb',
                  borderRadius: '6px',
                  fontSize: '12px',
                  boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)'
                }}
                formatter={(value: number, name: string) => {
                  if (name === 'count') return [`${value} checks`, 'Total Checks'];
                  return [value, name];
                }}
                labelFormatter={(label, payload) => {
                  if (payload?.[0]) {
                    const data = payload[0].payload;
                    const successRate = ((data.successful / data.count) * 100).toFixed(1);
                    return `${data.fullTime} • ${successRate}% uptime`;
                  }
                  return label;
                }}
              />
              <Bar
                dataKey="count"
                stroke="none"
                radius={[1, 1, 0, 0]}
              >
                {chartData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.fill} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </CardContent>
    </Card>
  );
};

// New component for error distribution
const ErrorDistributionChart: React.FC<{ data: MonitorHistory[] }> = ({ data }) => {
  const errorData = data
    .filter(item => !item.success && item.errorType)
    .reduce((acc: Record<string, number>, item) => {
      const errorType = item.errorType || 'Unknown';
      acc[errorType] = (acc[errorType] || 0) + 1;
      return acc;
    }, {});

  const chartData = Object.entries(errorData).map(([type, count], index) => ({
    name: type,
    value: count,
    percentage: ((count / Object.values(errorData).reduce((a, b) => a + b, 0)) * 100).toFixed(1)
  }));

  const COLORS = ['#ef4444', '#f97316', '#f59e0b', '#eab308', '#84cc16', '#22c55e', '#06b6d4'];

  if (chartData.length === 0) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <AlertCircle className="w-5 h-5" />
            Error Distribution
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex items-center justify-center h-32 text-muted-foreground">
            <CheckCircle className="w-8 h-8 mr-2 text-green-500" />
            No errors in selected time period
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <AlertCircle className="w-5 h-5" />
          Error Distribution
        </CardTitle>
        <CardDescription>
          Error types breakdown • {Object.values(errorData).reduce((a, b) => a + b, 0)} total errors
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div className="h-64 flex">
          <div className="w-2/3">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={chartData}
                  cx="50%"
                  cy="50%"
                  innerRadius={40}
                  outerRadius={80}
                  paddingAngle={2}
                  dataKey="value"
                >
                  {chartData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip 
                  formatter={(value: number, name, props) => [
                    `${value} (${props.payload.percentage}%)`,
                    'Errors'
                  ]}
                />
              </PieChart>
            </ResponsiveContainer>
          </div>
          <div className="w-1/3 pl-4">
            <div className="space-y-2 mt-4">
              {chartData.map((entry, index) => (
                <div key={entry.name} className="flex items-center gap-2 text-sm">
                  <div 
                    className="w-3 h-3 rounded-full" 
                    style={{ backgroundColor: COLORS[index % COLORS.length] }}
                  />
                  <div className="flex-1">
                    <div className="font-medium">{entry.name}</div>
                    <div className="text-muted-foreground">{entry.value} ({entry.percentage}%)</div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

const HistoryTable: React.FC<{ 
  data: MonitorHistory[], 
  timeRange: string,
  onTimeRangeChange: (range: string) => void 
}> = ({ data, timeRange, onTimeRangeChange }) => {
  const [showDetails, setShowDetails] = useState(false);
  const [selectedItem, setSelectedItem] = useState<MonitorHistory | null>(null);
  const [filterStatus, setFilterStatus] = useState<'all' | 'success' | 'error'>('all');
  
  const filteredData = React.useMemo(() => {
    let result = data.slice(0, 500); // Show more data
    
    if (filterStatus === 'success') {
      result = result.filter(item => item.success);
    } else if (filterStatus === 'error') {
      result = result.filter(item => !item.success);
    }
    
    return result;
  }, [data, filterStatus]);

  const getStatusBadgeVariant = (success: boolean, statusCode?: number) => {
    if (!success) return "destructive";
    if (statusCode && statusCode >= 200 && statusCode < 300) return "default";
    if (statusCode && statusCode >= 300 && statusCode < 400) return "secondary";
    return "outline";
  };

  const getTimingColor = (responseTime: number, warning?: number, critical?: number) => {
    if (critical && responseTime > critical) return "text-red-600";
    if (warning && responseTime > warning) return "text-yellow-600";
    return "text-green-600";
  };

  return (
    <Card>
      <CardHeader>
        <div className="flex items-center justify-between">
          <div>
            <CardTitle className="flex items-center gap-2">
              <Database className="w-5 h-5" />
              Detailed History
            </CardTitle>
            <CardDescription>
              Comprehensive check results • {filteredData.length} of {data.length} records
            </CardDescription>
          </div>
          <div className="flex items-center gap-2">
            <Select value={filterStatus} onValueChange={(v: any) => setFilterStatus(v)}>
              <SelectTrigger className="w-32">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Results</SelectItem>
                <SelectItem value="success">Success Only</SelectItem>
                <SelectItem value="error">Errors Only</SelectItem>
              </SelectContent>
            </Select>
            <Select value={timeRange} onValueChange={onTimeRangeChange}>
              <SelectTrigger className="w-32">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="1h">Last Hour</SelectItem>
                <SelectItem value="24h">Last 24h</SelectItem>
                <SelectItem value="7d">Last 7 days</SelectItem>
                <SelectItem value="30d">Last 30 days</SelectItem>
              </SelectContent>
            </Select>
            <Button
              variant="outline"
              size="sm"
              onClick={() => setShowDetails(!showDetails)}
            >
              {showDetails ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
            </Button>
          </div>
        </div>
      </CardHeader>
      <CardContent>
        <div className="border rounded-lg">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead className="w-12">Status</TableHead>
                <TableHead>Timestamp</TableHead>
                <TableHead>Response</TableHead>
                <TableHead className="text-right">Timing</TableHead>
                {showDetails && (
                  <>
                    <TableHead className="text-right">Size</TableHead>
                    <TableHead>Server</TableHead>
                    <TableHead className="w-12">Details</TableHead>
                  </>
                )}
              </TableRow>
            </TableHeader>
            <TableBody>
              {filteredData.map((item) => (
                <TableRow 
                  key={item.id}
                  className={cn(
                    "cursor-pointer hover:bg-muted/50",
                    !item.success && "bg-red-50/50 dark:bg-red-900/5"
                  )}
                  onClick={() => setSelectedItem(selectedItem?.id === item.id ? null : item)}
                >
                  <TableCell>
                    <div className="flex items-center gap-2">
                      {item.success ? (
                        <CheckCircle className="w-4 h-4 text-green-600" />
                      ) : (
                        <AlertCircle className="w-4 h-4 text-red-600" />
                      )}
                    </div>
                  </TableCell>
                  <TableCell>
                    <div className="space-y-1">
                      <div className="text-sm font-medium">
                        {new Date(item.executedAt).toLocaleString()}
                      </div>
                      <div className="text-xs text-muted-foreground">
                        {new Date(item.executedAt).toISOString().split('T')[0]}
                      </div>
                    </div>
                  </TableCell>
                  <TableCell>
                    <div className="flex items-center gap-2">
                      {item.responseStatusCode && (
                        <Badge 
                          variant={getStatusBadgeVariant(item.success, item.responseStatusCode)}
                          className="text-xs font-mono"
                        >
                          {item.responseStatusCode}
                        </Badge>
                      )}
                      {!item.success && item.errorType && (
                        <Badge variant="outline" className="text-xs">
                          {item.errorType}
                        </Badge>
                      )}
                    </div>
                    {item.errorMessage && (
                      <div className="text-xs text-red-600 dark:text-red-400 mt-1 truncate max-w-48">
                        {item.errorMessage}
                      </div>
                    )}
                  </TableCell>
                  <TableCell className="text-right">
                    <div className="space-y-1">
                      <div className={cn(
                        "text-sm font-mono font-medium",
                        getTimingColor(item.responseTime, item.warningThresholdMs, item.criticalThresholdMs)
                      )}>
                        {item.responseTime}ms
                      </div>
                      {showDetails && (item.dnsLookupMs || item.tcpConnectMs) && (
                        <div className="text-xs text-muted-foreground">
                          DNS: {item.dnsLookupMs || 0}ms • TCP: {item.tcpConnectMs || 0}ms
                        </div>
                      )}
                    </div>
                  </TableCell>
                  {showDetails && (
                    <>
                      <TableCell className="text-right">
                        {item.responseSizeBytes && (
                          <div className="text-sm font-mono">
                            {(item.responseSizeBytes / 1024).toFixed(1)}KB
                          </div>
                        )}
                      </TableCell>
                      <TableCell>
                        <div className="text-sm truncate max-w-32">
                          {item.responseServer || '-'}
                        </div>
                        {item.responseContentType && (
                          <div className="text-xs text-muted-foreground truncate">
                            {item.responseContentType.split(';')[0]}
                          </div>
                        )}
                      </TableCell>
                      <TableCell>
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={(e) => {
                            e.stopPropagation();
                            setSelectedItem(selectedItem?.id === item.id ? null : item);
                          }}
                        >
                          <Info className="w-3 h-3" />
                        </Button>
                      </TableCell>
                    </>
                  )}
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </div>
        
        {/* Expanded details panel */}
        {selectedItem && (
          <Card className="mt-4">
            <CardHeader>
              <CardTitle className="text-base">Request Details</CardTitle>
              <CardDescription>
                {new Date(selectedItem.executedAt).toLocaleString()}
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="space-y-4">
                  <div>
                    <h4 className="text-sm font-medium mb-2">Timing Breakdown</h4>
                    <div className="space-y-2 text-sm">
                      <div className="flex justify-between">
                        <span>DNS Lookup:</span>
                        <span className="font-mono">{selectedItem.dnsLookupMs || 0}ms</span>
                      </div>
                      <div className="flex justify-between">
                        <span>TCP Connect:</span>
                        <span className="font-mono">{selectedItem.tcpConnectMs || 0}ms</span>
                      </div>
                      <div className="flex justify-between">
                        <span>TLS Handshake:</span>
                        <span className="font-mono">{selectedItem.tlsHandshakeMs || 0}ms</span>
                      </div>
                      <div className="flex justify-between">
                        <span>Time to First Byte:</span>
                        <span className="font-mono">{selectedItem.timeToFirstByteMs || selectedItem.responseTime}ms</span>
                      </div>
                      <div className="flex justify-between font-medium border-t pt-2">
                        <span>Total Response Time:</span>
                        <span className="font-mono">{selectedItem.responseTime}ms</span>
                      </div>
                    </div>
                  </div>
                  
                  <div>
                    <h4 className="text-sm font-medium mb-2">Response Info</h4>
                    <div className="space-y-2 text-sm">
                      <div className="flex justify-between">
                        <span>Status Code:</span>
                        <Badge variant={getStatusBadgeVariant(selectedItem.success, selectedItem.responseStatusCode)}>
                          {selectedItem.responseStatusCode || 'N/A'}
                        </Badge>
                      </div>
                      <div className="flex justify-between">
                        <span>Content Type:</span>
                        <span className="font-mono text-xs">{selectedItem.responseContentType || 'N/A'}</span>
                      </div>
                      <div className="flex justify-between">
                        <span>Response Size:</span>
                        <span className="font-mono">
                          {selectedItem.responseSizeBytes ? `${(selectedItem.responseSizeBytes / 1024).toFixed(1)}KB` : 'N/A'}
                        </span>
                      </div>
                      <div className="flex justify-between">
                        <span>Server:</span>
                        <span className="font-mono text-xs">{selectedItem.responseServer || 'N/A'}</span>
                      </div>
                    </div>
                  </div>
                </div>
                
                <div className="space-y-4">
                  {selectedItem.rawResponseHeaders && (
                    <div>
                      <h4 className="text-sm font-medium mb-2">Response Headers</h4>
                      <pre className="text-xs bg-muted p-2 rounded overflow-x-auto max-h-32">
{JSON.stringify(selectedItem.rawResponseHeaders, null, 2)}
                      </pre>
                    </div>
                  )}
                  
                  {selectedItem.rawResponseBody && (
                    <div>
                      <h4 className="text-sm font-medium mb-2">Response Body (Preview)</h4>
                      <pre className="text-xs bg-muted p-2 rounded overflow-x-auto max-h-32">
{typeof selectedItem.rawResponseBody === 'string' 
  ? selectedItem.rawResponseBody.substring(0, 500) + (selectedItem.rawResponseBody.length > 500 ? '...' : '')
  : JSON.stringify(selectedItem.rawResponseBody, null, 2).substring(0, 500) + '...'
}
                      </pre>
                    </div>
                  )}
                </div>
              </div>
            </CardContent>
          </Card>
        )}
      </CardContent>
    </Card>
  );
};

const MonitorDetailContent: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [monitor, setMonitor] = useState<MonitorDetails | null>(null);
  const [history, setHistory] = useState<MonitorHistory[]>([]);
  const [stats, setStats] = useState<MonitorStats | null>(null);
  const [loading, setLoading] = useState(true);
  const [timeRange, setTimeRange] = useState('24h');
  const [autoRefresh, setAutoRefresh] = useState(false);
  const [lastRefresh, setLastRefresh] = useState<Date>(new Date());
  const [availabilityTimeRange, setAvailabilityTimeRange] = useState('24h');
  const [customStartDate, setCustomStartDate] = useState('');
  const [customEndDate, setCustomEndDate] = useState('');

  // Filter history based on availability time range
  const filteredHistory = React.useMemo(() => {
    const now = new Date();
    let startTime = new Date();

    switch (availabilityTimeRange) {
      case '1h':
        startTime = new Date(now.getTime() - 1 * 60 * 60 * 1000);
        break;
      case '6h':
        startTime = new Date(now.getTime() - 6 * 60 * 60 * 1000);
        break;
      case '24h':
        startTime = new Date(now.getTime() - 24 * 60 * 60 * 1000);
        break;
      case '7d':
        startTime = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
        break;
      case '30d':
        startTime = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
        break;
      default:
        startTime = new Date(now.getTime() - 24 * 60 * 60 * 1000);
    }

    return history.filter(item => {
      const itemDate = new Date(item.executedAt);
      return itemDate >= startTime;
    });
  }, [history, availabilityTimeRange]);

  // Prepare chart data for histogram
  const chartData = React.useMemo(() => {
    const groupingInterval = availabilityTimeRange === '1h' ? 5 : availabilityTimeRange === '6h' ? 15 : availabilityTimeRange === '24h' ? 60 : availabilityTimeRange === '7d' ? 720 : 1440;
    
    const groupedData = filteredHistory.reduce((acc: Record<string, { total: number, successful: number, errors: number }>, item) => {
      const date = new Date(item.executedAt);
      let key: string;
      
      if (groupingInterval === 5) {
        const minutes = Math.floor(date.getMinutes() / 5) * 5;
        key = new Date(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), minutes).toISOString();
      } else if (groupingInterval === 15) {
        const minutes = Math.floor(date.getMinutes() / 15) * 15;
        key = new Date(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), minutes).toISOString();
      } else if (groupingInterval === 60) {
        key = new Date(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours()).toISOString();
      } else {
        key = new Date(date.getFullYear(), date.getMonth(), date.getDate()).toISOString();
      }
      
      if (!acc[key]) {
        acc[key] = { total: 0, successful: 0, errors: 0 };
      }
      acc[key].total++;
      if (item.success) {
        acc[key].successful++;
      } else {
        acc[key].errors++;
      }
      return acc;
    }, {});

    return Object.entries(groupedData)
      .sort(([a], [b]) => new Date(a).getTime() - new Date(b).getTime())
      .map(([timestamp, stats]) => {
        const date = new Date(timestamp);
        const successRate = (stats.successful / stats.total) * 100;
        let barColor = '#10b981';
        
        if (successRate < 100) {
          if (successRate >= 95) {
            barColor = '#f59e0b';
          } else {
            barColor = '#ef4444';
          }
        }
        
        return {
          time: groupingInterval <= 15 
            ? date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
            : groupingInterval === 60
            ? date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
            : date.toLocaleDateString([], { month: 'short', day: 'numeric' }),
          fullTime: date.toLocaleString(),
          count: stats.total,
          successful: stats.successful,
          errors: stats.errors,
          fill: barColor
        };
      });
  }, [filteredHistory, availabilityTimeRange]);

  // Auto-refresh effect
  useEffect(() => {
    if (!autoRefresh) return;
    
    const interval = setInterval(() => {
      fetchMonitorData();
      setLastRefresh(new Date());
    }, 30000); // Refresh every 30 seconds
    
    return () => clearInterval(interval);
  }, [autoRefresh, id]);

  const fetchMonitorData = async () => {
    if (!id) return;
    
    try {
      setLoading(true);
      
      // Fetch monitor history using the proper API endpoint
      const apiUrl = `${import.meta.env.VITE_API_BASE_URL}monitors/history/${id}`;
      console.log('Fetching from URL:', apiUrl);
      
      const historyResponse = await fetch(apiUrl);
      console.log('Response status:', historyResponse.status);
      
      if (!historyResponse.ok) {
        throw new Error(`Failed to fetch monitor history: ${historyResponse.status}`);
      }
      
      const historyData = await historyResponse.json();
      
      if (historyData.length === 0) {
        setLoading(false);
        return;
      }

      // Get monitor details from the first record
      const firstRecord = historyData[0];
      setMonitor({
        id: firstRecord.id,
        monitorId: firstRecord.monitorId,
        monitorName: firstRecord.monitorName,
        monitorType: firstRecord.monitorType,
        targetHost: firstRecord.targetHost,
        targetPort: firstRecord.targetPort,
        targetPath: firstRecord.targetPath,
        httpMethod: firstRecord.httpMethod,
        agentId: firstRecord.agentId,
        agentRegion: firstRecord.agentRegion,
        expectedStatusCode: firstRecord.expectedStatusCode,
        createdAt: firstRecord.executedAt
      });

      // Process history data with enhanced fields
      const processedHistory: MonitorHistory[] = historyData.map((item: any) => ({
        id: item.id,
        executedAt: item.executedAt,
        success: item.success,
        responseTime: item.responseTime,
        responseStatusCode: item.responseStatusCode,
        errorMessage: item.errorMessage,
        errorType: item.errorType,
        rawResponseBody: item.rawResponseBody,
        responseSizeBytes: item.responseSizeBytes,
        responseContentType: item.responseContentType,
        responseServer: item.responseServer,
        responseCacheStatus: item.responseCacheStatus,
        dnsLookupMs: item.dnsLookupMs,
        tcpConnectMs: item.tcpConnectMs,
        tlsHandshakeMs: item.tlsHandshakeMs,
        timeToFirstByteMs: item.timeToFirstByteMs,
        warningThresholdMs: item.warningThresholdMs,
        criticalThresholdMs: item.criticalThresholdMs,
        rawResponseHeaders: item.rawResponseHeaders,
        rawNetworkData: item.rawNetworkData
      }));
      
      setHistory(processedHistory);

      // Calculate comprehensive stats from history data
      const successfulChecks = processedHistory.filter(h => h.success).length;
      const totalChecks = processedHistory.length;
      const responseTimes = processedHistory.map(h => h.responseTime || 0).sort((a, b) => a - b);
      const avgResponseTime = responseTimes.reduce((sum, time) => sum + time, 0) / responseTimes.length;
      const successRate = totalChecks > 0 ? (successfulChecks / totalChecks) * 100 : 0;
      const uptime = successRate;
      const incidents = processedHistory.filter(h => !h.success).length;
      const lastFailure = processedHistory.find(h => !h.success);
      
      // Calculate percentiles
      const p95Index = Math.floor(responseTimes.length * 0.95);
      const p99Index = Math.floor(responseTimes.length * 0.99);
      const p95ResponseTime = responseTimes[p95Index] || 0;
      const p99ResponseTime = responseTimes[p99Index] || 0;
      
      // Calculate timing averages
      const dnsTimings = processedHistory.filter(h => h.dnsLookupMs).map(h => h.dnsLookupMs!);
      const connectTimings = processedHistory.filter(h => h.tcpConnectMs).map(h => h.tcpConnectMs!);
      const tlsTimings = processedHistory.filter(h => h.tlsHandshakeMs).map(h => h.tlsHandshakeMs!);
      
      const avgDnsTime = dnsTimings.length > 0 ? dnsTimings.reduce((a, b) => a + b, 0) / dnsTimings.length : 0;
      const avgConnectTime = connectTimings.length > 0 ? connectTimings.reduce((a, b) => a + b, 0) / connectTimings.length : 0;
      const avgTlsTime = tlsTimings.length > 0 ? tlsTimings.reduce((a, b) => a + b, 0) / tlsTimings.length : 0;
      
      // Error distribution
      const errorsByType = processedHistory
        .filter(h => !h.success && h.errorType)
        .reduce((acc: Record<string, number>, h) => {
          acc[h.errorType!] = (acc[h.errorType!] || 0) + 1;
          return acc;
        }, {});
      
      // Hourly stats for the last 24 hours
      const now = new Date();
      const last24Hours = new Date(now.getTime() - 24 * 60 * 60 * 1000);
      const recentHistory = processedHistory.filter(h => new Date(h.executedAt) > last24Hours);
      
      const hourlyStats = Array.from({ length: 24 }, (_, i) => {
        const hourStart = new Date(now.getTime() - (24 - i) * 60 * 60 * 1000);
        hourStart.setMinutes(0, 0, 0);
        const hourEnd = new Date(hourStart.getTime() + 60 * 60 * 1000);
        
        const hourData = recentHistory.filter(h => {
          const checkTime = new Date(h.executedAt);
          return checkTime >= hourStart && checkTime < hourEnd;
        });
        
        const hourSuccessful = hourData.filter(h => h.success).length;
        const hourTotal = hourData.length;
        const hourAvgResponseTime = hourTotal > 0 
          ? hourData.reduce((sum, h) => sum + (h.responseTime || 0), 0) / hourTotal 
          : 0;
        
        return {
          hour: hourStart.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
          checks: hourTotal,
          successRate: hourTotal > 0 ? (hourSuccessful / hourTotal) * 100 : 100,
          avgResponseTime: Math.round(hourAvgResponseTime)
        };
      });

      setStats({
        uptime: Math.round(uptime * 10) / 10,
        avgResponseTime: Math.round(avgResponseTime),
        totalChecks,
        successRate: Math.round(successRate * 10) / 10,
        incidents,
        lastIncident: lastFailure?.executedAt,
        p95ResponseTime: Math.round(p95ResponseTime),
        p99ResponseTime: Math.round(p99ResponseTime),
        avgDnsTime: Math.round(avgDnsTime),
        avgConnectTime: Math.round(avgConnectTime),
        avgTlsTime: Math.round(avgTlsTime),
        errorsByType,
        hourlyStats
      });

    } catch (error) {
      console.error('Error fetching monitor data:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        
        // Fetch monitor history using the proper API endpoint
        const apiUrl = `${import.meta.env.VITE_API_BASE_URL}monitors/history/${id}`;
        console.log('Fetching from URL:', apiUrl);
        
        const historyResponse = await fetch(apiUrl);
        console.log('Response status:', historyResponse.status);
        
        if (!historyResponse.ok) {
          throw new Error(`Failed to fetch monitor history: ${historyResponse.status}`);
        }
        
        const historyData = await historyResponse.json();
        
        if (historyData.length === 0) {
          setLoading(false);
          return;
        }

        // Get monitor details from the first record
        const firstRecord = historyData[0];
        setMonitor({
          id: firstRecord.id,
          monitorId: firstRecord.monitorId,
          monitorName: firstRecord.monitorName,
          monitorType: firstRecord.monitorType,
          targetHost: firstRecord.targetHost,
          targetPort: firstRecord.targetPort,
          targetPath: firstRecord.targetPath,
          httpMethod: firstRecord.httpMethod,
          agentId: firstRecord.agentId,
          agentRegion: firstRecord.agentRegion,
          expectedStatusCode: firstRecord.expectedStatusCode,
          createdAt: firstRecord.executedAt
        });

        // Process history data
        const processedHistory: MonitorHistory[] = historyData.map((item: any) => ({
          id: item.id,
          executedAt: item.executedAt,
          success: item.success,
          responseTime: item.responseTime,
          responseStatusCode: item.responseStatusCode,
          errorMessage: item.errorMessage,
          rawResponseBody: item.rawResponseBody
        }));
        
        setHistory(processedHistory);

        // Calculate comprehensive stats from history data
        const successfulChecks = processedHistory.filter(h => h.success).length;
        const totalChecks = processedHistory.length;
        const responseTimes = processedHistory.map(h => h.responseTime || 0).sort((a, b) => a - b);
        const avgResponseTime = responseTimes.reduce((sum, time) => sum + time, 0) / responseTimes.length;
        const successRate = totalChecks > 0 ? (successfulChecks / totalChecks) * 100 : 0;
        const uptime = successRate;
        const incidents = processedHistory.filter(h => !h.success).length;
        const lastFailure = processedHistory.find(h => !h.success);
        
        // Calculate percentiles
        const p95Index = Math.floor(responseTimes.length * 0.95);
        const p99Index = Math.floor(responseTimes.length * 0.99);
        const p95ResponseTime = responseTimes[p95Index] || 0;
        const p99ResponseTime = responseTimes[p99Index] || 0;
        
        // Calculate timing averages
        const dnsTimings = processedHistory.filter(h => h.dnsLookupMs).map(h => h.dnsLookupMs!);
        const connectTimings = processedHistory.filter(h => h.tcpConnectMs).map(h => h.tcpConnectMs!);
        const tlsTimings = processedHistory.filter(h => h.tlsHandshakeMs).map(h => h.tlsHandshakeMs!);
        
        const avgDnsTime = dnsTimings.length > 0 ? dnsTimings.reduce((a, b) => a + b, 0) / dnsTimings.length : 0;
        const avgConnectTime = connectTimings.length > 0 ? connectTimings.reduce((a, b) => a + b, 0) / connectTimings.length : 0;
        const avgTlsTime = tlsTimings.length > 0 ? tlsTimings.reduce((a, b) => a + b, 0) / tlsTimings.length : 0;
        
        // Error distribution
        const errorsByType = processedHistory
          .filter(h => !h.success && h.errorType)
          .reduce((acc: Record<string, number>, h) => {
            acc[h.errorType!] = (acc[h.errorType!] || 0) + 1;
            return acc;
          }, {});
        
        // Hourly stats for the last 24 hours
        const now = new Date();
        const last24Hours = new Date(now.getTime() - 24 * 60 * 60 * 1000);
        const recentHistory = processedHistory.filter(h => new Date(h.executedAt) > last24Hours);
        
        const hourlyStats = Array.from({ length: 24 }, (_, i) => {
          const hourStart = new Date(now.getTime() - (24 - i) * 60 * 60 * 1000);
          hourStart.setMinutes(0, 0, 0);
          const hourEnd = new Date(hourStart.getTime() + 60 * 60 * 1000);
          
          const hourData = recentHistory.filter(h => {
            const checkTime = new Date(h.executedAt);
            return checkTime >= hourStart && checkTime < hourEnd;
          });
          
          const hourSuccessful = hourData.filter(h => h.success).length;
          const hourTotal = hourData.length;
          const hourAvgResponseTime = hourTotal > 0 
            ? hourData.reduce((sum, h) => sum + (h.responseTime || 0), 0) / hourTotal 
            : 0;
          
          return {
            hour: hourStart.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
            checks: hourTotal,
            successRate: hourTotal > 0 ? (hourSuccessful / hourTotal) * 100 : 100,
            avgResponseTime: Math.round(hourAvgResponseTime)
          };
        });

        setStats({
          uptime: Math.round(uptime * 10) / 10,
          avgResponseTime: Math.round(avgResponseTime),
          totalChecks,
          successRate: Math.round(successRate * 10) / 10,
          incidents,
          lastIncident: lastFailure?.executedAt,
          p95ResponseTime: Math.round(p95ResponseTime),
          p99ResponseTime: Math.round(p99ResponseTime),
          avgDnsTime: Math.round(avgDnsTime),
          avgConnectTime: Math.round(avgConnectTime),
          avgTlsTime: Math.round(avgTlsTime),
          errorsByType,
          hourlyStats
        });

      } catch (error) {
        console.error('Error fetching monitor data:', error);
      } finally {
        setLoading(false);
      }
    };
    
    fetchData();
  }, [id]);

  const latestCheck = history[0];

  if (loading) {
    return (
      <div className="container mx-auto p-6">
        <div className="space-y-6">
          <div className="animate-pulse">
            <div className="h-8 bg-muted rounded mb-4"></div>
            <div className="grid grid-cols-4 gap-4">
              {[...Array(4)].map((_, i) => (
                <div key={i} className="h-24 bg-muted rounded"></div>
              ))}
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (!monitor || !stats) {
    return (
      <div className="container mx-auto p-6">
        <div className="text-center space-y-4">
          <AlertCircle className="w-12 h-12 text-muted-foreground mx-auto" />
          <h2 className="text-xl font-semibold">Monitor not found</h2>
          <Button onClick={() => navigate('/monitors/http')}>
            <ArrowLeft className="w-4 h-4 mr-2" />
            Back to Monitors
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white border-b border-gray-200">
        <div className="container mx-auto px-6 py-4">
          <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Button variant="ghost" onClick={() => navigate('/monitors/http')}>
            <ArrowLeft className="w-4 h-4 mr-2" />
            Back
          </Button>
          <div className="space-y-1">
            <div className="flex items-center gap-3">
              <div className={cn(
                "p-2 rounded-lg",
                monitor.monitorType === 'HTTPS' ? "bg-green-100 text-green-700" : "bg-blue-100 text-blue-700"
              )}>
                {monitor.monitorType === 'HTTPS' ? <Globe className="w-5 h-5" /> : <Network className="w-5 h-5" />}
              </div>
              <div>
                <h1 className="text-2xl font-bold">{monitor.monitorName}</h1>
                <div className="flex items-center gap-4 text-sm text-muted-foreground">
                  <div className="flex items-center gap-1">
                    <MapPin className="w-4 h-4" />
                    {monitor.agentRegion}
                  </div>
                  <Badge variant="outline">{monitor.monitorType}</Badge>
                  <span className="font-mono">
                    {monitor.httpMethod} {monitor.targetHost}
                    {monitor.targetPort && monitor.targetPort !== 80 && monitor.targetPort !== 443 ? `:${monitor.targetPort}` : ''}
                    {monitor.targetPath}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        {/* Controls */}
        <div className="flex items-center gap-2">
          <div className="flex items-center gap-2 px-3 py-2 bg-muted/50 rounded-lg">
            <Clock className="w-4 h-4 text-muted-foreground" />
            <span className="text-sm text-muted-foreground">
              Updated: {lastRefresh.toLocaleTimeString()}
            </span>
          </div>
          <Button
            variant="outline"
            size="sm"
            onClick={() => setAutoRefresh(!autoRefresh)}
          >
            <RefreshCw className={cn("w-4 h-4 mr-2", autoRefresh && "animate-spin")} />
            {autoRefresh ? 'Auto' : 'Manual'}
          </Button>
          <Button
            variant="outline"
            size="sm"
            onClick={() => {
              fetchMonitorData();
              setLastRefresh(new Date());
            }}
          >
            <RefreshCw className="w-4 h-4" />
          </Button>
        </div>
      </div>

      {/* Current Status */}
      <Card>
        <CardContent className="p-6">
          <div className="flex items-center justify-between">
            <div className="space-y-2">
              <h3 className="text-lg font-semibold">Current Status</h3>
              <StatusIndicator 
                status={latestCheck?.responseStatusCode} 
                success={latestCheck?.success || false}
              />
              <p className="text-sm text-muted-foreground">
                Last checked: {latestCheck ? new Date(latestCheck.executedAt).toLocaleString() : 'Never'}
              </p>
            </div>
            <div className="text-right space-y-1">
              <p className="text-2xl font-bold">{stats.uptime}%</p>
              <p className="text-sm text-muted-foreground">30-day uptime</p>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Enhanced Metrics */}
      <div className="grid grid-cols-2 lg:grid-cols-4 xl:grid-cols-6 gap-4">
        <MetricCard
          title="Avg Response Time"
          value={stats.avgResponseTime}
          suffix="ms"
          change={`P95: ${stats.p95ResponseTime}ms`}
          icon={<Zap className="w-4 h-4" />}
        />
        <MetricCard
          title="Success Rate"
          value={`${stats.successRate}%`}
          change={`${stats.totalChecks} total checks`}
          trend={stats.successRate > 95 ? "up" : stats.successRate > 90 ? "stable" : "down"}
          icon={<CheckCircle className="w-4 h-4" />}
        />
        <MetricCard
          title="P99 Response Time"
          value={stats.p99ResponseTime}
          suffix="ms"
          change={`P95: ${stats.p95ResponseTime}ms`}
          icon={<Timer className="w-4 h-4" />}
        />
        <MetricCard
          title="DNS Lookup"
          value={stats.avgDnsTime}
          suffix="ms avg"
          change="Network timing"
          icon={<Globe className="w-4 h-4" />}
        />
        <MetricCard
          title="TCP Connect"
          value={stats.avgConnectTime}
          suffix="ms avg"
          change="Connection timing"
          icon={<Network className="w-4 h-4" />}
        />
        <MetricCard
          title="Incidents"
          value={stats.incidents}
          change={stats.lastIncident ? `Last: ${new Date(stats.lastIncident).toLocaleDateString()}` : "No incidents"}
          trend={stats.incidents === 0 ? "up" : "down"}
          icon={<AlertCircle className="w-4 h-4" />}
        />
      </div>

      {/* Enhanced Charts and Analysis */}
      <Tabs defaultValue="overview" className="space-y-4">
        <div className="flex items-center justify-between">
          <TabsList>
            <TabsTrigger value="overview">Overview</TabsTrigger>
            <TabsTrigger value="performance">Performance</TabsTrigger>
            <TabsTrigger value="history">History</TabsTrigger>
            <TabsTrigger value="errors">Errors</TabsTrigger>
            <TabsTrigger value="config">Configuration</TabsTrigger>
          </TabsList>
          
          <div className="flex items-center gap-2">
            <Filter className="w-4 h-4 text-muted-foreground" />
            <Select value={timeRange} onValueChange={setTimeRange}>
              <SelectTrigger className="w-32">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="1h">Last Hour</SelectItem>
                <SelectItem value="24h">Last 24h</SelectItem>
                <SelectItem value="7d">Last 7 days</SelectItem>
                <SelectItem value="30d">Last 30 days</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>

        <TabsContent value="overview" className="space-y-6">
          <div className="grid grid-cols-1 gap-6">
            <ResponseTimeChart data={history} timeRange={timeRange} showDetails={true} />
          </div>
          <div className="space-y-6">
            <UptimeChart 
              data={history} 
              timeRange={timeRange} 
              availabilityTimeRange={availabilityTimeRange}
              onTimeRangeChange={setAvailabilityTimeRange}
              customStartDate={customStartDate}
              customEndDate={customEndDate}
              onCustomStartChange={setCustomStartDate}
              onCustomEndChange={setCustomEndDate}
            />
            <ErrorDistributionChart data={history} />
          </div>
        </TabsContent>

        <TabsContent value="performance" className="space-y-6">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <BarChart3 className="w-5 h-5" />
                  Hourly Performance
                </CardTitle>
                <CardDescription>Last 24 hours performance breakdown</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="h-64">
                  <ResponsiveContainer width="100%" height="100%">
                    <BarChart data={stats.hourlyStats}>
                      <CartesianGrid strokeDasharray="3 3" className="opacity-30" />
                      <XAxis dataKey="hour" fontSize={11} tickLine={false} axisLine={false} />
                      <YAxis fontSize={11} tickLine={false} axisLine={false} />
                      <Tooltip />
                      <Bar dataKey="avgResponseTime" fill="#3b82f6" name="Avg Response Time (ms)" />
                    </BarChart>
                  </ResponsiveContainer>
                </div>
              </CardContent>
            </Card>
            
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Activity className="w-5 h-5" />
                  Check Frequency
                </CardTitle>
                <CardDescription>Checks per hour distribution</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="h-64">
                  <ResponsiveContainer width="100%" height="100%">
                    <AreaChart data={stats.hourlyStats}>
                      <CartesianGrid strokeDasharray="3 3" className="opacity-30" />
                      <XAxis dataKey="hour" fontSize={11} tickLine={false} axisLine={false} />
                      <YAxis fontSize={11} tickLine={false} axisLine={false} />
                      <Tooltip />
                      <Area 
                        type="monotone" 
                        dataKey="checks" 
                        stroke="#10b981" 
                        fill="#10b981" 
                        fillOpacity={0.6}
                        name="Checks"
                      />
                    </AreaChart>
                  </ResponsiveContainer>
                </div>
              </CardContent>
            </Card>
          </div>
        </TabsContent>

        <TabsContent value="history">
          <HistoryTable 
            data={history} 
            timeRange={timeRange}
            onTimeRangeChange={setTimeRange}
          />
        </TabsContent>
        
        <TabsContent value="errors">
          <div className="space-y-6">
            <ErrorDistributionChart data={history} />
            {stats.incidents > 0 && (
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <AlertTriangle className="w-5 h-5" />
                    Recent Failures
                  </CardTitle>
                  <CardDescription>
                    Last {Math.min(stats.incidents, 10)} failed checks
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="space-y-2">
                    {history.filter(h => !h.success).slice(0, 10).map((item) => (
                      <div key={item.id} className="flex items-center justify-between p-3 border rounded-lg bg-red-50/50 dark:bg-red-900/5">
                        <div className="space-y-1">
                          <div className="flex items-center gap-2">
                            <AlertCircle className="w-4 h-4 text-red-600" />
                            <span className="text-sm font-medium">
                              {new Date(item.executedAt).toLocaleString()}
                            </span>
                            {item.errorType && (
                              <Badge variant="outline" className="text-xs">
                                {item.errorType}
                              </Badge>
                            )}
                          </div>
                          {item.errorMessage && (
                            <p className="text-xs text-red-600">{item.errorMessage}</p>
                          )}
                        </div>
                        <div className="text-sm text-muted-foreground">
                          {item.responseStatusCode || 'No Response'}
                        </div>
                      </div>
                    ))}
                  </div>
                </CardContent>
              </Card>
            )}
          </div>
        </TabsContent>

        <TabsContent value="config">
          <Card>
            <CardHeader>
              <CardTitle>Monitor Configuration</CardTitle>
              <CardDescription>
                Configuration details for this monitor
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <label className="text-sm font-medium">Monitor ID</label>
                    <code className="block p-2 bg-muted rounded text-sm">{monitor.monitorId}</code>
                  </div>
                  <div className="space-y-2">
                    <label className="text-sm font-medium">Agent ID</label>
                    <code className="block p-2 bg-muted rounded text-sm">{monitor.agentId}</code>
                  </div>
                  <div className="space-y-2">
                    <label className="text-sm font-medium">Target Host</label>
                    <code className="block p-2 bg-muted rounded text-sm">{monitor.targetHost}</code>
                  </div>
                  <div className="space-y-2">
                    <label className="text-sm font-medium">Expected Status</label>
                    <code className="block p-2 bg-muted rounded text-sm">{monitor.expectedStatusCode}</code>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
};

const MonitorDetail: React.FC = () => {
  return (
    <DashboardLayout>
      <MonitorDetailContent />
    </DashboardLayout>
  );
};

export default MonitorDetail;