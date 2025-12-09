import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Card, CardBody, Col, FormText, Row } from 'reactstrap';
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

  const defaultValues = () => {
    if (isNew) {
      return {
        pingEnabled: true,
        pingInterval: 60,
        pingTimeoutMs: 5000,
        pingRetryCount: 3,
        hardwareMonitoringEnabled: true,
        hardwareMonitoringInterval: 300,
        cpuWarningThreshold: 70,
        cpuDangerThreshold: 90,
        memoryWarningThreshold: 75,
        memoryDangerThreshold: 90,
        diskWarningThreshold: 80,
        diskDangerThreshold: 95,
      };
    }
    return {
      ...instanceEntity,
      datacenter: instanceEntity?.datacenter?.id,
      agent: instanceEntity?.agent?.id,
    };
  };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="10">
          <Card>
            <CardBody>
              <h4 id="infraMirrorApp.instance.home.createOrEditLabel" data-cy="InstanceCreateUpdateHeading">
                <Translate contentKey="infraMirrorApp.instance.home.createOrEditLabel">Create or edit a Instance</Translate>
              </h4>
              <hr />
              {loading ? (
                <p>Loading...</p>
              ) : (
                <ValidatedForm key={isNew ? 'new' : instanceEntity?.id} defaultValues={defaultValues()} onSubmit={saveEntity}>
                  <h5 className="mt-3 mb-3">Basic Information</h5>
                  <Row>
                    <Col md="6">
                      <ValidatedField
                        label={translate('infraMirrorApp.instance.name') + ' *'}
                        id="instance-name"
                        name="name"
                        data-cy="name"
                        type="text"
                        validate={{
                          required: { value: true, message: translate('entity.validation.required') },
                          maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                        }}
                      />
                    </Col>
                    <Col md="6">
                      <ValidatedField
                        label={translate('infraMirrorApp.instance.hostname') + ' *'}
                        id="instance-hostname"
                        name="hostname"
                        data-cy="hostname"
                        type="text"
                        validate={{
                          required: { value: true, message: translate('entity.validation.required') },
                          maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                        }}
                      />
                    </Col>
                  </Row>
                  <Row>
                    <Col md="12">
                      <ValidatedField
                        label={translate('infraMirrorApp.instance.description')}
                        id="instance-description"
                        name="description"
                        data-cy="description"
                        type="textarea"
                        validate={{
                          maxLength: { value: 500, message: translate('entity.validation.maxlength', { max: 500 }) },
                        }}
                      />
                    </Col>
                  </Row>
                  <Row>
                    <Col md="4">
                      <ValidatedField
                        label={translate('infraMirrorApp.instance.instanceType') + ' *'}
                        id="instance-instanceType"
                        name="instanceType"
                        data-cy="instanceType"
                        type="text"
                        validate={{
                          required: { value: true, message: translate('entity.validation.required') },
                          maxLength: { value: 50, message: translate('entity.validation.maxlength', { max: 50 }) },
                        }}
                      />
                    </Col>
                    <Col md="4">
                      <ValidatedField
                        label={translate('infraMirrorApp.instance.monitoringType') + ' *'}
                        id="instance-monitoringType"
                        name="monitoringType"
                        data-cy="monitoringType"
                        type="text"
                        validate={{
                          required: { value: true, message: translate('entity.validation.required') },
                          maxLength: { value: 50, message: translate('entity.validation.maxlength', { max: 50 }) },
                        }}
                      />
                    </Col>
                    <Col md="4">
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
                    </Col>
                  </Row>
                  <Row>
                    <Col md="4">
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
                    </Col>
                    <Col md="4">
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
                    </Col>
                    <Col md="4">
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
                    </Col>
                  </Row>
                  <Row>
                    <Col md="6">
                      <ValidatedField
                        id="instance-datacenter"
                        name="datacenter"
                        data-cy="datacenter"
                        label={translate('infraMirrorApp.instance.datacenter') + ' *'}
                        type="select"
                        required
                      >
                        <option value="" key="0" />
                        {datacenters
                          ? datacenters.map(otherEntity => (
                              <option value={otherEntity.id} key={otherEntity.id}>
                                {otherEntity.name}
                              </option>
                            ))
                          : null}
                      </ValidatedField>
                      <FormText>
                        <Translate contentKey="entity.validation.required">This field is required.</Translate>
                      </FormText>
                    </Col>
                    <Col md="6">
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
                                {otherEntity.name}
                              </option>
                            ))
                          : null}
                      </ValidatedField>
                    </Col>
                  </Row>
                  <Row>
                    <Col md="12">
                      <ValidatedField
                        label={translate('infraMirrorApp.instance.tags')}
                        id="instance-tags"
                        name="tags"
                        data-cy="tags"
                        type="textarea"
                        rows="2"
                      />
                    </Col>
                  </Row>

                  <h5 className="mt-4 mb-3">Ping Monitoring</h5>
                  <Row>
                    <Col md="3">
                      <ValidatedField
                        label={translate('infraMirrorApp.instance.pingEnabled')}
                        id="instance-pingEnabled"
                        name="pingEnabled"
                        data-cy="pingEnabled"
                        check
                        type="checkbox"
                      />
                    </Col>
                    <Col md="3">
                      <ValidatedField
                        label={translate('infraMirrorApp.instance.pingInterval') + ' *'}
                        id="instance-pingInterval"
                        name="pingInterval"
                        data-cy="pingInterval"
                        type="text"
                        placeholder="60"
                        validate={{
                          required: { value: true, message: translate('entity.validation.required') },
                          validate: v => isNumber(v) || translate('entity.validation.number'),
                        }}
                      />
                    </Col>
                    <Col md="3">
                      <ValidatedField
                        label={translate('infraMirrorApp.instance.pingTimeoutMs') + ' *'}
                        id="instance-pingTimeoutMs"
                        name="pingTimeoutMs"
                        data-cy="pingTimeoutMs"
                        type="text"
                        placeholder="5000"
                        validate={{
                          required: { value: true, message: translate('entity.validation.required') },
                          validate: v => isNumber(v) || translate('entity.validation.number'),
                        }}
                      />
                    </Col>
                    <Col md="3">
                      <ValidatedField
                        label={translate('infraMirrorApp.instance.pingRetryCount') + ' *'}
                        id="instance-pingRetryCount"
                        name="pingRetryCount"
                        data-cy="pingRetryCount"
                        type="text"
                        placeholder="3"
                        validate={{
                          required: { value: true, message: translate('entity.validation.required') },
                          validate: v => isNumber(v) || translate('entity.validation.number'),
                        }}
                      />
                    </Col>
                  </Row>

                  <h5 className="mt-4 mb-3">Hardware Monitoring</h5>
                  <Row>
                    <Col md="6">
                      <ValidatedField
                        label={translate('infraMirrorApp.instance.hardwareMonitoringEnabled')}
                        id="instance-hardwareMonitoringEnabled"
                        name="hardwareMonitoringEnabled"
                        data-cy="hardwareMonitoringEnabled"
                        check
                        type="checkbox"
                      />
                    </Col>
                    <Col md="6">
                      <ValidatedField
                        label={translate('infraMirrorApp.instance.hardwareMonitoringInterval') + ' *'}
                        id="instance-hardwareMonitoringInterval"
                        name="hardwareMonitoringInterval"
                        data-cy="hardwareMonitoringInterval"
                        type="text"
                        placeholder="300"
                        validate={{
                          required: { value: true, message: translate('entity.validation.required') },
                          validate: v => isNumber(v) || translate('entity.validation.number'),
                        }}
                      />
                    </Col>
                  </Row>

                  <h5 className="mt-4 mb-3">Threshold Settings</h5>
                  <Row>
                    <Col md="6">
                      <ValidatedField
                        label={translate('infraMirrorApp.instance.cpuWarningThreshold') + ' (%) *'}
                        id="instance-cpuWarningThreshold"
                        name="cpuWarningThreshold"
                        data-cy="cpuWarningThreshold"
                        type="text"
                        placeholder="70"
                        validate={{
                          required: { value: true, message: translate('entity.validation.required') },
                          validate: v => isNumber(v) || translate('entity.validation.number'),
                        }}
                      />
                    </Col>
                    <Col md="6">
                      <ValidatedField
                        label={translate('infraMirrorApp.instance.cpuDangerThreshold') + ' (%) *'}
                        id="instance-cpuDangerThreshold"
                        name="cpuDangerThreshold"
                        data-cy="cpuDangerThreshold"
                        type="text"
                        placeholder="90"
                        validate={{
                          required: { value: true, message: translate('entity.validation.required') },
                          validate: v => isNumber(v) || translate('entity.validation.number'),
                        }}
                      />
                    </Col>
                  </Row>
                  <Row>
                    <Col md="6">
                      <ValidatedField
                        label={translate('infraMirrorApp.instance.memoryWarningThreshold') + ' (%) *'}
                        id="instance-memoryWarningThreshold"
                        name="memoryWarningThreshold"
                        data-cy="memoryWarningThreshold"
                        type="text"
                        placeholder="75"
                        validate={{
                          required: { value: true, message: translate('entity.validation.required') },
                          validate: v => isNumber(v) || translate('entity.validation.number'),
                        }}
                      />
                    </Col>
                    <Col md="6">
                      <ValidatedField
                        label={translate('infraMirrorApp.instance.memoryDangerThreshold') + ' (%) *'}
                        id="instance-memoryDangerThreshold"
                        name="memoryDangerThreshold"
                        data-cy="memoryDangerThreshold"
                        type="text"
                        placeholder="90"
                        validate={{
                          required: { value: true, message: translate('entity.validation.required') },
                          validate: v => isNumber(v) || translate('entity.validation.number'),
                        }}
                      />
                    </Col>
                  </Row>
                  <Row>
                    <Col md="6">
                      <ValidatedField
                        label={translate('infraMirrorApp.instance.diskWarningThreshold') + ' (%) *'}
                        id="instance-diskWarningThreshold"
                        name="diskWarningThreshold"
                        data-cy="diskWarningThreshold"
                        type="text"
                        placeholder="80"
                        validate={{
                          required: { value: true, message: translate('entity.validation.required') },
                          validate: v => isNumber(v) || translate('entity.validation.number'),
                        }}
                      />
                    </Col>
                    <Col md="6">
                      <ValidatedField
                        label={translate('infraMirrorApp.instance.diskDangerThreshold') + ' (%) *'}
                        id="instance-diskDangerThreshold"
                        name="diskDangerThreshold"
                        data-cy="diskDangerThreshold"
                        type="text"
                        placeholder="95"
                        validate={{
                          required: { value: true, message: translate('entity.validation.required') },
                          validate: v => isNumber(v) || translate('entity.validation.number'),
                        }}
                      />
                    </Col>
                  </Row>

                  <div className="mt-4">
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
                  </div>
                </ValidatedForm>
              )}
            </CardBody>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default InstanceUpdate;
