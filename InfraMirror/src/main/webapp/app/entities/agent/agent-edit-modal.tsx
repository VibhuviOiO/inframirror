import React, { useState, useEffect } from 'react';
import { Button, Form, FormGroup, Label, Input, FormFeedback } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSave, faTimes } from '@fortawesome/free-solid-svg-icons';
import { IAgent } from 'app/shared/model/agent.model';
import axios from 'axios';
import { toast } from 'react-toastify';

interface AgentEditModalProps {
  isOpen: boolean;
  toggle: () => void;
  agent?: IAgent | null;
  onSave?: () => void;
}

export const AgentEditModal: React.FC<AgentEditModalProps> = ({ isOpen, toggle, agent, onSave }) => {
  if (!isOpen) return null;

  const [formData, setFormData] = useState<any>({
    name: '',
    hostname: '',
    ipAddress: '',
    osType: '',
    osVersion: '',
    agentVersion: '',
    status: 'ACTIVE',
    tags: '',
    datacenterId: null,
    regionId: null,
  });
  const [saving, setSaving] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [regions, setRegions] = useState([]);
  const [datacenters, setDatacenters] = useState([]);

  useEffect(() => {
    if (isOpen) {
      axios.get('/api/regions?size=1000').then(res => setRegions(res.data));
      axios.get('/api/datacenters?size=1000').then(res => setDatacenters(res.data));
    }
  }, [isOpen]);

  useEffect(() => {
    if (agent) {
      setFormData({
        name: agent.name || '',
        hostname: agent.hostname || '',
        ipAddress: agent.ipAddress || '',
        osType: agent.osType || '',
        osVersion: agent.osVersion || '',
        agentVersion: agent.agentVersion || '',
        status: agent.status || 'ACTIVE',
        tags: agent.tags ? JSON.stringify(agent.tags, null, 2) : '',
        datacenterId: agent.datacenter?.id || null,
        regionId: agent.region?.id || null,
      });
    } else {
      setFormData({
        name: '',
        hostname: '',
        ipAddress: '',
        osType: '',
        osVersion: '',
        agentVersion: '',
        status: 'ACTIVE',
        tags: '',
        datacenterId: null,
        regionId: null,
      });
    }
    setErrors({});
  }, [agent, isOpen]);

  const validate = () => {
    const newErrors: Record<string, string> = {};
    if (!formData.name?.trim()) newErrors.name = 'Name is required';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    setSaving(true);
    try {
      const data = {
        ...formData,
        tags: formData.tags ? JSON.parse(formData.tags) : null,
        datacenter: formData.datacenterId ? { id: formData.datacenterId } : null,
        region: formData.regionId ? { id: formData.regionId } : null,
      };
      delete data.datacenterId;
      delete data.regionId;

      if (agent?.id) {
        await axios.put(`/api/agents/${agent.id}`, { ...data, id: agent.id });
        toast.success('Agent updated successfully');
      } else {
        await axios.post('/api/agents', data);
        toast.success('Agent created successfully');
      }
      onSave?.();
      toggle();
    } catch (error) {
      toast.error('Failed to save agent');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="card">
      <Form onSubmit={handleSubmit}>
        <div className="card-header d-flex justify-content-between align-items-center">
          <h6 className="mb-0">{agent?.id ? 'Edit Agent' : 'Create Agent'}</h6>
          <Button close onClick={toggle} />
        </div>
        <div className="card-body" style={{ maxHeight: 'calc(100vh - 250px)', overflowY: 'auto' }}>
          <div className="row">
            <div className="col-md-6">
              <FormGroup className="mb-3">
                <Label for="name">
                  Name <span className="text-danger">*</span>
                </Label>
                <Input
                  type="text"
                  id="name"
                  value={formData.name}
                  onChange={e => setFormData({ ...formData, name: e.target.value })}
                  invalid={!!errors.name}
                  placeholder="agent-aws-us-east-1"
                />
                <FormFeedback>{errors.name}</FormFeedback>
              </FormGroup>
            </div>
            <div className="col-md-6">
              <FormGroup className="mb-3">
                <Label for="hostname">Hostname</Label>
                <Input
                  type="text"
                  id="hostname"
                  value={formData.hostname}
                  onChange={e => setFormData({ ...formData, hostname: e.target.value })}
                  placeholder="ip-10-0-1-5.ec2.internal"
                />
              </FormGroup>
            </div>
          </div>

          <div className="row">
            <div className="col-md-6">
              <FormGroup className="mb-3">
                <Label for="ipAddress">IP Address</Label>
                <Input
                  type="text"
                  id="ipAddress"
                  value={formData.ipAddress}
                  onChange={e => setFormData({ ...formData, ipAddress: e.target.value })}
                  placeholder="10.0.1.5"
                />
              </FormGroup>
            </div>
            <div className="col-md-6">
              <FormGroup className="mb-3">
                <Label for="osType">OS Type</Label>
                <Input
                  type="text"
                  id="osType"
                  value={formData.osType}
                  onChange={e => setFormData({ ...formData, osType: e.target.value })}
                  placeholder="Linux"
                />
              </FormGroup>
            </div>
          </div>

          <div className="row">
            <div className="col-md-6">
              <FormGroup className="mb-3">
                <Label for="osVersion">OS Version</Label>
                <Input
                  type="text"
                  id="osVersion"
                  value={formData.osVersion}
                  onChange={e => setFormData({ ...formData, osVersion: e.target.value })}
                  placeholder="Ubuntu 22.04"
                />
              </FormGroup>
            </div>
            <div className="col-md-6">
              <FormGroup className="mb-3">
                <Label for="agentVersion">Agent Version</Label>
                <Input
                  type="text"
                  id="agentVersion"
                  value={formData.agentVersion}
                  onChange={e => setFormData({ ...formData, agentVersion: e.target.value })}
                  placeholder="1.0.0"
                />
              </FormGroup>
            </div>
          </div>

          <div className="row">
            <div className="col-md-6">
              <FormGroup className="mb-3">
                <Label for="status">Status</Label>
                <Input
                  type="select"
                  id="status"
                  value={formData.status}
                  onChange={e => setFormData({ ...formData, status: e.target.value })}
                >
                  <option value="ACTIVE">Active</option>
                  <option value="INACTIVE">Inactive</option>
                  <option value="OFFLINE">Offline</option>
                </Input>
              </FormGroup>
            </div>
            <div className="col-md-6">
              <FormGroup className="mb-3">
                <Label for="datacenterId">Datacenter</Label>
                <Input
                  type="select"
                  id="datacenterId"
                  value={formData.datacenterId || ''}
                  onChange={e => setFormData({ ...formData, datacenterId: e.target.value ? Number(e.target.value) : null })}
                >
                  <option value="">Select Datacenter</option>
                  {datacenters.map(dc => (
                    <option key={dc.id} value={dc.id}>
                      {dc.name}
                    </option>
                  ))}
                </Input>
              </FormGroup>
            </div>
          </div>

          <FormGroup className="mb-3">
            <Label for="regionId">Region</Label>
            <Input
              type="select"
              id="regionId"
              value={formData.regionId || ''}
              onChange={e => setFormData({ ...formData, regionId: e.target.value ? Number(e.target.value) : null })}
            >
              <option value="">Select Region</option>
              {regions.map(region => (
                <option key={region.id} value={region.id}>
                  {region.name}
                </option>
              ))}
            </Input>
          </FormGroup>

          <FormGroup className="mb-3">
            <Label for="tags">Tags (JSON)</Label>
            <Input
              type="textarea"
              id="tags"
              value={formData.tags}
              onChange={e => setFormData({ ...formData, tags: e.target.value })}
              rows={4}
              placeholder={'{\n  "environment": "production",\n  "team": "platform"\n}'}
            />
            <small className="text-muted">Enter valid JSON format</small>
          </FormGroup>
        </div>
        <div className="card-footer d-flex justify-content-end gap-2 py-2">
          <Button color="secondary" size="sm" onClick={toggle} disabled={saving}>
            <FontAwesomeIcon icon={faTimes} /> Cancel
          </Button>
          <Button color="primary" size="sm" type="submit" disabled={saving}>
            <FontAwesomeIcon icon={faSave} /> {saving ? 'Saving...' : 'Save'}
          </Button>
        </div>
      </Form>
    </div>
  );
};

export default AgentEditModal;
