// Get container logs (static)
export async function getContainerLogs({
  host,
  port,
  protocol = 'http',
  ca,
  cert,
  key,
  containerId,
  stdout = true,
  stderr = true,
  since,
  until,
  timestamps,
  tail,
}: {
  host: string;
  port: string | number;
  protocol?: string;
  ca?: string;
  cert?: string;
  key?: string;
  containerId: string;
  stdout?: boolean;
  stderr?: boolean;
  since?: number;
  until?: number;
  timestamps?: boolean;
  tail?: string | number;
}) {
  const params = new URLSearchParams({
    host,
    port: String(port),
    protocol,
    ...(ca ? { ca } : {}),
    ...(cert ? { cert } : {}),
    ...(key ? { key } : {}),
    stdout: String(stdout),
    stderr: String(stderr),
    follow: 'false',
    ...(since ? { since: String(since) } : {}),
    ...(until ? { until: String(until) } : {}),
    ...(timestamps ? { timestamps: String(timestamps) } : {}),
    ...(tail ? { tail: String(tail) } : {}),
  });
  const url = apiUrl(`/dockerops/containers/${containerId}/logs?${params.toString()}`);
  const response = await fetch(url);
  if (!response.ok) throw new Error(await response.text());
  const data = await response.json();
  return data.logs;
}

// Stream container logs (live)
export async function streamContainerLogs({
  host,
  port,
  protocol = 'http',
  ca,
  cert,
  key,
  containerId,
  stdout = true,
  stderr = true,
  since,
  until,
  timestamps,
  tail,
  onChunk,
}: {
  host: string;
  port: string | number;
  protocol?: string;
  ca?: string;
  cert?: string;
  key?: string;
  containerId: string;
  stdout?: boolean;
  stderr?: boolean;
  since?: number;
  until?: number;
  timestamps?: boolean;
  tail?: string | number;
  onChunk: (chunk: string) => void;
}) {
  const params = new URLSearchParams({
    host,
    port: String(port),
    protocol,
    ...(ca ? { ca } : {}),
    ...(cert ? { cert } : {}),
    ...(key ? { key } : {}),
    stdout: String(stdout),
    stderr: String(stderr),
    follow: 'true',
    ...(since ? { since: String(since) } : {}),
    ...(until ? { until: String(until) } : {}),
    ...(timestamps ? { timestamps: String(timestamps) } : {}),
    ...(tail ? { tail: String(tail) } : {}),
  });
  const url = apiUrl(`/dockerops/containers/${containerId}/logs?${params.toString()}`);
  const response = await fetch(url);
  if (!response.body) throw new Error('No stream available');
  const reader = response.body.getReader();
  const decoder = new TextDecoder();
  while (true) {
    const { done, value } = await reader.read();
    if (done) break;
    const chunk = decoder.decode(value);
    onChunk(chunk);
  }
}
import { useQuery } from '@tanstack/react-query';
import axios from 'axios';

const API_BASE = (import.meta.env.VITE_API_BASE_URL ?? '').replace(/\/$/, '');
export function apiUrl(path: string) {
  return `${API_BASE}${path}`;
}

export interface DockerPort {
  IP?: string;
  PrivatePort: number;
  PublicPort?: number;
  Type: string;
}

export interface DockerMount {
  Type: string;
  Name?: string;
  Source?: string;
  Destination?: string;
  Driver?: string;
  Mode?: string;
  RW?: boolean;
  Propagation?: string;
}

export interface DockerNetwork {
  IPAMConfig?: any;
  Links?: any;
  Aliases?: any;
  MacAddress?: string;
  DriverOpts?: any;
  GwPriority?: number;
  NetworkID?: string;
  EndpointID?: string;
  Gateway?: string;
  IPAddress?: string;
  IPPrefixLen?: number;
  IPv6Gateway?: string;
  GlobalIPv6Address?: string;
  GlobalIPv6PrefixLen?: number;
  DNSNames?: any;
}

export interface DockerNetworkSettings {
  Networks: Record<string, DockerNetwork>;
}

export interface DockerContainer {
  Id: string;
  Names: string[];
  Image: string;
  ImageID: string;
  Command: string;
  Created: number;
  Ports: DockerPort[];
  SizeRw?: number;
  SizeRootFs?: number;
  Labels: Record<string, string>;
  State: string;
  Status: string;
  HostConfig: {
    NetworkMode: string;
    [key: string]: any;
  };
  NetworkSettings: DockerNetworkSettings;
  Mounts: DockerMount[];
}

export interface DockerListContainersParams {
  host: string;
  port: number | string;
  protocol?: string;
  ca?: string;
  cert?: string;
  key?: string;
  all?: boolean;
  limit?: number;
  size?: boolean;
  filters?: Record<string, string[]>;
}

export function useDockerContainers(params: DockerListContainersParams, enabled = true) {
  return useQuery<DockerContainer[]>({
    queryKey: ['docker-containers', params],
    queryFn: async () => {
      const searchParams = new URLSearchParams();
      Object.entries(params).forEach(([key, value]) => {
        if (value === undefined) return;
        if (typeof value === 'object' && key === 'filters') {
          searchParams.append('filters', JSON.stringify(value));
        } else {
          searchParams.append(key, String(value));
        }
      });
      const { data } = await axios.get(apiUrl('/dockerops/containers'), { params: searchParams });
      return Array.isArray(data) ? data : [];
    },
    enabled,
    select: (data) => Array.isArray(data) ? data : [],
  });
}

export interface DockerInspectParams {
  host: string;
  port: number | string;
  protocol?: string;
  ca?: string;
  cert?: string;
  key?: string;
  id: string;
  size?: boolean;
}

export function useDockerInspect(params: DockerInspectParams, enabled = true) {
  return useQuery({
    queryKey: ['docker-inspect', params],
    queryFn: async () => {
      const { id, ...rest } = params;
      const searchParams = new URLSearchParams();
      Object.entries(rest).forEach(([key, value]) => {
        if (value === undefined) return;
        searchParams.append(key, String(value));
      });
      const url = apiUrl(`/dockerops/containers/${id}/inspect?${searchParams.toString()}`);
      const { data } = await axios.get(url);
      return data;
    },
    enabled,
  });
}

export interface DockerStatsParams {
  host: string;
  port: number | string;
  protocol?: string;
  ca?: string;
  cert?: string;
  key?: string;
  id: string;
  stream?: boolean;
}

export interface DockerStats {
  name: string;
  id: string;
  read: string;
  preread: string;
  pids_stats: Record<string, any>;
  blkio_stats: Record<string, any>;
  num_procs: number;
  storage_stats: Record<string, any>;
  cpu_stats: Record<string, any>;
  precpu_stats: Record<string, any>;
  memory_stats: Record<string, any>;
  networks?: Record<string, any>;
}

export function useDockerStats(params: DockerStatsParams, enabled = true) {
  return useQuery<DockerStats>({
    queryKey: ['docker-stats', params],
    queryFn: async () => {
      const { id, ...rest } = params;
      const searchParams = new URLSearchParams();
      Object.entries(rest).forEach(([key, value]) => {
        if (value === undefined) return;
        searchParams.append(key, String(value));
      });
      const url = apiUrl(`/dockerops/containers/${id}/stats?${searchParams.toString()}`);
      const { data } = await axios.get(url);
      return data;
    },
    enabled,
  });
}
