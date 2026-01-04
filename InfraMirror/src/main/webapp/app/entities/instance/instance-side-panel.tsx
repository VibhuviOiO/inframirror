import React, { useEffect, useState, useCallback } from 'react';
import { Button, Form, FormGroup, Label, Input, FormFeedback } from 'reactstrap';
import { Translate, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { toast } from 'react-toastify';
import { IInstance } from 'app/shared/model/instance.model';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { createEntity, updateEntity } from './instance.reducer';
import { getEntities as getDatacenters } from 'app/entities/datacenter/datacenter.reducer';

interface InstanceSidePanelProps {
  isOpen: boolean;
  onClose: () => void;
  instance: IInstance | null;
  onSuccess: () => void;
}

const InstanceSidePanel: React.FC<InstanceSidePanelProps> = ({ isOpen, onClose, instance, onSuccess }) => {
  const dispatch = useAppDispatch();
  const datacenters = useAppSelector(state => state.datacenter.entities);
  const [formData, setFormData] = useState<IInstance>({
    name: '',
    hostname: '',
    instanceType: 'VM',
    privateIpAddress: '',
    publicIpAddress: '',
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    dispatch(getDatacenters({}));
  }, [dispatch]);

  useEffect(() => {
    if (instance) {
      setFormData(instance);
    } else {
      setFormData({
        name: '',
        hostname: '',
        instanceType: 'VM',
        privateIpAddress: '',
        publicIpAddress: '',
      });
    }
    setErrors({});
  }, [instance, isOpen]);

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
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({ ...prev, [name]: type === 'checkbox' ? checked : value }));
    setErrors(prev => ({ ...prev, [name]: '' }));
  }, []);

  const handleSelectChange = useCallback(
    (e: React.ChangeEvent<HTMLInputElement>) => {
      const { name, value } = e.target;
      if (name === 'datacenter') {
        const datacenterId = value ? parseInt(value, 10) : null;
        const datacenter = datacenters.find(d => d.id === datacenterId);
        setFormData(prev => ({ ...prev, datacenter }));
      } else {
        setFormData(prev => ({ ...prev, [name]: value }));
      }
      setErrors(prev => ({ ...prev, [name]: '' }));
    },
    [datacenters],
  );

  const validate = useCallback(() => {
    const newErrors: Record<string, string> = {};
    if (!formData.name?.trim()) newErrors.name = 'Name is required';
    if (!formData.hostname?.trim()) newErrors.hostname = 'Hostname is required';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  }, [formData.name, formData.hostname]);

  const handleSubmit = useCallback(
    async (e: React.FormEvent) => {
      e.preventDefault();
      if (!validate()) return;

      setLoading(true);
      try {
        const action = instance?.id ? updateEntity(formData) : createEntity(formData);
        await dispatch(action).unwrap();
        toast.success(instance?.id ? 'Instance updated successfully' : 'Instance created successfully');
        onClose();
        onSuccess();
      } catch (error) {
        toast.error(error.message || 'Operation failed');
      } finally {
        setLoading(false);
      }
    },
    [dispatch, formData, instance, validate, onClose, onSuccess],
  );

  if (!isOpen) return null;

  return (
    <>
      <div className="side-panel-overlay" onClick={onClose} />
      <div className={`side-panel ${isOpen ? 'open' : ''}`}>
        <div className="side-panel-header">
          <h5>
            {instance ? (
              <Translate contentKey="infraMirrorApp.instance.home.editLabel">Edit Instance</Translate>
            ) : (
              <Translate contentKey="infraMirrorApp.instance.home.createLabel">Create Instance</Translate>
            )}
          </h5>
          <Button close onClick={onClose} />
        </div>
        <div className="side-panel-body">
          <Form onSubmit={handleSubmit}>
            <FormGroup>
              <Label for="name">
                <Translate contentKey="infraMirrorApp.instance.name">Name</Translate> <span className="text-danger">*</span>
              </Label>
              <Input type="text" name="name" id="name" value={formData.name} onChange={handleChange} invalid={!!errors.name} />
              {errors.name && <FormFeedback>{errors.name}</FormFeedback>}
            </FormGroup>

            <FormGroup>
              <Label for="hostname">
                <Translate contentKey="infraMirrorApp.instance.hostname">Hostname</Translate> <span className="text-danger">*</span>
              </Label>
              <Input
                type="text"
                name="hostname"
                id="hostname"
                value={formData.hostname}
                onChange={handleChange}
                invalid={!!errors.hostname}
              />
              {errors.hostname && <FormFeedback>{errors.hostname}</FormFeedback>}
            </FormGroup>

            <FormGroup>
              <Label for="instanceType">
                <Translate contentKey="infraMirrorApp.instance.instanceType">Instance Type</Translate>
              </Label>
              <Input type="select" name="instanceType" id="instanceType" value={formData.instanceType} onChange={handleSelectChange}>
                <option value="VM">VM</option>
                <option value="BARE_METAL">BARE_METAL</option>
                <option value="CONTAINER">CONTAINER</option>
              </Input>
            </FormGroup>

            <FormGroup>
              <Label for="datacenter">
                <Translate contentKey="infraMirrorApp.instance.datacenter">Datacenter</Translate>
              </Label>
              <Input type="select" name="datacenter" id="datacenter" value={formData.datacenter?.id || ''} onChange={handleSelectChange}>
                <option value="">None</option>
                {datacenters.map(dc => (
                  <option key={dc.id} value={dc.id}>
                    {dc.name}
                  </option>
                ))}
              </Input>
            </FormGroup>

            <FormGroup>
              <Label for="privateIpAddress">Private IP Address</Label>
              <Input
                type="text"
                name="privateIpAddress"
                id="privateIpAddress"
                value={formData.privateIpAddress || ''}
                onChange={handleChange}
                placeholder="192.168.1.100"
              />
            </FormGroup>

            <FormGroup>
              <Label for="publicIpAddress">Public IP Address</Label>
              <Input
                type="text"
                name="publicIpAddress"
                id="publicIpAddress"
                value={formData.publicIpAddress || ''}
                onChange={handleChange}
                placeholder="203.0.113.1"
              />
            </FormGroup>
          </Form>
        </div>
        <div className="side-panel-footer">
          <Button color="secondary" onClick={onClose} disabled={loading}>
            <Translate contentKey="entity.action.cancel">Cancel</Translate>
          </Button>
          <Button color="primary" onClick={handleSubmit} disabled={loading} data-cy="entityCreateSaveButton">
            {loading ? <FontAwesomeIcon icon="spinner" spin /> : <FontAwesomeIcon icon="save" />}{' '}
            <Translate contentKey="entity.action.save">Save</Translate>
          </Button>
        </div>
      </div>
    </>
  );
};

export default InstanceSidePanel;
