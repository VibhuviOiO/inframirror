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
    status: 'ACTIVE',
  });
  const [saving, setSaving] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (agent) {
      setFormData({
        name: agent.name || '',
        status: agent.status || 'ACTIVE',
      });
    } else {
      setFormData({
        name: '',
        status: 'ACTIVE',
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
      if (agent?.id) {
        await axios.put(`/api/agents/${agent.id}`, { ...formData, id: agent.id });
        toast.success('Agent updated successfully');
      } else {
        await axios.post('/api/agents', formData);
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
              placeholder="agent aws us east 1"
            />
            <FormFeedback>{errors.name}</FormFeedback>
          </FormGroup>

          <FormGroup className="mb-3">
            <Label for="status">Status</Label>
            <Input type="select" id="status" value={formData.status} onChange={e => setFormData({ ...formData, status: e.target.value })}>
              <option value="ACTIVE">Active</option>
              <option value="INACTIVE">Inactive</option>
              <option value="OFFLINE">Offline</option>
            </Input>
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
