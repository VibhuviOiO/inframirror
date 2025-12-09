import React, { useState, useEffect } from 'react';
import { Button, Form, FormGroup, Label, Input, FormFeedback } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSave, faTimes } from '@fortawesome/free-solid-svg-icons';
import { IRegion } from 'app/shared/model/region.model';
import axios from 'axios';
import { toast } from 'react-toastify';

interface RegionEditModalProps {
  isOpen: boolean;
  toggle: () => void;
  region?: IRegion | null;
  onSave?: () => void;
}

export const RegionEditModal: React.FC<RegionEditModalProps> = ({ isOpen, toggle, region, onSave }) => {
  if (!isOpen) return null;

  const [formData, setFormData] = useState<IRegion>({
    name: '',
    regionCode: '',
    groupName: '',
  });
  const [saving, setSaving] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (region) {
      setFormData(region);
    } else {
      setFormData({
        name: '',
        regionCode: '',
        groupName: '',
      });
    }
    setErrors({});
  }, [region, isOpen]);

  const validate = () => {
    const newErrors: Record<string, string> = {};
    if (!formData.name?.trim()) newErrors.name = 'Name is required';
    if (!formData.regionCode?.trim()) newErrors.regionCode = 'Region code is required';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    setSaving(true);
    try {
      if (region?.id) {
        await axios.put(`/api/regions/${region.id}`, formData);
        toast.success('Region updated successfully');
      } else {
        await axios.post('/api/regions', formData);
        toast.success('Region created successfully');
      }
      onSave?.();
      toggle();
    } catch (error) {
      toast.error('Failed to save region');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="card">
      <Form onSubmit={handleSubmit}>
        <div className="card-header d-flex justify-content-between align-items-center">
          <h6 className="mb-0">{region?.id ? 'Edit Region' : 'Create Region'}</h6>
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
            <Label for="regionCode">
              Region Code <span className="text-danger">*</span>
            </Label>
            <Input
              type="text"
              id="regionCode"
              value={formData.regionCode}
              onChange={e => setFormData({ ...formData, regionCode: e.target.value })}
              invalid={!!errors.regionCode}
            />
            <FormFeedback>{errors.regionCode}</FormFeedback>
          </FormGroup>

          <FormGroup className="mb-3">
            <Label for="groupName">Group Name</Label>
            <Input
              type="text"
              id="groupName"
              value={formData.groupName || ''}
              onChange={e => setFormData({ ...formData, groupName: e.target.value })}
            />
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

export default RegionEditModal;
