import React from 'react';
import { Line } from 'react-chartjs-2';
import type { DockerStats } from '../../../hooks/useDockerOps';

interface CpuStatsChartProps {
  stats: DockerStats;
}

export const CpuStatsChart: React.FC<CpuStatsChartProps> = ({ stats }) => {
  // Calculate CPU %
  const cpuPercent = stats.cpu_stats && stats.cpu_stats.cpu_usage && stats.cpu_stats.system_cpu_usage
    ? ((stats.cpu_stats.cpu_usage.total_usage / stats.cpu_stats.system_cpu_usage) * 100).toFixed(2)
    : '0';

  // Chart data (single point, no history)
  const data = {
    labels: ['CPU'],
    datasets: [
      {
        label: 'CPU %',
        data: [parseFloat(cpuPercent)],
        borderColor: '#36a2eb',
        backgroundColor: 'rgba(54,162,235,0.2)',
        fill: true,
        tension: 0.4,
      },
    ],
  };

  const options = {
    responsive: true,
    plugins: {
      legend: { display: false },
      tooltip: { enabled: true },
    },
    scales: {
      y: { beginAtZero: true, max: 100 },
    },
  };

  return (
    <div className="flex flex-col items-center">
      <Line data={data} options={options} height={60} />
      <div className="text-xs mt-2">CPU Usage: <span className="font-bold">{cpuPercent}%</span></div>
      <div className="text-xs text-gray-500">Kernel: {(stats.cpu_stats?.cpu_usage?.usage_in_kernelmode/1e9).toFixed(2)}s | User: {(stats.cpu_stats?.cpu_usage?.usage_in_usermode/1e9).toFixed(2)}s</div>
    </div>
  );
};
