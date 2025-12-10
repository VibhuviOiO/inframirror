import React, { useState, useEffect } from 'react';
import { Button, Form, FormGroup, Label, Input, FormFeedback } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSave, faTimes } from '@fortawesome/free-solid-svg-icons';
import axios from 'axios';
import { toast } from 'react-toastify';

interface ApiKeyCreateModalProps {
  isOpen: boolean;
  toggle: () => void;
  onSuccess: (apiKey: any) => void;
}

export const ApiKeyCreateModal: React.FC<ApiKeyCreateModalProps> = ({ isOpen, toggle, onSuccess }) => {
  if (!isOpen) return null;

  const [formData, setFormData] = useState({ name: '', description: '', expiresAt: '' });
  const [saving, setSaving] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (isOpen) {
      setFormData({ name: '', description: '', expiresAt: '' });
      setErrors({});
    }
  }, [isOpen]);

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
      const payload = {
        name: formData.name,
        description: formData.description,
        expiresAt: formData.expiresAt ? new Date(formData.expiresAt).toISOString() : null,
      };
      const response = await axios.post('/api/api-keys', payload);
      toast.success('API Key created successfully');
      onSuccess(response.data);
      toggle();
    } catch (error) {
      toast.error('Failed to create API key');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="card">
      <Form onSubmit={handleSubmit}>
        <div className="card-header d-flex justify-content-between align-items-center">
          <h6 className="mb-0">Create API Key</h6>
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
            <Label for="description">Description</Label>
            <Input
              type="text"
              id="description"
              value={formData.description}
              onChange={e => setFormData({ ...formData, description: e.target.value })}
            />
          </FormGroup>

          <FormGroup className="mb-3">
            <Label for="expiresAt">Expires At (Optional)</Label>
            <Input
              type="datetime-local"
              id="expiresAt"
              value={formData.expiresAt}
              onChange={e => setFormData({ ...formData, expiresAt: e.target.value })}
            />
          </FormGroup>
        </div>
        <div className="card-footer d-flex justify-content-end gap-2 py-2">
          <Button color="secondary" size="sm" onClick={toggle} disabled={saving}>
            <FontAwesomeIcon icon={faTimes} /> Cancel
          </Button>
          <Button color="primary" size="sm" type="submit" disabled={saving}>
            <FontAwesomeIcon icon={faSave} /> {saving ? 'Creating...' : 'Create'}
          </Button>
        </div>
      </Form>
    </div>
  );
};

export default ApiKeyCreateModal;
