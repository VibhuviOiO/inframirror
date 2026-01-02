import React, { useEffect, useState } from 'react';
import { Button, Input, Badge, Spinner } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import axios from 'axios';
import './manage-items-panel.scss';

interface ManageItemsPanelProps {
  statusPageId: number;
  statusPageName: string;
  isPublic?: boolean;
  onClose: () => void;
  onItemsUpdated: () => void;
}

interface AvailableItem {
  id: number;
  name: string;
  type: 'HTTP' | 'INSTANCE' | 'SERVICE';
  isAdded: boolean;
  group?: string;
  itemId?: number;
  dependencies?: number[];
}

interface StatusPageItem {
  id?: number;
  itemType: string;
  itemId: number;
  displayOrder: number;
}

type FilterType = 'all' | 'added' | 'notAdded';

export const ManageItemsPanel: React.FC<ManageItemsPanelProps> = ({
  statusPageId,
  statusPageName,
  isPublic = false,
  onClose,
  onItemsUpdated,
}) => {
  const [activeTab, setActiveTab] = useState<'HTTP' | 'INSTANCE' | 'SERVICE'>('HTTP');
  const [search, setSearch] = useState('');
  const [items, setItems] = useState<AvailableItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [currentItems, setCurrentItems] = useState<StatusPageItem[]>([]);
  const [selectedIds, setSelectedIds] = useState<Set<number>>(new Set());
  const [filter, setFilter] = useState<FilterType>('all');
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [showHelp, setShowHelp] = useState(false);
  const pageSize = 50;

  useEffect(() => {
    fetchCurrentItems();
  }, [statusPageId]);

  useEffect(() => {
    setPage(0);
    setItems([]);
    setHasMore(true);
    fetchAvailableItems(true);
  }, [activeTab, currentItems, search]);

  useEffect(() => {
    // Auto-select items that are already added
    const addedItemIds = items.filter(item => item.isAdded).map(item => item.id);
    if (addedItemIds.length > 0) {
      console.error('Auto-selecting added items:', addedItemIds);
      setSelectedIds(new Set(addedItemIds));
    }
  }, [items]);

  const fetchCurrentItems = async () => {
    try {
      const response = await axios.get(`/api/status-page-items`, {
        params: {
          'statusPageId.equals': statusPageId,
          size: 1000,
        },
      });
      console.error('Current items fetched:', response.data);
      setCurrentItems(response.data);
    } catch (error) {
      console.error('Error fetching current items:', error);
    }
  };

  const fetchAvailableItems = async (reset = false) => {
    if (loading || (!reset && !hasMore)) return;
    setLoading(true);
    try {
      let endpoint = '';
      let typeKey = '';

      switch (activeTab) {
        case 'HTTP':
          endpoint = '/api/http-monitors';
          typeKey = 'HTTP';
          break;
        case 'INSTANCE':
          endpoint = '/api/instances';
          typeKey = 'INSTANCE';
          break;
        case 'SERVICE':
          endpoint = '/api/monitored-services';
          typeKey = 'SERVICE';
          break;
        default:
          return;
      }

      const currentPage = reset ? 0 : page;
      const params: any = { page: currentPage, size: pageSize, sort: 'id,asc' };
      if (search) {
        params['name.contains'] = search;
      }

      const itemsResponse = await axios.get(endpoint, { params });

      const availableItems: AvailableItem[] = itemsResponse.data.map((item: any) => {
        let group = '';
        let itemName = '';

        if (typeKey === 'INSTANCE') {
          itemName = item.hostname || item.name || `Instance ${item.id}`;
          if (item.datacenter?.name) {
            group = item.datacenter.name;
          }
        } else if (typeKey === 'SERVICE') {
          itemName = item.name || `Service ${item.id}`;
          if (item.datacenter?.name) {
            group = item.datacenter.name;
          }
        } else {
          itemName = item.name || `${typeKey} ${item.id}`;
        }

        const isAdded = currentItems.some(ci => (ci.itemType === typeKey || ci.itemType === `${typeKey}_MONITOR`) && ci.itemId === item.id);

        return {
          id: item.id,
          itemId: item.id,
          name: itemName,
          type: typeKey as 'HTTP' | 'INSTANCE' | 'SERVICE',
          isAdded,
          group,
        };
      });

      setItems(prev => (reset ? availableItems : [...prev, ...availableItems]));
      setHasMore(itemsResponse.data.length === pageSize);
      setPage(currentPage + 1);
    } catch (error) {
      console.error('Error fetching items:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleToggleItem = async (item: AvailableItem) => {
    setSaving(true);
    try {
      if (item.isAdded) {
        const existingItem = currentItems.find(ci => ci.itemType === item.type && ci.itemId === item.id);
        if (existingItem?.id) {
          await axios.delete(`/api/status-page-items/${existingItem.id}`);
        }
      } else {
        const maxOrder = currentItems.reduce((max, ci) => Math.max(max, ci.displayOrder || 0), 0);
        await axios.post('/api/status-page-items', {
          itemType: item.type,
          itemId: item.id,
          displayOrder: maxOrder + 1,
          statusPage: { id: statusPageId },
        });
      }

      await fetchCurrentItems();
      await fetchAvailableItems();
      onItemsUpdated();
    } catch (error) {
      console.error('Error toggling item:', error);
    } finally {
      setSaving(false);
    }
  };

  const handleBulkAdd = async () => {
    console.error('handleBulkAdd called, selectedIds:', Array.from(selectedIds));
    setSaving(true);
    try {
      const itemsToAdd = Array.from(selectedIds)
        .map(id => items.find(item => item.id === id))
        .filter(item => item && !item.isAdded);

      console.error('Items to add:', itemsToAdd);

      if (itemsToAdd.length === 0) {
        console.error('No items to add, returning');
        setSaving(false);
        return;
      }

      const maxOrder = currentItems.reduce((max, ci) => Math.max(max, ci.displayOrder || 0), 0);
      console.error('Max order:', maxOrder);

      const promises = itemsToAdd.map((item, index) => {
        const payload = {
          itemType: item.type,
          itemId: item.id,
          displayOrder: maxOrder + index + 1,
          statusPage: { id: statusPageId },
        };
        console.error('Posting payload:', payload);
        return axios.post('/api/status-page-items', payload);
      });

      await Promise.all(promises);
      console.error('All items added successfully');

      setSelectedIds(new Set());
      await fetchCurrentItems();
      await fetchAvailableItems();
      onItemsUpdated();
    } catch (error) {
      console.error('Error bulk adding items:', error);
    } finally {
      setSaving(false);
    }
  };

  const handleBulkRemove = async () => {
    setSaving(true);
    try {
      const itemsToRemove = Array.from(selectedIds)
        .map(id => {
          const item = items.find(i => i.id === id);
          if (!item?.isAdded) return null;
          return currentItems.find(ci => ci.itemType === item.type && ci.itemId === item.id);
        })
        .filter(item => item?.id);

      await Promise.all(itemsToRemove.map(item => axios.delete(`/api/status-page-items/${item.id}`)));

      setSelectedIds(new Set());
      await fetchCurrentItems();
      await fetchAvailableItems();
      onItemsUpdated();
    } catch (error) {
      console.error('Error bulk removing items:', error);
    } finally {
      setSaving(false);
    }
  };

  const handleToggleSelect = (id: number) => {
    const newSelected = new Set(selectedIds);
    if (newSelected.has(id)) {
      newSelected.delete(id);
    } else {
      newSelected.add(id);
    }
    setSelectedIds(newSelected);
  };

  const handleSelectAll = () => {
    const filtered = items
      .filter(item => {
        if (filter === 'added') return item.isAdded;
        if (filter === 'notAdded') return !item.isAdded;
        return true;
      })
      .filter(item => item.name.toLowerCase().includes(search.toLowerCase()));

    if (selectedIds.size === filtered.length) {
      setSelectedIds(new Set());
    } else {
      setSelectedIds(new Set(filtered.map(item => item.id)));
    }
  };

  const filteredItems = items
    .filter(item => {
      if (filter === 'added') return item.isAdded;
      if (filter === 'notAdded') return !item.isAdded;
      return true;
    })
    .filter(item => item.name.toLowerCase().includes(search.toLowerCase()));

  const groupedItems = filteredItems.reduce(
    (acc, item) => {
      const group = item.group || 'Other';
      if (!acc[group]) acc[group] = [];
      acc[group].push(item);
      return acc;
    },
    {} as Record<string, AvailableItem[]>,
  );

  const addedCount = items.filter(i => i.isAdded).length;
  const selectedCount = selectedIds.size;
  const selectedAddedCount = Array.from(selectedIds).filter(id => items.find(i => i.id === id)?.isAdded).length;
  const selectedNotAddedCount = selectedCount - selectedAddedCount;

  return (
    <div className="manage-items-panel">
      <div className="panel-overlay" onClick={onClose} />
      <div className="panel-content">
        <div className="panel-header">
          <div>
            <h5 className="mb-1">Manage Items</h5>
            <p className="text-muted mb-0 small">{statusPageName}</p>
          </div>
          <div className="d-flex gap-2">
            <Button color="link" size="sm" onClick={() => setShowHelp(!showHelp)}>
              <FontAwesomeIcon icon="question-circle" /> Help
            </Button>
            <Button close onClick={onClose} />
          </div>
        </div>

        {showHelp && (
          <div className="help-section">
            <div className="alert alert-info mb-0">
              <h6 className="mb-2">
                <FontAwesomeIcon icon="info-circle" /> How to Build Dependency Trees
              </h6>
              <div className="dependency-flow">
                <div className="flow-step">
                  <strong>Step 1:</strong> Add items to status page (check items below)
                </div>
                <div className="flow-arrow">↓</div>
                <div className="flow-step">
                  <strong>Step 2:</strong> Go to dependency tree view
                </div>
                <div className="flow-arrow">↓</div>
                <div className="flow-step">
                  <strong>Step 3:</strong> Click &ldquo;Add Dependency&rdquo; on any item to build chains
                </div>
              </div>
              <div className="mt-3">
                <strong>Example Chain:</strong>
                <div className="example-chain">
                  <span className="chain-item http">HTTP Monitor</span>
                  <FontAwesomeIcon icon="arrow-right" className="mx-2" />
                  <span className="chain-item service">Service</span>
                  <FontAwesomeIcon icon="arrow-right" className="mx-2" />
                  <span className="chain-item instance">Instance</span>
                </div>
                <small className="text-muted d-block mt-2">This shows: HTTP depends on Service, Service depends on Instance</small>
              </div>
            </div>
          </div>
        )}

        <div className="panel-body">
          <div className="tabs-container">
            <div className="nav nav-tabs">
              <button className={`nav-link ${activeTab === 'HTTP' ? 'active' : ''}`} onClick={() => setActiveTab('HTTP')} disabled={saving}>
                <FontAwesomeIcon icon="globe" /> HTTP Monitors
                {activeTab === 'HTTP' && (
                  <Badge color="primary" className="ms-2">
                    {addedCount}
                  </Badge>
                )}
              </button>
              {!isPublic && (
                <>
                  <button
                    className={`nav-link ${activeTab === 'INSTANCE' ? 'active' : ''}`}
                    onClick={() => setActiveTab('INSTANCE')}
                    disabled={saving}
                  >
                    <FontAwesomeIcon icon="server" /> Instances
                    {activeTab === 'INSTANCE' && (
                      <Badge color="primary" className="ms-2">
                        {addedCount}
                      </Badge>
                    )}
                  </button>
                  <button
                    className={`nav-link ${activeTab === 'SERVICE' ? 'active' : ''}`}
                    onClick={() => setActiveTab('SERVICE')}
                    disabled={saving}
                  >
                    <FontAwesomeIcon icon="cogs" /> Services
                    {activeTab === 'SERVICE' && (
                      <Badge color="primary" className="ms-2">
                        {addedCount}
                      </Badge>
                    )}
                  </button>
                </>
              )}
            </div>
          </div>

          <div className="search-container">
            <Input
              type="text"
              placeholder={`Search ${activeTab.toLowerCase()} monitors...`}
              value={search}
              onChange={e => setSearch(e.target.value)}
              disabled={saving}
            />
          </div>

          <div className="filter-container">
            <div className="btn-group btn-group-sm" role="group">
              <button
                type="button"
                className={`btn ${filter === 'all' ? 'btn-primary' : 'btn-outline-secondary'}`}
                onClick={() => setFilter('all')}
                disabled={saving}
              >
                All ({items.length})
              </button>
              <button
                type="button"
                className={`btn ${filter === 'added' ? 'btn-primary' : 'btn-outline-secondary'}`}
                onClick={() => setFilter('added')}
                disabled={saving}
              >
                Added ({addedCount})
              </button>
              <button
                type="button"
                className={`btn ${filter === 'notAdded' ? 'btn-primary' : 'btn-outline-secondary'}`}
                onClick={() => setFilter('notAdded')}
                disabled={saving}
              >
                Not Added ({items.length - addedCount})
              </button>
            </div>
          </div>

          {selectedCount > 0 && (
            <div className="bulk-actions">
              <div className="d-flex justify-content-between align-items-center">
                <span className="text-muted">{selectedCount} selected</span>
                <div className="d-flex gap-2">
                  {selectedNotAddedCount > 0 && (
                    <Button color="success" size="sm" onClick={handleBulkAdd} disabled={saving}>
                      {saving ? <Spinner size="sm" /> : <FontAwesomeIcon icon="plus" />}
                      <span className="ms-1">Add {selectedNotAddedCount}</span>
                    </Button>
                  )}
                  {selectedAddedCount > 0 && (
                    <Button color="danger" size="sm" onClick={handleBulkRemove} disabled={saving}>
                      {saving ? <Spinner size="sm" /> : <FontAwesomeIcon icon="minus" />}
                      <span className="ms-1">Remove {selectedAddedCount}</span>
                    </Button>
                  )}
                  <Button color="secondary" size="sm" onClick={() => setSelectedIds(new Set())} disabled={saving}>
                    Clear
                  </Button>
                </div>
              </div>
            </div>
          )}

          <div
            className="items-list"
            onScroll={e => {
              const target = e.target as HTMLDivElement;
              if (target.scrollHeight - target.scrollTop <= target.clientHeight + 100) {
                fetchAvailableItems();
              }
            }}
          >
            {loading ? (
              <div className="text-center py-5">
                <Spinner color="primary" />
                <p className="text-muted mt-2">Loading items...</p>
              </div>
            ) : filteredItems.length === 0 ? (
              <div className="text-center py-5 text-muted">
                <FontAwesomeIcon icon="inbox" size="3x" className="mb-3" />
                <p>No items found</p>
              </div>
            ) : (
              <>
                <div className="select-all-row">
                  <label className="d-flex align-items-center">
                    <input
                      type="checkbox"
                      checked={selectedIds.size === filteredItems.length && filteredItems.length > 0}
                      onChange={handleSelectAll}
                      disabled={saving}
                      className="me-2"
                    />
                    <span>Select All ({filteredItems.length})</span>
                  </label>
                </div>
                {Object.entries(groupedItems).map(([group, groupItems]) => (
                  <div key={group} className="item-group">
                    {group !== 'Other' && <div className="group-header">{group}</div>}
                    {groupItems.map(item => (
                      <>
                        <div key={`${item.type}-${item.id}`} className={`item-row ${item.isAdded ? 'added' : ''}`}>
                          <label className="item-checkbox">
                            <input type="checkbox" checked={item.isAdded} onChange={() => handleToggleItem(item)} disabled={saving} />
                          </label>
                          <div className="item-info">
                            <FontAwesomeIcon
                              icon={item.type === 'HTTP' ? 'globe' : item.type === 'INSTANCE' ? 'server' : 'cogs'}
                              className="me-2 text-muted"
                            />
                            <span>{item.name}</span>
                          </div>
                          {item.isAdded && <Badge color="success">Added</Badge>}
                        </div>
                      </>
                    ))}
                  </div>
                ))}
                {loading && (
                  <div className="text-center py-3">
                    <Spinner size="sm" color="primary" />
                  </div>
                )}
                {!loading && !hasMore && items.length > 0 && <div className="text-center py-2 text-muted small">No more items</div>}
              </>
            )}
          </div>
        </div>

        <div className="panel-footer">
          <Button color="secondary" onClick={onClose}>
            Close
          </Button>
        </div>
      </div>
    </div>
  );
};

export default ManageItemsPanel;
