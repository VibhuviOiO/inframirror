import React, { useState, useEffect } from 'react';
import { Button, Form, FormGroup, Label, Input, FormFeedback } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSave, faTimes } from '@fortawesome/free-solid-svg-icons';
import { IInstance } from 'app/shared/model/instance.model';
import axios from 'axios';
import { toast } from 'react-toastify';

interface InstanceEditModalProps {
  isOpen: boolean;
  toggle: () => void;
  instance?: IInstance | null;
  onSave?: () => void;
}

export const InstanceEditModal: React.FC<InstanceEditModalProps> = ({ isOpen, toggle, instance, onSave }) => {
  if (!isOpen) return null;

  const [formData, setFormData] = useState<any>({
    name: '',
    hostname: '',
    description: '',
    instanceType: 'VM',
    monitoringType: 'SELF_HOSTED',
    operatingSystem: '',
    platform: '',
    privateIpAddress: '',
    publicIpAddress: '',
    pingEnabled: true,
    pingInterval: 30,
    pingTimeoutMs: 3000,
    pingRetryCount: 2,
    hardwareMonitoringEnabled: false,
    hardwareMonitoringInterval: 300,
    cpuWarningThreshold: 70,
    cpuDangerThreshold: 90,
    memoryWarningThreshold: 75,
    memoryDangerThreshold: 90,
    diskWarningThreshold: 80,
    diskDangerThreshold: 95,
  });
  const [saving, setSaving] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [datacenters, setDatacenters] = useState([]);
  const [agents, setAgents] = useState([]);

  useEffect(() => {
    axios.get('/api/datacenters?size=1000').then(res => setDatacenters(res.data));
    axios.get('/api/agents?size=1000').then(res => setAgents(res.data));
  }, []);

  useEffect(() => {
    if (instance) {
      setFormData({
        ...instance,
        datacenterId: instance.datacenter?.id || '',
        agentId: instance.agent?.id || '',
      });
    } else {
      setFormData({
        name: '',
        hostname: '',
        description: '',
        instanceType: 'VM',
        monitoringType: 'SELF_HOSTED',
        operatingSystem: '',
        platform: '',
        privateIpAddress: '',
        publicIpAddress: '',
        pingEnabled: true,
        pingInterval: 30,
        pingTimeoutMs: 3000,
        pingRetryCount: 2,
        hardwareMonitoringEnabled: false,
        hardwareMonitoringInterval: 300,
        cpuWarningThreshold: 70,
        cpuDangerThreshold: 90,
        memoryWarningThreshold: 75,
        memoryDangerThreshold: 90,
        diskWarningThreshold: 80,
        diskDangerThreshold: 95,
      });
    }
    setErrors({});
  }, [instance, isOpen]);

  const validate = () => {
    const newErrors: Record<string, string> = {};
    if (!formData.name?.trim()) newErrors.name = 'Name is required';
    if (!formData.hostname?.trim()) newErrors.hostname = 'Hostname is required';
    if (!formData.datacenterId) newErrors.datacenterId = 'Datacenter is required';
    if (formData.monitoringType === 'AGENT_MONITORED' && !formData.agentId) newErrors.agentId = 'Agent is required';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    setSaving(true);
    try {
      const payload = {
        ...formData,
        datacenterId: formData.datacenterId ? +formData.datacenterId : null,
        agentId: formData.monitoringType === 'AGENT_MONITORED' && formData.agentId ? +formData.agentId : null,
      };
      if (instance?.id) {
        await axios.put(`/api/instances/${instance.id}`, payload);
        toast.success('Instance updated successfully');
      } else {
        await axios.post('/api/instances', payload);
        toast.success('Instance created successfully');
      }
      onSave?.();
      toggle();
    } catch (error) {
      toast.error('Failed to save instance');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="card">
      <Form onSubmit={handleSubmit}>
        <div className="card-header d-flex justify-content-between align-items-center">
          <h6 className="mb-0">{instance?.id ? 'Edit Instance' : 'Create Instance'}</h6>
          <Button close onClick={toggle} />
        </div>
        <div className="card-body" style={{ maxHeight: 'calc(100vh - 250px)', overflowY: 'auto' }}>
          <div className="row">
            <div className="col-md-6">
              <FormGroup className="mb-2">
                <Label className="form-label-sm">
                  Name <span className="text-danger">*</span>
                </Label>
                <Input
                  bsSize="sm"
                  value={formData.name}
                  onChange={e => setFormData({ ...formData, name: e.target.value })}
                  invalid={!!errors.name}
                />
                <FormFeedback>{errors.name}</FormFeedback>
              </FormGroup>
            </div>
            <div className="col-md-6">
              <FormGroup className="mb-2">
                <Label className="form-label-sm">
                  Hostname <span className="text-danger">*</span>
                </Label>
                <Input
                  bsSize="sm"
                  value={formData.hostname}
                  onChange={e => setFormData({ ...formData, hostname: e.target.value })}
                  invalid={!!errors.hostname}
                />
                <FormFeedback>{errors.hostname}</FormFeedback>
              </FormGroup>
            </div>
          </div>

          <FormGroup className="mb-2">
            <Label className="form-label-sm">Description</Label>
            <Input
              bsSize="sm"
              type="textarea"
              rows={2}
              value={formData.description || ''}
              onChange={e => setFormData({ ...formData, description: e.target.value })}
            />
          </FormGroup>

          <div className="row">
            <div className="col-md-6">
              <FormGroup className="mb-2">
                <Label className="form-label-sm">Instance Type</Label>
                <Input
                  bsSize="sm"
                  type="select"
                  value={formData.instanceType}
                  onChange={e => setFormData({ ...formData, instanceType: e.target.value })}
                >
                  <option value="VM">VM</option>
                  <option value="BARE_METAL">Bare Metal</option>
                  <option value="CONTAINER">Container</option>
                  <option value="CLOUD_INSTANCE">Cloud Instance</option>
                </Input>
              </FormGroup>
            </div>
            <div className="col-md-6">
              <FormGroup className="mb-2">
                <Label className="form-label-sm">Monitoring Type</Label>
                <Input
                  bsSize="sm"
                  type="select"
                  value={formData.monitoringType}
                  onChange={e => setFormData({ ...formData, monitoringType: e.target.value })}
                >
                  <option value="SELF_HOSTED">Self Hosted</option>
                  <option value="AGENT_MONITORED">Agent Monitored</option>
                </Input>
              </FormGroup>
            </div>
          </div>

          <FormGroup className="mb-2">
            <Label className="form-label-sm">
              Datacenter <span className="text-danger">*</span>
            </Label>
            <Input
              bsSize="sm"
              type="select"
              value={formData.datacenterId}
              onChange={e => setFormData({ ...formData, datacenterId: e.target.value })}
              invalid={!!errors.datacenterId}
            >
              <option value="">Select Datacenter</option>
              {datacenters.map(dc => (
                <option key={dc.id} value={dc.id}>
                  {dc.name}
                </option>
              ))}
            </Input>
            <FormFeedback>{errors.datacenterId}</FormFeedback>
          </FormGroup>

          {formData.monitoringType === 'AGENT_MONITORED' && (
            <FormGroup className="mb-2">
              <Label className="form-label-sm">
                Agent <span className="text-danger">*</span>
              </Label>
              <Input
                bsSize="sm"
                type="select"
                value={formData.agentId}
                onChange={e => setFormData({ ...formData, agentId: e.target.value })}
                invalid={!!errors.agentId}
              >
                <option value="">Select Agent</option>
                {agents.map(agent => (
                  <option key={agent.id} value={agent.id}>
                    {agent.name}
                  </option>
                ))}
              </Input>
              <FormFeedback>{errors.agentId}</FormFeedback>
            </FormGroup>
          )}

          <div className="row">
            <div className="col-md-6">
              <FormGroup className="mb-2">
                <Label className="form-label-sm">Operating System</Label>
                <Input
                  bsSize="sm"
                  value={formData.operatingSystem || ''}
                  onChange={e => setFormData({ ...formData, operatingSystem: e.target.value })}
                />
              </FormGroup>
            </div>
            <div className="col-md-6">
              <FormGroup className="mb-2">
                <Label className="form-label-sm">Platform</Label>
                <Input bsSize="sm" value={formData.platform || ''} onChange={e => setFormData({ ...formData, platform: e.target.value })} />
              </FormGroup>
            </div>
          </div>

          <div className="row">
            <div className="col-md-6">
              <FormGroup className="mb-2">
                <Label className="form-label-sm">Private IP</Label>
                <Input
                  bsSize="sm"
                  value={formData.privateIpAddress || ''}
                  onChange={e => setFormData({ ...formData, privateIpAddress: e.target.value })}
                />
              </FormGroup>
            </div>
            <div className="col-md-6">
              <FormGroup className="mb-2">
                <Label className="form-label-sm">Public IP</Label>
                <Input
                  bsSize="sm"
                  value={formData.publicIpAddress || ''}
                  onChange={e => setFormData({ ...formData, publicIpAddress: e.target.value })}
                />
              </FormGroup>
            </div>
          </div>

          <hr className="my-3" />
          <h6 className="mb-2" style={{ fontSize: '0.9rem' }}>
            Ping Monitoring
          </h6>
          <FormGroup check className="mb-2 form-check-sm">
            <Input
              type="checkbox"
              checked={formData.pingEnabled}
              onChange={e => setFormData({ ...formData, pingEnabled: e.target.checked })}
            />
            <Label check className="form-label-sm">
              Enable Ping
            </Label>
          </FormGroup>

          {formData.pingEnabled && (
            <div className="row">
              <div className="col-md-4">
                <FormGroup className="mb-2">
                  <Label className="form-label-sm">Interval (s)</Label>
                  <Input
                    bsSize="sm"
                    type="number"
                    value={formData.pingInterval}
                    onChange={e => setFormData({ ...formData, pingInterval: +e.target.value })}
                  />
                </FormGroup>
              </div>
              <div className="col-md-4">
                <FormGroup className="mb-2">
                  <Label className="form-label-sm">Timeout (ms)</Label>
                  <Input
                    bsSize="sm"
                    type="number"
                    value={formData.pingTimeoutMs}
                    onChange={e => setFormData({ ...formData, pingTimeoutMs: +e.target.value })}
                  />
                </FormGroup>
              </div>
              <div className="col-md-4">
                <FormGroup className="mb-2">
                  <Label className="form-label-sm">Retry</Label>
                  <Input
                    bsSize="sm"
                    type="number"
                    value={formData.pingRetryCount}
                    onChange={e => setFormData({ ...formData, pingRetryCount: +e.target.value })}
                  />
                </FormGroup>
              </div>
            </div>
          )}

          <hr className="my-3" />
          <h6 className="mb-2" style={{ fontSize: '0.9rem' }}>
            Hardware Monitoring
          </h6>
          <FormGroup check className="mb-2 form-check-sm">
            <Input
              type="checkbox"
              checked={formData.hardwareMonitoringEnabled}
              onChange={e => setFormData({ ...formData, hardwareMonitoringEnabled: e.target.checked })}
              disabled={formData.monitoringType === 'SELF_HOSTED'}
            />
            <Label check className="form-label-sm">
              Enable Hardware {formData.monitoringType === 'SELF_HOSTED' && '(requires Agent)'}
            </Label>
          </FormGroup>

          {formData.hardwareMonitoringEnabled && (
            <>
              <FormGroup className="mb-2">
                <Label className="form-label-sm">Interval (s)</Label>
                <Input
                  bsSize="sm"
                  type="number"
                  value={formData.hardwareMonitoringInterval}
                  onChange={e => setFormData({ ...formData, hardwareMonitoringInterval: +e.target.value })}
                />
              </FormGroup>
              <div className="row">
                <div className="col-md-6">
                  <FormGroup className="mb-2">
                    <Label className="form-label-sm">CPU Warning %</Label>
                    <Input
                      bsSize="sm"
                      type="number"
                      value={formData.cpuWarningThreshold}
                      onChange={e => setFormData({ ...formData, cpuWarningThreshold: +e.target.value })}
                    />
                  </FormGroup>
                </div>
                <div className="col-md-6">
                  <FormGroup className="mb-2">
                    <Label className="form-label-sm">CPU Danger %</Label>
                    <Input
                      bsSize="sm"
                      type="number"
                      value={formData.cpuDangerThreshold}
                      onChange={e => setFormData({ ...formData, cpuDangerThreshold: +e.target.value })}
                    />
                  </FormGroup>
                </div>
              </div>
              <div className="row">
                <div className="col-md-6">
                  <FormGroup className="mb-2">
                    <Label className="form-label-sm">Memory Warning %</Label>
                    <Input
                      bsSize="sm"
                      type="number"
                      value={formData.memoryWarningThreshold}
                      onChange={e => setFormData({ ...formData, memoryWarningThreshold: +e.target.value })}
                    />
                  </FormGroup>
                </div>
                <div className="col-md-6">
                  <FormGroup className="mb-2">
                    <Label className="form-label-sm">Memory Danger %</Label>
                    <Input
                      bsSize="sm"
                      type="number"
                      value={formData.memoryDangerThreshold}
                      onChange={e => setFormData({ ...formData, memoryDangerThreshold: +e.target.value })}
                    />
                  </FormGroup>
                </div>
              </div>
              <div className="row">
                <div className="col-md-6">
                  <FormGroup className="mb-2">
                    <Label className="form-label-sm">Disk Warning %</Label>
                    <Input
                      bsSize="sm"
                      type="number"
                      value={formData.diskWarningThreshold}
                      onChange={e => setFormData({ ...formData, diskWarningThreshold: +e.target.value })}
                    />
                  </FormGroup>
                </div>
                <div className="col-md-6">
                  <FormGroup className="mb-2">
                    <Label className="form-label-sm">Disk Danger %</Label>
                    <Input
                      bsSize="sm"
                      type="number"
                      value={formData.diskDangerThreshold}
                      onChange={e => setFormData({ ...formData, diskDangerThreshold: +e.target.value })}
                    />
                  </FormGroup>
                </div>
              </div>
            </>
          )}
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

export default InstanceEditModal;
