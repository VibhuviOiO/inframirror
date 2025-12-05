import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getAgents } from 'app/entities/agent/agent.reducer';
import { getEntities as getInstances } from 'app/entities/instance/instance.reducer';
import { createEntity, getEntity, reset, updateEntity } from './instance-heartbeat.reducer';

export const InstanceHeartbeatUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const agents = useAppSelector(state => state.agent.entities);
  const instances = useAppSelector(state => state.instance.entities);
  const instanceHeartbeatEntity = useAppSelector(state => state.instanceHeartbeat.entity);
  const loading = useAppSelector(state => state.instanceHeartbeat.loading);
  const updating = useAppSelector(state => state.instanceHeartbeat.updating);
  const updateSuccess = useAppSelector(state => state.instanceHeartbeat.updateSuccess);

  const handleClose = () => {
    navigate(`/instance-heartbeat${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getAgents({}));
    dispatch(getInstances({}));
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
    values.executedAt = convertDateTimeToServer(values.executedAt);
    if (values.responseTimeMs !== undefined && typeof values.responseTimeMs !== 'number') {
      values.responseTimeMs = Number(values.responseTimeMs);
    }
    if (values.packetLoss !== undefined && typeof values.packetLoss !== 'number') {
      values.packetLoss = Number(values.packetLoss);
    }
    if (values.jitterMs !== undefined && typeof values.jitterMs !== 'number') {
      values.jitterMs = Number(values.jitterMs);
    }
    if (values.cpuUsage !== undefined && typeof values.cpuUsage !== 'number') {
      values.cpuUsage = Number(values.cpuUsage);
    }
    if (values.memoryUsage !== undefined && typeof values.memoryUsage !== 'number') {
      values.memoryUsage = Number(values.memoryUsage);
    }
    if (values.diskUsage !== undefined && typeof values.diskUsage !== 'number') {
      values.diskUsage = Number(values.diskUsage);
    }
    if (values.loadAverage !== undefined && typeof values.loadAverage !== 'number') {
      values.loadAverage = Number(values.loadAverage);
    }
    if (values.processCount !== undefined && typeof values.processCount !== 'number') {
      values.processCount = Number(values.processCount);
    }
    if (values.networkRxBytes !== undefined && typeof values.networkRxBytes !== 'number') {
      values.networkRxBytes = Number(values.networkRxBytes);
    }
    if (values.networkTxBytes !== undefined && typeof values.networkTxBytes !== 'number') {
      values.networkTxBytes = Number(values.networkTxBytes);
    }
    if (values.uptimeSeconds !== undefined && typeof values.uptimeSeconds !== 'number') {
      values.uptimeSeconds = Number(values.uptimeSeconds);
    }

    const entity = {
      ...instanceHeartbeatEntity,
      ...values,
      agent: agents.find(it => it.id.toString() === values.agent?.toString()),
      instance: instances.find(it => it.id.toString() === values.instance?.toString()),
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
          executedAt: displayDefaultDateTime(),
        }
      : {
          ...instanceHeartbeatEntity,
          executedAt: convertDateTimeFromServer(instanceHeartbeatEntity.executedAt),
          agent: instanceHeartbeatEntity?.agent?.id,
          instance: instanceHeartbeatEntity?.instance?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="infraMirrorApp.instanceHeartbeat.home.createOrEditLabel" data-cy="InstanceHeartbeatCreateUpdateHeading">
            <Translate contentKey="infraMirrorApp.instanceHeartbeat.home.createOrEditLabel">Create or edit a InstanceHeartbeat</Translate>
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
                  id="instance-heartbeat-id"
                  label={translate('infraMirrorApp.instanceHeartbeat.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('infraMirrorApp.instanceHeartbeat.executedAt')}
                id="instance-heartbeat-executedAt"
                name="executedAt"
                data-cy="executedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.instanceHeartbeat.heartbeatType')}
                id="instance-heartbeat-heartbeatType"
                name="heartbeatType"
                data-cy="heartbeatType"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.instanceHeartbeat.success')}
                id="instance-heartbeat-success"
                name="success"
                data-cy="success"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('infraMirrorApp.instanceHeartbeat.responseTimeMs')}
                id="instance-heartbeat-responseTimeMs"
                name="responseTimeMs"
                data-cy="responseTimeMs"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.instanceHeartbeat.packetLoss')}
                id="instance-heartbeat-packetLoss"
                name="packetLoss"
                data-cy="packetLoss"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.instanceHeartbeat.jitterMs')}
                id="instance-heartbeat-jitterMs"
                name="jitterMs"
                data-cy="jitterMs"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.instanceHeartbeat.cpuUsage')}
                id="instance-heartbeat-cpuUsage"
                name="cpuUsage"
                data-cy="cpuUsage"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.instanceHeartbeat.memoryUsage')}
                id="instance-heartbeat-memoryUsage"
                name="memoryUsage"
                data-cy="memoryUsage"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.instanceHeartbeat.diskUsage')}
                id="instance-heartbeat-diskUsage"
                name="diskUsage"
                data-cy="diskUsage"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.instanceHeartbeat.loadAverage')}
                id="instance-heartbeat-loadAverage"
                name="loadAverage"
                data-cy="loadAverage"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.instanceHeartbeat.processCount')}
                id="instance-heartbeat-processCount"
                name="processCount"
                data-cy="processCount"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.instanceHeartbeat.networkRxBytes')}
                id="instance-heartbeat-networkRxBytes"
                name="networkRxBytes"
                data-cy="networkRxBytes"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.instanceHeartbeat.networkTxBytes')}
                id="instance-heartbeat-networkTxBytes"
                name="networkTxBytes"
                data-cy="networkTxBytes"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.instanceHeartbeat.uptimeSeconds')}
                id="instance-heartbeat-uptimeSeconds"
                name="uptimeSeconds"
                data-cy="uptimeSeconds"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.instanceHeartbeat.status')}
                id="instance-heartbeat-status"
                name="status"
                data-cy="status"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.instanceHeartbeat.errorMessage')}
                id="instance-heartbeat-errorMessage"
                name="errorMessage"
                data-cy="errorMessage"
                type="textarea"
              />
              <ValidatedField
                label={translate('infraMirrorApp.instanceHeartbeat.errorType')}
                id="instance-heartbeat-errorType"
                name="errorType"
                data-cy="errorType"
                type="text"
                validate={{
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.instanceHeartbeat.metadata')}
                id="instance-heartbeat-metadata"
                name="metadata"
                data-cy="metadata"
                type="textarea"
              />
              <ValidatedField
                id="instance-heartbeat-agent"
                name="agent"
                data-cy="agent"
                label={translate('infraMirrorApp.instanceHeartbeat.agent')}
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
              <ValidatedField
                id="instance-heartbeat-instance"
                name="instance"
                data-cy="instance"
                label={translate('infraMirrorApp.instanceHeartbeat.instance')}
                type="select"
                required
              >
                <option value="" key="0" />
                {instances
                  ? instances.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/instance-heartbeat" replace color="info">
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

export default InstanceHeartbeatUpdate;
