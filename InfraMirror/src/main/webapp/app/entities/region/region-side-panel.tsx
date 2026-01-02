import React, { useEffect, useState, useCallback } from 'react';
import { Button, Form, FormGroup, Label, Input, FormFeedback } from 'reactstrap';
import { Translate, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { toast } from 'react-toastify';
import { IRegion } from 'app/shared/model/region.model';
import { useAppDispatch } from 'app/config/store';
import { createEntity, updateEntity } from './region.reducer';

interface RegionSidePanelProps {
  isOpen: boolean;
  onClose: () => void;
  region: IRegion | null;
  onSuccess: () => void;
}

const RegionSidePanel: React.FC<RegionSidePanelProps> = ({ isOpen, onClose, region, onSuccess }) => {
  const dispatch = useAppDispatch();
  const [formData, setFormData] = useState<IRegion>({
    name: '',
    regionCode: '',
    groupName: '',
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [loading, setLoading] = useState(false);

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
    if (!formData.name?.trim()) newErrors.name = translate('entity.validation.required');
    if (!formData.regionCode?.trim()) newErrors.regionCode = translate('entity.validation.required');
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  }, [formData.name, formData.regionCode]);

  const handleSubmit = useCallback(
    async (e: React.FormEvent) => {
      e.preventDefault();
      if (!validate()) return;

      setLoading(true);
      try {
        const action = region?.id ? updateEntity(formData) : createEntity(formData);
        await dispatch(action).unwrap();
        toast.success(translate(region?.id ? 'infraMirrorApp.region.updated' : 'infraMirrorApp.region.created', { param: formData.name }));
        onClose();
        onSuccess();
      } catch (error) {
        toast.error(translate('error.http.500'));
      } finally {
        setLoading(false);
      }
    },
    [dispatch, formData, region, validate, onClose, onSuccess],
  );

  if (!isOpen) return null;

  return (
    <>
      <div className="side-panel-overlay" onClick={onClose} />
      <div className={`side-panel ${isOpen ? 'open' : ''}`}>
        <div className="side-panel-header">
          <h5>
            {region ? (
              <Translate contentKey="infraMirrorApp.region.home.editLabel">Edit Region</Translate>
            ) : (
              <Translate contentKey="infraMirrorApp.region.home.createLabel">Create Region</Translate>
            )}
          </h5>
          <Button close onClick={onClose} />
        </div>
        <div className="side-panel-body">
          <Form onSubmit={handleSubmit}>
            <FormGroup>
              <Label for="name">
                <Translate contentKey="infraMirrorApp.region.name">Name</Translate> <span className="text-danger">*</span>
              </Label>
              <Input type="text" name="name" id="name" value={formData.name} onChange={handleChange} invalid={!!errors.name} />
              {errors.name && <FormFeedback>{errors.name}</FormFeedback>}
            </FormGroup>

            <FormGroup>
              <Label for="regionCode">
                <Translate contentKey="infraMirrorApp.region.regionCode">Region Code</Translate> <span className="text-danger">*</span>
              </Label>
              <Input
                type="text"
                name="regionCode"
                id="regionCode"
                value={formData.regionCode}
                onChange={handleChange}
                invalid={!!errors.regionCode}
              />
              {errors.regionCode && <FormFeedback>{errors.regionCode}</FormFeedback>}
            </FormGroup>

            <FormGroup>
              <Label for="groupName">
                <Translate contentKey="infraMirrorApp.region.groupName">Group Name</Translate>
              </Label>
              <Input type="text" name="groupName" id="groupName" value={formData.groupName || ''} onChange={handleChange} />
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

export default RegionSidePanel;
