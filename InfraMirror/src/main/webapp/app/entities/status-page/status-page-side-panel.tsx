import React, { useEffect, useState } from 'react';
import { Button, Form, FormGroup, Label, Input, FormFeedback } from 'reactstrap';
import { Translate, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import axios from 'axios';
import { toast } from 'react-toastify';
import './status-page-side-panel.scss';

interface StatusPageSidePanelProps {
  isOpen: boolean;
  onClose: () => void;
  statusPage: any;
  onSuccess: () => void;
}

const StatusPageSidePanel: React.FC<StatusPageSidePanelProps> = ({ isOpen, onClose, statusPage, onSuccess }) => {
  const [formData, setFormData] = useState({
    name: '',
    slug: '',
    description: '',
    isPublic: false,
    isActive: true,
    isHomePage: false,
    allowedRoles: '',
  });
  const [errors, setErrors] = useState<any>({});
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (statusPage) {
      setFormData({
        name: statusPage.name || '',
        slug: statusPage.slug || '',
        description: statusPage.description || '',
        isPublic: statusPage.isPublic || false,
        isActive: statusPage.isActive !== undefined ? statusPage.isActive : true,
        isHomePage: statusPage.isHomePage || false,
        allowedRoles: statusPage.allowedRoles || '',
      });
    } else {
      setFormData({
        name: '',
        slug: '',
        description: '',
        isPublic: false,
        isActive: true,
        isHomePage: false,
        allowedRoles: '',
      });
    }
    setErrors({});
  }, [statusPage, isOpen]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value, type } = e.target;
    const checked = (e.target as HTMLInputElement).checked;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }));
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: null }));
    }
  };

  const validate = () => {
    const newErrors: any = {};
    if (!formData.name.trim()) {
      newErrors.name = translate('entity.validation.required');
    } else if (formData.name.length > 200) {
      newErrors.name = translate('entity.validation.maxlength', { max: 200 });
    }
    if (!formData.slug.trim()) {
      newErrors.slug = translate('entity.validation.required');
    } else if (formData.slug.length > 100) {
      newErrors.slug = translate('entity.validation.maxlength', { max: 100 });
    }
    if (formData.description && formData.description.length > 500) {
      newErrors.description = translate('entity.validation.maxlength', { max: 500 });
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    setLoading(true);
    try {
      const payload = {
        ...formData,
        ...(statusPage && { id: statusPage.id }),
      };

      if (statusPage) {
        await axios.put(`/api/status-pages/${statusPage.id}`, payload);
        toast.success(translate('infraMirrorApp.statusPage.updated', { param: formData.name }));
      } else {
        await axios.post('/api/status-pages', payload);
        toast.success(translate('infraMirrorApp.statusPage.created', { param: formData.name }));
      }
      onSuccess();
    } catch (error) {
      console.error('Error saving status page:', error);
      toast.error(translate('error.http.500'));
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <>
      <div className="side-panel-overlay" onClick={onClose} />
      <div className={`side-panel ${isOpen ? 'open' : ''}`}>
        <div className="side-panel-header">
          <h5>
            {statusPage ? (
              <Translate contentKey="infraMirrorApp.statusPage.home.editLabel">Edit Status Page</Translate>
            ) : (
              <Translate contentKey="infraMirrorApp.statusPage.home.createLabel">Create Status Page</Translate>
            )}
          </h5>
          <Button close onClick={onClose} />
        </div>
        <div className="side-panel-body">
          <Form onSubmit={handleSubmit}>
            <FormGroup>
              <Label for="name">
                <Translate contentKey="infraMirrorApp.statusPage.name">Name</Translate> <span className="text-danger">*</span>
              </Label>
              <Input
                type="text"
                name="name"
                id="name"
                value={formData.name}
                onChange={handleChange}
                invalid={!!errors.name}
                maxLength={200}
              />
              {errors.name && <FormFeedback>{errors.name}</FormFeedback>}
            </FormGroup>

            <FormGroup>
              <Label for="slug">
                <Translate contentKey="infraMirrorApp.statusPage.slug">Slug</Translate> <span className="text-danger">*</span>
              </Label>
              <Input
                type="text"
                name="slug"
                id="slug"
                value={formData.slug}
                onChange={handleChange}
                invalid={!!errors.slug}
                maxLength={100}
              />
              <small className="form-text text-muted">URL: /s/{formData.slug || 'your-slug'}</small>
              {errors.slug && <FormFeedback>{errors.slug}</FormFeedback>}
            </FormGroup>

            <FormGroup>
              <Label for="description">
                <Translate contentKey="infraMirrorApp.statusPage.description">Description</Translate>
              </Label>
              <Input
                type="textarea"
                name="description"
                id="description"
                value={formData.description}
                onChange={handleChange}
                invalid={!!errors.description}
                maxLength={500}
                rows={3}
              />
              {errors.description && <FormFeedback>{errors.description}</FormFeedback>}
            </FormGroup>

            <FormGroup check className="mb-3">
              <Label check>
                <Input type="checkbox" name="isPublic" checked={formData.isPublic} onChange={handleChange} />{' '}
                <Translate contentKey="infraMirrorApp.statusPage.isPublic">Public</Translate>
              </Label>
            </FormGroup>

            <FormGroup check className="mb-3">
              <Label check>
                <Input type="checkbox" name="isActive" checked={formData.isActive} onChange={handleChange} />{' '}
                <Translate contentKey="infraMirrorApp.statusPage.isActive">Active</Translate>
              </Label>
            </FormGroup>

            <FormGroup check className="mb-3">
              <Label check>
                <Input type="checkbox" name="isHomePage" checked={formData.isHomePage} onChange={handleChange} />{' '}
                <Translate contentKey="infraMirrorApp.statusPage.isHomePage">Home Page</Translate>
              </Label>
            </FormGroup>

            <FormGroup>
              <Label for="allowedRoles">
                <Translate contentKey="infraMirrorApp.statusPage.allowedRoles">Allowed Roles</Translate>
              </Label>
              <Input type="textarea" name="allowedRoles" id="allowedRoles" value={formData.allowedRoles} onChange={handleChange} rows={2} />
            </FormGroup>
          </Form>
        </div>
        <div className="side-panel-footer">
          <Button color="secondary" onClick={onClose} disabled={loading}>
            <Translate contentKey="entity.action.cancel">Cancel</Translate>
          </Button>
          <Button color="primary" onClick={handleSubmit} disabled={loading}>
            {loading ? <FontAwesomeIcon icon="spinner" spin /> : <FontAwesomeIcon icon="save" />}{' '}
            <Translate contentKey="entity.action.save">Save</Translate>
          </Button>
        </div>
      </div>
    </>
  );
};

export default StatusPageSidePanel;
