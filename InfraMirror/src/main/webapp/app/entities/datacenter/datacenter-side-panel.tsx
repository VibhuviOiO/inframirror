import React, { useEffect, useState, useCallback } from 'react';
import { Button, Form, FormGroup, Label, Input, FormFeedback } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTimes, faSave } from '@fortawesome/free-solid-svg-icons';
import { toast } from 'react-toastify';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getRegions } from 'app/entities/region/region.reducer';
import { createEntity, updateEntity } from './datacenter.reducer';

interface DatacenterSidePanelProps {
  isOpen: boolean;
  onClose: () => void;
  datacenter: any;
  onSuccess: () => void;
}

const DatacenterSidePanel: React.FC<DatacenterSidePanelProps> = ({ isOpen, onClose, datacenter, onSuccess }) => {
  const dispatch = useAppDispatch();
  const regions = useAppSelector(state => state.region.entities);
  const [formData, setFormData] = useState({ name: '', code: '', regionId: '' });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    dispatch(getRegions({}));
  }, []);

  useEffect(() => {
    if (datacenter) {
      setFormData({
        name: datacenter.name || '',
        code: datacenter.code || '',
        regionId: datacenter.region?.id || '',
      });
    } else {
      setFormData({ name: '', code: '', regionId: '' });
    }
    setErrors({});
  }, [datacenter, isOpen]);

  useEffect(() => {
    const handleEscape = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && isOpen) {
        onClose();
      }
    };
    document.addEventListener('keydown', handleEscape);
    return () => document.removeEventListener('keydown', handleEscape);
  }, [isOpen, onClose]);

  const handleChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    setErrors(prev => ({ ...prev, [name]: '' }));
  }, []);

  const validate = useCallback(() => {
    const newErrors: Record<string, string> = {};
    if (!formData.name?.trim()) newErrors.name = 'Name is required';
    if (!formData.code?.trim()) newErrors.code = 'Code is required';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  }, [formData.name, formData.code]);

  const handleSubmit = useCallback(
    async (e: React.FormEvent) => {
      e.preventDefault();
      if (!validate()) return;

      setLoading(true);
      try {
        const entity = {
          ...(datacenter || {}),
          name: formData.name,
          code: formData.code,
          region: formData.regionId ? { id: formData.regionId } : null,
        };
        const action = datacenter?.id ? updateEntity(entity) : createEntity(entity);
        await dispatch(action).unwrap();
        toast.success(datacenter?.id ? 'Datacenter updated successfully' : 'Datacenter created successfully');
        onClose();
        onSuccess();
      } catch (error) {
        toast.error(error.message || 'Operation failed');
      } finally {
        setLoading(false);
      }
    },
    [dispatch, formData, datacenter, validate, onClose, onSuccess],
  );

  if (!isOpen) return null;

  return (
    <>
      <div className="side-panel-overlay" onClick={onClose} />
      <div className={`side-panel ${isOpen ? 'open' : ''}`}>
        <div className="side-panel-header">
          <h5 className="mb-0">{datacenter ? 'Edit Datacenter' : 'Create Datacenter'}</h5>
          <Button color="link" className="btn-close" onClick={onClose} aria-label="Close" />
        </div>
        <div className="side-panel-body">
          <Form onSubmit={handleSubmit}>
            <FormGroup>
              <Label for="name">
                Name <span className="text-danger">*</span>
              </Label>
              <Input type="text" name="name" id="name" value={formData.name} onChange={handleChange} invalid={!!errors.name} />
              {errors.name && <FormFeedback>{errors.name}</FormFeedback>}
            </FormGroup>

            <FormGroup>
              <Label for="code">
                Code <span className="text-danger">*</span>
              </Label>
              <Input type="text" name="code" id="code" value={formData.code} onChange={handleChange} invalid={!!errors.code} />
              {errors.code && <FormFeedback>{errors.code}</FormFeedback>}
            </FormGroup>

            <FormGroup>
              <Label for="regionId">Region</Label>
              <Input type="select" name="regionId" id="regionId" value={formData.regionId} onChange={handleChange}>
                <option value="">Select a region</option>
                {regions.map(region => (
                  <option key={region.id} value={region.id}>
                    {region.name}
                  </option>
                ))}
              </Input>
            </FormGroup>
          </Form>
        </div>
        <div className="side-panel-footer">
          <Button color="secondary" onClick={onClose} disabled={loading}>
            <FontAwesomeIcon icon={faTimes} className="me-1" />
            Cancel
          </Button>
          <Button color="primary" onClick={handleSubmit} disabled={loading} data-cy="entityCreateSaveButton">
            <FontAwesomeIcon icon={faSave} className="me-1" />
            {loading ? 'Saving...' : 'Save'}
          </Button>
        </div>
      </div>
    </>
  );
};

export default DatacenterSidePanel;
