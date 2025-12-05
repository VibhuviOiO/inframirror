import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getAgents } from 'app/entities/agent/agent.reducer';
import { getEntities as getServices } from 'app/entities/service/service.reducer';
import { getEntities as getServiceInstances } from 'app/entities/service-instance/service-instance.reducer';
import { createEntity, getEntity, reset, updateEntity } from './service-heartbeat.reducer';

export const ServiceHeartbeatUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const agents = useAppSelector(state => state.agent.entities);
  const services = useAppSelector(state => state.service.entities);
  const serviceInstances = useAppSelector(state => state.serviceInstance.entities);
  const serviceHeartbeatEntity = useAppSelector(state => state.serviceHeartbeat.entity);
  const loading = useAppSelector(state => state.serviceHeartbeat.loading);
  const updating = useAppSelector(state => state.serviceHeartbeat.updating);
  const updateSuccess = useAppSelector(state => state.serviceHeartbeat.updateSuccess);

  const handleClose = () => {
    navigate(`/service-heartbeat${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getAgents({}));
    dispatch(getServices({}));
    dispatch(getServiceInstances({}));
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

    const entity = {
      ...serviceHeartbeatEntity,
      ...values,
      agent: agents.find(it => it.id.toString() === values.agent?.toString()),
      service: services.find(it => it.id.toString() === values.service?.toString()),
      serviceInstance: serviceInstances.find(it => it.id.toString() === values.serviceInstance?.toString()),
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
          ...serviceHeartbeatEntity,
          executedAt: convertDateTimeFromServer(serviceHeartbeatEntity.executedAt),
          agent: serviceHeartbeatEntity?.agent?.id,
          service: serviceHeartbeatEntity?.service?.id,
          serviceInstance: serviceHeartbeatEntity?.serviceInstance?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="infraMirrorApp.serviceHeartbeat.home.createOrEditLabel" data-cy="ServiceHeartbeatCreateUpdateHeading">
            <Translate contentKey="infraMirrorApp.serviceHeartbeat.home.createOrEditLabel">Create or edit a ServiceHeartbeat</Translate>
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
                  id="service-heartbeat-id"
                  label={translate('infraMirrorApp.serviceHeartbeat.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('infraMirrorApp.serviceHeartbeat.executedAt')}
                id="service-heartbeat-executedAt"
                name="executedAt"
                data-cy="executedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.serviceHeartbeat.success')}
                id="service-heartbeat-success"
                name="success"
                data-cy="success"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('infraMirrorApp.serviceHeartbeat.status')}
                id="service-heartbeat-status"
                name="status"
                data-cy="status"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.serviceHeartbeat.responseTimeMs')}
                id="service-heartbeat-responseTimeMs"
                name="responseTimeMs"
                data-cy="responseTimeMs"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.serviceHeartbeat.errorMessage')}
                id="service-heartbeat-errorMessage"
                name="errorMessage"
                data-cy="errorMessage"
                type="textarea"
              />
              <ValidatedField
                label={translate('infraMirrorApp.serviceHeartbeat.metadata')}
                id="service-heartbeat-metadata"
                name="metadata"
                data-cy="metadata"
                type="textarea"
              />
              <ValidatedField
                id="service-heartbeat-agent"
                name="agent"
                data-cy="agent"
                label={translate('infraMirrorApp.serviceHeartbeat.agent')}
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
                id="service-heartbeat-service"
                name="service"
                data-cy="service"
                label={translate('infraMirrorApp.serviceHeartbeat.service')}
                type="select"
                required
              >
                <option value="" key="0" />
                {services
                  ? services.map(otherEntity => (
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
                id="service-heartbeat-serviceInstance"
                name="serviceInstance"
                data-cy="serviceInstance"
                label={translate('infraMirrorApp.serviceHeartbeat.serviceInstance')}
                type="select"
              >
                <option value="" key="0" />
                {serviceInstances
                  ? serviceInstances.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/service-heartbeat" replace color="info">
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

export default ServiceHeartbeatUpdate;
