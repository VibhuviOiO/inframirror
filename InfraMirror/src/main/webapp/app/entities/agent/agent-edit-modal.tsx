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
    regionId: null,
  });
  const [saving, setSaving] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [regions, setRegions] = useState([]);

  useEffect(() => {
    if (isOpen) {
      axios.get('/api/regions?size=1000').then(res => setRegions(res.data));
    }
  }, [isOpen]);

  useEffect(() => {
    if (agent) {
      setFormData({
        name: agent.name || '',
        regionId: agent.region?.id || null,
      });
    } else {
      setFormData({
        name: '',
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
        region: formData.regionId ? { id: formData.regionId } : null,
      };
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
            />
            <FormFeedback>{errors.name}</FormFeedback>
          </FormGroup>

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
