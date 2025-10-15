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
  const [customStartDateTime, setCustomStartDateTime] = useState<Date | null>(null);
  const [customEndDateTime, setCustomEndDateTime] = useState<Date | null>(null);
  const [showCustomDatePicker, setShowCustomDatePicker] = useState(false);
  const [selectedDatacenter, setSelectedDatacenter] = useState<string>('all');
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
        case '1h':
          startTime = new Date(now.getTime() - 1 * 60 * 60 * 1000);
          break;
        case '3h':
          startTime = new Date(now.getTime() - 3 * 60 * 60 * 1000);
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

    return { startTime, endTime };
  }, [availabilityTimeRange, customStartDateTime, customEndDateTime]);

  // Filter history based on time range and datacenter
  const filteredHistory = React.useMemo(() => {
    const { startTime, endTime } = getTimeRange();

    return history.filter(item => {
      const itemDate = new Date(item.executedAt);
      const timeInRange = itemDate >= startTime && itemDate <= endTime;
      const datacenterMatch = selectedDatacenter === 'all' || item.agentRegion === selectedDatacenter;
      return timeInRange && datacenterMatch;
    });
  }, [history, getTimeRange, selectedDatacenter]);

  // Get unique datacenters for dropdown
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
            frequency: 60, // Default frequency
            enabled: true, // Default enabled
            warningThresholdMs: latestRecord.warningThresholdMs || 500,
            criticalThresholdMs: latestRecord.criticalThresholdMs || 1000,
            createdAt: latestRecord.executedAt || new Date().toISOString(),
            updatedAt: latestRecord.executedAt || new Date().toISOString()
          };
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

      {/* Main Content Area - Checkly Style */}
      <div className="max-w-7xl mx-auto px-6 py-6">
        {/* Time Range Filter */}
        <div className="flex items-center justify-end gap-2 mb-6">
          <Button
            variant={availabilityTimeRange === '24h' ? 'default' : 'ghost'}
            size="sm"
            onClick={() => {
              setAvailabilityTimeRange('24h');
              setShowCustomDatePicker(false);
            }}
            className="font-medium"
          >
            24 HOURS
          </Button>
          <Button
            variant={availabilityTimeRange === '7d' ? 'default' : 'ghost'}
            size="sm"
            onClick={() => {
              setAvailabilityTimeRange('7d');
              setShowCustomDatePicker(false);
            }}
            className="font-medium"
          >
            7 DAYS
          </Button>
          <Button
            variant={availabilityTimeRange === '30d' ? 'default' : 'ghost'}
            size="sm"
            onClick={() => {
              setAvailabilityTimeRange('30d');
              setShowCustomDatePicker(false);
            }}
            className="font-medium"
          >
            30 DAYS
          </Button>
          <div className="flex items-center gap-2 ml-4">
            <Button
              variant="ghost"
              size="sm"
              onClick={() => {
                const now = new Date();
                const oneDayAgo = new Date(now.getTime() - 24 * 60 * 60 * 1000);
                navigate(`/monitors`);
              }}
            >
              ← Back
            </Button>
            <span className="text-gray-400">|</span>
            <span className="text-sm text-gray-600">
              {(() => {
                const pages = Math.ceil(filteredHistory.length / 100);
                return `1 of ${Math.max(pages, 2)}`;
              })()}
            </span>
            <Button variant="ghost" size="sm" disabled>
              →
            </Button>
          </div>
        </div>

        {/* Monitor Cards Grouped by Datacenter */}
        <div className="space-y-4">
          {(() => {
            // Group history by datacenter
            const datacenterGroups = filteredHistory.reduce((acc, record) => {
              const dc = record.agentRegion || 'unknown';
              if (!acc[dc]) {
                acc[dc] = [];
              }
              acc[dc].push(record);
              return acc;
            }, {} as Record<string, MonitorHistory[]>);

            return Object.entries(datacenterGroups).map(([datacenter, records]) => {
              // Calculate metrics for this datacenter
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
              const hasSlowResponses = p99 > warningThreshold;
              
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
                          {monitor?.monitorName || 'API Check'}
                        </h3>
                        <div className="flex items-center gap-2 mt-1">
                          <Badge variant="secondary" className="text-xs font-normal">
                            API
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
                    <div className="flex items-end gap-0.5 h-32 mb-2">
                      {(() => {
                        // Create time buckets for visualization
                        const now = new Date();
                        const timeRangeMs = availabilityTimeRange === '24h' ? 24 * 60 * 60 * 1000 :
                                          availabilityTimeRange === '7d' ? 7 * 24 * 60 * 60 * 1000 :
                                          30 * 24 * 60 * 60 * 1000;
                        const startTime = new Date(now.getTime() - timeRangeMs);
                        
                        // Determine bucket size based on range
                        const numBuckets = 50;
                        const bucketSize = timeRangeMs / numBuckets;
                        
                        const buckets = Array.from({ length: numBuckets }, (_, i) => {
                          const bucketStart = startTime.getTime() + i * bucketSize;
                          const bucketEnd = bucketStart + bucketSize;
                          
                          const bucketRecords = records.filter(r => {
                            const recordTime = new Date(r.executedAt).getTime();
                            return recordTime >= bucketStart && recordTime < bucketEnd;
                          });
                          
                          const bucketSuccess = bucketRecords.filter(r => r.success).length;
                          const bucketTotal = bucketRecords.length;
                          
                          return {
                            success: bucketSuccess,
                            total: bucketTotal,
                            hasData: bucketTotal > 0
                          };
                        });
                        
                        const maxCount = Math.max(...buckets.map(b => b.total), 1);
                        
                        return buckets.map((bucket, idx) => {
                          const height = bucket.hasData ? Math.max((bucket.total / maxCount) * 100, 10) : 0;
                          const allSuccess = bucket.total > 0 && bucket.success === bucket.total;
                          
                          return (
                            <div
                              key={idx}
                              className="flex-1 rounded-sm transition-all hover:opacity-80 cursor-pointer relative group"
                              style={{
                                height: `${height}%`,
                                backgroundColor: allSuccess ? '#10b981' : bucket.hasData ? '#ef4444' : '#e5e7eb',
                                minHeight: bucket.hasData ? '8px' : '4px'
                              }}
                              title={bucket.hasData ? `${bucket.success}/${bucket.total} successful` : 'No data'}
                            />
                          );
                        });
                      })()}
                    </div>
                    
                    {/* Time Labels */}
                    <div className="flex justify-between text-xs text-gray-500">
                      <span>
                        {(() => {
                          const timeRangeMs = availabilityTimeRange === '24h' ? 24 * 60 * 60 * 1000 :
                                            availabilityTimeRange === '7d' ? 7 * 24 * 60 * 60 * 1000 :
                                            30 * 24 * 60 * 60 * 1000;
                          const start = new Date(new Date().getTime() - timeRangeMs);
                          return availabilityTimeRange === '24h' ? 'about 4 hours ago' :
                                 availabilityTimeRange === '7d' ? 'about 7 days ago' :
                                 'about 30 days ago';
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
    </DashboardLayout>
  );
};

export default function MonitorDetail() {
  return <MonitorDetailContent />;
}
