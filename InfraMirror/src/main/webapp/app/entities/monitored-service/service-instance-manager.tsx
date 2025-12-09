import React, { useState, useEffect } from 'react';
import { Button, Table, Badge, Input } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import axios from 'axios';

interface ServiceInstanceManagerProps {
  serviceId: number;
}

export const ServiceInstanceManager: React.FC<ServiceInstanceManagerProps> = ({ serviceId }) => {
  const [instances, setInstances] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [showAddRow, setShowAddRow] = useState(false);
  const [availableInstances, setAvailableInstances] = useState<any[]>([]);
  const [newInstance, setNewInstance] = useState({ instanceId: '', port: '' });

  useEffect(() => {
    loadInstances();
    loadAvailableInstances();
  }, [serviceId]);

  const loadInstances = async () => {
    try {
      setLoading(true);
      const response = await axios.get(`/api/monitored-services/${serviceId}/instances`);
      setInstances(response.data);
    } catch (error) {
      console.error('Failed to load instances', error);
    } finally {
      setLoading(false);
    }
  };

  const loadAvailableInstances = async () => {
    try {
      const response = await axios.get('/api/instances');
      setAvailableInstances(response.data);
    } catch (error) {
      console.error('Failed to load instances', error);
    }
  };

  const handleAddInstance = async () => {
    if (!newInstance.instanceId || !newInstance.port) return;
    try {
      await axios.post(`/api/monitored-services/${serviceId}/instances`, {
        port: parseInt(newInstance.port, 10),
        isActive: true,
        instance: { id: parseInt(newInstance.instanceId, 10) },
      });
      setShowAddRow(false);
      setNewInstance({ instanceId: '', port: '' });
      loadInstances();
    } catch (error) {
      console.error('Failed to add instance', error);
    }
  };

  const handleToggleActive = async (si: any) => {
    try {
      await axios.patch(`/api/service-instances/${si.id}`, { ...si, isActive: !si.isActive });
      loadInstances();
    } catch (error) {
      console.error('Failed to toggle instance status', error);
    }
  };

  const handleDeleteInstance = async (instanceId: number) => {
    if (!confirm('Are you sure you want to remove this instance?')) return;
    try {
      await axios.delete(`/api/service-instances/${instanceId}`);
      loadInstances();
    } catch (error) {
      console.error('Failed to delete instance', error);
    }
  };

  if (loading) {
    return (
      <div className="text-center p-3">
        <FontAwesomeIcon icon="spinner" spin /> Loading instances...
      </div>
    );
  }

  return (
    <div style={{ backgroundColor: '#f8f9fa', padding: '1rem' }}>
      <Table size="sm" bordered style={{ marginBottom: 0, backgroundColor: 'white' }}>
        <thead>
          <tr>
            <th>Instance</th>
            <th>Hostname</th>
            <th>Port</th>
            <th>Status</th>
            <th style={{ width: '80px' }}>Actions</th>
          </tr>
        </thead>
        <tbody>
          {instances.map((si: any) => (
            <tr key={si.id}>
              <td>{si.instance?.name || 'N/A'}</td>
              <td>{si.instance?.hostname || 'N/A'}</td>
              <td>{si.port}</td>
              <td>
                <Badge
                  color={si.isActive ? 'success' : 'secondary'}
                  style={{ cursor: 'pointer' }}
                  onClick={() => handleToggleActive(si)}
                  title="Click to toggle"
                >
                  {si.isActive ? 'Active' : 'Inactive'}
                </Badge>
              </td>
              <td>
                <Button
                  color="link"
                  size="sm"
                  onClick={() => handleDeleteInstance(si.id)}
                  style={{ padding: 0, color: '#dc3545' }}
                  title="Remove"
                >
                  <FontAwesomeIcon icon="trash" />
                </Button>
              </td>
            </tr>
          ))}
          {showAddRow && (
            <tr style={{ backgroundColor: '#fff3cd' }}>
              <td>
                <Input
                  type="select"
                  bsSize="sm"
                  value={newInstance.instanceId}
                  onChange={e => setNewInstance({ ...newInstance, instanceId: e.target.value })}
                >
                  <option value="">Select Instance</option>
                  {availableInstances.map(inst => (
                    <option key={inst.id} value={inst.id}>
                      {inst.name}
                    </option>
                  ))}
                </Input>
              </td>
              <td style={{ fontSize: '0.85rem', color: '#6c757d' }}>
                {newInstance.instanceId
                  ? availableInstances.find(i => i.id === parseInt(newInstance.instanceId, 10))?.hostname || '-'
                  : '-'}
              </td>
              <td>
                <Input
                  type="number"
                  bsSize="sm"
                  value={newInstance.port}
                  onChange={e => setNewInstance({ ...newInstance, port: e.target.value })}
                  placeholder="Port"
                />
              </td>
              <td>
                <Badge color="success">Active</Badge>
              </td>
              <td>
                <div className="d-flex gap-1">
                  <Button color="success" size="sm" onClick={handleAddInstance} disabled={!newInstance.instanceId || !newInstance.port}>
                    Save
                  </Button>
                  <Button
                    color="secondary"
                    size="sm"
                    onClick={() => {
                      setShowAddRow(false);
                      setNewInstance({ instanceId: '', port: '' });
                    }}
                  >
                    Cancel
                  </Button>
                </div>
              </td>
            </tr>
          )}
          <tr>
            <td colSpan={5} style={{ textAlign: 'center', padding: '8px' }}>
              <Button color="link" size="sm" onClick={() => setShowAddRow(true)} disabled={showAddRow}>
                <FontAwesomeIcon icon="plus" className="me-1" />
                Add Instance
              </Button>
            </td>
          </tr>
        </tbody>
      </Table>
    </div>
  );
};
