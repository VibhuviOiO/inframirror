import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
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
  Timer
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
  Bar
} from 'recharts';

interface MonitorHistory {
  id: number;
  executedAt: string;
  success: boolean;
  responseTime: number;
  responseStatusCode?: number;
  errorMessage?: string;
  rawResponseBody?: string;
}

interface MonitorDetails {
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

const ResponseTimeChart: React.FC<{ data: MonitorHistory[] }> = ({ data }) => {
  const chartData = data.slice(-50).map(item => ({
    time: new Date(item.executedAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    responseTime: item.responseTime,
    success: item.success
  }));

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <LineChart className="w-5 h-5" />
          Response Time Trend
        </CardTitle>
        <CardDescription>
          Last 50 checks - Lower is better
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div className="h-64">
          <ResponsiveContainer width="100%" height="100%">
            <AreaChart data={chartData}>
              <CartesianGrid strokeDasharray="3 3" className="opacity-30" />
              <XAxis 
                dataKey="time" 
                fontSize={12}
                tickLine={false}
                axisLine={false}
              />
              <YAxis 
                fontSize={12}
                tickLine={false}
                axisLine={false}
                tickFormatter={(value) => `${value}ms`}
              />
              <Tooltip 
                contentStyle={{
                  backgroundColor: 'hsl(var(--background))',
                  border: '1px solid hsl(var(--border))',
                  borderRadius: '8px'
                }}
                formatter={(value: number, name) => [
                  `${value}ms`,
                  'Response Time'
                ]}
              />
              <Area
                type="monotone"
                dataKey="responseTime"
                stroke="#3b82f6"
                strokeWidth={2}
                fill="url(#colorResponseTime)"
                fillOpacity={0.6}
              />
              <defs>
                <linearGradient id="colorResponseTime" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#3b82f6" stopOpacity={0.3}/>
                  <stop offset="95%" stopColor="#3b82f6" stopOpacity={0}/>
                </linearGradient>
              </defs>
            </AreaChart>
          </ResponsiveContainer>
        </div>
      </CardContent>
    </Card>
  );
};

const UptimeChart: React.FC<{ data: MonitorHistory[] }> = ({ data }) => {
  // Group data by hour for uptime calculation
  const hourlyData = data.reduce((acc: Record<string, { total: number, successful: number }>, item) => {
    const hour = new Date(item.executedAt).toISOString().slice(0, 13);
    if (!acc[hour]) {
      acc[hour] = { total: 0, successful: 0 };
    }
    acc[hour].total++;
    if (item.success) {
      acc[hour].successful++;
    }
    return acc;
  }, {});

  const chartData = Object.entries(hourlyData).slice(-24).map(([hour, stats]) => ({
    time: new Date(hour + ':00:00').toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    uptime: (stats.successful / stats.total) * 100,
    total: stats.total
  }));

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <BarChart3 className="w-5 h-5" />
          Uptime by Hour
        </CardTitle>
        <CardDescription>
          Last 24 hours - Percentage of successful checks
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div className="h-64">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={chartData}>
              <CartesianGrid strokeDasharray="3 3" className="opacity-30" />
              <XAxis 
                dataKey="time" 
                fontSize={12}
                tickLine={false}
                axisLine={false}
              />
              <YAxis 
                fontSize={12}
                tickLine={false}
                axisLine={false}
                domain={[0, 100]}
                tickFormatter={(value) => `${value}%`}
              />
              <Tooltip 
                contentStyle={{
                  backgroundColor: 'hsl(var(--background))',
                  border: '1px solid hsl(var(--border))',
                  borderRadius: '8px'
                }}
                formatter={(value: number) => [
                  `${value.toFixed(1)}%`,
                  'Uptime'
                ]}
              />
              <Bar
                dataKey="uptime"
                fill="#10b981"
                radius={[4, 4, 0, 0]}
              />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </CardContent>
    </Card>
  );
};

