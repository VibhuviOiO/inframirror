import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getSchedules } from 'app/entities/schedule/schedule.reducer';
import { createEntity, getEntity, reset, updateEntity } from './http-monitor.reducer';

export const HttpMonitorUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const schedules = useAppSelector(state => state.schedule.entities);
  const httpMonitorEntity = useAppSelector(state => state.httpMonitor.entity);
  const loading = useAppSelector(state => state.httpMonitor.loading);
  const updating = useAppSelector(state => state.httpMonitor.updating);
  const updateSuccess = useAppSelector(state => state.httpMonitor.updateSuccess);

  const handleClose = () => {
    navigate(`/http-monitor${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getSchedules({}));
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

    const entity = {
      ...httpMonitorEntity,
      ...values,
      schedule: schedules.find(it => it.id.toString() === values.schedule?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...httpMonitorEntity,
          schedule: httpMonitorEntity?.schedule?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="infraMirrorApp.httpMonitor.home.createOrEditLabel" data-cy="HttpMonitorCreateUpdateHeading">
            <Translate contentKey="infraMirrorApp.httpMonitor.home.createOrEditLabel">Create or edit a HttpMonitor</Translate>
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
                  id="http-monitor-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.name')}
                id="http-monitor-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.method')}
                id="http-monitor-method"
                name="method"
                data-cy="method"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 10, message: translate('entity.validation.maxlength', { max: 10 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.type')}
                id="http-monitor-type"
                name="type"
                data-cy="type"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 10, message: translate('entity.validation.maxlength', { max: 10 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.url')}
                id="http-monitor-url"
                name="url"
                data-cy="url"
                type="textarea"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.headers')}
                id="http-monitor-headers"
                name="headers"
                data-cy="headers"
                type="textarea"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.body')}
                id="http-monitor-body"
                name="body"
                data-cy="body"
                type="textarea"
              />
              <ValidatedField
                id="http-monitor-schedule"
                name="schedule"
                data-cy="schedule"
                label={translate('infraMirrorApp.httpMonitor.schedule')}
                type="select"
              >
                <option value="" key="0" />
                {schedules
                  ? schedules.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/http-monitor" replace color="info">
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

export default HttpMonitorUpdate;
