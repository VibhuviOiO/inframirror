import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { createEntity, getEntity, reset, updateEntity } from './agent-lock.reducer';

export const AgentLockUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const agentLockEntity = useAppSelector(state => state.agentLock.entity);
  const loading = useAppSelector(state => state.agentLock.loading);
  const updating = useAppSelector(state => state.agentLock.updating);
  const updateSuccess = useAppSelector(state => state.agentLock.updateSuccess);

  const handleClose = () => {
    navigate(`/agent-lock${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
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
    if (values.agentId !== undefined && typeof values.agentId !== 'number') {
      values.agentId = Number(values.agentId);
    }
    values.acquiredAt = convertDateTimeToServer(values.acquiredAt);
    values.expiresAt = convertDateTimeToServer(values.expiresAt);

    const entity = {
      ...agentLockEntity,
      ...values,
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
          acquiredAt: displayDefaultDateTime(),
          expiresAt: displayDefaultDateTime(),
        }
      : {
          ...agentLockEntity,
          acquiredAt: convertDateTimeFromServer(agentLockEntity.acquiredAt),
          expiresAt: convertDateTimeFromServer(agentLockEntity.expiresAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="infraMirrorApp.agentLock.home.createOrEditLabel" data-cy="AgentLockCreateUpdateHeading">
            <Translate contentKey="infraMirrorApp.agentLock.home.createOrEditLabel">Create or edit a AgentLock</Translate>
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
                  id="agent-lock-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('infraMirrorApp.agentLock.agentId')}
                id="agent-lock-agentId"
                name="agentId"
                data-cy="agentId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.agentLock.acquiredAt')}
                id="agent-lock-acquiredAt"
                name="acquiredAt"
                data-cy="acquiredAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.agentLock.expiresAt')}
                id="agent-lock-expiresAt"
                name="expiresAt"
                data-cy="expiresAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/agent-lock" replace color="info">
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

export default AgentLockUpdate;
