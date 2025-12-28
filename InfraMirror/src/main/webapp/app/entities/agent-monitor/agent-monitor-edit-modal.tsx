import React, { useState, useEffect } from 'react';
import { Button, Form, FormGroup, Label, Input, FormFeedback } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSave, faTimes } from '@fortawesome/free-solid-svg-icons';
import { IAgentMonitor } from 'app/shared/model/agent-monitor.model';
import axios from 'axios';
import { toast } from 'react-toastify';

interface AgentMonitorEditModalProps {
  isOpen: boolean;
  toggle: () => void;
  agentMonitor?: IAgentMonitor | null;
  onSave?: () => void;
}

export const AgentMonitorEditModal: React.FC<AgentMonitorEditModalProps> = ({ isOpen, toggle, agentMonitor, onSave }) => {
  if (!isOpen) return null;

  const [formData, setFormData] = useState<any>({
    agentId: '',
    monitorType: 'HTTP',
    monitorId: '',
    active: true,
  });
  const [saving, setSaving] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [agents, setAgents] = useState<any[]>([]);
  const [httpMonitors, setHttpMonitors] = useState<any[]>([]);
  const [instances, setInstances] = useState<any[]>([]);
  const [monitoredServices, setMonitoredServices] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (isOpen) {
      loadData();
    }
  }, [isOpen]);

  useEffect(() => {
    if (agentMonitor) {
      setFormData({
        agentId: agentMonitor.agent?.id || '',
        monitorType: agentMonitor.monitorType || 'HTTP',
        monitorId: agentMonitor.monitorId || '',
        active: agentMonitor.active ?? true,
      });
    } else {
      setFormData({
        agentId: '',
        monitorType: 'HTTP',
        monitorId: '',
        active: true,
      });
    }
    setErrors({});
  }, [agentMonitor, isOpen]);

  const loadData = async () => {
    setLoading(true);
    try {
      const [agentsRes, httpMonitorsRes, instancesRes, servicesRes] = await Promise.all([
        axios.get('/api/agents?size=1000'),
        axios.get('/api/http-monitors?size=1000'),
        axios.get('/api/instances?size=1000'),
        axios.get('/api/monitored-services?size=1000'),
      ]);
      setAgents(agentsRes.data);
      setHttpMonitors(httpMonitorsRes.data);
      setInstances(instancesRes.data);
      setMonitoredServices(servicesRes.data);
    } catch (error) {
      toast.error('Failed to load data');
    } finally {
      setLoading(false);
    }
  };

  const validate = () => {
    const newErrors: Record<string, string> = {};
    if (!formData.agentId) newErrors.agentId = 'Agent is required';
    if (!formData.monitorId) newErrors.monitorId = 'Monitor is required';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    setSaving(true);
    try {
      const payload: any = {
        active: formData.active,
        monitorType: formData.monitorType,
        monitorId: formData.monitorId,
        agent: { id: formData.agentId },
      };

      if (agentMonitor?.id) {
        await axios.put(`/api/agent-monitors/${agentMonitor.id}`, { ...payload, id: agentMonitor.id });
        toast.success('Agent monitor updated successfully');
      } else {
        await axios.post('/api/agent-monitors', payload);
        toast.success('Agent monitor created successfully');
      }
      onSave?.();
      toggle();
    } catch (error) {
      toast.error('Failed to save agent monitor');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="card">
      <Form onSubmit={handleSubmit}>
        <div className="card-header d-flex justify-content-between align-items-center">
          <h6 className="mb-0">{agentMonitor?.id ? 'Edit Agent Monitor' : 'Assign Monitor to Agent'}</h6>
          <Button close onClick={toggle} />
        </div>
        <div className="card-body" style={{ maxHeight: 'calc(100vh - 250px)', overflowY: 'auto' }}>
          {loading ? (
            <div className="text-center py-4">
              <FontAwesomeIcon icon="spinner" spin size="2x" />
            </div>
          ) : (
            <>
              <FormGroup className="mb-3">
                <Label for="agentId">
                  Agent <span className="text-danger">*</span>
                </Label>
                <Input
                  type="select"
                  id="agentId"
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

              <FormGroup className="mb-3">
                <Label for="monitorType">Monitor Type</Label>
                <Input
                  type="select"
                  id="monitorType"
                  value={formData.monitorType}
                  onChange={e => setFormData({ ...formData, monitorType: e.target.value, monitorId: '' })}
                >
                  <option value="HTTP">HTTP Monitor</option>
                  <option value="INSTANCE">Instance</option>
                  <option value="SERVICE">Monitored Service</option>
                </Input>
              </FormGroup>

              <FormGroup className="mb-3">
                <Label for="monitorId">
                  {formData.monitorType === 'HTTP' && 'HTTP Monitor'}
                  {formData.monitorType === 'INSTANCE' && 'Instance'}
                  {formData.monitorType === 'SERVICE' && 'Monitored Service'} <span className="text-danger">*</span>
                </Label>
                <Input
                  type="select"
                  id="monitorId"
                  value={formData.monitorId}
                  onChange={e => setFormData({ ...formData, monitorId: e.target.value })}
                  invalid={!!errors.monitorId}
                >
                  <option value="">Select {formData.monitorType === 'HTTP' ? 'Monitor' : formData.monitorType}</option>
                  {formData.monitorType === 'HTTP' &&
                    httpMonitors.map(monitor => (
                      <option key={monitor.id} value={monitor.id}>
                        {monitor.name || monitor.url}
                      </option>
                    ))}
                  {formData.monitorType === 'INSTANCE' &&
                    instances.map(instance => (
                      <option key={instance.id} value={instance.id}>
                        {instance.name || instance.hostname}
                      </option>
                    ))}
                  {formData.monitorType === 'SERVICE' &&
                    monitoredServices.map(service => (
                      <option key={service.id} value={service.id}>
                        {service.name}
                      </option>
                    ))}
                </Input>
                <FormFeedback>{errors.monitorId}</FormFeedback>
              </FormGroup>

              <FormGroup className="mb-3">
                <div className="form-check">
                  <Input
                    type="checkbox"
                    id="active"
                    checked={formData.active}
                    onChange={e => setFormData({ ...formData, active: e.target.checked })}
                    className="form-check-input"
                  />
                  <Label for="active" className="form-check-label">
                    Active
                  </Label>
                </div>
              </FormGroup>
            </>
          )}
        </div>
        <div className="card-footer d-flex justify-content-end gap-2 py-2">
          <Button color="secondary" size="sm" onClick={toggle} disabled={saving}>
            <FontAwesomeIcon icon={faTimes} /> Cancel
          </Button>
          <Button color="primary" size="sm" type="submit" disabled={saving || loading}>
            <FontAwesomeIcon icon={faSave} /> {saving ? 'Saving...' : 'Save'}
          </Button>
        </div>
      </Form>
    </div>
  );
};

export default AgentMonitorEditModal;
