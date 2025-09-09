import React from 'react';
import { Bar } from 'react-chartjs-2';
import type { DockerStats } from '../../../hooks/useDockerOps';

interface DiskIoStatsBarProps {
  stats: DockerStats;
}

export const DiskIoStatsBar: React.FC<DiskIoStatsBarProps> = ({ stats }) => {
  const blkio = stats.blkio_stats?.io_service_bytes_recursive || [];
  const read = blkio.find((b: any) => b.op === 'read')?.value || 0;
  const write = blkio.find((b: any) => b.op === 'write')?.value || 0;

  const data = {
    labels: ['Read', 'Write'],
    datasets: [
      {
        label: 'Disk I/O',
        data: [read, write],
        backgroundColor: ['#2196f3', '#8bc34a'],
        borderWidth: 1,
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
      y: { beginAtZero: true },
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
      <Bar data={data} options={options} height={60} />
      <div className="text-xs mt-2">Read: <span className="font-bold">{formatBytes(read)}</span> | Write: <span className="font-bold">{formatBytes(write)}</span></div>
    </div>
  );
};
