import React, { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Button, Form, FormGroup, Label, Input, FormFeedback } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTimes, faSave } from '@fortawesome/free-solid-svg-icons';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getRegions } from 'app/entities/region/region.reducer';
import { createEntity, updateEntity } from './datacenter.reducer';
import { toast } from 'react-toastify';

interface DatacenterSidePanelProps {
  isOpen: boolean;
  onClose: () => void;
  datacenter: any;
  onSuccess: () => void;
}

const DatacenterSidePanel: React.FC<DatacenterSidePanelProps> = ({ isOpen, onClose, datacenter, onSuccess }) => {
  const dispatch = useAppDispatch();
  const regions = useAppSelector(state => state.region.entities);
  const updating = useAppSelector(state => state.datacenter.updating);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    defaultValues: {
      name: '',
      code: '',
      regionId: '',
    },
  });

  useEffect(() => {
    dispatch(getRegions({}));
  }, []);

  useEffect(() => {
    const handleEscape = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && isOpen) {
        onClose();
      }
    };
    document.addEventListener('keydown', handleEscape);
    return () => document.removeEventListener('keydown', handleEscape);
  }, [isOpen, onClose]);

  useEffect(() => {
    if (datacenter) {
      reset({
        name: datacenter.name || '',
        code: datacenter.code || '',
        regionId: datacenter.region?.id || '',
      });
    } else {
      reset({ name: '', code: '', regionId: '' });
    }
  }, [datacenter, reset]);

  const onSubmit = async data => {
    const entity = {
      ...datacenter,
      name: data.name,
      code: data.code,
      region: data.regionId ? { id: data.regionId } : null,
    };

    try {
      if (datacenter?.id) {
        await dispatch(updateEntity(entity)).unwrap();
        toast.success('Datacenter updated successfully');
        onClose();
        onSuccess();
      } else {
        await dispatch(createEntity(entity)).unwrap();
        toast.success('Datacenter created successfully');
        onClose();
        onSuccess();
      }
    } catch (error) {
      toast.error(error.message || 'Operation failed');
    }
  };

  if (!isOpen) return null;

  return (
    <>
      <div className="side-panel-overlay" onClick={onClose} />
      <div className="side-panel">
        <div className="side-panel-header">
          <h5 className="mb-0">{datacenter ? 'Edit Datacenter' : 'Create Datacenter'}</h5>
          <Button color="link" className="btn-close" onClick={onClose} aria-label="Close" />
        </div>
        <div className="side-panel-body">
          <Form onSubmit={handleSubmit(onSubmit)}>
            <FormGroup>
              <Label for="name">
                Name <span className="text-danger">*</span>
              </Label>
              <Input
                id="name"
                type="text"
                {...register('name', { required: 'Name is required' })}
                invalid={!!errors.name}
                disabled={updating}
              />
              {errors.name && <FormFeedback>{errors.name.message}</FormFeedback>}
            </FormGroup>

            <FormGroup>
              <Label for="code">
                Code <span className="text-danger">*</span>
              </Label>
              <Input
                id="code"
                type="text"
                {...register('code', { required: 'Code is required' })}
                invalid={!!errors.code}
                disabled={updating}
              />
              {errors.code && <FormFeedback>{errors.code.message}</FormFeedback>}
            </FormGroup>

            <FormGroup>
              <Label for="regionId">Region</Label>
              <Input id="regionId" type="select" {...register('regionId')} disabled={updating}>
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
          <Button color="secondary" onClick={onClose} disabled={updating}>
            <FontAwesomeIcon icon={faTimes} className="me-1" />
            Cancel
          </Button>
          <Button color="primary" onClick={handleSubmit(onSubmit)} disabled={updating} data-cy="entityCreateSaveButton">
            <FontAwesomeIcon icon={faSave} className="me-1" />
            {updating ? 'Saving...' : 'Save'}
          </Button>
        </div>
      </div>
    </>
  );
};

export default DatacenterSidePanel;
