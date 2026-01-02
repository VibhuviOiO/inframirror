/* eslint-disable complexity */
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

  useEffect(() => {
    if (managingDepsFor && allItems.filter(i => i.type === dependencyTab).length === 0) {
      loadItemsForType(dependencyTab);
    }
  }, [managingDepsFor, dependencyTab]);

  const loadData = async () => {
    setLoading(true);
    try {
      const treeRes = await axios.get<TreeNode[]>(`/api/status-pages/${statusPageId}/dependencies`);
      setTree(treeRes.data);
      setAllItems([]);
    } catch (error) {
      console.error('Failed to load dependency tree:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadItemsForType = async (type: 'HTTP' | 'SERVICE' | 'INSTANCE') => {
    try {
      let items = [];
      if (type === 'HTTP') {
        const res = await axios.get('/api/http-monitors', { params: { size: 100 } });
        items = res.data.map(i => ({ id: i.id, name: i.name, type: 'HTTP' }));
      } else if (type === 'SERVICE') {
        const res = await axios.get('/api/monitored-services', { params: { size: 250 } });
        items = res.data.map(i => ({ id: i.id, name: i.name, type: 'SERVICE' }));
      } else if (type === 'INSTANCE') {
        const res = await axios.get('/api/instances', { params: { size: 1000 } });
        items = res.data.map(i => ({ id: i.id, name: i.hostname || `Instance ${i.id}`, type: 'INSTANCE', instanceType: i.instanceType }));
      }
      setAllItems(prev => [...prev.filter(i => i.type !== type), ...items]);
    } catch (error) {
      console.error(`Failed to load ${type} items:`, error);
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
    setSaving(true);
    try {
      const payload = {
        statusPage: { id: statusPageId },
        parentType: parentNode.type,
        parentId: parentNode.itemId,
        childType,
        childId,
      };
      await axios.post('/api/status-dependencies', payload);
      await loadData();
    } catch (error) {
      console.error('Error adding dependency:', error);
    } finally {
      setSaving(false);
    }
  };

  const canAddDependencies = (node: TreeNode) => {
    // BARE_METAL instances cannot have dependencies
    if (node.type === 'INSTANCE' && node.metadata) {
      try {
        const meta = JSON.parse(node.metadata);
        if (meta.instanceType === 'BARE_METAL') {
          return false;
        }
      } catch (e) {
        // If metadata parsing fails, allow dependencies
      }
    }
    return true;
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
              {canAddDependencies(node) && (
                <Button
                  color="primary"
                  size="sm"
                  outline
                  onClick={() => setManagingDepsFor(managingDepsFor === node.id ? null : node.id)}
                  disabled={saving}
                >
                  <FontAwesomeIcon icon="link" className="me-1" />
                  {hasChildren ? `Dependencies (${node.children.length})` : 'Add Dependencies'}
                </Button>
              )}
            </div>
            {node.errorMessage && (
              <div className="alert alert-danger mt-2 mb-0 py-1 small">
                <FontAwesomeIcon icon="exclamation-circle" className="me-1" />
                {node.errorMessage}
              </div>
            )}
            {managingDepsFor === node.id && (
              <div className="dependency-manager-panel mt-3 p-3 bg-white border rounded shadow-sm">
                <div className="d-flex justify-content-between align-items-center mb-3 pb-2 border-bottom">
                  <div>
                    <h6 className="mb-0">
                      <FontAwesomeIcon icon="link" className="me-2 text-primary" />
                      What does <strong>{node.name}</strong> depend on?
                    </h6>
                    <small className="text-muted">Check items that this {getTypeLabel(node.type).toLowerCase()} relies on</small>
                  </div>
                  <Button size="sm" color="light" onClick={() => setManagingDepsFor(null)}>
                    <FontAwesomeIcon icon="times" className="me-1" />
                    Close
                  </Button>
                </div>

                <div className="btn-group mb-3 w-100" role="group">
                  <button
                    type="button"
                    className={`btn btn-sm ${dependencyTab === 'HTTP' ? 'btn-primary' : 'btn-outline-primary'}`}
                    onClick={() => setDependencyTab('HTTP')}
                  >
                    <FontAwesomeIcon icon="globe" className="me-1" />
                    APIs
                  </button>
                  {!isPublic && (
                    <>
                      <button
                        type="button"
                        className={`btn btn-sm ${dependencyTab === 'SERVICE' ? 'btn-primary' : 'btn-outline-primary'}`}
                        onClick={() => setDependencyTab('SERVICE')}
                      >
                        <FontAwesomeIcon icon="server" className="me-1" />
                        Services
                      </button>
                      <button
                        type="button"
                        className={`btn btn-sm ${dependencyTab === 'INSTANCE' ? 'btn-primary' : 'btn-outline-primary'}`}
                        onClick={() => setDependencyTab('INSTANCE')}
                      >
                        <FontAwesomeIcon icon="hdd" className="me-1" />
                        Instances
                      </button>
                    </>
                  )}
                </div>

                <div className="input-group input-group-sm mb-3">
                  <span className="input-group-text bg-white">
                    <FontAwesomeIcon icon="search" className="text-muted" />
                  </span>
                  <input
                    type="text"
                    className="form-control"
                    placeholder={`Search ${dependencyTab === 'HTTP' ? 'APIs' : dependencyTab === 'SERVICE' ? 'Services' : 'Instances'}...`}
                    onChange={e => {
                      const val = e.target.value.toLowerCase();
                      setAllItems(prev =>
                        prev.map(item => ({
                          ...item,
                          hidden: item.type === dependencyTab && !item.name.toLowerCase().includes(val),
                        })),
                      );
                    }}
                  />
                </div>

                <div className="dependency-items-list" style={{ maxHeight: '350px', overflowY: 'auto' }}>
                  {allItems
                    .filter(i => {
                      if (i.type !== dependencyTab) return false;
                      if (i.hidden) return false;
                      if (i.id === node.itemId && i.type === node.type) return false;
                      return true;
                    })
                    .map(item => {
                      const isChild = node.children.some(c => c.itemId === item.id && c.type === item.type);
                      return (
                        <label
                          key={`${item.type}-${item.id}`}
                          className={`dependency-item d-flex align-items-center gap-3 p-3 mb-2 border rounded ${isChild ? 'border-success bg-success-subtle' : 'bg-light'} ${saving ? 'opacity-50' : ''}`}
                          style={{ cursor: saving ? 'not-allowed' : 'pointer', transition: 'all 0.2s' }}
                        >
                          <input
                            type="checkbox"
                            className="form-check-input m-0"
                            style={{ width: '20px', height: '20px', cursor: 'pointer' }}
                            checked={isChild}
                            onChange={async () => {
                              if (isChild) {
                                setSaving(true);
                                try {
                                  const depsRes = await axios.get('/api/status-dependencies', {
                                    params: { 'statusPageId.equals': statusPageId },
                                  });
                                  const dep = depsRes.data.find(
                                    d =>
                                      d.parentId === node.itemId &&
                                      d.parentType === node.type &&
                                      d.childId === item.id &&
                                      d.childType === item.type,
                                  );
                                  if (dep) {
                                    await axios.delete(`/api/status-dependencies/${dep.id}`);
                                    await loadData();
                                  }
                                } catch (error) {
                                  console.error('Error removing dependency:', error);
                                } finally {
                                  setSaving(false);
                                }
                              } else {
                                await handleAddDependency(node, item.id, item.type);
                              }
                            }}
                            disabled={saving}
                          />
                          <div className="flex-grow-1">
                            <div className="fw-semibold">{item.name}</div>
                          </div>
                          {isChild && (
                            <Badge color="success" className="px-2 py-1">
                              <FontAwesomeIcon icon="check" className="me-1" />
                              Connected
                            </Badge>
                          )}
                        </label>
                      );
                    })}
                  {allItems.filter(i => i.type === dependencyTab && !i.hidden).length === 0 && (
                    <div className="text-center py-5 text-muted">
                      <FontAwesomeIcon icon="inbox" size="3x" className="mb-3 opacity-25" />
                      <p className="mb-0">
                        No {dependencyTab === 'HTTP' ? 'APIs' : dependencyTab === 'SERVICE' ? 'services' : 'instances'} found
                      </p>
                    </div>
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
          {canAddDependencies(node) && (
            <Button
              color="link"
              size="sm"
              className="text-primary"
              onClick={() => setManagingDepsFor(managingDepsFor === node.id ? null : node.id)}
              disabled={saving}
              title="Manage dependencies"
            >
              <FontAwesomeIcon icon="link" />
            </Button>
          )}
        </div>
        {managingDepsFor === node.id && (
          <div className="dependency-manager-panel mt-3 mb-3 p-3 bg-white border rounded shadow-sm">
            <div className="d-flex justify-content-between align-items-center mb-3 pb-2 border-bottom">
              <div>
                <h6 className="mb-0">
                  <FontAwesomeIcon icon="link" className="me-2 text-primary" />
                  What does <strong>{node.name}</strong> depend on?
                </h6>
                <small className="text-muted">Check items that this {getTypeLabel(node.type).toLowerCase()} relies on</small>
              </div>
              <Button size="sm" color="light" onClick={() => setManagingDepsFor(null)}>
                <FontAwesomeIcon icon="times" />
              </Button>
            </div>

            <div className="btn-group mb-3 w-100" role="group">
              <button
                type="button"
                className={`btn btn-sm ${dependencyTab === 'HTTP' ? 'btn-primary' : 'btn-outline-primary'}`}
                onClick={() => setDependencyTab('HTTP')}
              >
                <FontAwesomeIcon icon="globe" className="me-1" />
                APIs
              </button>
              {!isPublic && (
                <>
                  <button
                    type="button"
                    className={`btn btn-sm ${dependencyTab === 'SERVICE' ? 'btn-primary' : 'btn-outline-primary'}`}
                    onClick={() => setDependencyTab('SERVICE')}
                  >
                    <FontAwesomeIcon icon="server" className="me-1" />
                    Services
                  </button>
                  <button
                    type="button"
                    className={`btn btn-sm ${dependencyTab === 'INSTANCE' ? 'btn-primary' : 'btn-outline-primary'}`}
                    onClick={() => setDependencyTab('INSTANCE')}
                  >
                    <FontAwesomeIcon icon="hdd" className="me-1" />
                    Instances
                  </button>
                </>
              )}
            </div>

            <div className="input-group input-group-sm mb-3">
              <span className="input-group-text bg-white">
                <FontAwesomeIcon icon="search" className="text-muted" />
              </span>
              <input
                type="text"
                className="form-control"
                placeholder={`Search ${dependencyTab === 'HTTP' ? 'APIs' : dependencyTab === 'SERVICE' ? 'Services' : 'Instances'}...`}
                onChange={e => {
                  const val = e.target.value.toLowerCase();
                  setAllItems(prev =>
                    prev.map(item => ({
                      ...item,
                      hidden: item.type === dependencyTab && !item.name.toLowerCase().includes(val),
                    })),
                  );
                }}
              />
            </div>

            <div className="dependency-items-list" style={{ maxHeight: '350px', overflowY: 'auto' }}>
              {allItems
                .filter(i => {
                  if (i.type !== dependencyTab) return false;
                  if (i.hidden) return false;
                  if (i.id === node.itemId && i.type === node.type) return false;
                  return true;
                })
                .map(item => {
                  const isChild = node.children.some(c => c.itemId === item.id && c.type === item.type);
                  return (
                    <label
                      key={`${item.type}-${item.id}`}
                      className={`dependency-item d-flex align-items-center gap-3 p-3 mb-2 border rounded ${isChild ? 'border-success bg-success-subtle' : 'bg-light'} ${saving ? 'opacity-50' : ''}`}
                      style={{ cursor: saving ? 'not-allowed' : 'pointer', transition: 'all 0.2s' }}
                    >
                      <input
                        type="checkbox"
                        className="form-check-input m-0"
                        style={{ width: '20px', height: '20px', cursor: 'pointer' }}
                        checked={isChild}
                        onChange={async () => {
                          if (isChild) {
                            setSaving(true);
                            try {
                              const depsRes = await axios.get('/api/status-dependencies', {
                                params: { 'statusPageId.equals': statusPageId },
                              });
                              const dep = depsRes.data.find(
                                d =>
                                  d.parentId === node.itemId &&
                                  d.parentType === node.type &&
                                  d.childId === item.id &&
                                  d.childType === item.type,
                              );
                              if (dep) {
                                await axios.delete(`/api/status-dependencies/${dep.id}`);
                                await loadData();
                              }
                            } catch (error) {
                              console.error('Error removing dependency:', error);
                            } finally {
                              setSaving(false);
                            }
                          } else {
                            await handleAddDependency(node, item.id, item.type);
                          }
                        }}
                        disabled={saving}
                      />
                      <div className="flex-grow-1">
                        <div className="fw-semibold">{item.name}</div>
                      </div>
                      {isChild && (
                        <Badge color="success" className="px-2 py-1">
                          <FontAwesomeIcon icon="check" className="me-1" />
                          Connected
                        </Badge>
                      )}
                    </label>
                  );
                })}
              {allItems.filter(i => i.type === dependencyTab && !i.hidden).length === 0 && (
                <div className="text-center py-5 text-muted">
                  <FontAwesomeIcon icon="inbox" size="3x" className="mb-3 opacity-25" />
                  <p className="mb-0">
                    No {dependencyTab === 'HTTP' ? 'APIs' : dependencyTab === 'SERVICE' ? 'services' : 'instances'} found
                  </p>
                </div>
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
