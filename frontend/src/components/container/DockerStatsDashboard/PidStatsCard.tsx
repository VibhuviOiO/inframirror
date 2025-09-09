import React from 'react';
import type { DockerStats } from '../../../hooks/useDockerOps';

interface PidStatsCardProps {
  stats: DockerStats;
}

export const PidStatsCard: React.FC<PidStatsCardProps> = ({ stats }) => {
  const current = stats.pids_stats?.current ?? 0;
  const limit = stats.pids_stats?.limit ?? 0;
  return (
    <div className="flex flex-col items-center p-2 border rounded bg-white shadow text-xs">
      <div className="font-bold">PIDs</div>
      <div>Current: <span className="font-bold">{current}</span></div>
      <div>Limit: <span className="font-bold">{limit}</span></div>
      <div className="w-full bg-gray-200 rounded-full h-2 mt-2">
        <div
          className="bg-blue-500 h-2 rounded-full"
          style={{ width: limit ? `${Math.min(100, (current / limit) * 100)}%` : '0%' }}
        />
      </div>
    </div>
  );
};
