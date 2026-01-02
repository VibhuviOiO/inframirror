import React, { useEffect, useState } from 'react';
import { Button, Input, Badge } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import axios from 'axios';
import './manage-dependencies-panel.scss';

interface Dependency {
  id?: number;
  parentType: string;
  parentId: number;
  parentName?: string;
  childType: string;
  childId: number;
  childName?: string;
}

interface Item {
  id: number;
  name: string;
  type: string;
}

interface ManageDependenciesPanelProps {
  statusPageId: number;
  onClose: () => void;
  onDependenciesUpdated: () => void;
}

export const ManageDependenciesPanel: React.FC<ManageDependenciesPanelProps> = ({ statusPageId, onClose, onDependenciesUpdated }) => {
  const [dependencies, setDependencies] = useState<Dependency[]>([]);
  const [items, setItems] = useState<Item[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedParent, setSelectedParent] = useState<number | null>(null);
  const [selectedChild, setSelectedChild] = useState<number | null>(null);

  useEffect(() => {
    fetchData();
  }, [statusPageId]);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [depsRes, itemsRes] = await Promise.all([
        axios.get(`/api/status-dependencies`, { params: { 'statusPageId.equals': statusPageId } }),
        axios.get(`/api/status-page-items`, { params: { 'statusPageId.equals': statusPageId } }),
      ]);

      setDependencies(depsRes.data);
      setItems(itemsRes.data.map(item => ({ id: item.itemId, name: item.itemName, type: item.itemType })));
    } catch (error) {
      console.error('Error fetching dependencies:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleAddDependency = async () => {
    if (!selectedParent || !selectedChild) return;

    const parent = items.find(i => i.id === selectedParent);
    const child = items.find(i => i.id === selectedChild);

    try {
      await axios.post('/api/status-dependencies', {
        statusPage: { id: statusPageId },
        parentType: parent.type,
        parentId: parent.id,
        childType: child.type,
        childId: child.id,
      });

      setSelectedParent(null);
      setSelectedChild(null);
      fetchData();
      onDependenciesUpdated();
    } catch (error) {
      console.error('Error adding dependency:', error);
    }
  };

  const handleDeleteDependency = async (depId: number) => {
    try {
      await axios.delete(`/api/status-dependencies/${depId}`);
      fetchData();
      onDependenciesUpdated();
    } catch (error) {
      console.error('Error deleting dependency:', error);
    }
  };

  const getItemName = (type: string, id: number, enrichedName?: string) => {
    if (enrichedName) return enrichedName;
    return items.find(i => i.id === id && i.type === type)?.name || `${type} #${id}`;
  };

  return (
    <div className="manage-dependencies-overlay" onClick={onClose}>
      <div className="manage-dependencies-panel" onClick={e => e.stopPropagation()}>
        <div className="panel-header">
          <h5>Manage Dependencies</h5>
          <Button close onClick={onClose} />
        </div>

        <div className="panel-body">
          <div className="add-dependency-section">
            <h6>Add Dependency</h6>
            <div className="dependency-form">
              <div className="form-group">
                <label>Parent Item</label>
                <Input type="select" value={selectedParent || ''} onChange={e => setSelectedParent(Number(e.target.value))}>
                  <option value="">Select parent...</option>
                  {items.map(item => (
                    <option key={`${item.type}-${item.id}`} value={item.id}>
                      {item.name} ({item.type})
                    </option>
                  ))}
                </Input>
              </div>
              <div className="dependency-arrow">
                <FontAwesomeIcon icon="arrow-down" />
              </div>
              <div className="form-group">
                <label>Child Item (depends on parent)</label>
                <Input type="select" value={selectedChild || ''} onChange={e => setSelectedChild(Number(e.target.value))}>
                  <option value="">Select child...</option>
                  {items
                    .filter(item => item.id !== selectedParent)
                    .map(item => (
                      <option key={`${item.type}-${item.id}`} value={item.id}>
                        {item.name} ({item.type})
                      </option>
                    ))}
                </Input>
              </div>
              <Button color="primary" onClick={handleAddDependency} disabled={!selectedParent || !selectedChild}>
                <FontAwesomeIcon icon="plus" /> Add Dependency
              </Button>
            </div>
          </div>

          <div className="dependencies-list">
            <h6>
              Existing Dependencies <Badge color="secondary">{dependencies.length}</Badge>
            </h6>
            {loading ? (
              <div className="text-center py-3">
                <FontAwesomeIcon icon="spinner" spin />
              </div>
            ) : dependencies.length === 0 ? (
              <div className="alert alert-info">No dependencies defined yet</div>
            ) : (
              <div className="dependency-items">
                {dependencies.map(dep => (
                  <div key={dep.id} className="dependency-item">
                    <div className="dependency-content">
                      <div className="dependency-parent">
                        <Badge color="primary">{dep.parentType}</Badge>
                        <span>{getItemName(dep.parentType, dep.parentId, dep.parentName)}</span>
                      </div>
                      <FontAwesomeIcon icon="arrow-down" className="dependency-arrow-icon" />
                      <div className="dependency-child">
                        <Badge color="info">{dep.childType}</Badge>
                        <span>{getItemName(dep.childType, dep.childId, dep.childName)}</span>
                      </div>
                    </div>
                    <Button color="danger" size="sm" onClick={() => handleDeleteDependency(dep.id)}>
                      <FontAwesomeIcon icon="trash" />
                    </Button>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ManageDependenciesPanel;
