import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getStatusPages } from 'app/entities/status-page/status-page.reducer';
import { createEntity, getEntity, reset, updateEntity } from './status-dependency.reducer';

export const StatusDependencyUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const statusPages = useAppSelector(state => state.statusPage.entities);
  const statusDependencyEntity = useAppSelector(state => state.statusDependency.entity);
  const loading = useAppSelector(state => state.statusDependency.loading);
  const updating = useAppSelector(state => state.statusDependency.updating);
  const updateSuccess = useAppSelector(state => state.statusDependency.updateSuccess);

  const handleClose = () => {
    navigate(`/status-dependency${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getStatusPages({}));
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
    if (values.parentId !== undefined && typeof values.parentId !== 'number') {
      values.parentId = Number(values.parentId);
    }
    if (values.childId !== undefined && typeof values.childId !== 'number') {
      values.childId = Number(values.childId);
    }
    values.createdAt = convertDateTimeToServer(values.createdAt);

    const entity = {
      ...statusDependencyEntity,
      ...values,
      statusPage: statusPages.find(it => it.id.toString() === values.statusPage?.toString()),
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
        }
      : {
          ...statusDependencyEntity,
          createdAt: convertDateTimeFromServer(statusDependencyEntity.createdAt),
          statusPage: statusDependencyEntity?.statusPage?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="infraMirrorApp.statusDependency.home.createOrEditLabel" data-cy="StatusDependencyCreateUpdateHeading">
            <Translate contentKey="infraMirrorApp.statusDependency.home.createOrEditLabel">Create or edit a StatusDependency</Translate>
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
                  id="status-dependency-id"
                  label={translate('infraMirrorApp.statusDependency.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('infraMirrorApp.statusDependency.parentType')}
                id="status-dependency-parentType"
                name="parentType"
                data-cy="parentType"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.statusDependency.parentId')}
                id="status-dependency-parentId"
                name="parentId"
                data-cy="parentId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.statusDependency.childType')}
                id="status-dependency-childType"
                name="childType"
                data-cy="childType"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.statusDependency.childId')}
                id="status-dependency-childId"
                name="childId"
                data-cy="childId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.statusDependency.metadata')}
                id="status-dependency-metadata"
                name="metadata"
                data-cy="metadata"
                type="textarea"
              />
              <ValidatedField
                label={translate('infraMirrorApp.statusDependency.createdAt')}
                id="status-dependency-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="status-dependency-statusPage"
                name="statusPage"
                data-cy="statusPage"
                label={translate('infraMirrorApp.statusDependency.statusPage')}
                type="select"
              >
                <option value="" key="0" />
                {statusPages
                  ? statusPages.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/status-dependency" replace color="info">
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

export default StatusDependencyUpdate;
