import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getDatacenters } from 'app/entities/datacenter/datacenter.reducer';
import { getEntities as getAgents } from 'app/entities/agent/agent.reducer';
import { createEntity, getEntity, reset, updateEntity } from './instance.reducer';

export const InstanceUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const datacenters = useAppSelector(state => state.datacenter.entities);
  const agents = useAppSelector(state => state.agent.entities);
  const instanceEntity = useAppSelector(state => state.instance.entity);
  const loading = useAppSelector(state => state.instance.loading);
  const updating = useAppSelector(state => state.instance.updating);
  const updateSuccess = useAppSelector(state => state.instance.updateSuccess);

  const handleClose = () => {
    navigate(`/instance${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getDatacenters({}));
    dispatch(getAgents({}));
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
    if (values.pingInterval !== undefined && typeof values.pingInterval !== 'number') {
      values.pingInterval = Number(values.pingInterval);
    }
    if (values.pingTimeoutMs !== undefined && typeof values.pingTimeoutMs !== 'number') {
      values.pingTimeoutMs = Number(values.pingTimeoutMs);
    }
    if (values.pingRetryCount !== undefined && typeof values.pingRetryCount !== 'number') {
      values.pingRetryCount = Number(values.pingRetryCount);
    }
    if (values.hardwareMonitoringInterval !== undefined && typeof values.hardwareMonitoringInterval !== 'number') {
      values.hardwareMonitoringInterval = Number(values.hardwareMonitoringInterval);
    }
    if (values.cpuWarningThreshold !== undefined && typeof values.cpuWarningThreshold !== 'number') {
      values.cpuWarningThreshold = Number(values.cpuWarningThreshold);
    }
    if (values.cpuDangerThreshold !== undefined && typeof values.cpuDangerThreshold !== 'number') {
      values.cpuDangerThreshold = Number(values.cpuDangerThreshold);
    }
    if (values.memoryWarningThreshold !== undefined && typeof values.memoryWarningThreshold !== 'number') {
      values.memoryWarningThreshold = Number(values.memoryWarningThreshold);
    }
    if (values.memoryDangerThreshold !== undefined && typeof values.memoryDangerThreshold !== 'number') {
      values.memoryDangerThreshold = Number(values.memoryDangerThreshold);
    }
    if (values.diskWarningThreshold !== undefined && typeof values.diskWarningThreshold !== 'number') {
      values.diskWarningThreshold = Number(values.diskWarningThreshold);
    }
    if (values.diskDangerThreshold !== undefined && typeof values.diskDangerThreshold !== 'number') {
      values.diskDangerThreshold = Number(values.diskDangerThreshold);
    }
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);
    values.lastPingAt = convertDateTimeToServer(values.lastPingAt);
    values.lastHardwareCheckAt = convertDateTimeToServer(values.lastHardwareCheckAt);

    const entity = {
      ...instanceEntity,
      ...values,
      datacenter: datacenters.find(it => it.id.toString() === values.datacenter?.toString()),
      agent: agents.find(it => it.id.toString() === values.agent?.toString()),
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
          lastPingAt: displayDefaultDateTime(),
          lastHardwareCheckAt: displayDefaultDateTime(),
        }
      : {
          ...instanceEntity,
          createdAt: convertDateTimeFromServer(instanceEntity.createdAt),
          updatedAt: convertDateTimeFromServer(instanceEntity.updatedAt),
          lastPingAt: convertDateTimeFromServer(instanceEntity.lastPingAt),
          lastHardwareCheckAt: convertDateTimeFromServer(instanceEntity.lastHardwareCheckAt),
          datacenter: instanceEntity?.datacenter?.id,
          agent: instanceEntity?.agent?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="infraMirrorApp.instance.home.createOrEditLabel" data-cy="InstanceCreateUpdateHeading">
            <Translate contentKey="infraMirrorApp.instance.home.createOrEditLabel">Create or edit a Instance</Translate>
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
                  id="instance-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('infraMirrorApp.instance.name')}
                id="instance-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.instance.hostname')}
                id="instance-hostname"
                name="hostname"
                data-cy="hostname"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.instance.description')}
                id="instance-description"
                name="description"
                data-cy="description"
                type="text"
                validate={{
                  maxLength: { value: 500, message: translate('entity.validation.maxlength', { max: 500 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.instance.instanceType')}
                id="instance-instanceType"
                name="instanceType"
                data-cy="instanceType"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 50, message: translate('entity.validation.maxlength', { max: 50 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.instance.monitoringType')}
                id="instance-monitoringType"
                name="monitoringType"
                data-cy="monitoringType"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 50, message: translate('entity.validation.maxlength', { max: 50 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.instance.operatingSystem')}
                id="instance-operatingSystem"
                name="operatingSystem"
                data-cy="operatingSystem"
                type="text"
                validate={{
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.instance.platform')}
                id="instance-platform"
                name="platform"
                data-cy="platform"
                type="text"
                validate={{
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.instance.privateIpAddress')}
                id="instance-privateIpAddress"
                name="privateIpAddress"
                data-cy="privateIpAddress"
                type="text"
                validate={{
                  maxLength: { value: 50, message: translate('entity.validation.maxlength', { max: 50 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.instance.publicIpAddress')}
                id="instance-publicIpAddress"
                name="publicIpAddress"
                data-cy="publicIpAddress"
                type="text"
                validate={{
                  maxLength: { value: 50, message: translate('entity.validation.maxlength', { max: 50 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.instance.tags')}
                id="instance-tags"
                name="tags"
                data-cy="tags"
                type="textarea"
              />
              <ValidatedField
                label={translate('infraMirrorApp.instance.pingEnabled')}
                id="instance-pingEnabled"
                name="pingEnabled"
                data-cy="pingEnabled"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('infraMirrorApp.instance.pingInterval')}
                id="instance-pingInterval"
                name="pingInterval"
                data-cy="pingInterval"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.instance.pingTimeoutMs')}
                id="instance-pingTimeoutMs"
                name="pingTimeoutMs"
                data-cy="pingTimeoutMs"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.instance.pingRetryCount')}
                id="instance-pingRetryCount"
                name="pingRetryCount"
                data-cy="pingRetryCount"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.instance.hardwareMonitoringEnabled')}
                id="instance-hardwareMonitoringEnabled"
                name="hardwareMonitoringEnabled"
                data-cy="hardwareMonitoringEnabled"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('infraMirrorApp.instance.hardwareMonitoringInterval')}
                id="instance-hardwareMonitoringInterval"
                name="hardwareMonitoringInterval"
                data-cy="hardwareMonitoringInterval"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.instance.cpuWarningThreshold')}
                id="instance-cpuWarningThreshold"
                name="cpuWarningThreshold"
                data-cy="cpuWarningThreshold"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.instance.cpuDangerThreshold')}
                id="instance-cpuDangerThreshold"
                name="cpuDangerThreshold"
                data-cy="cpuDangerThreshold"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.instance.memoryWarningThreshold')}
                id="instance-memoryWarningThreshold"
                name="memoryWarningThreshold"
                data-cy="memoryWarningThreshold"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.instance.memoryDangerThreshold')}
                id="instance-memoryDangerThreshold"
                name="memoryDangerThreshold"
                data-cy="memoryDangerThreshold"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.instance.diskWarningThreshold')}
                id="instance-diskWarningThreshold"
                name="diskWarningThreshold"
                data-cy="diskWarningThreshold"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.instance.diskDangerThreshold')}
                id="instance-diskDangerThreshold"
                name="diskDangerThreshold"
                data-cy="diskDangerThreshold"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.instance.createdAt')}
                id="instance-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('infraMirrorApp.instance.updatedAt')}
                id="instance-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('infraMirrorApp.instance.lastPingAt')}
                id="instance-lastPingAt"
                name="lastPingAt"
                data-cy="lastPingAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('infraMirrorApp.instance.lastHardwareCheckAt')}
                id="instance-lastHardwareCheckAt"
                name="lastHardwareCheckAt"
                data-cy="lastHardwareCheckAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="instance-datacenter"
                name="datacenter"
                data-cy="datacenter"
                label={translate('infraMirrorApp.instance.datacenter')}
                type="select"
                required
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
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="instance-agent"
                name="agent"
                data-cy="agent"
                label={translate('infraMirrorApp.instance.agent')}
                type="select"
              >
                <option value="" key="0" />
                {agents
                  ? agents.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/instance" replace color="info">
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

export default InstanceUpdate;
