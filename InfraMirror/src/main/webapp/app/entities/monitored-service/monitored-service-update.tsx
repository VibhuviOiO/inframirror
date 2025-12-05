import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getDatacenters } from 'app/entities/datacenter/datacenter.reducer';
import { createEntity, getEntity, reset, updateEntity } from './monitored-service.reducer';

export const MonitoredServiceUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const datacenters = useAppSelector(state => state.datacenter.entities);
  const monitoredServiceEntity = useAppSelector(state => state.monitoredService.entity);
  const loading = useAppSelector(state => state.monitoredService.loading);
  const updating = useAppSelector(state => state.monitoredService.updating);
  const updateSuccess = useAppSelector(state => state.monitoredService.updateSuccess);

  const handleClose = () => {
    navigate(`/monitored-service${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getDatacenters({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.intervalSeconds !== undefined && typeof values.intervalSeconds !== 'number') {
      values.intervalSeconds = Number(values.intervalSeconds);
    }
    if (values.timeoutMs !== undefined && typeof values.timeoutMs !== 'number') {
      values.timeoutMs = Number(values.timeoutMs);
    }
    if (values.retryCount !== undefined && typeof values.retryCount !== 'number') {
      values.retryCount = Number(values.retryCount);
    }
    if (values.latencyWarningMs !== undefined && typeof values.latencyWarningMs !== 'number') {
      values.latencyWarningMs = Number(values.latencyWarningMs);
    }
    if (values.latencyCriticalMs !== undefined && typeof values.latencyCriticalMs !== 'number') {
      values.latencyCriticalMs = Number(values.latencyCriticalMs);
    }
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...monitoredServiceEntity,
      ...values,
      datacenter: datacenters.find(it => it.id.toString() === values.datacenter?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          createdAt: displayDefaultDateTime(),
          updatedAt: displayDefaultDateTime(),
        }
      : {
          ...monitoredServiceEntity,
          createdAt: convertDateTimeFromServer(monitoredServiceEntity.createdAt),
          updatedAt: convertDateTimeFromServer(monitoredServiceEntity.updatedAt),
          datacenter: monitoredServiceEntity?.datacenter?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="infraMirrorApp.monitoredService.home.createOrEditLabel" data-cy="MonitoredServiceCreateUpdateHeading">
            <Translate contentKey="infraMirrorApp.monitoredService.home.createOrEditLabel">Create or edit a MonitoredService</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="monitored-service-id"
                  label={translate('infraMirrorApp.monitoredService.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('infraMirrorApp.monitoredService.name')}
                id="monitored-service-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 200, message: translate('entity.validation.maxlength', { max: 200 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.monitoredService.description')}
                id="monitored-service-description"
                name="description"
                data-cy="description"
                type="text"
                validate={{
                  maxLength: { value: 500, message: translate('entity.validation.maxlength', { max: 500 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.monitoredService.serviceType')}
                id="monitored-service-serviceType"
                name="serviceType"
                data-cy="serviceType"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 50, message: translate('entity.validation.maxlength', { max: 50 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.monitoredService.environment')}
                id="monitored-service-environment"
                name="environment"
                data-cy="environment"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.monitoredService.monitoringEnabled')}
                id="monitored-service-monitoringEnabled"
                name="monitoringEnabled"
                data-cy="monitoringEnabled"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('infraMirrorApp.monitoredService.clusterMonitoringEnabled')}
                id="monitored-service-clusterMonitoringEnabled"
                name="clusterMonitoringEnabled"
                data-cy="clusterMonitoringEnabled"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('infraMirrorApp.monitoredService.intervalSeconds')}
                id="monitored-service-intervalSeconds"
                name="intervalSeconds"
                data-cy="intervalSeconds"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.monitoredService.timeoutMs')}
                id="monitored-service-timeoutMs"
                name="timeoutMs"
                data-cy="timeoutMs"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.monitoredService.retryCount')}
                id="monitored-service-retryCount"
                name="retryCount"
                data-cy="retryCount"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.monitoredService.latencyWarningMs')}
                id="monitored-service-latencyWarningMs"
                name="latencyWarningMs"
                data-cy="latencyWarningMs"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.monitoredService.latencyCriticalMs')}
                id="monitored-service-latencyCriticalMs"
                name="latencyCriticalMs"
                data-cy="latencyCriticalMs"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.monitoredService.advancedConfig')}
                id="monitored-service-advancedConfig"
                name="advancedConfig"
                data-cy="advancedConfig"
                type="textarea"
              />
              <ValidatedField
                label={translate('infraMirrorApp.monitoredService.isActive')}
                id="monitored-service-isActive"
                name="isActive"
                data-cy="isActive"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('infraMirrorApp.monitoredService.createdAt')}
                id="monitored-service-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('infraMirrorApp.monitoredService.updatedAt')}
                id="monitored-service-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="monitored-service-datacenter"
                name="datacenter"
                data-cy="datacenter"
                label={translate('infraMirrorApp.monitoredService.datacenter')}
                type="select"
              >
                <option value="" key="0" />
                {datacenters
                  ? datacenters.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/monitored-service" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default MonitoredServiceUpdate;
