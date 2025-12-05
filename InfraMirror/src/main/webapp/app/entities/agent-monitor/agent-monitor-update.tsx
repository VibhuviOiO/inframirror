import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getAgents } from 'app/entities/agent/agent.reducer';
import { getEntities as getHttpMonitors } from 'app/entities/http-monitor/http-monitor.reducer';
import { createEntity, getEntity, reset, updateEntity } from './agent-monitor.reducer';

export const AgentMonitorUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const agents = useAppSelector(state => state.agent.entities);
  const httpMonitors = useAppSelector(state => state.httpMonitor.entities);
  const agentMonitorEntity = useAppSelector(state => state.agentMonitor.entity);
  const loading = useAppSelector(state => state.agentMonitor.loading);
  const updating = useAppSelector(state => state.agentMonitor.updating);
  const updateSuccess = useAppSelector(state => state.agentMonitor.updateSuccess);

  const handleClose = () => {
    navigate('/agent-monitor');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getAgents({}));
    dispatch(getHttpMonitors({}));
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
    values.createdDate = convertDateTimeToServer(values.createdDate);
    values.lastModifiedDate = convertDateTimeToServer(values.lastModifiedDate);

    const entity = {
      ...agentMonitorEntity,
      ...values,
      agent: agents.find(it => it.id.toString() === values.agent?.toString()),
      monitor: httpMonitors.find(it => it.id.toString() === values.monitor?.toString()),
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
          createdDate: displayDefaultDateTime(),
          lastModifiedDate: displayDefaultDateTime(),
        }
      : {
          ...agentMonitorEntity,
          createdDate: convertDateTimeFromServer(agentMonitorEntity.createdDate),
          lastModifiedDate: convertDateTimeFromServer(agentMonitorEntity.lastModifiedDate),
          agent: agentMonitorEntity?.agent?.id,
          monitor: agentMonitorEntity?.monitor?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="infraMirrorApp.agentMonitor.home.createOrEditLabel" data-cy="AgentMonitorCreateUpdateHeading">
            <Translate contentKey="infraMirrorApp.agentMonitor.home.createOrEditLabel">Create or edit a AgentMonitor</Translate>
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
                  id="agent-monitor-id"
                  label={translate('infraMirrorApp.agentMonitor.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('infraMirrorApp.agentMonitor.active')}
                id="agent-monitor-active"
                name="active"
                data-cy="active"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('infraMirrorApp.agentMonitor.createdBy')}
                id="agent-monitor-createdBy"
                name="createdBy"
                data-cy="createdBy"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 50, message: translate('entity.validation.maxlength', { max: 50 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.agentMonitor.createdDate')}
                id="agent-monitor-createdDate"
                name="createdDate"
                data-cy="createdDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('infraMirrorApp.agentMonitor.lastModifiedBy')}
                id="agent-monitor-lastModifiedBy"
                name="lastModifiedBy"
                data-cy="lastModifiedBy"
                type="text"
                validate={{
                  maxLength: { value: 50, message: translate('entity.validation.maxlength', { max: 50 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.agentMonitor.lastModifiedDate')}
                id="agent-monitor-lastModifiedDate"
                name="lastModifiedDate"
                data-cy="lastModifiedDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="agent-monitor-agent"
                name="agent"
                data-cy="agent"
                label={translate('infraMirrorApp.agentMonitor.agent')}
                type="select"
                required
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
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <ValidatedField
                id="agent-monitor-monitor"
                name="monitor"
                data-cy="monitor"
                label={translate('infraMirrorApp.agentMonitor.monitor')}
                type="select"
                required
              >
                <option value="" key="0" />
                {httpMonitors
                  ? httpMonitors.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/agent-monitor" replace color="info">
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

export default AgentMonitorUpdate;
