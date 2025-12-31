import './public-status-page.scss';

import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import axios from 'axios';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';

dayjs.extend(relativeTime);

interface RegionHealth {
  status: 'UP' | 'WARNING' | 'CRITICAL' | 'DOWN';
  responseTimeMs: number | null;
  agentName: string;
  successRate: number;
}

interface MonitorStatus {
  monitorId: number;
  monitorName: string;
  url: string;
  regionHealth: { [region: string]: RegionHealth };
}

interface PublicStatusPageData {
  name: string;
  description: string;
  slug: string;
  logoUrl: string | null;
  themeColor: string | null;
  headerText: string | null;
  footerText: string | null;
  showResponseTimes: boolean;
  showUptimePercentage: boolean;
  autoRefreshSeconds: number | null;
  regions: string[];
  monitors: MonitorStatus[];
}

export const PublicStatusPage = () => {
  const { slug } = useParams<{ slug: string }>();
  const [data, setData] = useState<PublicStatusPageData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchData = async () => {
    try {
      const url = `/public/status/${slug}`;
      const response = await axios.get<PublicStatusPageData>(url);
      setData(response.data);
      setError(null);
    } catch (err) {
      setError('Status page not found or unavailable');
      console.error('Error fetching public status page:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [slug]);

  useEffect(() => {
    if (data?.autoRefreshSeconds) {
      const interval = setInterval(() => {
        fetchData();
      }, data.autoRefreshSeconds * 1000);
      return () => clearInterval(interval);
    }
  }, [data?.autoRefreshSeconds]);

  const getStatusSvg = (status: string, color: string) => {
    switch (status) {
      case 'UP':
        return (
          <svg width="16" height="16" viewBox="0 0 16 16">
            <circle cx="8" cy="8" r="8" fill={color} />
            <path d="M6 8l2 2 4-4" stroke="white" strokeWidth="1.5" fill="none" strokeLinecap="round" strokeLinejoin="round" />
          </svg>
        );
      case 'WARNING':
        return (
          <svg width="16" height="16" viewBox="0 0 16 16">
            <circle cx="8" cy="8" r="8" fill={color} />
            <path d="M8 4v5M8 11v1" stroke="white" strokeWidth="1.5" strokeLinecap="round" />
          </svg>
        );
      case 'CRITICAL':
        return (
          <svg width="16" height="16" viewBox="0 0 16 16">
            <circle cx="8" cy="8" r="8" fill={color} />
            <path d="M8 4v5M8 11v1" stroke="white" strokeWidth="1.5" strokeLinecap="round" />
          </svg>
        );
      case 'DOWN':
        return (
          <svg width="16" height="16" viewBox="0 0 16 16">
            <circle cx="8" cy="8" r="8" fill={color} />
            <path d="M5 5l6 6M11 5l-6 6" stroke="white" strokeWidth="1.5" strokeLinecap="round" />
          </svg>
        );
      default:
        return null;
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'UP':
        return '#34a853';
      case 'WARNING':
        return '#fbbc04';
      case 'CRITICAL':
        return '#ff6d00';
      case 'DOWN':
        return '#ea4335';
      default:
        return '#9aa0a6';
    }
  };

  const getStatusTitle = (status: string) => {
    switch (status) {
      case 'UP':
        return 'Available - Normal latency';
      case 'WARNING':
        return 'Available - Elevated latency';
      case 'CRITICAL':
        return 'Available - High latency';
      case 'DOWN':
        return 'Service disruption';
      default:
        return 'Unknown status';
    }
  };

  if (loading) {
    return (
      <div className="public-status-page">
        <div className="loading-container">
          <FontAwesomeIcon icon="spinner" spin size="3x" />
          <p>Loading status page...</p>
        </div>
      </div>
    );
  }

  if (error || !data) {
    return (
      <div className="public-status-page">
        <div className="error-container">
          <FontAwesomeIcon icon="exclamation-triangle" size="3x" className="text-warning" />
          <h3>{error || 'Status page not found'}</h3>
        </div>
      </div>
    );
  }

  return (
    <div className="status-page-wrapper">
      <nav className="status-navbar">
        <div className="navbar-content">
          <div className="navbar-brand">
            {data.logoUrl && <img src={data.logoUrl} alt={data.name} />}
            <span className="navbar-title">{data.name}</span>
          </div>
          <Link to={`/status-page/view/${slug}`} className="console-link">
            <FontAwesomeIcon icon="lock" className="me-1" />
            Internal View
          </Link>
        </div>
      </nav>

      <div className="status-page">
        <div className="status-header">
          <h1>{data.name}</h1>
          {data.description && <p className="status-subtitle">{data.description}</p>}
          {data.headerText && <p className="status-description">{data.headerText}</p>}
        </div>

        <div className="status-legend-container">
          <div className="status-legend">
            <div className="legend-item">
              <div className="legend-icon">{getStatusSvg('UP', '#34a853')}</div>
              <span>Available - Normal</span>
            </div>
            <div className="legend-item">
              <div className="legend-icon">{getStatusSvg('WARNING', '#fbbc04')}</div>
              <span>Available - Elevated</span>
            </div>
            <div className="legend-item">
              <div className="legend-icon">{getStatusSvg('CRITICAL', '#ff6d00')}</div>
              <span>Available - High</span>
            </div>
            <div className="legend-item">
              <div className="legend-icon">{getStatusSvg('DOWN', '#ea4335')}</div>
              <span>Service Disruption</span>
            </div>
          </div>
          {data.autoRefreshSeconds && (
            <div className="last-update-text">
              <FontAwesomeIcon icon="sync" className="me-1" />
              Refreshes every {data.autoRefreshSeconds}s
            </div>
          )}
        </div>

        {data.monitors.length === 0 ? (
          <div className="status-page-empty">
            <h2>No monitoring data available</h2>
            <p>Start monitoring your services to see their status here</p>
          </div>
        ) : (
          <div className="status-table-container">
            <table className="status-table">
              <thead>
                <tr>
                  <th className="api-column">Service</th>
                  {data.regions.map(region => (
                    <th key={region} className="region-column">
                      {region}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {data.monitors.map(monitor => (
                  <tr key={monitor.monitorId}>
                    <td className="api-name">
                      <div>{monitor.monitorName}</div>
                      <div className="api-url">{monitor.url}</div>
                    </td>
                    {data.regions.map(region => {
                      const health = monitor.regionHealth[region];
                      return (
                        <td key={region} className="region-status">
                          {health ? (
                            <div
                              className="status-indicator"
                              title={`${getStatusTitle(health.status)} - ${health.agentName} - ${health.successRate}% uptime`}
                            >
                              <div className="status-icon">{getStatusSvg(health.status, getStatusColor(health.status))}</div>
                              {data.showResponseTimes && health.responseTimeMs && (
                                <span className="response-time">{health.responseTimeMs}ms</span>
                              )}
                              {data.showUptimePercentage && <span className="success-rate">{health.successRate}%</span>}
                            </div>
                          ) : (
                            <div className="status-indicator unknown">
                              <span className="no-data">—</span>
                            </div>
                          )}
                        </td>
                      );
                    })}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {data.footerText && (
        <footer className="status-footer">
          <div className="footer-content">
            <p>
              © {new Date().getFullYear()} {data.name} • {data.footerText}
            </p>
          </div>
        </footer>
      )}
    </div>
  );
};

export default PublicStatusPage;