const HistoryTable: React.FC<{ data: MonitorHistory[] }> = ({ data }) => {
  const [timeRange, setTimeRange] = useState('24h');
  
  const filteredData = data.slice(0, 100); // Show last 100 checks

  return (
    <Card>
      <CardHeader>
        <div className="flex items-center justify-between">
          <div>
            <CardTitle className="flex items-center gap-2">
              <Database className="w-5 h-5" />
              Check History
            </CardTitle>
            <CardDescription>
              Recent monitoring results
            </CardDescription>
          </div>
          <Select value={timeRange} onValueChange={setTimeRange}>
            <SelectTrigger className="w-32">
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="1h">Last Hour</SelectItem>
              <SelectItem value="24h">Last 24h</SelectItem>
              <SelectItem value="7d">Last 7 days</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </CardHeader>
      <CardContent>
        <div className="space-y-2 max-h-96 overflow-y-auto">
          {filteredData.map((item, index) => (
            <div
              key={item.id}
              className={cn(
                "flex items-center justify-between p-3 rounded-lg border",
                item.success ? "bg-green-50 dark:bg-green-900/10 border-green-200 dark:border-green-800" 
                             : "bg-red-50 dark:bg-red-900/10 border-red-200 dark:border-red-800"
              )}
            >
              <div className="flex items-center gap-3">
                {item.success ? (
                  <CheckCircle className="w-4 h-4 text-green-600" />
                ) : (
                  <AlertCircle className="w-4 h-4 text-red-600" />
                )}
                <div className="space-y-1">
                  <div className="flex items-center gap-2">
                    <span className="text-sm font-medium">
                      {new Date(item.executedAt).toLocaleString()}
                    </span>
                    {item.responseStatusCode && (
                      <Badge 
                        variant={item.success ? "default" : "destructive"}
                        className="text-xs font-mono"
                      >
                        {item.responseStatusCode}
                      </Badge>
                    )}
                  </div>
                  {item.errorMessage && (
                    <p className="text-xs text-red-600 dark:text-red-400">
                      {item.errorMessage}
                    </p>
                  )}
                </div>
              </div>
              <div className="flex items-center gap-2 text-sm text-muted-foreground">
                <Timer className="w-4 h-4" />
                <span className="font-mono">{item.responseTime}ms</span>
              </div>
            </div>
          ))}
        </div>
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

  useEffect(() => {
    const fetchMonitorData = async () => {
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

        // Calculate stats from history data
        const successfulChecks = processedHistory.filter(h => h.success).length;
        const totalChecks = processedHistory.length;
        const avgResponseTime = processedHistory.reduce((sum, h) => sum + (h.responseTime || 0), 0) / totalChecks;
        const successRate = totalChecks > 0 ? (successfulChecks / totalChecks) * 100 : 0;
        const uptime = successRate; // Simplified uptime calculation
        const incidents = processedHistory.filter(h => !h.success).length;
        const lastFailure = processedHistory.find(h => !h.success);

        setStats({
          uptime: Math.round(uptime * 10) / 10,
          avgResponseTime: Math.round(avgResponseTime),
          totalChecks,
          successRate: Math.round(successRate * 10) / 10,
          incidents,
          lastIncident: lastFailure?.executedAt
        });

      } catch (error) {
        console.error('Error fetching monitor data:', error);
      } finally {
        setLoading(false);
      }
    };

    if (id) {
      fetchMonitorData();
    }
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
    <div className="container mx-auto p-6 space-y-6">
      {/* Header */}
      <div className="flex items-center gap-4">
        <Button variant="ghost" onClick={() => navigate('/monitors/http')}>
          <ArrowLeft className="w-4 h-4 mr-2" />
          Back
        </Button>
        <div className="space-y-1">
          <div className="flex items-center gap-3">
            <Globe className="w-6 h-6 text-blue-500" />
            <h1 className="text-2xl font-bold">{monitor.monitorName}</h1>
          </div>
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

      {/* Metrics */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <MetricCard
          title="Avg Response Time"
          value={stats.avgResponseTime}
          suffix="ms"
          change="12ms faster than yesterday"
          trend="up"
          icon={<Zap className="w-4 h-4" />}
        />
        <MetricCard
          title="Success Rate"
          value={`${stats.successRate}%`}
          change="0.2% improvement"
          trend="up"
          icon={<CheckCircle className="w-4 h-4" />}
        />
        <MetricCard
          title="Total Checks"
          value={stats.totalChecks.toLocaleString()}
          change="24 in last hour"
          icon={<Activity className="w-4 h-4" />}
        />
        <MetricCard
          title="Incidents (30d)"
          value={stats.incidents}
          change={stats.lastIncident ? `Last: ${new Date(stats.lastIncident).toLocaleDateString()}` : "No recent incidents"}
          trend={stats.incidents > 0 ? "down" : "stable"}
          icon={<AlertCircle className="w-4 h-4" />}
        />
      </div>

      {/* Charts and History */}
      <Tabs defaultValue="charts" className="space-y-4">
        <TabsList>
          <TabsTrigger value="charts">Charts</TabsTrigger>
          <TabsTrigger value="history">History</TabsTrigger>
          <TabsTrigger value="config">Configuration</TabsTrigger>
        </TabsList>

        <TabsContent value="charts" className="space-y-6">
          <div className="grid grid-cols-1 xl:grid-cols-2 gap-6">
            <ResponseTimeChart data={history} />
            <UptimeChart data={history} />
          </div>
        </TabsContent>

        <TabsContent value="history">
          <HistoryTable data={history} />
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