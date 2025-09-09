import React from 'react';
import { Doughnut } from 'react-chartjs-2';
import type { DockerStats } from '../../../hooks/useDockerOps';

interface MemoryStatsDonutProps {
  stats: DockerStats;
}

export const MemoryStatsDonut: React.FC<MemoryStatsDonutProps> = ({ stats }) => {
  const memStats = stats.memory_stats || {};
  const used = memStats.usage || 0;
  const limit = memStats.limit || 1;
  const percent = ((used / limit) * 100).toFixed(2);

  const data = {
    labels: ['Used', 'Free'],
    datasets: [
      {
        data: [used, limit - used],
        backgroundColor: ['#4caf50', '#e0e0e0'],
        borderWidth: 1,
      },
    ],
  };

  const options = {
    cutout: '70%',
    plugins: {
      legend: { display: false },
      tooltip: { enabled: true },
    },
  };

  function formatBytes(bytes: number) {
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes/1024).toFixed(1)} KB`;
    if (bytes < 1024 * 1024 * 1024) return `${(bytes/1024/1024).toFixed(1)} MB`;
    return `${(bytes/1024/1024/1024).toFixed(1)} GB`;
  }

  return (
    <div className="flex flex-col items-center">
      <Doughnut data={data} options={options} height={60} />
      <div className="text-xs mt-2">Memory Usage: <span className="font-bold">{percent}%</span></div>
      <div className="text-xs text-gray-500">{formatBytes(used)} / {formatBytes(limit)}</div>
    </div>
  );
};
