import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { createEntity, getEntity, reset, updateEntity } from './audit-trail.reducer';

export const AuditTrailUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const auditTrailEntity = useAppSelector(state => state.auditTrail.entity);
  const loading = useAppSelector(state => state.auditTrail.loading);
  const updating = useAppSelector(state => state.auditTrail.updating);
  const updateSuccess = useAppSelector(state => state.auditTrail.updateSuccess);

  const handleClose = () => {
    navigate(`/audit-trail${location.search}`);
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
    if (values.entityId !== undefined && typeof values.entityId !== 'number') {
      values.entityId = Number(values.entityId);
    }
    values.timestamp = convertDateTimeToServer(values.timestamp);

    const entity = {
      ...auditTrailEntity,
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
          timestamp: displayDefaultDateTime(),
        }
      : {
          ...auditTrailEntity,
          timestamp: convertDateTimeFromServer(auditTrailEntity.timestamp),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="infraMirrorApp.auditTrail.home.createOrEditLabel" data-cy="AuditTrailCreateUpdateHeading">
            <Translate contentKey="infraMirrorApp.auditTrail.home.createOrEditLabel">Create or edit a AuditTrail</Translate>
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
                  id="audit-trail-id"
                  label={translate('infraMirrorApp.auditTrail.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('infraMirrorApp.auditTrail.action')}
                id="audit-trail-action"
                name="action"
                data-cy="action"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.auditTrail.entityName')}
                id="audit-trail-entityName"
                name="entityName"
                data-cy="entityName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.auditTrail.entityId')}
                id="audit-trail-entityId"
                name="entityId"
                data-cy="entityId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.auditTrail.oldValue')}
                id="audit-trail-oldValue"
                name="oldValue"
                data-cy="oldValue"
                type="textarea"
              />
              <ValidatedField
                label={translate('infraMirrorApp.auditTrail.newValue')}
                id="audit-trail-newValue"
                name="newValue"
                data-cy="newValue"
                type="textarea"
              />
              <ValidatedField
                label={translate('infraMirrorApp.auditTrail.timestamp')}
                id="audit-trail-timestamp"
                name="timestamp"
                data-cy="timestamp"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.auditTrail.ipAddress')}
                id="audit-trail-ipAddress"
                name="ipAddress"
                data-cy="ipAddress"
                type="text"
                validate={{
                  maxLength: { value: 45, message: translate('entity.validation.maxlength', { max: 45 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.auditTrail.userAgent')}
                id="audit-trail-userAgent"
                name="userAgent"
                data-cy="userAgent"
                type="textarea"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/audit-trail" replace color="info">
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

export default AuditTrailUpdate;
