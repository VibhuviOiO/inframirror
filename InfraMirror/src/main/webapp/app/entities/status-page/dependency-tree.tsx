import React, { useState, useEffect } from 'react';
import { Card, CardBody, Spinner, Badge, Button } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import axios from 'axios';
import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';
import './dependency-tree.scss';

dayjs.extend(relativeTime);

interface TreeNode {
  id: string;
  name: string;
  type: string;
  itemId: number;
  status: 'UP' | 'DOWN' | 'DEGRADED' | 'UNKNOWN';
  lastChecked?: string;
  responseTimeMs?: number;
  errorMessage?: string;
  metadata?: string;
  children: TreeNode[];
}

interface DependencyTreeProps {
  statusPageId: number;
  isPublic?: boolean;
}

export const DependencyTree: React.FC<DependencyTreeProps> = ({ statusPageId, isPublic = false }) => {
  const [loading, setLoading] = useState(true);
  const [tree, setTree] = useState<TreeNode[]>([]);
  const [expandedNodes, setExpandedNodes] = useState<Set<string>>(new Set());
  const [autoRefresh, setAutoRefresh] = useState(false);
  const [managingDepsFor, setManagingDepsFor] = useState<string | null>(null);
  const [allItems, setAllItems] = useState<any[]>([]);
  const [dependencyTab, setDependencyTab] = useState<'HTTP' | 'INSTANCE' | 'SERVICE'>('HTTP');
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    loadData();
  }, [statusPageId]);

  useEffect(() => {
    if (autoRefresh) {
      const interval = setInterval(loadData, 30000);
      return () => clearInterval(interval);
    }
  }, [autoRefresh, statusPageId]);

  const loadData = async () => {
    setLoading(true);
    try {
      const [treeRes, httpRes, instancesRes, servicesRes] = await Promise.all([
        axios.get<TreeNode[]>(`/api/status-pages/${statusPageId}/dependencies`),
        axios.get('/api/http-monitors', { params: { size: 1000 } }),
        axios.get('/api/instances', { params: { size: 1000 } }),
        axios.get('/api/monitored-services', { params: { size: 1000 } }),
      ]);
      setTree(treeRes.data);
      const all = [
        ...httpRes.data.map(i => ({ id: i.id, name: i.name, type: 'HTTP' })),
        ...instancesRes.data.map(i => ({ id: i.id, name: i.hostname || `Instance ${i.id}`, type: 'INSTANCE' })),
        ...servicesRes.data.map(i => ({ id: i.id, name: i.name, type: 'SERVICE' })),
      ];
      setAllItems(all);
    } catch (error) {
      console.error('Failed to load dependency tree:', error);
    } finally {
      setLoading(false);
    }
  };

  const toggleNode = (nodeId: string) => {
    setExpandedNodes(prev => {
      const next = new Set(prev);
      if (next.has(nodeId)) {
        next.delete(nodeId);
      } else {
        next.add(nodeId);
      }
      return next;
    });
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'UP':
        return <FontAwesomeIcon icon="check-circle" className="text-success" />;
      case 'DOWN':
        return <FontAwesomeIcon icon="times-circle" className="text-danger" />;
      case 'DEGRADED':
        return <FontAwesomeIcon icon="exclamation-triangle" className="text-warning" />;
      default:
        return <FontAwesomeIcon icon="question-circle" className="text-muted" />;
    }
  };

  const getTypeIcon = (type: string) => {
    switch (type) {
      case 'HTTP_MONITOR':
      case 'HTTP':
        return <FontAwesomeIcon icon="globe" className="text-primary" />;
      case 'SERVICE':
        return <FontAwesomeIcon icon="server" className="text-success" />;
      case 'INSTANCE':
        return <FontAwesomeIcon icon="hdd" className="text-info" />;
      default:
        return <FontAwesomeIcon icon="question-circle" className="text-secondary" />;
    }
  };

  const getTypeLabel = (type: string) => {
    switch (type) {
      case 'HTTP_MONITOR':
      case 'HTTP':
        return 'API';
      case 'SERVICE':
        return 'Service';
      case 'INSTANCE':
        return 'Instance';
      default:
        return type;
    }
  };

  const handleAddDependency = async (parentNode: TreeNode, childId: number, childType: string) => {
    console.error('Adding dependency:', { parentNode, childId, childType, statusPageId });
    setSaving(true);
    try {
      const payload = {
        statusPage: { id: statusPageId },
        parentType: parentNode.type,
        parentId: parentNode.itemId,
        childType,
        childId,
      };
      console.error('Posting dependency payload:', payload);
      const response = await axios.post('/api/status-dependencies', payload);
      console.error('Dependency created:', response.data);
      await loadData();
    } catch (error) {
      console.error('Error adding dependency:', error);
      if (axios.isAxiosError(error)) {
        console.error('Response:', error.response?.data);
      }
    } finally {
      setSaving(false);
    }
  };

  const handleRemoveDependency = async (parentNode: TreeNode, childId: number) => {
    setSaving(true);
    try {
      const depsRes = await axios.get('/api/status-dependencies', {
        params: { 'statusPageId.equals': statusPageId },
      });
      const dep = depsRes.data.find(d => d.parentId === parentNode.itemId && d.childId === childId);
      if (dep) {
        await axios.delete(`/api/status-dependencies/${dep.id}`);
        await loadData();
      }
    } catch (error) {
      console.error('Error removing dependency:', error);
    } finally {
      setSaving(false);
    }
  };

  const renderNode = (node: TreeNode, level = 0) => {
    const hasChildren = node.children && node.children.length > 0;
    const isExpanded = expandedNodes.has(node.id);

    if (level === 0) {
      return (
        <Card key={node.id} className="mb-2 shadow-sm">
          <CardBody className="py-2">
            <div className="d-flex justify-content-between align-items-center">
              <div className="d-flex align-items-center gap-2 flex-grow-1">
                {hasChildren && (
                  <span className="node-toggle" onClick={() => toggleNode(node.id)} style={{ cursor: 'pointer' }}>
                    <FontAwesomeIcon icon={isExpanded ? 'chevron-down' : 'chevron-right'} />
                  </span>
                )}
                <span className="node-status">{getStatusIcon(node.status)}</span>
                <span className="node-type-icon">{getTypeIcon(node.type)}</span>
                <strong>{node.name}</strong>
                <Badge color="light" className="py-0">
                  {getTypeLabel(node.type)}
                </Badge>
                {node.responseTimeMs !== null && node.responseTimeMs !== undefined && (
                  <span className="small text-muted">
                    <FontAwesomeIcon icon="clock" className="me-1" />
                    {node.responseTimeMs}ms
                  </span>
                )}
                {node.lastChecked && (
                  <span className="small text-muted">
                    <FontAwesomeIcon icon="history" className="me-1" />
                    {dayjs(node.lastChecked).fromNow()}
                  </span>
                )}
              </div>
              <Button
                color="info"
                size="sm"
                onClick={() => setManagingDepsFor(managingDepsFor === node.id ? null : node.id)}
                disabled={saving}
              >
                <FontAwesomeIcon icon="sitemap" className="me-1" />
                Add Dependency
              </Button>
            </div>
            {node.errorMessage && (
              <div className="alert alert-danger mt-2 mb-0 py-1 small">
                <FontAwesomeIcon icon="exclamation-circle" className="me-1" />
                {node.errorMessage}
              </div>
            )}
            {managingDepsFor === node.id && (
              <div className="dependency-manager mt-2">
                <div className="d-flex gap-2 mb-2">
                  <Button size="sm" color={dependencyTab === 'HTTP' ? 'primary' : 'secondary'} onClick={() => setDependencyTab('HTTP')}>
                    HTTP
                  </Button>
                  {!isPublic && (
                    <>
                      <Button
                        size="sm"
                        color={dependencyTab === 'INSTANCE' ? 'primary' : 'secondary'}
                        onClick={() => setDependencyTab('INSTANCE')}
                      >
                        Instance
                      </Button>
                      <Button
                        size="sm"
                        color={dependencyTab === 'SERVICE' ? 'primary' : 'secondary'}
                        onClick={() => setDependencyTab('SERVICE')}
                      >
                        Service
                      </Button>
                    </>
                  )}
                </div>
                <div className="dependency-list">
                  {allItems
                    .filter(i => {
                      if (i.type !== dependencyTab) return false;
                      if (i.id === node.itemId && i.type === node.type) return false;
                      const childIds = node.children.map(c => ({ id: c.itemId, type: c.type }));
                      return !childIds.some(c => c.id === i.id && c.type === i.type);
                    })
                    .map(item => (
                      <div key={item.id} className="d-flex align-items-center gap-2 mb-1">
                        <Button size="sm" color="success" onClick={() => handleAddDependency(node, item.id, item.type)} disabled={saving}>
                          <FontAwesomeIcon icon="plus" />
                        </Button>
                        <span>{item.name}</span>
                      </div>
                    ))}
                  {allItems.filter(i => i.type === dependencyTab).length === 0 && (
                    <div className="text-muted small">No {dependencyTab} items available</div>
                  )}
                </div>
              </div>
            )}
            {hasChildren && isExpanded && <div className="mt-2 ms-4">{node.children.map(child => renderNode(child, level + 1))}</div>}
          </CardBody>
        </Card>
      );
    }

    return (
      <div key={node.id} className="mb-2 border-start border-2 border-primary ps-3">
        <div className="d-flex justify-content-between align-items-center py-2">
          <div className="d-flex align-items-center gap-2 flex-grow-1">
            <span className="dependency-level-indicator" style={{ fontSize: '0.75rem', color: '#6c757d' }}>
              L{level}
            </span>
            {hasChildren && (
              <span className="node-toggle" onClick={() => toggleNode(node.id)} style={{ cursor: 'pointer' }}>
                <FontAwesomeIcon icon={isExpanded ? 'chevron-down' : 'chevron-right'} />
              </span>
            )}
            <span className="node-status">{getStatusIcon(node.status)}</span>
            <span className="node-type-icon">{getTypeIcon(node.type)}</span>
            <span className="fw-bold">{node.name}</span>
            <Badge color="light" className="py-0 small">
              {getTypeLabel(node.type)}
            </Badge>
            {node.responseTimeMs !== null && node.responseTimeMs !== undefined && (
              <span className="small text-muted">
                <FontAwesomeIcon icon="clock" className="me-1" />
                {node.responseTimeMs}ms
              </span>
            )}
          </div>
          <Button
            color="link"
            size="sm"
            className="text-info"
            onClick={() => setManagingDepsFor(managingDepsFor === node.id ? null : node.id)}
            disabled={saving}
          >
            <FontAwesomeIcon icon="plus" />
          </Button>
        </div>
        {managingDepsFor === node.id && (
          <div className="dependency-manager mt-2 mb-2">
            <div className="d-flex gap-2 mb-2">
              <Button size="sm" color={dependencyTab === 'HTTP' ? 'primary' : 'secondary'} onClick={() => setDependencyTab('HTTP')}>
                HTTP
              </Button>
              {!isPublic && (
                <>
                  <Button
                    size="sm"
                    color={dependencyTab === 'INSTANCE' ? 'primary' : 'secondary'}
                    onClick={() => setDependencyTab('INSTANCE')}
                  >
                    Instance
                  </Button>
                  <Button
                    size="sm"
                    color={dependencyTab === 'SERVICE' ? 'primary' : 'secondary'}
                    onClick={() => setDependencyTab('SERVICE')}
                  >
                    Service
                  </Button>
                </>
              )}
            </div>
            <div className="dependency-list">
              {allItems
                .filter(i => {
                  if (i.type !== dependencyTab) return false;
                  if (i.id === node.itemId && i.type === node.type) return false;
                  const childIds = node.children.map(c => ({ id: c.itemId, type: c.type }));
                  return !childIds.some(c => c.id === i.id && c.type === i.type);
                })
                .map(item => (
                  <div key={item.id} className="d-flex align-items-center gap-2 mb-1">
                    <Button size="sm" color="success" onClick={() => handleAddDependency(node, item.id, item.type)} disabled={saving}>
                      <FontAwesomeIcon icon="plus" />
                    </Button>
                    <span>{item.name}</span>
                  </div>
                ))}
              {allItems.filter(i => i.type === dependencyTab).length === 0 && (
                <div className="text-muted small">No {dependencyTab} items available</div>
              )}
            </div>
          </div>
        )}
        {hasChildren && isExpanded && <div className="ms-3 mt-1">{node.children.map(child => renderNode(child, level + 1))}</div>}
      </div>
    );
  };

  if (loading) {
    return (
      <Card className="dependency-tree-card">
        <CardBody className="text-center p-5">
          <Spinner color="primary" />
          <p className="mt-3 text-muted">Loading infrastructure dependency tree...</p>
        </CardBody>
      </Card>
    );
  }

  return (
    <div>
      {tree.length === 0 ? (
        <Card className="dependency-tree-card">
          <CardBody>
            <div className="alert alert-info mb-0">
              <FontAwesomeIcon icon="info-circle" className="me-2" />
              No items configured for this status page. Add monitors to see the dependency tree.
            </div>
          </CardBody>
        </Card>
      ) : (
        <>
          <div className="d-flex justify-content-end mb-2">
            <Button color={autoRefresh ? 'success' : 'secondary'} size="sm" onClick={() => setAutoRefresh(!autoRefresh)}>
              <FontAwesomeIcon icon={autoRefresh ? 'pause' : 'sync'} />
              <span className="ms-1">{autoRefresh ? 'Auto-refresh ON' : 'Auto-refresh OFF'}</span>
            </Button>
          </div>
          <div>{tree.map(node => renderNode(node, 0))}</div>
        </>
      )}
    </div>
  );
};

export default DependencyTree;
