import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { DashboardLayout } from "@/components/dashboard/DashboardLayout";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
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
  Network,
  Server,
  Eye,
  EyeOff,
  Settings,
  Info,
  Users,
  Shield,
  Bell,
  Play,
  Pause,
  MoreHorizontal,
  Download,
  Share,
  Star,
  ChevronDown,
  XCircle,
  AlertTriangle
} from 'lucide-react';
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
  errorType?: string;
  errorMessage?: string;
  agentRegion?: string;
}

interface MonitorDetails {
  id: number;
  monitorName: string;
  url: string;
  monitorType: string;
  frequency: number;
  enabled: boolean;
  warningThresholdMs?: number;
  criticalThresholdMs?: number;
  createdAt: string;
  updatedAt: string;
}

interface MonitorStats {
  totalChecks: number;
  successfulChecks: number;
  failedChecks: number;
  averageResponseTime: number;
  uptimePercentage: number;
}

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
  const [customStartTime, setCustomStartTime] = useState('');
  const [customEndTime, setCustomEndTime] = useState('');
  const [showCustomDatePicker, setShowCustomDatePicker] = useState(false);
  const [selectedDatacenter, setSelectedDatacenter] = useState<string>('all');

  // Filter history based on availability time range and datacenter
  const filteredHistory = React.useMemo(() => {
    const now = new Date();
    let startTime = new Date();
    let endTime = now;

    if (availabilityTimeRange === 'custom' && customStartDate && customEndDate) {
      // Handle custom date range
      const startDateTime = customStartTime 
        ? `${customStartDate}T${customStartTime}` 
        : `${customStartDate}T00:00:00`;
      const endDateTime = customEndTime 
        ? `${customEndDate}T${customEndTime}` 
        : `${customEndDate}T23:59:59`;
      
      startTime = new Date(startDateTime);
      endTime = new Date(endDateTime);
    } else {
      // Handle predefined time ranges
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

    return history.filter(item => {
      const itemDate = new Date(item.executedAt);
      const timeInRange = itemDate >= startTime && itemDate <= endTime;
      const datacenterMatch = selectedDatacenter === 'all' || item.agentRegion === selectedDatacenter;
      return timeInRange && datacenterMatch;
    });
  }, [history, availabilityTimeRange, customStartDate, customEndDate, customStartTime, customEndTime, selectedDatacenter]);

  // Get unique datacenters for dropdown
  const uniqueDatacenters = React.useMemo(() => {
    const datacenters = [...new Set(history.map(h => h.agentRegion).filter(Boolean))];
    return datacenters.sort();
  }, [history]);

  // Prepare chart data for histogram with more granular time buckets
  const chartData = React.useMemo(() => {
    if (filteredHistory.length === 0) return [];
    
    // Create more time buckets for better visualization
    const now = new Date();
    const buckets: Record<string, { total: number, successful: number, errors: number }> = {};
    let bucketSize: number;
    let bucketCount: number;
    
    // Determine bucket size based on time range to get ~50-100 bars
    switch (availabilityTimeRange) {
      case '1h':
        bucketSize = 2 * 60 * 1000; // 2 minutes
        bucketCount = 30;
        break;
      case '6h':
        bucketSize = 5 * 60 * 1000; // 5 minutes  
        bucketCount = 72;
        break;
      case '24h':
        bucketSize = 30 * 60 * 1000; // 30 minutes
        bucketCount = 48;
        break;
      case '7d':
        bucketSize = 3 * 60 * 60 * 1000; // 3 hours
        bucketCount = 56;
        break;
      case '30d':
        bucketSize = 12 * 60 * 60 * 1000; // 12 hours
        bucketCount = 60;
        break;
      default:
        bucketSize = 30 * 60 * 1000; // 30 minutes
        bucketCount = 48;
    }
    
    // Generate time buckets
    const startTime = new Date(now.getTime() - (bucketCount * bucketSize));
    for (let i = 0; i < bucketCount; i++) {
      const bucketStart = new Date(startTime.getTime() + (i * bucketSize));
      const bucketKey = bucketStart.toISOString();
      buckets[bucketKey] = { total: 0, successful: 0, errors: 0 };
    }
    
    // Fill buckets with data
    filteredHistory.forEach(item => {
      const itemTime = new Date(item.executedAt);
      const bucketIndex = Math.floor((itemTime.getTime() - startTime.getTime()) / bucketSize);
      if (bucketIndex >= 0 && bucketIndex < bucketCount) {
        const bucketStart = new Date(startTime.getTime() + (bucketIndex * bucketSize));
        const bucketKey = bucketStart.toISOString();
        if (buckets[bucketKey]) {
          buckets[bucketKey].total++;
          if (item.success) {
            buckets[bucketKey].successful++;
          } else {
            buckets[bucketKey].errors++;
          }
        }
      }
    });
    
    // Convert to chart data
    return Object.entries(buckets)
      .sort(([a], [b]) => new Date(a).getTime() - new Date(b).getTime())
      .map(([timestamp, stats]) => {
        const date = new Date(timestamp);
        const successRate = stats.total > 0 ? (stats.successful / stats.total) * 100 : 100;
        let barColor = '#e5e7eb'; // Gray for no data
        
        if (stats.total > 0) {
          if (successRate >= 100) {
            barColor = '#10b981'; // Green for 100% success
          } else if (successRate >= 95) {
            barColor = '#f59e0b'; // Yellow for 95-99% success
          } else {
            barColor = '#ef4444'; // Red for <95% success
          }
        }
        
        return {
          time: bucketSize < 3600000 
            ? date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
            : bucketSize < 86400000
            ? date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
            : date.toLocaleDateString([], { month: 'short', day: 'numeric' }),
          fullTime: date.toLocaleString(),
          count: stats.total,
          successful: stats.successful,
          errors: stats.errors,
          fill: barColor,
          successRate: Math.round(successRate)
        };
      });
  }, [filteredHistory, availabilityTimeRange]);

  // Auto-refresh effect
  useEffect(() => {
    if (!autoRefresh) return;
    
    const interval = setInterval(() => {
      fetchMonitorData();
      setLastRefresh(new Date());
    }, 30000);
    
    return () => clearInterval(interval);
  }, [autoRefresh, id]);

  const fetchMonitorData = async () => {
    if (!id) return;
    
    try {
      setLoading(true);
      
      // Fetch latest monitor data using the correct endpoint
      const monitorResponse = await fetch(`/api/monitors/latest/${id}`);
      if (monitorResponse.ok) {
        const monitorData = await monitorResponse.json();
        // Transform the data to match our interface
        const transformedMonitor = {
          id: monitorData.id || parseInt(id),
          monitorName: monitorData.monitorId || `Monitor ${id}`,
          url: monitorData.targetHost || 'Unknown URL',
          monitorType: monitorData.monitorType || 'HTTPS',
          frequency: 60, // Default frequency
          enabled: true, // Default enabled
          warningThresholdMs: monitorData.warningThresholdMs,
          criticalThresholdMs: monitorData.criticalThresholdMs,
          createdAt: monitorData.executedAt || new Date().toISOString(),
          updatedAt: monitorData.executedAt || new Date().toISOString()
        };
        setMonitor(transformedMonitor);
      }

      // Fetch monitor history using the correct endpoint
      const historyResponse = await fetch(`/api/monitors/history/${id}?limit=1000`);
      if (historyResponse.ok) {
        const historyData = await historyResponse.json();
        setHistory(Array.isArray(historyData) ? historyData : []);
      }

    } catch (error) {
      console.error('Error fetching monitor data:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMonitorData();
  }, [id]);

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <RefreshCw className="w-8 h-8 animate-spin mx-auto mb-4 text-blue-600" />
          <p className="text-muted-foreground">Loading monitor details...</p>
        </div>
      </div>
    );
  }

  if (!monitor) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <AlertCircle className="w-8 h-8 mx-auto mb-4 text-red-500" />
          <p className="text-muted-foreground">Monitor not found</p>
          <Button variant="outline" onClick={() => navigate('/monitors')} className="mt-4">
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
          {/* Row 1: Title and Controls */}
          <div className="flex items-start justify-between">
            <div className="flex items-center gap-4">
              <Button 
                variant="ghost" 
                size="sm" 
                onClick={() => navigate('/monitors')}
                className="flex items-center gap-2"
              >
                <ArrowLeft className="w-4 h-4" />
              </Button>
              
              <div>
                <h1 className="font-bold text-2xl text-gray-900">{monitor.monitorName || `Monitor ${id}`}</h1>
              </div>
            </div>
            
            <div className="flex items-center gap-4">
              <div className="text-right">
                <div className="text-2xl font-bold text-green-600">
                  {(() => {
                    const successCount = filteredHistory.filter(h => h.success).length;
                    const totalCount = filteredHistory.length;
                    const uptime = totalCount > 0 ? ((successCount / totalCount) * 100).toFixed(2) : '100.00';
                    return `${uptime}%`;
                  })()}
                </div>
                <div className="text-xs text-gray-500">Uptime</div>
              </div>
              
              <div className="flex flex-col items-end gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setAutoRefresh(!autoRefresh)}
                  className={`${autoRefresh ? 'bg-green-50 text-green-700 border-green-200' : ''}`}
                >
                  <RefreshCw className={`w-4 h-4 mr-2 ${autoRefresh ? 'animate-spin' : ''}`} />
                  {autoRefresh ? 'Auto-refresh ON' : 'Auto-refresh OFF'}
                </Button>
                
                {/* Status Badge */}
                <div className="flex items-center gap-2">
                  {(() => {
                    const successCount = filteredHistory.filter(h => h.success).length;
                    const totalCount = filteredHistory.length;
                    const uptime = totalCount > 0 ? (successCount / totalCount) * 100 : 100;
                    
                    if (uptime >= 99) {
                      return (
                        <>
                          <CheckCircle className="w-4 h-4 text-green-500" />
                          <span className="text-sm text-green-600 font-medium">Check is passing</span>
                        </>
                      );
                    } else if (uptime >= 95) {
                      return (
                        <>
                          <AlertTriangle className="w-4 h-4 text-yellow-500" />
                          <span className="text-sm text-yellow-600 font-medium">Check has issues</span>
                        </>
                      );
                    } else {
                      return (
                        <>
                          <XCircle className="w-4 h-4 text-red-500" />
                          <span className="text-sm text-red-600 font-medium">Check is failing</span>
                        </>
                      );
                    }
                  })()}
                </div>
              </div>
            </div>
          </div>
          
          {/* Row 2: Monitor Details */}
          <div className="mt-3 flex items-center justify-between">
            <div className="flex items-center gap-8 text-sm">
              <div className="flex items-center gap-2">
                <Globe className="w-4 h-4 text-gray-500" />
                <span className="font-medium">URL:</span>
                <span className="font-mono text-blue-600">{monitor.url}</span>
              </div>
              <div className="flex items-center gap-2">
                <Network className="w-4 h-4 text-gray-500" />
                <span className="font-medium">Method:</span>
                <Badge variant="secondary">GET</Badge>
              </div>
              <div className="flex items-center gap-2">
                <Shield className="w-4 h-4 text-gray-500" />
                <span className="font-medium">Protocol:</span>
                <Badge variant="outline">HTTPS</Badge>
              </div>
            </div>
            
            <div className="flex items-center gap-8 text-sm text-gray-600">
              <div className="flex items-center gap-2">
                <MapPin className="w-4 h-4" />
                <span>
                  {(() => {
                    const uniqueDatacenters = [...new Set(history.map(h => h.agentRegion).filter(Boolean))];
                    return `${uniqueDatacenters.length} datacenter${uniqueDatacenters.length !== 1 ? 's' : ''} • ${uniqueDatacenters.join(', ')}`;
                  })()}
                </span>
              </div>
              <div className="flex items-center gap-2">
                <Clock className="w-4 h-4" />
                <span>Every 30 seconds</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="flex h-screen">
        {/* Left Panel - Main Content */}
        <div className="flex-1 p-4">
          {/* Filter Bar */}
          <div className="bg-white rounded-lg border border-gray-200 p-4 mb-4">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-6">
                <div className="flex items-center gap-2">
                  <Filter className="w-4 h-4 text-gray-500" />
                  <Button 
                    variant={availabilityTimeRange === 'custom' ? 'default' : 'outline'} 
                    size="sm"
                    onClick={() => {
                      setShowCustomDatePicker(!showCustomDatePicker);
                      if (!showCustomDatePicker) {
                        setAvailabilityTimeRange('custom');
                      }
                    }}
                  >
                    Custom
                  </Button>
                </div>
                
                <div className="flex items-center gap-1">
                  <Button 
                    variant={availabilityTimeRange === '1h' ? 'default' : 'outline'} 
                    size="sm"
                    onClick={() => {
                      setAvailabilityTimeRange('1h');
                      setShowCustomDatePicker(false);
                    }}
                  >
                    Today
                  </Button>
                  <Button 
                    variant={availabilityTimeRange === '1h' ? 'default' : 'outline'} 
                    size="sm"
                    onClick={() => {
                      setAvailabilityTimeRange('1h');
                      setShowCustomDatePicker(false);
                    }}
                  >
                    1hr
                  </Button>
                  <Button 
                    variant={availabilityTimeRange === '6h' ? 'default' : 'outline'} 
                    size="sm"
                    onClick={() => {
                      setAvailabilityTimeRange('6h');
                      setShowCustomDatePicker(false);
                    }}
                  >
                    3hr
                  </Button>
                  <Button 
                    variant={availabilityTimeRange === '24h' ? 'default' : 'outline'} 
                    size="sm"
                    onClick={() => {
                      setAvailabilityTimeRange('24h');
                      setShowCustomDatePicker(false);
                    }}
                  >
                    24hr
                  </Button>
                  <Button 
                    variant={availabilityTimeRange === '7d' ? 'default' : 'outline'} 
                    size="sm"
                    onClick={() => {
                      setAvailabilityTimeRange('7d');
                      setShowCustomDatePicker(false);
                    }}
                  >
                    7d
                  </Button>
                  <Button 
                    variant={availabilityTimeRange === '30d' ? 'default' : 'outline'} 
                    size="sm"
                    onClick={() => {
                      setAvailabilityTimeRange('30d');
                      setShowCustomDatePicker(false);
                    }}
                  >
                    30d
                  </Button>
                </div>
                
                <Select value={selectedDatacenter} onValueChange={setSelectedDatacenter}>
                  <SelectTrigger className="w-48">
                    <div className="flex items-center gap-2">
                      <Server className="w-4 h-4 text-gray-500" />
                      <SelectValue placeholder="All Datacenters" />
                    </div>
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="all">All Datacenters ({uniqueDatacenters.length})</SelectItem>
                    {uniqueDatacenters.map((datacenter) => (
                      <SelectItem key={datacenter} value={datacenter}>
                        {datacenter}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              
              <div className="flex items-center gap-3">
                <Badge variant="secondary" className="bg-green-100 text-green-800">
                  <CheckCircle className="w-3 h-3 mr-1" />
                  Passed
                </Badge>
                <Badge variant="secondary" className="bg-red-100 text-red-800">
                  <XCircle className="w-3 h-3 mr-1" />
                  Failed
                </Badge>
                <Badge variant="secondary" className="bg-yellow-100 text-yellow-800">
                  <AlertTriangle className="w-3 h-3 mr-1" />
                  Degraded
                </Badge>
                <Button variant="ghost" size="sm">
                  <RefreshCw className="w-4 h-4 mr-2" />
                  Has retries
                </Button>
              </div>
            </div>
            
            {/* Custom Date/Time Picker */}
            {showCustomDatePicker && (
              <div className="mt-4 p-4 bg-gray-50 rounded-lg border">
                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label htmlFor="startDate" className="text-sm font-medium">From</Label>
                    <div className="flex gap-2">
                      <Input
                        id="startDate"
                        type="date"
                        value={customStartDate}
                        onChange={(e) => setCustomStartDate(e.target.value)}
                        className="flex-1"
                      />
                      <Input
                        type="time"
                        value={customStartTime}
                        onChange={(e) => setCustomStartTime(e.target.value)}
                        className="w-32"
                        placeholder="HH:MM"
                      />
                    </div>
                  </div>
                  
                  <div className="space-y-2">
                    <Label htmlFor="endDate" className="text-sm font-medium">To</Label>
                    <div className="flex gap-2">
                      <Input
                        id="endDate"
                        type="date"
                        value={customEndDate}
                        onChange={(e) => setCustomEndDate(e.target.value)}
                        className="flex-1"
                      />
                      <Input
                        type="time"
                        value={customEndTime}
                        onChange={(e) => setCustomEndTime(e.target.value)}
                        className="w-32"
                        placeholder="HH:MM"
                      />
                    </div>
                  </div>
                </div>
                
                <div className="flex justify-end gap-2 mt-3">
                  <Button 
                    variant="outline" 
                    size="sm"
                    onClick={() => {
                      setShowCustomDatePicker(false);
                      setAvailabilityTimeRange('24h');
                    }}
                  >
                    Cancel
                  </Button>
                  <Button 
                    size="sm"
                    onClick={() => {
                      if (customStartDate && customEndDate) {
                        setAvailabilityTimeRange('custom');
                        setShowCustomDatePicker(false);
                      }
                    }}
                    disabled={!customStartDate || !customEndDate}
                  >
                    Apply Custom Range
                  </Button>
                </div>
              </div>
            )}
          </div>

          {/* Summary Stats */}
          <div className="bg-white rounded-lg border border-gray-200 p-4 mb-4">
            <div className="text-sm text-gray-600 mb-3">
              Oct 12 11:00 - Oct 12 11:29 · {filteredHistory.length} results
            </div>
            
            <div className="grid grid-cols-3 gap-6 mb-4">
              <div>
                <div className="text-2xl font-light text-gray-900 mb-1">
                  {filteredHistory.filter(h => h.success).length}
                </div>
                <div className="flex items-center gap-2 text-sm">
                  <div className="w-2 h-2 bg-green-500 rounded-full"></div>
                  <span className="text-gray-600">Passed results: {filteredHistory.filter(h => h.success).length}</span>
                </div>
              </div>
              
              <div>
                <div className="text-2xl font-light text-gray-900 mb-1">0</div>
                <div className="flex items-center gap-2 text-sm">
                  <div className="w-2 h-2 bg-yellow-500 rounded-full"></div>
                  <span className="text-gray-600">Degraded results: 0</span>
                </div>
              </div>
              
              <div>
                <div className="text-2xl font-light text-gray-900 mb-1">
                  {filteredHistory.filter(h => !h.success).length}
                </div>
                <div className="flex items-center gap-2 text-sm">
                  <div className="w-2 h-2 bg-red-500 rounded-full"></div>
                  <span className="text-gray-600">Failed results: {filteredHistory.filter(h => !h.success).length}</span>
                </div>
              </div>
            </div>

            {/* Histogram Chart */}
            <div className="h-48">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart 
                  data={chartData} 
                  margin={{ top: 20, right: 5, left: 5, bottom: 20 }}
                  barCategoryGap="2%"
                >
                  <XAxis 
                    dataKey="time" 
                    fontSize={11}
                    tickLine={false}
                    axisLine={{ stroke: '#e5e7eb', strokeWidth: 1 }}
                    tick={{ fill: '#6b7280' }}
                    interval="preserveStartEnd"
                  />
                  <YAxis 
                    fontSize={11}
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
                    formatter={(value: number, name: string, props: any) => {
                      const data = props.payload;
                      if (data.count === 0) {
                        return ['No data', 'Checks'];
                      }
                      return [
                        `${data.count} checks (${data.successful} ✓, ${data.errors} ✗)`,
                        `${data.successRate}% uptime`
                      ];
                    }}
                    labelFormatter={(label, payload) => {
                      if (payload?.[0]) {
                        const data = payload[0].payload;
                        return data.fullTime;
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
          </div>

          {/* Error Groups */}
          <div className="bg-white rounded-lg border border-gray-200 p-4">
            <div className="flex items-center gap-2 mb-4">
              <h3 className="text-lg font-medium">Error Groups</h3>
              <Badge variant="secondary" className="bg-pink-100 text-pink-800">
                beta
              </Badge>
            </div>
            
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-gray-200">
                    <th className="text-left py-3 text-sm font-medium text-gray-600">MESSAGE</th>
                    <th className="text-left py-3 text-sm font-medium text-gray-600">FIRST SEEN</th>
                    <th className="text-left py-3 text-sm font-medium text-gray-600">LAST SEEN</th>
                    <th className="text-left py-3 text-sm font-medium text-gray-600">EVENTS</th>
                    <th className="text-left py-3 text-sm font-medium text-gray-600">LOCATIONS</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td colSpan={5} className="text-center py-8 text-gray-500">
                      No errors found for selected filters.
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        {/* Right Panel - Run Results */}
        <div className="w-80 bg-white border-l border-gray-200 flex flex-col">
          <div className="p-4 border-b border-gray-200">
            <h3 className="font-medium text-gray-900">Run results</h3>
            <div className="text-sm text-gray-500 mt-1">Last 24 hours</div>
          </div>
          
          <div className="flex-1 overflow-y-auto">
            {filteredHistory.slice(0, 50).map((result, index) => (
              <div key={result.id} className="flex items-center gap-3 p-3 border-b border-gray-100 hover:bg-gray-50 cursor-pointer">
                <div className="flex items-center gap-2 flex-1">
                  {result.success ? (
                    <CheckCircle className="w-4 h-4 text-green-500 flex-shrink-0" />
                  ) : (
                    <XCircle className="w-4 h-4 text-red-500 flex-shrink-0" />
                  )}
                  <div className="flex-1 min-w-0">
                    <div className="text-sm font-medium text-gray-900">
                      {result.agentRegion || 'Unknown Region'}
                    </div>
                    <div className="text-xs text-gray-500 flex items-center gap-2">
                      <span>{result.responseTime}ms</span>
                      {result.responseStatusCode && (
                        <Badge variant="outline" className="text-xs px-1 py-0">
                          {result.responseStatusCode}
                        </Badge>
                      )}
                    </div>
                  </div>
                </div>
                <div className="text-xs text-gray-500 flex-shrink-0">
                  {(() => {
                    const now = new Date();
                    const resultDate = new Date(result.executedAt);
                    const diffInMinutes = Math.floor((now.getTime() - resultDate.getTime()) / (1000 * 60));
                    
                    if (diffInMinutes < 1) return 'just now';
                    if (diffInMinutes < 60) return `${diffInMinutes}m`;
                    
                    const diffInHours = Math.floor(diffInMinutes / 60);
                    if (diffInHours < 24) return `${diffInHours}h`;
                    
                    const diffInDays = Math.floor(diffInHours / 24);
                    return `${diffInDays}d`;
                  })()}
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
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