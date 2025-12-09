import React, { useState, useEffect } from 'react';
import { Button, Form, FormGroup, Label, Input, FormFeedback } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSave, faTimes, faPlus, faTrash } from '@fortawesome/free-solid-svg-icons';
import { IMonitoredService } from 'app/shared/model/monitored-service.model';
import axios from 'axios';
import { toast } from 'react-toastify';

interface MonitoredServiceEditModalProps {
  isOpen: boolean;
  toggle: () => void;
  service?: IMonitoredService | null;
  onSave?: () => void;
}

const SERVICE_TYPES = ['TCP', 'CASSANDRA', 'MONGODB', 'REDIS', 'KAFKA', 'POSTGRESQL', 'MYSQL', 'ELASTICSEARCH', 'RABBITMQ', 'CUSTOM'];
const ENVIRONMENTS = ['DEV', 'QA', 'STAGE', 'PROD', 'DMZ', 'DR'];

export const MonitoredServiceEditModal: React.FC<MonitoredServiceEditModalProps> = ({ isOpen, toggle, service, onSave }) => {
  if (!isOpen) return null;
  const [formData, setFormData] = useState<IMonitoredService>({
    name: '',
    serviceType: 'TCP',
    environment: 'PROD',
    monitoringEnabled: true,
    clusterMonitoringEnabled: false,
    intervalSeconds: 30,
    timeoutMs: 2000,
    retryCount: 2,
    latencyWarningMs: 200,
    latencyCriticalMs: 600,
    isActive: true,
  });
  const [saving, setSaving] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [datacenters, setDatacenters] = useState([]);

  useEffect(() => {
    axios.get('/api/datacenters').then(res => setDatacenters(res.data));
  }, []);

  useEffect(() => {
    if (service) {
      setFormData({
        ...service,
        advancedConfig: service.advancedConfig || '',
      });
    } else {
      setFormData({
        name: '',
        serviceType: 'TCP',
        environment: 'PROD',
        monitoringEnabled: true,
        clusterMonitoringEnabled: false,
        intervalSeconds: 30,
        timeoutMs: 2000,
        retryCount: 2,
        latencyWarningMs: 200,
        latencyCriticalMs: 600,
        isActive: true,
      });
    }
    setErrors({});
  }, [service, isOpen]);

  const validate = () => {
    const newErrors: Record<string, string> = {};
    if (!formData.name?.trim()) newErrors.name = 'Name is required';
    if (!formData.serviceType) newErrors.serviceType = 'Service type is required';
    if (!formData.intervalSeconds || formData.intervalSeconds < 1) newErrors.intervalSeconds = 'Interval must be at least 1 second';
    if (!formData.timeoutMs || formData.timeoutMs < 100) newErrors.timeoutMs = 'Timeout must be at least 100ms';
    if (formData.advancedConfig) {
      try {
        JSON.parse(formData.advancedConfig);
      } catch {
        newErrors.advancedConfig = 'Invalid JSON format';
      }
    }
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
        advancedConfig: formData.advancedConfig ? JSON.parse(formData.advancedConfig) : null,
      };
      let savedService;
      if (service?.id) {
        const res = await axios.put(`/api/monitored-services/${service.id}`, payload);
        savedService = res.data;
        toast.success('Service updated successfully');
      } else {
        const res = await axios.post('/api/monitored-services', payload);
        savedService = res.data;
        toast.success('Service created successfully');
      }
      onSave?.();
      toggle();
    } catch (error) {
      toast.error('Failed to save service');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="card">
      <Form onSubmit={handleSubmit}>
        <div className="card-header d-flex justify-content-between align-items-center">
          <h6 className="mb-0">{service?.id ? 'Edit Service' : 'Create Service'}</h6>
          <Button close onClick={toggle} />
        </div>
        <div className="card-body" style={{ maxHeight: 'calc(100vh - 250px)', overflowY: 'auto' }}>
          <div className="row">
            <div className="col-md-6">
              <FormGroup className="mb-2">
                <Label for="datacenter" className="form-label-sm">
                  Datacenter
                </Label>
                <Input
                  bsSize="sm"
                  type="select"
                  id="datacenter"
                  value={formData.datacenter?.id || ''}
                  onChange={e => setFormData({ ...formData, datacenter: datacenters.find(d => d.id === +e.target.value) })}
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
            <div className="col-md-6">
              <FormGroup className="mb-2">
                <Label for="name" className="form-label-sm">
                  Name <span className="text-danger">*</span>
                </Label>
                <Input
                  bsSize="sm"
                  type="text"
                  id="name"
                  value={formData.name}
                  onChange={e => setFormData({ ...formData, name: e.target.value })}
                  invalid={!!errors.name}
                />
                <FormFeedback>{errors.name}</FormFeedback>
              </FormGroup>
            </div>
          </div>

          <FormGroup className="mb-2">
            <Label for="description" className="form-label-sm">
              Description
            </Label>
            <Input
              bsSize="sm"
              type="textarea"
              id="description"
              rows={2}
              value={formData.description || ''}
              onChange={e => setFormData({ ...formData, description: e.target.value })}
            />
          </FormGroup>

          <div className="row">
            <div className="col-md-6">
              <FormGroup className="mb-2">
                <Label for="serviceType" className="form-label-sm">
                  Service Type <span className="text-danger">*</span>
                </Label>
                <Input
                  bsSize="sm"
                  type="select"
                  id="serviceType"
                  value={formData.serviceType}
                  onChange={e => setFormData({ ...formData, serviceType: e.target.value })}
                  invalid={!!errors.serviceType}
                >
                  {SERVICE_TYPES.map(type => (
                    <option key={type} value={type}>
                      {type}
                    </option>
                  ))}
                </Input>
                <FormFeedback>{errors.serviceType}</FormFeedback>
              </FormGroup>
            </div>
            <div className="col-md-6">
              <FormGroup className="mb-2">
                <Label for="environment" className="form-label-sm">
                  Environment
                </Label>
                <Input
                  bsSize="sm"
                  type="select"
                  id="environment"
                  value={formData.environment}
                  onChange={e => setFormData({ ...formData, environment: e.target.value })}
                >
                  {ENVIRONMENTS.map(env => (
                    <option key={env} value={env}>
                      {env}
                    </option>
                  ))}
                </Input>
              </FormGroup>
            </div>
          </div>

          <div className="row">
            <div className="col-md-6">
              <FormGroup className="mb-2">
                <Label for="intervalSeconds" className="form-label-sm">
                  Interval (seconds) <span className="text-danger">*</span>
                </Label>
                <Input
                  bsSize="sm"
                  type="number"
                  id="intervalSeconds"
                  value={formData.intervalSeconds}
                  onChange={e => setFormData({ ...formData, intervalSeconds: +e.target.value })}
                  invalid={!!errors.intervalSeconds}
                />
                <FormFeedback>{errors.intervalSeconds}</FormFeedback>
              </FormGroup>
            </div>
            <div className="col-md-6">
              <FormGroup className="mb-2">
                <Label for="timeoutMs" className="form-label-sm">
                  Timeout (ms) <span className="text-danger">*</span>
                </Label>
                <Input
                  bsSize="sm"
                  type="number"
                  id="timeoutMs"
                  value={formData.timeoutMs}
                  onChange={e => setFormData({ ...formData, timeoutMs: +e.target.value })}
                  invalid={!!errors.timeoutMs}
                />
                <FormFeedback>{errors.timeoutMs}</FormFeedback>
              </FormGroup>
            </div>
          </div>

          <div className="row">
            <div className="col-md-4">
              <FormGroup className="mb-2">
                <Label for="retryCount" className="form-label-sm">
                  Retry Count
                </Label>
                <Input
                  bsSize="sm"
                  type="number"
                  id="retryCount"
                  value={formData.retryCount}
                  onChange={e => setFormData({ ...formData, retryCount: +e.target.value })}
                />
              </FormGroup>
            </div>
            <div className="col-md-4">
              <FormGroup className="mb-2">
                <Label for="latencyWarningMs" className="form-label-sm">
                  Warning (ms)
                </Label>
                <Input
                  bsSize="sm"
                  type="number"
                  id="latencyWarningMs"
                  value={formData.latencyWarningMs || ''}
                  onChange={e => setFormData({ ...formData, latencyWarningMs: e.target.value ? +e.target.value : null })}
                />
              </FormGroup>
            </div>
            <div className="col-md-4">
              <FormGroup className="mb-2">
                <Label for="latencyCriticalMs" className="form-label-sm">
                  Critical (ms)
                </Label>
                <Input
                  bsSize="sm"
                  type="number"
                  id="latencyCriticalMs"
                  value={formData.latencyCriticalMs || ''}
                  onChange={e => setFormData({ ...formData, latencyCriticalMs: e.target.value ? +e.target.value : null })}
                />
              </FormGroup>
            </div>
          </div>

          <FormGroup className="mb-2">
            <Label for="advancedConfig" className="form-label-sm">
              Advanced Configuration (JSON)
            </Label>
            <Input
              bsSize="sm"
              type="textarea"
              id="advancedConfig"
              rows={3}
              value={formData.advancedConfig || ''}
              onChange={e => setFormData({ ...formData, advancedConfig: e.target.value })}
              invalid={!!errors.advancedConfig}
              placeholder='{"key": "value"}'
            />
            <FormFeedback>{errors.advancedConfig}</FormFeedback>
          </FormGroup>

          <div className="row mb-2">
            <div className="col-md-6">
              <FormGroup check className="form-check-sm">
                <Input
                  type="checkbox"
                  checked={formData.monitoringEnabled}
                  onChange={e => setFormData({ ...formData, monitoringEnabled: e.target.checked })}
                />
                <Label check className="form-label-sm">
                  Monitoring Enabled
                </Label>
              </FormGroup>
            </div>
            <div className="col-md-6">
              <FormGroup check className="form-check-sm">
                <Input
                  type="checkbox"
                  checked={formData.clusterMonitoringEnabled}
                  onChange={e => setFormData({ ...formData, clusterMonitoringEnabled: e.target.checked })}
                />
                <Label check className="form-label-sm">
                  Cluster Monitoring
                </Label>
              </FormGroup>
            </div>
          </div>
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

export default MonitoredServiceEditModal;
