import React, { useEffect } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { Button, Form, FormGroup, Label, Input, FormFeedback } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTimes, faSave } from '@fortawesome/free-solid-svg-icons';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { createEntity, updateEntity } from './monitored-service.reducer';
import { getEntities as getDatacenters } from '../datacenter/datacenter.reducer';
import { toast } from 'react-toastify';

interface MonitoredServiceSidePanelProps {
  isOpen: boolean;
  onClose: () => void;
  service: any;
  onSuccess: () => void;
}

const MonitoredServiceSidePanel: React.FC<MonitoredServiceSidePanelProps> = ({ isOpen, onClose, service, onSuccess }) => {
  const dispatch = useAppDispatch();
  const updating = useAppSelector(state => state.monitoredService.updating);
  const datacenters = useAppSelector(state => state.datacenter.entities);

  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    defaultValues: {
      name: '',
      description: '',
      serviceType: 'HTTP',
      environment: 'PRODUCTION',
      intervalSeconds: 60,
      timeoutMs: 5000,
      retryCount: 3,
      monitoringEnabled: true,
      clusterMonitoringEnabled: false,
      isActive: true,
      datacenterId: '',
    },
  });

  useEffect(() => {
    dispatch(getDatacenters({}));
  }, []);

  useEffect(() => {
    if (service) {
      reset({
        name: service.name || '',
        description: service.description || '',
        serviceType: service.serviceType || 'HTTP',
        environment: service.environment || 'PRODUCTION',
        intervalSeconds: service.intervalSeconds || 60,
        timeoutMs: service.timeoutMs || 5000,
        retryCount: service.retryCount || 3,
        monitoringEnabled: service.monitoringEnabled ?? true,
        clusterMonitoringEnabled: service.clusterMonitoringEnabled ?? false,
        isActive: service.isActive ?? true,
        datacenterId: service.datacenter?.id || '',
      });
    } else {
      reset({
        name: '',
        description: '',
        serviceType: 'HTTP',
        environment: 'PRODUCTION',
        intervalSeconds: 60,
        timeoutMs: 5000,
        retryCount: 3,
        monitoringEnabled: true,
        clusterMonitoringEnabled: false,
        isActive: true,
        datacenterId: '',
      });
    }
  }, [service, reset]);

  const onSubmit = async data => {
    const entityData = {
      ...(service || {}),
      ...data,
      datacenter: data.datacenterId ? { id: data.datacenterId } : null,
    };
    delete entityData.datacenterId;

    try {
      if (service?.id) {
        await dispatch(updateEntity(entityData)).unwrap();
        toast.success('Service updated successfully');
      } else {
        await dispatch(createEntity(entityData)).unwrap();
        toast.success('Service created successfully');
      }
      onClose();
      onSuccess();
    } catch (error) {
      toast.error(error.message || 'Operation failed');
    }
  };

  if (!isOpen) return null;

  return (
    <>
      <div className="side-panel-overlay" onClick={onClose} />
      <div className={`side-panel ${isOpen ? 'open' : ''}`}>
        <div className="side-panel-header">
          <h5 className="mb-0">{service ? 'Edit Service' : 'Create Service'}</h5>
          <Button color="link" className="btn-close" onClick={onClose} aria-label="Close" />
        </div>
        <div className="side-panel-body">
          <Form onSubmit={handleSubmit(onSubmit)}>
            <FormGroup>
              <Label for="name">
                Name <span className="text-danger">*</span>
              </Label>
              <Controller
                name="name"
                control={control}
                rules={{ required: 'Name is required' }}
                render={({ field }) => <Input {...field} id="name" type="text" invalid={!!errors.name} disabled={updating} />}
              />
              {errors.name && <FormFeedback>{errors.name.message}</FormFeedback>}
            </FormGroup>

            <FormGroup>
              <Label for="description">Description</Label>
              <Controller
                name="description"
                control={control}
                render={({ field }) => <Input {...field} id="description" type="textarea" rows={3} disabled={updating} />}
              />
            </FormGroup>

            <FormGroup>
              <Label for="serviceType">
                Service Type <span className="text-danger">*</span>
              </Label>
              <Controller
                name="serviceType"
                control={control}
                rules={{ required: 'Service type is required' }}
                render={({ field }) => (
                  <Input {...field} id="serviceType" type="select" invalid={!!errors.serviceType} disabled={updating}>
                    <option value="HTTP">HTTP</option>
                    <option value="HTTPS">HTTPS</option>
                    <option value="TCP">TCP</option>
                    <option value="DATABASE">DATABASE</option>
                    <option value="REDIS">REDIS</option>
                    <option value="KAFKA">KAFKA</option>
                    <option value="RABBITMQ">RABBITMQ</option>
                  </Input>
                )}
              />
              {errors.serviceType && <FormFeedback>{errors.serviceType.message}</FormFeedback>}
            </FormGroup>

            <FormGroup>
              <Label for="environment">
                Environment <span className="text-danger">*</span>
              </Label>
              <Controller
                name="environment"
                control={control}
                rules={{ required: 'Environment is required' }}
                render={({ field }) => (
                  <Input {...field} id="environment" type="select" invalid={!!errors.environment} disabled={updating}>
                    <option value="PRODUCTION">PRODUCTION</option>
                    <option value="STAGING">STAGING</option>
                    <option value="DEVELOPMENT">DEVELOPMENT</option>
                    <option value="QA">QA</option>
                  </Input>
                )}
              />
              {errors.environment && <FormFeedback>{errors.environment.message}</FormFeedback>}
            </FormGroup>

            <FormGroup>
              <Label for="datacenterId">Datacenter</Label>
              <Controller
                name="datacenterId"
                control={control}
                render={({ field }) => (
                  <Input {...field} id="datacenterId" type="select" disabled={updating}>
                    <option value="">Select Datacenter</option>
                    {datacenters.map(dc => (
                      <option key={dc.id} value={dc.id}>
                        {dc.name}
                      </option>
                    ))}
                  </Input>
                )}
              />
            </FormGroup>

            <FormGroup>
              <Label for="intervalSeconds">Interval (seconds)</Label>
              <Controller
                name="intervalSeconds"
                control={control}
                render={({ field }) => <Input {...field} id="intervalSeconds" type="number" min={10} disabled={updating} />}
              />
            </FormGroup>

            <FormGroup>
              <Label for="timeoutMs">Timeout (ms)</Label>
              <Controller
                name="timeoutMs"
                control={control}
                render={({ field }) => <Input {...field} id="timeoutMs" type="number" min={100} disabled={updating} />}
              />
            </FormGroup>

            <FormGroup>
              <Label for="retryCount">Retry Count</Label>
              <Controller
                name="retryCount"
                control={control}
                render={({ field }) => <Input {...field} id="retryCount" type="number" min={0} max={10} disabled={updating} />}
              />
            </FormGroup>

            <FormGroup check className="mb-3">
              <Controller
                name="monitoringEnabled"
                control={control}
                render={({ field: { value, ...field } }) => (
                  <Input {...field} id="monitoringEnabled" type="checkbox" checked={value} disabled={updating} />
                )}
              />
              <Label for="monitoringEnabled" check>
                Monitoring Enabled
              </Label>
            </FormGroup>

            <FormGroup check className="mb-3">
              <Controller
                name="clusterMonitoringEnabled"
                control={control}
                render={({ field: { value, ...field } }) => (
                  <Input {...field} id="clusterMonitoringEnabled" type="checkbox" checked={value} disabled={updating} />
                )}
              />
              <Label for="clusterMonitoringEnabled" check>
                Cluster Monitoring Enabled
              </Label>
            </FormGroup>

            <FormGroup check>
              <Controller
                name="isActive"
                control={control}
                render={({ field: { value, ...field } }) => (
                  <Input {...field} id="isActive" type="checkbox" checked={value} disabled={updating} />
                )}
              />
              <Label for="isActive" check>
                Active
              </Label>
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

export default MonitoredServiceSidePanel;
