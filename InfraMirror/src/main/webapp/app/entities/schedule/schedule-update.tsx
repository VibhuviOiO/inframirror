import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { createEntity, getEntity, reset, updateEntity } from './schedule.reducer';

export const ScheduleUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const scheduleEntity = useAppSelector(state => state.schedule.entity);
  const loading = useAppSelector(state => state.schedule.loading);
  const updating = useAppSelector(state => state.schedule.updating);
  const updateSuccess = useAppSelector(state => state.schedule.updateSuccess);

  const handleClose = () => {
    navigate('/schedule');
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
    if (values.interval !== undefined && typeof values.interval !== 'number') {
      values.interval = Number(values.interval);
    }
    if (values.thresholdsWarning !== undefined && typeof values.thresholdsWarning !== 'number') {
      values.thresholdsWarning = Number(values.thresholdsWarning);
    }
    if (values.thresholdsCritical !== undefined && typeof values.thresholdsCritical !== 'number') {
      values.thresholdsCritical = Number(values.thresholdsCritical);
    }

    const entity = {
      ...scheduleEntity,
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
      ? {}
      : {
          ...scheduleEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="infraMirrorApp.schedule.home.createOrEditLabel" data-cy="ScheduleCreateUpdateHeading">
            <Translate contentKey="infraMirrorApp.schedule.home.createOrEditLabel">Create or edit a Schedule</Translate>
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
                  id="schedule-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('infraMirrorApp.schedule.name')}
                id="schedule-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 50, message: translate('entity.validation.maxlength', { max: 50 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.schedule.interval')}
                id="schedule-interval"
                name="interval"
                data-cy="interval"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.schedule.includeResponseBody')}
                id="schedule-includeResponseBody"
                name="includeResponseBody"
                data-cy="includeResponseBody"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('infraMirrorApp.schedule.thresholdsWarning')}
                id="schedule-thresholdsWarning"
                name="thresholdsWarning"
                data-cy="thresholdsWarning"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.schedule.thresholdsCritical')}
                id="schedule-thresholdsCritical"
                name="thresholdsCritical"
                data-cy="thresholdsCritical"
                type="text"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/schedule" replace color="info">
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

export default ScheduleUpdate;
