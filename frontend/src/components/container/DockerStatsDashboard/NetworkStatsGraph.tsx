import React from 'react';
import { Line } from 'react-chartjs-2';
import type { DockerStats } from '../../../hooks/useDockerOps';

interface NetworkStatsGraphProps {
  stats: DockerStats;
}

export const NetworkStatsGraph: React.FC<NetworkStatsGraphProps> = ({ stats }) => {
  const net = stats.networks?.eth0 || {};
  const rx = net.rx_bytes || 0;
  const tx = net.tx_bytes || 0;

  const data = {
    labels: ['Rx', 'Tx'],
    datasets: [
      {
        label: 'Network I/O',
        data: [rx, tx],
        borderColor: ['#36a2eb', '#ff9800'],
        backgroundColor: ['rgba(54,162,235,0.2)', 'rgba(255,152,0,0.2)'],
        fill: false,
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
      <Line data={data} options={options} height={60} />
      <div className="text-xs mt-2">Rx: <span className="font-bold">{formatBytes(rx)}</span> | Tx: <span className="font-bold">{formatBytes(tx)}</span></div>
    </div>
  );
};
