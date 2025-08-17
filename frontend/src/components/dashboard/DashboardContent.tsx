import { MetricsCard } from './MetricsCard';
import { ChartCard } from './ChartCard';
import { Users, Package, TrendingUp, TrendingDown } from 'lucide-react';

export const DashboardContent = () => {
  const metrics = [
    {
      title: 'Total Users',
      value: '3,782',
      change: '+11.01%',
      isPositive: true,
      icon: Users
    },
    {
      title: 'Active Orders',
      value: '5,359',
      change: '-9.05%',
      isPositive: false,
      icon: Package  
    },
    {
      title: 'Revenue',
      value: '$20,478',
      change: '+12.5%',
      isPositive: true,
      icon: TrendingUp
    },
    {
      title: 'Bounce Rate',
      value: '24.59%',
      change: '-2.1%',
      isPositive: true,
      icon: TrendingDown
    }
  ];

  const monthlySalesData = [
    { month: 'Jan', value: 150 },
    { month: 'Feb', value: 340 },
    { month: 'Mar', value: 180 },
    { month: 'Apr', value: 280 },
    { month: 'May', value: 160 },
    { month: 'Jun', value: 190 },
    { month: 'Jul', value: 260 },
    { month: 'Aug', value: 120 },
    { month: 'Sep', value: 200 },
    { month: 'Oct', value: 340 },
    { month: 'Nov', value: 280 },
    { month: 'Dec', value: 140 }
  ];

  const revenueData = [
    { month: 'Jan', revenue: 180, profit: 80 },
    { month: 'Feb', revenue: 190, profit: 90 },
    { month: 'Mar', revenue: 170, profit: 70 },
    { month: 'Apr', revenue: 160, profit: 60 },
    { month: 'May', revenue: 180, profit: 80 },
    { month: 'Jun', revenue: 200, profit: 100 },
    { month: 'Jul', revenue: 230, profit: 130 },
    { month: 'Aug', revenue: 240, profit: 140 },
    { month: 'Sep', revenue: 250, profit: 150 }
  ];

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div>
        <h1 className="text-2xl font-bold text-foreground">Dashboard</h1>
        <p className="text-muted-foreground">Welcome back! Here's what's happening with your system.</p>
      </div>

      {/* Metrics Grid */}
      <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
        {metrics.map((metric, index) => (
          <MetricsCard key={index} {...metric} />
        ))}
      </div>

      {/* Charts Grid */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        <ChartCard
          title="Monthly Sales"
          description="Sales performance over the last 12 months"
          data={monthlySalesData}
          type="bar"
        />
        <ChartCard
          title="Revenue vs Profit"
          description="Revenue and profit comparison"
          data={revenueData}
          type="line"
        />
      </div>

      {/* Additional Stats Card */}
      <div className="rounded-xl border border-gray-200 bg-card p-6 shadow-sm">
        <div className="flex items-center justify-between">
          <div>
            <h3 className="text-lg font-semibold text-card-foreground">Monthly Target</h3>
            <p className="text-sm text-muted-foreground">Target you've set for each month</p>
          </div>
          <div className="text-right">
            <div className="text-2xl font-bold text-primary">75.55%</div>
            <div className="text-sm text-success">+10%</div>
          </div>
        </div>
        <div className="mt-4">
          <div className="flex justify-between text-sm text-muted-foreground mb-2">
            <span>Progress</span>
            <span>$32,875 / $45,000</span>
          </div>
          <div className="w-full bg-gray-200 rounded-full h-2">
            <div className="bg-primary h-2 rounded-full" style={{ width: '75.55%' }}></div>
          </div>
          <p className="text-sm text-muted-foreground mt-2">
            You earn $3287 today, it's higher than last month. Keep up your good work!
          </p>
        </div>
      </div>
    </div>
  );
};