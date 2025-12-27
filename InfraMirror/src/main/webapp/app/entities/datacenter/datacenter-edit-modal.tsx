import React, { useState, useEffect } from 'react';
import { Button, Form, FormGroup, Label, Input, FormFeedback } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSave, faTimes } from '@fortawesome/free-solid-svg-icons';
import { IDatacenter } from 'app/shared/model/datacenter.model';
import axios from 'axios';
import { toast } from 'react-toastify';

interface DatacenterEditModalProps {
  isOpen: boolean;
  toggle: () => void;
  datacenter?: IDatacenter | null;
  onSave?: () => void;
}

export const DatacenterEditModal: React.FC<DatacenterEditModalProps> = ({ isOpen, toggle, datacenter, onSave }) => {
  if (!isOpen) return null;

  const [formData, setFormData] = useState<IDatacenter>({
    code: '',
    name: '',
    region: null,
  });
  const [regions, setRegions] = useState([]);
  const [saving, setSaving] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (isOpen) {
      axios.get('/api/regions?size=1000').then(res => setRegions(res.data));
    }
  }, [isOpen]);

  useEffect(() => {
    if (datacenter) {
      setFormData(datacenter);
    } else {
      setFormData({
        code: '',
        name: '',
        region: null,
      });
    }
    setErrors({});
  }, [datacenter, isOpen]);

  const validate = () => {
    const newErrors: Record<string, string> = {};
    if (!formData.code?.trim()) newErrors.code = 'Code is required';
    if (!formData.name?.trim()) newErrors.name = 'Name is required';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    setSaving(true);
    try {
      if (datacenter?.id) {
        await axios.put(`/api/datacenters/${datacenter.id}`, formData);
        toast.success('Datacenter updated successfully');
      } else {
        await axios.post('/api/datacenters', formData);
        toast.success('Datacenter created successfully');
      }
      onSave?.();
      toggle();
    } catch (error) {
      if (axios.isAxiosError(error) && error.response?.data?.fieldErrors) {
        const fieldErrors = error.response.data.fieldErrors;
        const errorMessages = fieldErrors.map(fe => `${fe.field}: ${fe.message}`).join(', ');
        toast.error(`Validation failed: ${errorMessages}`);
      } else if (axios.isAxiosError(error) && error.response?.data?.detail) {
        toast.error(error.response.data.detail);
      } else {
        toast.error('Failed to save datacenter');
      }
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="card">
      <Form onSubmit={handleSubmit}>
        <div className="card-header d-flex justify-content-between align-items-center">
          <h6 className="mb-0">{datacenter?.id ? 'Edit Datacenter' : 'Create Datacenter'}</h6>
          <Button close onClick={toggle} />
        </div>
        <div className="card-body" style={{ maxHeight: 'calc(100vh - 250px)', overflowY: 'auto' }}>
          <FormGroup className="mb-3">
            <Label for="code">
              Code <span className="text-danger">*</span>
            </Label>
            <Input
              type="text"
              id="code"
              value={formData.code}
              onChange={e => setFormData({ ...formData, code: e.target.value })}
              invalid={!!errors.code}
            />
            <FormFeedback>{errors.code}</FormFeedback>
          </FormGroup>

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
            <Label for="region">Region</Label>
            <Input
              type="select"
              id="region"
              value={formData.region?.id || ''}
              onChange={e => {
                const regionId = e.target.value;
                const selectedRegion = regions.find(r => r.id === Number(regionId));
                setFormData({ ...formData, region: selectedRegion || null });
              }}
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

export default DatacenterEditModal;
