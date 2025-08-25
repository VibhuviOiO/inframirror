import { useQuery } from '@tanstack/react-query';
import axios from 'axios';

const API_BASE = (import.meta.env.VITE_API_BASE_URL ?? '').replace(/\/$/, '');
function apiUrl(path: string) {
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
