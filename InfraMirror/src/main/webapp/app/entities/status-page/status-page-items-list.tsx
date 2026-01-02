import React, { useEffect, useState } from 'react';
import { Button, Badge, Spinner } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import axios from 'axios';
import './status-page-items-list.scss';

interface StatusPageItemsListProps {
  statusPageId: number;
  isPublic?: boolean;
}

interface PageItem {
  id: number;
  itemType: string;
  itemId: number;
  itemName: string;
  displayOrder: number;
  dependencies?: number[];
}

interface AvailableItem {
  id: number;
  name: string;
  type: string;
}

export const StatusPageItemsList: React.FC<StatusPageItemsListProps> = ({ statusPageId, isPublic = false }) => {
  const [items, setItems] = useState<PageItem[]>([]);
  const [allItems, setAllItems] = useState<AvailableItem[]>([]);
  const [expandedItemId, setExpandedItemId] = useState<number | null>(null);
  const [dependencyTab, setDependencyTab] = useState<'HTTP' | 'INSTANCE' | 'SERVICE'>('HTTP');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    fetchData();
  }, [statusPageId]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [itemsRes, httpRes, instancesRes, servicesRes, depsRes] = await Promise.all([
        axios.get(`/api/status-page-items`, { params: { 'statusPageId.equals': statusPageId, size: 1000 } }),
        axios.get('/api/http-monitors', { params: { size: 1000 } }),
        axios.get('/api/instances', { params: { size: 1000 } }),
        axios.get('/api/service-instances', { params: { size: 1000 } }),
        axios.get('/api/status-dependencies', { params: { 'statusPageId.equals': statusPageId } }),
      ]);

      const dependencies = depsRes.data;
      const itemsWithDeps = itemsRes.data.map(item => ({
        ...item,
        dependencies: dependencies.filter(dep => dep.parentId === item.itemId).map(dep => dep.childId),
      }));

      setItems(itemsWithDeps);

      const all = [
        ...httpRes.data.map(i => ({ id: i.id, name: i.name, type: 'HTTP' })),
        ...instancesRes.data.map(i => ({ id: i.id, name: i.hostname, type: 'INSTANCE' })),
        ...servicesRes.data.map(i => ({ id: i.id, name: i.name, type: 'SERVICE' })),
      ];
      setAllItems(all);
    } catch (error) {
      console.error('Error fetching data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleToggleDependencies = (itemId: number) => {
    setExpandedItemId(expandedItemId === itemId ? null : itemId);
    setDependencyTab('HTTP');
  };

  const handleAddDependency = async (parentItem: PageItem, childId: number, childType: string) => {
    setSaving(true);
    try {
      await axios.post('/api/status-dependencies', {
        statusPage: { id: statusPageId },
        parentType: parentItem.itemType,
        parentId: parentItem.itemId,
        childType,
        childId,
      });
      await fetchData();
    } catch (error) {
      console.error('Error adding dependency:', error);
    } finally {
      setSaving(false);
    }
  };

  const handleRemoveDependency = async (parentItem: PageItem, childId: number) => {
    setSaving(true);
    try {
      const depsRes = await axios.get('/api/status-dependencies', {
        params: { 'statusPageId.equals': statusPageId },
      });
      const dep = depsRes.data.find(d => d.parentId === parentItem.itemId && d.childId === childId);
      if (dep) {
        await axios.delete(`/api/status-dependencies/${dep.id}`);
        await fetchData();
      }
    } catch (error) {
      console.error('Error removing dependency:', error);
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="text-center py-4">
        <Spinner color="primary" />
      </div>
    );
  }

  return (
    <div className="status-page-items-list">
      <h5 className="mb-3">Status Page Items</h5>
      {items.length === 0 ? (
        <div className="alert alert-info">No items added yet. Click &quot;Manage Items&quot; to add items.</div>
      ) : (
        <div className="items-container">
          {items.map(item => (
            <div key={item.id} className="item-card">
              <div className="item-header">
                <div className="item-info">
                  <Badge color="primary">{item.itemType}</Badge>
                  <span className="item-name">{item.itemName}</span>
                  {item.dependencies && item.dependencies.length > 0 && <Badge color="secondary">{item.dependencies.length} deps</Badge>}
                </div>
                <Button color="info" size="sm" onClick={() => handleToggleDependencies(item.id)} disabled={saving}>
                  <FontAwesomeIcon icon={expandedItemId === item.id ? 'chevron-up' : 'sitemap'} className="me-1" />
                  {expandedItemId === item.id ? 'Hide' : 'Dependencies'}
                </Button>
              </div>

              {expandedItemId === item.id && (
                <div className="dependency-section">
                  <div className="dependency-tabs">
                    <button className={`dep-tab ${dependencyTab === 'HTTP' ? 'active' : ''}`} onClick={() => setDependencyTab('HTTP')}>
                      HTTP
                    </button>
                    {!isPublic && (
                      <>
                        <button
                          className={`dep-tab ${dependencyTab === 'INSTANCE' ? 'active' : ''}`}
                          onClick={() => setDependencyTab('INSTANCE')}
                        >
                          Instance
                        </button>
                        <button
                          className={`dep-tab ${dependencyTab === 'SERVICE' ? 'active' : ''}`}
                          onClick={() => setDependencyTab('SERVICE')}
                        >
                          Service
                        </button>
                      </>
                    )}
                  </div>
                  <div className="dependency-items">
                    {allItems
                      .filter(i => {
                        if (i.type !== dependencyTab) return false;
                        if (i.id === item.itemId) return false;
                        const addedItem = items.find(pi => pi.itemId === i.id);
                        if (!addedItem) return false;
                        const wouldCreateCircular = addedItem.dependencies?.includes(item.itemId);
                        return !wouldCreateCircular;
                      })
                      .map(depItem => {
                        const isDependent = item.dependencies?.includes(depItem.id);
                        return (
                          <div key={depItem.id} className="dependency-item-row">
                            <label>
                              <input
                                type="checkbox"
                                checked={isDependent}
                                onChange={() =>
                                  isDependent
                                    ? handleRemoveDependency(item, depItem.id)
                                    : handleAddDependency(item, depItem.id, depItem.type)
                                }
                                disabled={saving}
                              />
                              <span>{depItem.name}</span>
                            </label>
                          </div>
                        );
                      })}
                    {allItems.filter(i => {
                      if (i.type !== dependencyTab) return false;
                      if (i.id === item.itemId) return false;
                      const addedItem = items.find(pi => pi.itemId === i.id);
                      return addedItem;
                    }).length === 0 && <div className="text-muted text-center py-2">No {dependencyTab} items available</div>}
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default StatusPageItemsList;
