import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { createEntity, getEntity, reset, updateEntity } from './status-page.reducer';

export const StatusPageUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const statusPageEntity = useAppSelector(state => state.statusPage.entity);
  const loading = useAppSelector(state => state.statusPage.loading);
  const updating = useAppSelector(state => state.statusPage.updating);
  const updateSuccess = useAppSelector(state => state.statusPage.updateSuccess);

  const handleClose = () => {
    navigate(`/status-page${location.search}`);
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
    if (values.autoRefreshSeconds !== undefined && typeof values.autoRefreshSeconds !== 'number') {
      values.autoRefreshSeconds = Number(values.autoRefreshSeconds);
    }
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...statusPageEntity,
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
          createdAt: displayDefaultDateTime(),
          updatedAt: displayDefaultDateTime(),
        }
      : {
          ...statusPageEntity,
          createdAt: convertDateTimeFromServer(statusPageEntity.createdAt),
          updatedAt: convertDateTimeFromServer(statusPageEntity.updatedAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="infraMirrorApp.statusPage.home.createOrEditLabel" data-cy="StatusPageCreateUpdateHeading">
            <Translate contentKey="infraMirrorApp.statusPage.home.createOrEditLabel">Create or edit a StatusPage</Translate>
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
                  id="status-page-id"
                  label={translate('infraMirrorApp.statusPage.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('infraMirrorApp.statusPage.name')}
                id="status-page-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 200, message: translate('entity.validation.maxlength', { max: 200 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.statusPage.slug')}
                id="status-page-slug"
                name="slug"
                data-cy="slug"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.statusPage.description')}
                id="status-page-description"
                name="description"
                data-cy="description"
                type="text"
                validate={{
                  maxLength: { value: 500, message: translate('entity.validation.maxlength', { max: 500 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.statusPage.isPublic')}
                id="status-page-isPublic"
                name="isPublic"
                data-cy="isPublic"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('infraMirrorApp.statusPage.customDomain')}
                id="status-page-customDomain"
                name="customDomain"
                data-cy="customDomain"
                type="text"
                validate={{
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.statusPage.logoUrl')}
                id="status-page-logoUrl"
                name="logoUrl"
                data-cy="logoUrl"
                type="text"
                validate={{
                  maxLength: { value: 500, message: translate('entity.validation.maxlength', { max: 500 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.statusPage.themeColor')}
                id="status-page-themeColor"
                name="themeColor"
                data-cy="themeColor"
                type="text"
                validate={{
                  maxLength: { value: 7, message: translate('entity.validation.maxlength', { max: 7 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.statusPage.headerText')}
                id="status-page-headerText"
                name="headerText"
                data-cy="headerText"
                type="text"
                validate={{
                  maxLength: { value: 500, message: translate('entity.validation.maxlength', { max: 500 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.statusPage.footerText')}
                id="status-page-footerText"
                name="footerText"
                data-cy="footerText"
                type="text"
                validate={{
                  maxLength: { value: 500, message: translate('entity.validation.maxlength', { max: 500 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.statusPage.showResponseTimes')}
                id="status-page-showResponseTimes"
                name="showResponseTimes"
                data-cy="showResponseTimes"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('infraMirrorApp.statusPage.showUptimePercentage')}
                id="status-page-showUptimePercentage"
                name="showUptimePercentage"
                data-cy="showUptimePercentage"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('infraMirrorApp.statusPage.autoRefreshSeconds')}
                id="status-page-autoRefreshSeconds"
                name="autoRefreshSeconds"
                data-cy="autoRefreshSeconds"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.statusPage.monitorSelection')}
                id="status-page-monitorSelection"
                name="monitorSelection"
                data-cy="monitorSelection"
                type="textarea"
              />
              <ValidatedField
                label={translate('infraMirrorApp.statusPage.isActive')}
                id="status-page-isActive"
                name="isActive"
                data-cy="isActive"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('infraMirrorApp.statusPage.isHomePage')}
                id="status-page-isHomePage"
                name="isHomePage"
                data-cy="isHomePage"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('infraMirrorApp.statusPage.allowedRoles')}
                id="status-page-allowedRoles"
                name="allowedRoles"
                data-cy="allowedRoles"
                type="textarea"
              />
              <ValidatedField
                label={translate('infraMirrorApp.statusPage.createdAt')}
                id="status-page-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.statusPage.updatedAt')}
                id="status-page-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/status-page" replace color="info">
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

export default StatusPageUpdate;
