# HTTP Monitoring Frontend

Modern observability UI for HTTP API monitoring, inspired by industry-leading tools like Checkly, Uptime Kuma, and Gatus.

## 🎨 Design Philosophy

### Visual Inspiration
- **Checkly**: Clean API-centric monitoring cards with clear status indicators
- **Uptime Kuma**: Status tiles with color-coded health indicators and charts  
- **Gatus**: Compact summary tables with comprehensive metrics display

### Design Principles
- **Modern Observability**: Clean, data-dense layouts optimized for large screens
- **Status-First**: Immediate visual feedback with color-coded health indicators
- **Responsive Design**: Grid-based layouts that adapt to screen sizes
- **Smooth Animations**: Subtle transitions and loading states
- **Light/Dark Mode**: Full theme support with proper color contrast
- **Performance Focus**: Optimized for real-time monitoring dashboards

## 📊 Pages Overview

### 1. Monitoring Dashboard (`/monitoring`)
**Purpose**: High-level system health overview
- **Global Metrics**: Total monitors, uptime percentage, incidents
- **Regional Health**: Status breakdown by datacenter/region
- **Real-time Charts**: Response time trends, uptime distribution
- **Auto-refresh**: Configurable intervals with manual refresh control
- **Time Range Selection**: 1h, 24h, 7d views

**Key Components**:
- `StatusCard` - Key metrics with trend indicators
- `RegionHealthCard` - Regional status with health bars
- `ResponseTimeChart` - Multi-region performance trends
- `UptimeDistribution` - Pie chart of monitor status
- `MonitorStatusRow` - Recent activity feed

### 2. Monitors List (`/monitors`)
**Purpose**: Comprehensive monitor management and filtering
- **Monitor Cards**: Individual API endpoint cards with status
- **Advanced Filtering**: Status, region, search functionality
- **Performance Metrics**: Response time badges with color coding
- **Status Indicators**: Visual health status with icons and colors
- **Real-time Updates**: Live status with "just now" timestamps

**Key Components**:
- `MonitorCard` - Individual monitor display with hover effects
- `MonitorStatusBadge` - Color-coded status indicators
- `ResponseTimeBadge` - Performance-based color coding
- `StatCard` - Overview statistics with trends

### 3. Monitor Detail (`/monitors/:id`)
**Purpose**: Deep-dive analysis for individual monitors
- **Current Status**: Live status with detailed metrics
- **Historical Charts**: Response time trends and uptime bars
- **Check History**: Detailed log of recent monitoring results
- **Configuration View**: Monitor setup and parameters
- **Performance Analytics**: Min/max/avg response times with standard deviation

**Key Components**:
- `StatusIndicator` - Live status with animated indicators  
- `MetricCard` - Individual KPI displays with trends
- `ResponseTimeChart` - Area chart with gradient fills
- `UptimeChart` - Hourly uptime percentage bars
- `HistoryTable` - Paginated results with error details

## 🎯 UI/UX Features

### Color System
```typescript
// Health Status Colors
- Healthy (200-299): Green (#10b981)
- Degraded (300-399): Yellow (#f59e0b) 
- Down (400+): Red (#ef4444)
- Unknown/Loading: Gray (#6b7280)

// Response Time Colors (Performance-based)
- Fast (<200ms): Green
- Good (200-500ms): Yellow
- Slow (500-1000ms): Orange
- Critical (>1000ms): Red
```

### Interactive Elements
- **Hover Effects**: Card scaling and shadow enhancement
- **Status Animations**: Pulsing indicators for down/degraded services
- **Loading States**: Skeleton screens and shimmer effects
- **Smooth Transitions**: 200-300ms easing for all state changes
- **Keyboard Shortcuts**: Ctrl/Cmd+B for sidebar toggle

### Responsive Layout
- **Mobile**: Single column stack layout
- **Tablet**: 2-column grid with adjusted card sizes
- **Desktop**: 3-4 column grids for optimal information density
- **Large Screens**: Full-width charts with detailed metrics

## 📈 Data Integration

### Monitor Schema Alignment
All components use the exact Prisma schema fields:
```typescript
interface Monitor {
  // Core fields
  monitorId: string;
  monitorName: string;
  monitorType: 'HTTP' | 'TCP' | 'UDP' | 'PING' | 'DNS';
  
  // HTTP-specific
  targetHost: string;
  targetPort?: number;
  targetPath?: string;
  httpMethod?: string;
  responseStatusCode?: number;
  
  // Performance metrics
  responseTime: number;
  executedAt: string;
  success: boolean;
  
  // Agent context
  agentId: string;
  agentRegion: string;
  
  // Error handling
  errorMessage?: string;
  rawResponseBody?: string;
}
```

### API Integration Points
```typescript
// Replace mock data with real API calls
const fetchMonitors = async () => {
  const response = await fetch('/api/monitors');
  return response.json();
};

const fetchMonitorHistory = async (id: string) => {
  const response = await fetch(`/api/monitors/${id}/history`);
  return response.json();
};

const fetchDashboardStats = async () => {
  const response = await fetch('/api/monitoring/dashboard');
  return response.json();
};
```

## 🚀 Performance Optimizations

### Chart Performance
- **Data Limiting**: Show last 50 points for line charts, 24 hours for bars
- **Responsive Container**: Auto-sizing with proper aspect ratios
- **Lazy Loading**: Charts render only when visible
- **Memory Management**: Proper cleanup of chart instances

### Real-time Updates
- **WebSocket Integration**: Live status updates without polling
- **Optimistic Updates**: Immediate UI feedback before API response
- **Debounced Filtering**: 300ms delay on search input
- **Virtual Scrolling**: For large monitor lists (future enhancement)

## 🎨 Theming & Customization

### CSS Variables
```css
:root {
  --monitoring-success: 34 197 94;
  --monitoring-warning: 245 158 11;
  --monitoring-error: 239 68 68;
  --monitoring-chart-primary: 59 130 246;
  --monitoring-chart-secondary: 16 185 129;
}
```

### Component Variants
- **Badge Sizes**: sm, default, lg
- **Card States**: default, success, warning, error
- **Chart Types**: area, line, bar, pie with consistent styling
- **Status Icons**: checkmark, alert, activity with proper colors

## 📱 Navigation Integration

### Sidebar Menu Structure
```
Monitoring/
├── Overview (/monitoring) - Dashboard
└── Monitors (/monitors) - List view
    └── Monitor Detail (/monitors/:id) - Individual monitor
```

### Breadcrumb Navigation
- Home > Monitoring > Overview
- Home > Monitoring > Monitors  
- Home > Monitoring > Monitors > [Monitor Name]

## 🔧 Development Notes

### Component Architecture
- **Atomic Design**: Small, reusable components for consistent UX
- **Compound Components**: Complex widgets built from primitives
- **Hook-based Logic**: Custom hooks for data fetching and state management
- **TypeScript First**: Full type safety with proper interfaces

### Testing Strategy
- **Unit Tests**: Individual component testing with Jest/RTL
- **Integration Tests**: Page-level functionality testing
- **Visual Tests**: Storybook for component documentation
- **E2E Tests**: Critical user flows with Playwright

### Future Enhancements
- **Alert Rules**: Visual alert configuration interface
- **SLA Tracking**: Service level agreement monitoring
- **Incident Management**: Alert correlation and incident timelines
- **Multi-tenant**: Support for multiple organizations/teams
- **Custom Dashboards**: Drag-and-drop dashboard builder
- **Export Features**: PDF reports and CSV data export

This modern monitoring UI provides a comprehensive, performant, and visually appealing interface for HTTP API monitoring that scales from individual developers to enterprise operations teams.