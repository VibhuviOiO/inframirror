import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Button } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import axios from 'axios';
import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';
import DependencyTree from './dependency-tree';
import ManageItemsPanel from './manage-items-panel';
import './status-page-view.scss';

dayjs.extend(relativeTime);

interface StatusPageData {
  id?: number;
  name: string;
  description: string | null;
  slug: string;
  isPublic?: boolean;
  isActive?: boolean;
  isHomePage?: boolean;
  monitorSelection?: string | null;
  allowedRoles?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export const StatusPageView = () => {
  const { slug } = useParams<{ slug: string }>();
  const [data, setData] = useState<StatusPageData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showManagePanel, setShowManagePanel] = useState(false);
  const [refreshKey, setRefreshKey] = useState(0);

  const fetchData = async () => {
    try {
      const response = await axios.get<StatusPageData>(`/api/status-pages/view/${slug}`);
      setData(response.data);
      setError(null);
    } catch (err) {
      setError('Status page not found');
      console.error('Error fetching status page:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [slug]);

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
      <div>
        <div className="d-flex justify-content-between align-items-center mb-3">
          <h4>Status Page</h4>
          <Button tag={Link} to="/status-page" color="secondary" size="sm">
            <FontAwesomeIcon icon="arrow-left" /> Back
          </Button>
        </div>
        <div className="alert alert-warning">{error || 'Status page not found'}</div>
      </div>
    );
  }

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h4 className="mb-1">{data.name}</h4>
          {data.description && <p className="text-muted mb-0">{data.description}</p>}
        </div>
        <div className="d-flex align-items-center gap-2">
          <Button color="primary" size="sm" onClick={() => setShowManagePanel(true)}>
            <FontAwesomeIcon icon="list" /> Manage Items
          </Button>
          {data.isPublic && (
            <Button tag={Link} to={`/s/${slug}`} color="info" size="sm" target="_blank">
              <FontAwesomeIcon icon="external-link-alt" /> Public View
            </Button>
          )}
          <Button tag={Link} to="/status-page" color="secondary" size="sm">
            <FontAwesomeIcon icon="arrow-left" /> Back
          </Button>
        </div>
      </div>

      {data.id && <DependencyTree key={refreshKey} statusPageId={data.id} isPublic={data.isPublic} />}

      {showManagePanel && data.id && (
        <ManageItemsPanel
          statusPageId={data.id}
          statusPageName={data.name}
          isPublic={data.isPublic}
          onClose={() => setShowManagePanel(false)}
          onItemsUpdated={() => setRefreshKey(prev => prev + 1)}
        />
      )}
    </div>
  );
};

export default StatusPageView;
