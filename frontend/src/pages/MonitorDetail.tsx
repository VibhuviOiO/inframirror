import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { DashboardLayout } from "@/components/dashboard/DashboardLayout";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Checkbox } from "@/components/ui/checkbox";
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
  X,
  XCircle,
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
  AlertTriangle,
  Copy,
  ExternalLink
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
  agentId?: string;
}

interface MonitorDetails {
  id: number;
  monitorName: string;
  url: string;
  monitorType: string;
  targetHost?: string;
  targetPath?: string;
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
  const [availabilityTimeRange, setAvailabilityTimeRange] = useState('30m');
  const [customStartDateTime, setCustomStartDateTime] = useState<Date | null>(null);
  const [customEndDateTime, setCustomEndDateTime] = useState<Date | null>(null);
  const [showCustomDatePicker, setShowCustomDatePicker] = useState(false);
  const [selectedDatacenters, setSelectedDatacenters] = useState<string[]>(['all']);
  const [hoveredPerfPoint, setHoveredPerfPoint] = useState<number | null>(null);
  const [perfChartMousePos, setPerfChartMousePos] = useState<{ x: number; y: number } | null>(null);
  
  // Helper to get current time range
  const getTimeRange = React.useCallback(() => {
    const now = new Date();
    let startTime: Date;
    let endTime: Date = now;

    if (availabilityTimeRange === 'custom') {
      if (customStartDateTime && customEndDateTime) {
        startTime = customStartDateTime;
        endTime = customEndDateTime;
      } else {
        // Default to last 24 hours if custom not set
        startTime = new Date(now.getTime() - 24 * 60 * 60 * 1000);
      }
    } else {
      // Handle predefined time ranges
      switch (availabilityTimeRange) {
        case '5m':
          startTime = new Date(now.getTime() - 5 * 60 * 1000);
          break;
        case '15m':
          startTime = new Date(now.getTime() - 15 * 60 * 1000);
          break;
        case '30m':
          startTime = new Date(now.getTime() - 30 * 60 * 1000);
          break;
        case '1h':
          startTime = new Date(now.getTime() - 1 * 60 * 60 * 1000);
          break;
        case '4h':
          startTime = new Date(now.getTime() - 4 * 60 * 60 * 1000);
          break;
        case '24h':
          startTime = new Date(now.getTime() - 24 * 60 * 60 * 1000);
          break;
        case '2d':
          startTime = new Date(now.getTime() - 2 * 24 * 60 * 60 * 1000);
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

    return { startTime, endTime };
  }, [availabilityTimeRange, customStartDateTime, customEndDateTime]);

  // Filter history based on time range and region
  const filteredHistory = React.useMemo(() => {
    const { startTime, endTime } = getTimeRange();

    return history.filter(item => {
      const itemDate = new Date(item.executedAt);
      const timeInRange = itemDate >= startTime && itemDate <= endTime;
      const regionMatch = selectedDatacenters.includes('all') || 
                              (item.agentRegion && selectedDatacenters.includes(item.agentRegion));
      return timeInRange && regionMatch;
    });
  }, [history, getTimeRange, selectedDatacenters]);

  // Get unique regions for dropdown
  const uniqueDatacenters = React.useMemo(() => {
    const datacenters = [...new Set(history.map(h => h.agentRegion).filter(Boolean))];
    return datacenters.sort();
  }, [history]);

  // Prepare chart data - organized by time buckets with individual calls
  const chartData = React.useMemo(() => {
    console.log('Computing chartData for filteredHistory:', filteredHistory.length);
    
    if (filteredHistory.length === 0) {
      console.log('Returning empty chartData');
      return { buckets: [], calls: [] };
    }
    
    // Sort history by execution time
    const sortedHistory = [...filteredHistory].sort((a, b) => 
      new Date(a.executedAt).getTime() - new Date(b.executedAt).getTime()
    );
    
    // Determine bucket size based on time range
    let bucketSize: number;
    let bucketLabel: (date: Date) => string;
    
    switch (availabilityTimeRange) {
      case '1h':
        bucketSize = 5 * 60 * 1000; // 5 minutes
        bucketLabel = (date) => date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
        break;
      case '6h':
        bucketSize = 15 * 60 * 1000; // 15 minutes
        bucketLabel = (date) => date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
        break;
      case '24h':
        bucketSize = 60 * 60 * 1000; // 1 hour
        bucketLabel = (date) => date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
        break;
      case '7d':
        bucketSize = 6 * 60 * 60 * 1000; // 6 hours
        bucketLabel = (date) => date.toLocaleDateString([], { month: 'short', day: 'numeric', hour: '2-digit' });
        break;
      case '30d':
        bucketSize = 24 * 60 * 60 * 1000; // 1 day
        bucketLabel = (date) => date.toLocaleDateString([], { month: 'short', day: 'numeric' });
        break;
      default:
        bucketSize = 60 * 60 * 1000; // 1 hour
        bucketLabel = (date) => date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    }
    
    // Create buckets
    const buckets: Array<{
      startTime: Date;
      endTime: Date;
      label: string;
      calls: Array<any>;
      successCount: number;
      failureCount: number;
      warningCount: number;
      criticalCount: number;
    }> = [];
    
    if (sortedHistory.length > 0) {
      const firstTime = new Date(sortedHistory[0].executedAt);
      const lastTime = new Date(sortedHistory[sortedHistory.length - 1].executedAt);
      
      // Round down to bucket boundary
      const startTime = new Date(Math.floor(firstTime.getTime() / bucketSize) * bucketSize);
      const endTime = new Date(Math.ceil(lastTime.getTime() / bucketSize) * bucketSize);
      
      // Create bucket structure
      let currentBucketStart = new Date(startTime);
      while (currentBucketStart < endTime) {
        const bucketEnd = new Date(currentBucketStart.getTime() + bucketSize);
        buckets.push({
          startTime: new Date(currentBucketStart),
          endTime: bucketEnd,
          label: bucketLabel(currentBucketStart),
          calls: [],
          successCount: 0,
          failureCount: 0,
          warningCount: 0,
          criticalCount: 0
        });
        currentBucketStart = bucketEnd;
      }
    }
    
    // Transform each call and assign to buckets
    const allCalls = sortedHistory.map((item, index) => {
      const date = new Date(item.executedAt);
      const warningThreshold = monitor?.warningThresholdMs || 500;
      const criticalThreshold = monitor?.criticalThresholdMs || 1000;
      const responseTime = item.responseTime || 0;
      
      // Determine color and status
      let boxColor: string;
      let status: string;
      
      if (!item.success) {
        boxColor = '#ef4444'; // Red for failed
        status = 'Failed';
      } else if (responseTime >= criticalThreshold) {
        boxColor = '#f97316'; // Orange for critical
        status = 'Critical';
      } else if (responseTime >= warningThreshold) {
        boxColor = '#f59e0b'; // Yellow for warning
        status = 'Warning';
      } else {
        boxColor = '#10b981'; // Green for healthy
        status = 'Healthy';
      }
      
      const call = {
        id: item.id,
        index: index,
        time: date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' }),
        fullTime: date.toLocaleString(),
        timestamp: item.executedAt,
        responseTime: responseTime,
        success: item.success,
        statusCode: item.responseStatusCode,
        region: item.agentRegion,
        errorType: item.errorType,
        errorMessage: item.errorMessage,
        fill: boxColor,
        status: status,
        warningThreshold: warningThreshold,
        criticalThreshold: criticalThreshold,
      };
      
      // Find appropriate bucket
      const bucketIndex = buckets.findIndex(b => 
        date >= b.startTime && date < b.endTime
      );
      
      if (bucketIndex >= 0) {
        buckets[bucketIndex].calls.push(call);
        if (!item.success) {
          buckets[bucketIndex].failureCount++;
        } else if (responseTime >= criticalThreshold) {
          buckets[bucketIndex].criticalCount++;
        } else if (responseTime >= warningThreshold) {
          buckets[bucketIndex].warningCount++;
        } else {
          buckets[bucketIndex].successCount++;
        }
      }
      
      return call;
    });
    
    return { buckets, calls: allCalls };
  }, [filteredHistory, availabilityTimeRange, monitor]);

  // Compute heatmap data: adaptive grid based on call frequency and time range
  const heatmapData = React.useMemo(() => {
    if (!filteredHistory || filteredHistory.length === 0) {
      console.log('No filtered history for heatmap');
      return { 
        timePeriods: [], 
        rows: [], 
        viewMode: 'minutes',
        lineChartData: [],
        avgResponseTime: []
      };
    }

    console.log('Computing adaptive heatmap data');
    
    // Get time range
    const range = getTimeRange();
    const { startTime: rangeStart, endTime: rangeEnd } = range;
    
    const totalDurationMs = rangeEnd.getTime() - rangeStart.getTime();
    const totalHours = totalDurationMs / (1000 * 60 * 60);
    const totalDays = totalHours / 24;
    
    // Calculate average call frequency
    const avgCallIntervalMs = filteredHistory.length > 1 
      ? totalDurationMs / filteredHistory.length 
      : 60000; // default to 60s
    
    const avgCallIntervalSeconds = avgCallIntervalMs / 1000;
    
    // Determine view mode and granularity based on call frequency and time range
    let viewMode: 'seconds' | 'minutes' | 'hours' | 'days';
    let bucketSizeMs: number;
    let rowCount: number;
    let rowUnit: string;
    let bucketLabel: (date: Date) => string;
    
    // Adaptive logic based on call frequency
    if (avgCallIntervalSeconds <= 30 && totalHours <= 1) {
      // High frequency (≤30s interval) + short range → seconds view
      viewMode = 'seconds';
      bucketSizeMs = 60 * 1000; // 1-minute buckets on X-axis
      rowCount = 60; // 60 seconds on Y-axis
      rowUnit = 's';
      bucketLabel = (date: Date) => date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    } else if (avgCallIntervalSeconds <= 120 && totalHours <= 6) {
      // Medium frequency (≤2min interval) + medium range → minutes view
      viewMode = 'minutes';
      bucketSizeMs = totalHours <= 1 ? 5 * 60 * 1000 : 15 * 60 * 1000;
      rowCount = 60; // 60 minutes on Y-axis
      rowUnit = 'm';
      bucketLabel = (date: Date) => date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    } else if (totalHours <= 48) {
      // Hours view for 1-2 days
      viewMode = 'hours';
      bucketSizeMs = 60 * 60 * 1000; // 1-hour buckets
      rowCount = 24; // 24 hours on Y-axis
      rowUnit = 'h';
      bucketLabel = (date: Date) => date.toLocaleDateString([], { month: 'short', day: 'numeric', hour: '2-digit' });
    } else {
      // Days view for longer periods
      viewMode = 'days';
      bucketSizeMs = 24 * 60 * 60 * 1000; // Daily buckets
      rowCount = 7; // 7 days of week on Y-axis
      rowUnit = 'd';
      bucketLabel = (date: Date) => date.toLocaleDateString([], { month: 'short', day: 'numeric' });
    }
    
    // Create time period buckets (X-axis)
    const timePeriods: Array<{
      label: string;
      startTime: Date;
      endTime: Date;
      rowData: Map<number, Array<any>>; // row index -> calls
    }> = [];
    
    let currentTime = new Date(rangeStart);
    while (currentTime < rangeEnd) {
      const bucketEnd = new Date(Math.min(currentTime.getTime() + bucketSizeMs, rangeEnd.getTime()));
      timePeriods.push({
        label: bucketLabel(currentTime),
        startTime: new Date(currentTime),
        endTime: bucketEnd,
        rowData: new Map()
      });
      currentTime = bucketEnd;
    }
    
    // Function to get row index based on view mode
    const getRowIndex = (callTime: Date): number => {
      if (viewMode === 'seconds') {
        return callTime.getSeconds(); // 0-59
      } else if (viewMode === 'minutes') {
        return callTime.getMinutes(); // 0-59
      } else if (viewMode === 'hours') {
        return callTime.getHours(); // 0-23
      } else { // days
        return callTime.getDay(); // 0-6 (Sunday-Saturday)
      }
    };
    
    // Organize calls into time periods and rows
    filteredHistory.forEach(item => {
      const callTime = new Date(item.executedAt);
      const rowIndex = getRowIndex(callTime);
      
      // Find the time period bucket
      const periodIndex = timePeriods.findIndex(p => 
        callTime >= p.startTime && callTime < p.endTime
      );
      
      if (periodIndex >= 0) {
        const period = timePeriods[periodIndex];
        if (!period.rowData.has(rowIndex)) {
          period.rowData.set(rowIndex, []);
        }
        
        const warningThreshold = monitor?.warningThresholdMs || 500;
        const criticalThreshold = monitor?.criticalThresholdMs || 1000;
        const responseTime = item.responseTime || 0;
        
        period.rowData.get(rowIndex)!.push({
          id: item.id,
          time: callTime.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' }),
          fullTime: callTime.toLocaleString(),
          timestamp: item.executedAt,
          responseTime: responseTime,
          success: item.success,
          statusCode: item.responseStatusCode,
          region: item.agentRegion,
          errorType: item.errorType,
          errorMessage: item.errorMessage,
          warningThreshold,
          criticalThreshold
        });
      }
    });
    
    // Get row label based on view mode
    const getRowLabel = (index: number): string => {
      if (viewMode === 'seconds') {
        return index % 10 === 0 ? `${index}s` : '';
      } else if (viewMode === 'minutes') {
        return index % 5 === 0 ? `${index}m` : '';
      } else if (viewMode === 'hours') {
        return `${index.toString().padStart(2, '0')}h`;
      } else { // days
        const days = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
        return days[index];
      }
    };
    
    // Create rows based on view mode
    const rows = Array.from({ length: rowCount }, (_, rowIndex) => ({
      rowIndex,
      label: getRowLabel(rowIndex),
      unit: rowUnit,
      periods: timePeriods.map(period => ({
        calls: period.rowData.get(rowIndex) || [],
        count: (period.rowData.get(rowIndex) || []).length
      }))
    }));
    
    // Calculate line chart data (average response time per period)
    const lineChartData = timePeriods.map((period, idx) => {
      const allCallsInPeriod: any[] = [];
      period.rowData.forEach(calls => allCallsInPeriod.push(...calls));
      
      const avgResponseTime = allCallsInPeriod.length > 0
        ? allCallsInPeriod.reduce((sum, call) => sum + call.responseTime, 0) / allCallsInPeriod.length
        : 0;
      
      const successRate = allCallsInPeriod.length > 0
        ? (allCallsInPeriod.filter(c => c.success).length / allCallsInPeriod.length) * 100
        : 100;
      
      return {
        periodIndex: idx,
        label: period.label,
        avgResponseTime: Math.round(avgResponseTime),
        successRate: Math.round(successRate * 10) / 10,
        totalCalls: allCallsInPeriod.length,
        timestamp: period.startTime.getTime()
      };
    });
    
    console.log('Heatmap computed:', { 
      viewMode, 
      timePeriods: timePeriods.length, 
      rows: rows.length,
      avgCallInterval: avgCallIntervalSeconds.toFixed(1) + 's'
    });
    
    return { 
      timePeriods, 
      rows, 
      viewMode, 
      lineChartData,
      rowUnit 
    };
  }, [filteredHistory, availabilityTimeRange, monitor]);

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
      
      // Fetch monitor history - this contains all the data we need
      const historyResponse = await fetch(`/api/monitors/history/${id}?limit=1000`);
      if (historyResponse.ok) {
        const historyData = await historyResponse.json();
        const historyArray = Array.isArray(historyData) ? historyData : [];
        setHistory(historyArray);
        
        // Extract monitor details from the latest history record
        if (historyArray.length > 0) {
          const latestRecord = historyArray[0];
          const transformedMonitor = {
            id: latestRecord.monitorId || parseInt(id),
            monitorName: latestRecord.monitorId || `Monitor ${id}`,
            url: latestRecord.targetHost || 'Unknown URL',
            monitorType: latestRecord.monitorType || 'HTTPS',
            targetHost: latestRecord.targetHost || '',
            targetPath: latestRecord.targetPath || '/',
            frequency: 60, // Default frequency
            enabled: true, // Default enabled
            warningThresholdMs: latestRecord.warningThresholdMs || 500,
            criticalThresholdMs: latestRecord.criticalThresholdMs || 1000,
            createdAt: latestRecord.executedAt || new Date().toISOString(),
            updatedAt: latestRecord.executedAt || new Date().toISOString()
          };
          console.log('Transformed monitor:', transformedMonitor);
          setMonitor(transformedMonitor);
        }
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
    <DashboardLayout>
      <Card className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-6 py-6">
          {/* Row 1: Back Button, Title and Controls */}
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-3">
              <Button 
                variant="ghost" 
                size="icon"
                onClick={() => navigate('/monitors')}
                className="hover:bg-gray-100"
              >
                <ArrowLeft className="w-5 h-5" />
              </Button>
              
              <div>
                <h1 className="text-3xl font-bold text-gray-900">
                  {monitor.monitorName || `Monitor ${id}`}
                </h1>
              </div>
            </div>
            
            <div className="flex items-center gap-6">
              <Button
                variant="outline"
                size="default"
                onClick={() => setAutoRefresh(!autoRefresh)}
                className={`${autoRefresh ? 'bg-green-50 text-green-700 border-green-300 hover:bg-green-100' : 'hover:bg-gray-50'}`}
              >
                <RefreshCw className={`w-4 h-4 mr-2 ${autoRefresh ? 'animate-spin' : ''}`} />
                {autoRefresh ? 'Auto-refresh ON' : 'Auto-refresh OFF'}
              </Button>
            </div>
          </div>
          
          {/* Row 2: Monitor Metadata */}
          <div className="flex items-center gap-6 text-sm mb-4">
            <div className="flex items-center gap-2 text-gray-600">
              <Network className="w-4 h-4 text-gray-400" />
              <span className="text-gray-500">Method:</span>
              <Badge variant="secondary" className="font-medium">GET</Badge>
            </div>
            <div className="w-px h-4 bg-gray-300"></div>
            <div className="flex items-center gap-2 text-gray-600">
              <Shield className="w-4 h-4 text-gray-400" />
              <span className="text-gray-500">Protocol:</span>
              <Badge variant="outline" className="font-medium">
                {monitor.monitorType?.toUpperCase() || 'HTTPS'}
              </Badge>
            </div>
            <div className="w-px h-4 bg-gray-300"></div>
            <div className="flex items-center gap-2 text-gray-600">
              <MapPin className="w-4 h-4 text-gray-400" />
              <span className="text-gray-500">Regions:</span>
              <span className="font-medium text-gray-900">
                {(() => {
                  const uniqueDatacenters = [...new Set(history.map(h => h.agentRegion).filter(Boolean))];
                  return uniqueDatacenters.length;
                })()}
              </span>
            </div>
            <div className="w-px h-4 bg-gray-300"></div>
            <div className="flex items-center gap-2 text-gray-600">
              <Clock className="w-4 h-4 text-gray-400" />
              <span className="text-gray-500">Interval:</span>
              <span className="font-medium text-gray-900">30s</span>
            </div>
            <div className="w-px h-4 bg-gray-300"></div>
            <div className="flex items-center gap-2 text-gray-600">
              <TrendingUp className="w-4 h-4 text-gray-400" />
              <span className="text-gray-500">Uptime:</span>
              <span className="font-medium text-green-700">
                {(() => {
                  const successCount = filteredHistory.filter(h => h.success).length;
                  const totalCount = filteredHistory.length;
                  const uptime = totalCount > 0 ? ((successCount / totalCount) * 100).toFixed(2) : '100.00';
                  return `${uptime}%`;
                })()}
              </span>
            </div>
          </div>
          
          {/* Row 3: Monitoring URL - Terminal Style */}
          <div className="bg-gray-50 rounded-md p-2 border border-gray-200">
            <div className="flex items-center justify-between gap-4">
              <div className="flex items-center gap-3 flex-1 min-w-0">
                <div className="flex items-center gap-2 shrink-0">
                  <div className="w-3 h-3 rounded-full bg-green-500"></div>
                  <span className="text-gray-600 text-xs font-semibold uppercase tracking-wider">Endpoint</span>
                </div>
                <code className="text-gray-800 text-sm font-mono flex-1 truncate">
                  {(() => {
                    // Always construct complete URL from monitorType, targetHost, and targetPath
                    const protocol = (monitor.monitorType || 'http').toLowerCase();
                    const host = monitor.targetHost || monitor.url;
                    const path = monitor.targetPath || '/';
                    
                    // If we have targetHost, construct full URL
                    if (monitor.targetHost) {
                      const constructedUrl = `${protocol}://${host}${path}`;
                      return constructedUrl;
                    }
                    
                    // Otherwise use the url field
                    return monitor.url;
                  })()}
                </code>
              </div>
              <div className="flex items-center gap-1 shrink-0">
                <Button
                  variant="ghost"
                  size="icon"
                  className="h-7 w-7 text-gray-500 hover:text-gray-700 hover:bg-gray-200"
                  onClick={() => {
                    const protocol = (monitor.monitorType || 'http').toLowerCase();
                    const host = monitor.targetHost || monitor.url;
                    const path = monitor.targetPath || '/';
                    const urlToCopy = monitor.targetHost 
                      ? `${protocol}://${host}${path}`
                      : monitor.url;
                    navigator.clipboard.writeText(urlToCopy);
                  }}
                  title="Copy URL"
                >
                  <Copy className="w-4 h-4" />
                </Button>
                <Button
                  variant="ghost"
                  size="icon"
                  className="h-7 w-7 text-gray-500 hover:text-gray-700 hover:bg-gray-200"
                  onClick={() => {
                    const protocol = (monitor.monitorType || 'http').toLowerCase();
                    const host = monitor.targetHost || monitor.url;
                    const path = monitor.targetPath || '/';
                    const urlToOpen = monitor.targetHost 
                      ? `${protocol}://${host}${path}`
                      : monitor.url;
                    window.open(urlToOpen, '_blank');
                  }}
                  title="Open URL in new tab"
                >
                  <ExternalLink className="w-4 h-4" />
                </Button>
              </div>
            </div>
          </div>
        </div>

        {/* Main Content Area - Checkly Style */}
        <div className="max-w-7xl mx-auto px-6 py-6">
        {/* Time Range Filter */}
        <div className="flex items-center justify-between mb-6">
          {/* Region Multi-Select Filter */}
          <div className="flex items-center gap-2">
            <Label className="text-xs text-gray-400">
              Regions:
            </Label>
            <Popover>
              <PopoverTrigger asChild>
                <Button
                  variant="outline"
                  size="sm"
                  className="h-8 text-xs min-w-[200px] justify-between"
                >
                  <span className="truncate">
                    {selectedDatacenters.includes('all')
                      ? 'All Regions'
                      : selectedDatacenters.length === 0
                      ? 'Select regions'
                      : selectedDatacenters.length === 1
                      ? selectedDatacenters[0]
                      : `${selectedDatacenters.length} selected`}
                  </span>
                  <ChevronDown className="h-3 w-3 opacity-50" />
                </Button>
              </PopoverTrigger>
              <PopoverContent className="w-64 p-0" align="start">
                <div className="p-2">
                  <div className="flex items-center space-x-2">
                    <Checkbox
                      id="all-regions"
                      checked={selectedDatacenters.includes('all')}
                      onCheckedChange={(checked) => {
                        if (checked) {
                          setSelectedDatacenters(['all']);
                        } else {
                          setSelectedDatacenters([]);
                        }
                      }}
                    />
                    <Label
                      htmlFor="all-regions"
                      className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
                    >
                      All Regions
                    </Label>
                  </div>
                  {uniqueDatacenters.map((datacenter) => (
                    <div key={datacenter} className="flex items-center space-x-2 mt-2">
                      <Checkbox
                        id={datacenter}
                        checked={selectedDatacenters.includes(datacenter)}
                        onCheckedChange={(checked) => {
                          if (checked) {
                            if (selectedDatacenters.includes('all')) {
                              setSelectedDatacenters([datacenter]);
                            } else {
                              setSelectedDatacenters([...selectedDatacenters, datacenter]);
                            }
                          } else {
                            setSelectedDatacenters(selectedDatacenters.filter(d => d !== datacenter));
                          }
                        }}
                      />
                      <Label
                        htmlFor={datacenter}
                        className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
                      >
                        {datacenter}
                      </Label>
                    </div>
                  ))}
                </div>
              </PopoverContent>
            </Popover>
          </div>
          
          <div className="flex items-center gap-1 flex-wrap">
            <Button
              variant={availabilityTimeRange === '5m' ? 'default' : 'ghost'}
              size="sm"
              onClick={() => setAvailabilityTimeRange('5m')}
              className="text-xs h-8"
            >
              5 MIN
            </Button>
            <Button
              variant={availabilityTimeRange === '15m' ? 'default' : 'ghost'}
              size="sm"
              onClick={() => setAvailabilityTimeRange('15m')}
              className="text-xs h-8"
            >
              15 MIN
            </Button>
            <Button
              variant={availabilityTimeRange === '30m' ? 'default' : 'ghost'}
              size="sm"
              onClick={() => setAvailabilityTimeRange('30m')}
              className="text-xs h-8"
            >
              30 MIN
            </Button>
            <Button
              variant={availabilityTimeRange === '1h' ? 'default' : 'ghost'}
              size="sm"
              onClick={() => setAvailabilityTimeRange('1h')}
              className="text-xs h-8"
            >
              1 HOUR
            </Button>
            <Button
              variant={availabilityTimeRange === '4h' ? 'default' : 'ghost'}
              size="sm"
              onClick={() => setAvailabilityTimeRange('4h')}
              className="text-xs h-8"
            >
              4 HOURS
            </Button>
            <Button
              variant={availabilityTimeRange === '24h' ? 'default' : 'ghost'}
              size="sm"
              onClick={() => setAvailabilityTimeRange('24h')}
              className="text-xs h-8"
            >
              24 HOURS
            </Button>
            <Button
              variant={availabilityTimeRange === '2d' ? 'default' : 'ghost'}
              size="sm"
              onClick={() => setAvailabilityTimeRange('2d')}
              className="text-xs h-8"
            >
              2 DAYS
            </Button>
            <Button
              variant={availabilityTimeRange === '7d' ? 'default' : 'ghost'}
              size="sm"
              onClick={() => setAvailabilityTimeRange('7d')}
              className="text-xs h-8"
            >
              1 WEEK
            </Button>
            <Button
              variant={availabilityTimeRange === '30d' ? 'default' : 'ghost'}
              size="sm"
              onClick={() => setAvailabilityTimeRange('30d')}
              className="text-xs h-8"
            >
              1 MONTH
            </Button>
          </div>
        </div>

        {/* Performance Comparison Line Chart */}
        <Card>
          <CardHeader className="pb-4">
            <CardTitle className="text-base font-medium">
              Region Performance Comparison
            </CardTitle>
            <CardDescription className="text-xs">
              Response time trends across regions with threshold indicators
            </CardDescription>
          </CardHeader>
          <CardContent>
            {(() => {
              const warningThreshold = monitor?.warningThresholdMs || 500;
              const criticalThreshold = monitor?.criticalThresholdMs || 1000;
              
              // Get unique regions
              const uniqueDatacenters = Array.from(
                new Set(filteredHistory.map(item => item.agentRegion).filter(Boolean))
              ).sort();
              
              // Generate colors for each region
              const datacenterColors = [
                '#3b82f6', // blue
                '#10b981', // green
                '#f59e0b', // amber
                '#8b5cf6', // purple
                '#ec4899', // pink
                '#06b6d4', // cyan
                '#f97316', // orange
              ];
              
              // Create time buckets
              const { startTime, endTime } = getTimeRange();
              const timeRange = endTime.getTime() - startTime.getTime();
              const numBuckets = 50;
              const bucketSize = timeRange / numBuckets;
              
              // Prepare data points for each region
              const lineChartData = Array.from({ length: numBuckets }, (_, i) => {
                const bucketStart = startTime.getTime() + (i * bucketSize);
                const bucketEnd = bucketStart + bucketSize;
                
                const dataPoint: any = {
                  time: new Date(bucketStart).toLocaleTimeString([], { 
                    hour: '2-digit', 
                    minute: '2-digit',
                    hour12: false 
                  }),
                  warningThreshold,
                  criticalThreshold,
                };
                
                // Calculate average response time for each region in this bucket
                uniqueDatacenters.forEach((dc) => {
                  const dcRecords = filteredHistory.filter(record => {
                    const recordTime = new Date(record.executedAt).getTime();
                    return record.agentRegion === dc && 
                           recordTime >= bucketStart && 
                           recordTime < bucketEnd &&
                           record.success &&
                           record.responseTime;
                  });
                  
                  if (dcRecords.length > 0) {
                    const avgResponseTime = dcRecords.reduce((sum, r) => sum + (r.responseTime || 0), 0) / dcRecords.length;
                    dataPoint[dc!] = Math.round(avgResponseTime);
                  } else {
                    dataPoint[dc!] = null;
                  }
                });
                
                return dataPoint;
              });
              
              return (
                <ResponsiveContainer width="100%" height={200}>
                  <RechartsLineChart data={lineChartData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
                    <XAxis 
                      dataKey="time" 
                      stroke="#6b7280"
                      tick={{ fontSize: 11 }}
                      interval="preserveStartEnd"
                    />
                    <YAxis 
                      stroke="#6b7280"
                      tick={{ fontSize: 11 }}
                      label={{ value: 'Response Time (ms)', angle: -90, position: 'insideLeft', style: { fontSize: 11, fill: '#6b7280' } }}
                    />
                    <Tooltip
                      contentStyle={{
                        backgroundColor: '#ffffff',
                        border: '1px solid #e5e7eb',
                        borderRadius: '6px',
                        fontSize: '12px',
                      }}
                      labelStyle={{ color: '#374151', fontWeight: 'bold' }}
                    />
                    <Legend 
                      wrapperStyle={{ fontSize: '12px' }}
                      iconType="line"
                    />
                    
                    {/* Threshold Lines (Dotted) */}
                    <Line
                      type="monotone"
                      dataKey="warningThreshold"
                      stroke="#f59e0b"
                      strokeWidth={2}
                      strokeDasharray="5 5"
                      dot={false}
                      name="Warning Threshold"
                      isAnimationActive={false}
                    />
                    <Line
                      type="monotone"
                      dataKey="criticalThreshold"
                      stroke="#ef4444"
                      strokeWidth={2}
                      strokeDasharray="5 5"
                      dot={false}
                      name="Critical Threshold"
                      isAnimationActive={false}
                    />
                    
                    {/* Region Performance Lines (Solid) */}
                    {uniqueDatacenters.map((dc, index) => (
                      <Line
                        key={dc}
                        type="monotone"
                        dataKey={dc!}
                        stroke={datacenterColors[index % datacenterColors.length]}
                        strokeWidth={2}
                        dot={{ r: 2 }}
                        connectNulls
                        name={dc!}
                      />
                    ))}
                  </RechartsLineChart>
                </ResponsiveContainer>
              );
            })()}
          </CardContent>
        </Card>

        {/* Monitor Cards Grouped by Region */}
        <div className="space-y-4">
          {(() => {
            // Group history by region
            const datacenterGroups = filteredHistory.reduce((acc, record) => {
              const dc = record.agentRegion || 'unknown';
              if (!acc[dc]) {
                acc[dc] = [];
              }
              acc[dc].push(record);
              return acc;
            }, {} as Record<string, MonitorHistory[]>);

            return Object.entries(datacenterGroups).map(([datacenter, records]) => {
              // Calculate metrics for this region
              const successCount = records.filter(r => r.success).length;
              const totalCount = records.length;
              const availability = totalCount > 0 ? ((successCount / totalCount) * 100).toFixed(0) : '100';
              
              // Calculate percentiles
              const responseTimes = records
                .filter(r => r.responseTime)
                .map(r => r.responseTime || 0)
                .sort((a, b) => a - b);
              
              const p95Index = Math.floor(responseTimes.length * 0.95);
              const p99Index = Math.floor(responseTimes.length * 0.99);
              const p95 = responseTimes[p95Index] || 0;
              const p99 = responseTimes[p99Index] || 0;
              
              // Determine if there are any issues
              const hasFailures = successCount < totalCount;
              const warningThreshold = monitor?.warningThresholdMs || 500;
              const criticalThreshold = monitor?.criticalThresholdMs || 1000;
              const hasSlowResponses = p99 > warningThreshold;
              
              // Calculate status counts
              let healthyCount = 0;
              let warningCount = 0;
              let criticalCount = 0;
              let failedCount = 0;
              
              records.forEach(r => {
                if (!r.success) {
                  failedCount++;
                } else {
                  const rt = r.responseTime || 0;
                  if (rt >= criticalThreshold) {
                    criticalCount++;
                  } else if (rt >= warningThreshold) {
                    warningCount++;
                  } else {
                    healthyCount++;
                  }
                }
              });
              
              // Calculate change percentage (mock for now)
              const changePercent = '+0.1';
              
              return (
                <div key={datacenter} className="bg-white rounded-lg border border-gray-200 p-6 hover:shadow-md transition-shadow">
                  {/* Header Row */}
                  <div className="flex items-start justify-between mb-4">
                    <div className="flex items-center gap-3">
                      <div className={`w-10 h-10 rounded-lg flex items-center justify-center ${
                        hasFailures ? 'bg-red-100' : 'bg-green-100'
                      }`}>
                        {hasFailures ? (
                          <XCircle className="w-5 h-5 text-red-600" />
                        ) : (
                          <CheckCircle className="w-5 h-5 text-green-600" />
                        )}
                      </div>
                      <div>
                        <h3 className="text-lg font-semibold text-gray-900">
                          {datacenter}
                        </h3>
                        <div className="flex items-center gap-2 mt-1">
                          <Badge variant="secondary" className="text-xs font-normal">
                            {(() => {
                              // Get agentId from the first record
                              const agentId = records.length > 0 && records[0].agentId 
                                ? records[0].agentId 
                                : `${datacenter}-agent`;
                              return agentId;
                            })()}
                          </Badge>
                          <div className="flex items-center gap-1 text-xs text-gray-500">
                            <Clock className="w-3 h-3" />
                            <span>
                              {(() => {
                                if (records.length === 0) return 'No recent checks';
                                const latest = records[0];
                                const now = new Date();
                                const executedAt = new Date(latest.executedAt);
                                const diffMs = now.getTime() - executedAt.getTime();
                                const diffMins = Math.floor(diffMs / (1000 * 60));
                                
                                if (diffMins < 1) return 'Just now';
                                if (diffMins < 60) return `${diffMins} minute${diffMins !== 1 ? 's' : ''} ago`;
                                const diffHours = Math.floor(diffMins / 60);
                                return `${diffHours} hour${diffHours !== 1 ? 's' : ''} ago`;
                              })()}
                            </span>
                          </div>
                        </div>
                      </div>
                    </div>
                    
                    {/* Right Side Metrics */}
                    <div className="flex items-start gap-8">
                      <div>
                        <div className="text-xs text-gray-500 uppercase mb-1">Availability</div>
                        <div className="text-3xl font-bold text-gray-900">{availability}<span className="text-base font-normal">%</span></div>
                        {/* Status Dots */}
                        <div className="flex items-center gap-3 mt-2">
                          {healthyCount > 0 && (
                            <div className="flex items-center gap-1">
                              <div className="w-2 h-2 rounded-full bg-green-500"></div>
                              <span className="text-xs text-gray-600">{healthyCount}</span>
                            </div>
                          )}
                          {warningCount > 0 && (
                            <div className="flex items-center gap-1">
                              <div className="w-2 h-2 rounded-full bg-yellow-500"></div>
                              <span className="text-xs text-gray-600">{warningCount}</span>
                            </div>
                          )}
                          {criticalCount > 0 && (
                            <div className="flex items-center gap-1">
                              <div className="w-2 h-2 rounded-full bg-orange-500"></div>
                              <span className="text-xs text-gray-600">{criticalCount}</span>
                            </div>
                          )}
                          {failedCount > 0 && (
                            <div className="flex items-center gap-1">
                              <div className="w-2 h-2 rounded-full bg-red-500"></div>
                              <span className="text-xs text-gray-600">{failedCount}</span>
                            </div>
                          )}
                        </div>
                      </div>
                      <div>
                        <div className="text-xs text-gray-500 uppercase mb-1">P95</div>
                        <div className="text-3xl font-bold text-gray-900">{p95}<span className="text-base font-normal text-gray-500">ms</span></div>
                      </div>
                      <div>
                        <div className="text-xs text-gray-500 uppercase mb-1">P99</div>
                        <div className="text-3xl font-bold text-gray-900">{p99}<span className="text-base font-normal text-gray-500">ms</span></div>
                        {hasSlowResponses && (
                          <div className="mt-1 px-2 py-0.5 bg-red-100 text-red-700 text-xs font-medium rounded inline-block">
                            {changePercent}%
                          </div>
                        )}
                      </div>
                    </div>
                  </div>
                  
                  {/* Bar Chart Visualization */}
                  <div className="relative">
                    <ResponsiveContainer width="100%" height={120}>
                      <BarChart
                        data={(() => {
                          // Create time buckets for visualization
                          const now = new Date();
                          
                          // Calculate time range in milliseconds
                          let timeRangeMs: number;
                          switch(availabilityTimeRange) {
                            case '5m':
                              timeRangeMs = 5 * 60 * 1000;
                              break;
                            case '15m':
                              timeRangeMs = 15 * 60 * 1000;
                              break;
                            case '30m':
                              timeRangeMs = 30 * 60 * 1000;
                              break;
                            case '1h':
                              timeRangeMs = 60 * 60 * 1000;
                              break;
                            case '4h':
                              timeRangeMs = 4 * 60 * 60 * 1000;
                              break;
                            case '24h':
                              timeRangeMs = 24 * 60 * 60 * 1000;
                              break;
                            case '2d':
                              timeRangeMs = 2 * 24 * 60 * 60 * 1000;
                              break;
                            case '7d':
                              timeRangeMs = 7 * 24 * 60 * 60 * 1000;
                              break;
                            case '30d':
                              timeRangeMs = 30 * 24 * 60 * 60 * 1000;
                              break;
                            default:
                              timeRangeMs = 24 * 60 * 60 * 1000;
                          }
                          
                          const startTime = new Date(now.getTime() - timeRangeMs);
                          
                          const numBuckets = 50;
                          const bucketSize = timeRangeMs / numBuckets;
                          
                          return Array.from({ length: numBuckets }, (_, i) => {
                            const bucketStart = startTime.getTime() + i * bucketSize;
                            const bucketEnd = bucketStart + bucketSize;
                            const bucketStartDate = new Date(bucketStart);
                            
                            const bucketRecords = records.filter(r => {
                              const recordTime = new Date(r.executedAt).getTime();
                              return recordTime >= bucketStart && recordTime < bucketEnd;
                            });
                            
                            // Count by status
                            let healthy = 0;
                            let warning = 0;
                            let critical = 0;
                            let failed = 0;
                            
                            bucketRecords.forEach(r => {
                              if (!r.success) {
                                failed++;
                              } else {
                                const warningThreshold = monitor?.warningThresholdMs || 500;
                                const criticalThreshold = monitor?.criticalThresholdMs || 1000;
                                const rt = r.responseTime || 0;
                                
                                if (rt >= criticalThreshold) {
                                  critical++;
                                } else if (rt >= warningThreshold) {
                                  warning++;
                                } else {
                                  healthy++;
                                }
                              }
                            });
                            
                            return {
                              index: i,
                              time: bucketStartDate.toLocaleTimeString([], { 
                                hour: '2-digit', 
                                minute: '2-digit',
                                hour12: false 
                              }),
                              timestamp: bucketStartDate.toLocaleString(),
                              healthy,
                              warning,
                              critical,
                              failed,
                              total: bucketRecords.length
                            };
                          });
                        })()}
                        margin={{ top: 10, right: 10, left: 0, bottom: 20 }}
                      >
                        <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" vertical={false} />
                        <XAxis 
                          dataKey="time" 
                          stroke="#6b7280"
                          tick={{ fill: '#6b7280', fontSize: 10 }}
                          interval="preserveStartEnd"
                          angle={0}
                        />
                        <YAxis 
                          stroke="#9ca3af"
                          tick={{ fill: '#9ca3af', fontSize: 11 }}
                          width={35}
                        />
                        <Tooltip
                          content={({ active, payload }) => {
                            if (active && payload && payload.length) {
                              const data = payload[0].payload;
                              return (
                                <div className="bg-gray-900 text-white text-xs rounded-lg shadow-lg p-3">
                                  <div className="font-semibold mb-1">{data.timestamp}</div>
                                  <div className="font-semibold mb-2">Checks: {data.total}</div>
                                  {data.healthy > 0 && (
                                    <div className="flex items-center gap-2">
                                      <div className="w-3 h-3 rounded" style={{ backgroundColor: '#10b981' }} />
                                      <span>Healthy: {data.healthy}</span>
                                    </div>
                                  )}
                                  {data.warning > 0 && (
                                    <div className="flex items-center gap-2">
                                      <div className="w-3 h-3 rounded" style={{ backgroundColor: '#fbbf24' }} />
                                      <span>Warning: {data.warning}</span>
                                    </div>
                                  )}
                                  {data.critical > 0 && (
                                    <div className="flex items-center gap-2">
                                      <div className="w-3 h-3 rounded" style={{ backgroundColor: '#f97316' }} />
                                      <span>Critical: {data.critical}</span>
                                    </div>
                                  )}
                                  {data.failed > 0 && (
                                    <div className="flex items-center gap-2">
                                      <div className="w-3 h-3 rounded" style={{ backgroundColor: '#ef4444' }} />
                                      <span>Failed: {data.failed}</span>
                                    </div>
                                  )}
                                </div>
                              );
                            }
                            return null;
                          }}
                        />
                        <Bar dataKey="healthy" stackId="a" fill="#10b981" radius={[0, 0, 0, 0]} />
                        <Bar dataKey="warning" stackId="a" fill="#fbbf24" radius={[0, 0, 0, 0]} />
                        <Bar dataKey="critical" stackId="a" fill="#f97316" radius={[0, 0, 0, 0]} />
                        <Bar dataKey="failed" stackId="a" fill="#ef4444" radius={[2, 2, 0, 0]} />
                      </BarChart>
                    </ResponsiveContainer>
                    
                    {/* Time Labels */}
                    <div className="flex justify-between text-xs text-gray-500 mt-1 ml-9">
                      <span>
                        {(() => {
                          switch(availabilityTimeRange) {
                            case '5m': return '5 minutes ago';
                            case '15m': return '15 minutes ago';
                            case '30m': return '30 minutes ago';
                            case '1h': return '1 hour ago';
                            case '4h': return '4 hours ago';
                            case '24h': return 'about 4 hours ago';
                            case '2d': return '2 days ago';
                            case '7d': return 'about 7 days ago';
                            case '30d': return 'about 30 days ago';
                            default: return 'Last period';
                          }
                        })()}
                      </span>
                      <span>Last checks</span>
                      <span>now</span>
                    </div>
                  </div>
                </div>
              );
            });
          })()}
        </div>
      </div>
      </Card>
    </DashboardLayout>
  );
};

export default function MonitorDetail() {
  return <MonitorDetailContent />;
}
