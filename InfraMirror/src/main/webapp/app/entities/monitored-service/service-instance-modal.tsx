import React, { useState, useEffect } from 'react';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button, Form, FormGroup, Label, Input, Table } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import axios from 'axios';
import { toast } from 'react-toastify';

interface ServiceInstanceModalProps {
  isOpen: boolean;
  toggle: () => void;
  monitoredServiceId: number;
  monitoredServiceName: string;
}

export const ServiceInstanceModal: React.FC<ServiceInstanceModalProps> = ({ isOpen, toggle, monitoredServiceId, monitoredServiceName }) => {
  const [instances, setInstances] = useState([]);
  const [serviceInstances, setServiceInstances] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({ instanceId: '', port: '', isActive: true });
  const [editingId, setEditingId] = useState<number | null>(null);

  useEffect(() => {
    if (isOpen) {
      loadData();
    }
  }, [isOpen, monitoredServiceId]);

  const loadData = async () => {
    setLoading(true);
    try {
      const [instancesRes, serviceInstancesRes] = await Promise.all([
        axios.get('/api/instances'),
        axios.get(`/api/monitored-services/${monitoredServiceId}/instances`),
      ]);
      setInstances(instancesRes.data);
      setServiceInstances(serviceInstancesRes.data);
    } catch (error) {
      toast.error('Failed to load data');
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async () => {
    if (!formData.instanceId || !formData.port) {
      toast.error('Please select instance and enter port');
      return;
    }

    try {
      const entity = {
        port: +formData.port,
        isActive: formData.isActive,
        instance: { id: +formData.instanceId },
        monitoredService: { id: monitoredServiceId },
      };

      if (editingId) {
        await axios.put(`/api/service-instances/${editingId}`, { ...entity, id: editingId });
        toast.success('Instance updated');
      } else {
        await axios.post('/api/service-instances', entity);
        toast.success('Instance added');
      }

      setFormData({ instanceId: '', port: '', isActive: true });
      setEditingId(null);
      setShowForm(false);
      loadData();
    } catch (error) {
      toast.error('Failed to save instance');
    }
  };

  const handleEdit = (si: any) => {
    setEditingId(si.id);
    setFormData({
      instanceId: si.instance.id,
      port: si.port,
      isActive: si.isActive,
    });
    setShowForm(true);
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Delete this instance?')) return;
    try {
      await axios.delete(`/api/service-instances/${id}`);
      toast.success('Instance deleted');
      loadData();
    } catch (error) {
      toast.error('Failed to delete instance');
    }
  };

  return (
    <Modal isOpen={isOpen} toggle={toggle} size="lg">
      <ModalHeader toggle={toggle}>Service Instances - {monitoredServiceName}</ModalHeader>
      <ModalBody>
        {loading ? (
          <div className="text-center">Loading...</div>
        ) : (
          <>
            <div className="d-flex justify-content-between mb-3">
              <h6>Instances ({serviceInstances.length})</h6>
              <Button color="primary" size="sm" onClick={() => setShowForm(!showForm)}>
                <FontAwesomeIcon icon="plus" /> {showForm ? 'Cancel' : 'Add Instance'}
              </Button>
            </div>

            {showForm && (
              <div className="border rounded p-3 mb-3">
                <Form>
                  <div className="row">
                    <div className="col-md-5">
                      <FormGroup>
                        <Label>Instance</Label>
                        <Input
                          type="select"
                          value={formData.instanceId}
                          onChange={e => setFormData({ ...formData, instanceId: e.target.value })}
                        >
                          <option value="">Select Instance</option>
                          {instances.map((inst: any) => (
                            <option key={inst.id} value={inst.id}>
                              {inst.name} ({inst.hostname})
                            </option>
                          ))}
                        </Input>
                      </FormGroup>
                    </div>
                    <div className="col-md-3">
                      <FormGroup>
                        <Label>Port</Label>
                        <Input
                          type="number"
                          value={formData.port}
                          onChange={e => setFormData({ ...formData, port: e.target.value })}
                          placeholder="e.g., 6379"
                        />
                      </FormGroup>
                    </div>
                    <div className="col-md-2">
                      <FormGroup check className="mt-4">
                        <Input
                          type="checkbox"
                          checked={formData.isActive}
                          onChange={e => setFormData({ ...formData, isActive: e.target.checked })}
                        />
                        <Label check>Active</Label>
                      </FormGroup>
                    </div>
                    <div className="col-md-2 d-flex align-items-end">
                      <Button color="success" size="sm" onClick={handleSave}>
                        {editingId ? 'Update' : 'Add'}
                      </Button>
                    </div>
                  </div>
                </Form>
              </div>
            )}

            {serviceInstances.length > 0 ? (
              <Table size="sm" striped>
                <thead>
                  <tr>
                    <th>Instance</th>
                    <th>Hostname</th>
                    <th>Port</th>
                    <th>Status</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {serviceInstances.map((si: any) => (
                    <tr key={si.id}>
                      <td>{si.instance?.name || 'N/A'}</td>
                      <td>{si.instance?.hostname || 'N/A'}</td>
                      <td>{si.port}</td>
                      <td>
                        <span className={`badge ${si.isActive ? 'bg-success' : 'bg-secondary'}`}>
                          {si.isActive ? 'Active' : 'Inactive'}
                        </span>
                      </td>
                      <td>
                        <Button color="link" size="sm" onClick={() => handleEdit(si)} className="p-0 me-2">
                          <FontAwesomeIcon icon="edit" />
                        </Button>
                        <Button color="link" size="sm" onClick={() => handleDelete(si.id)} className="p-0 text-danger">
                          <FontAwesomeIcon icon="trash" />
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            ) : (
              <div className="text-center text-muted py-3">No instances configured</div>
            )}
          </>
        )}
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={toggle}>
          Close
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default ServiceInstanceModal;
