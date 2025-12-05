import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { createEntity, getEntity, reset, updateEntity } from './branding.reducer';

export const BrandingUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const brandingEntity = useAppSelector(state => state.branding.entity);
  const loading = useAppSelector(state => state.branding.loading);
  const updating = useAppSelector(state => state.branding.updating);
  const updateSuccess = useAppSelector(state => state.branding.updateSuccess);

  const handleClose = () => {
    navigate(`/branding${location.search}`);
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
    if (values.logoWidth !== undefined && typeof values.logoWidth !== 'number') {
      values.logoWidth = Number(values.logoWidth);
    }
    if (values.logoHeight !== undefined && typeof values.logoHeight !== 'number') {
      values.logoHeight = Number(values.logoHeight);
    }
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...brandingEntity,
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
          ...brandingEntity,
          createdAt: convertDateTimeFromServer(brandingEntity.createdAt),
          updatedAt: convertDateTimeFromServer(brandingEntity.updatedAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="infraMirrorApp.branding.home.createOrEditLabel" data-cy="BrandingCreateUpdateHeading">
            <Translate contentKey="infraMirrorApp.branding.home.createOrEditLabel">Create or edit a Branding</Translate>
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
                  id="branding-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('infraMirrorApp.branding.title')}
                id="branding-title"
                name="title"
                data-cy="title"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 200, message: translate('entity.validation.maxlength', { max: 200 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.branding.description')}
                id="branding-description"
                name="description"
                data-cy="description"
                type="text"
                validate={{
                  maxLength: { value: 500, message: translate('entity.validation.maxlength', { max: 500 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.branding.keywords')}
                id="branding-keywords"
                name="keywords"
                data-cy="keywords"
                type="text"
                validate={{
                  maxLength: { value: 300, message: translate('entity.validation.maxlength', { max: 300 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.branding.author')}
                id="branding-author"
                name="author"
                data-cy="author"
                type="text"
                validate={{
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.branding.faviconPath')}
                id="branding-faviconPath"
                name="faviconPath"
                data-cy="faviconPath"
                type="text"
                validate={{
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.branding.logoPath')}
                id="branding-logoPath"
                name="logoPath"
                data-cy="logoPath"
                type="text"
                validate={{
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.branding.logoWidth')}
                id="branding-logoWidth"
                name="logoWidth"
                data-cy="logoWidth"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.branding.logoHeight')}
                id="branding-logoHeight"
                name="logoHeight"
                data-cy="logoHeight"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.branding.footerTitle')}
                id="branding-footerTitle"
                name="footerTitle"
                data-cy="footerTitle"
                type="text"
                validate={{
                  maxLength: { value: 200, message: translate('entity.validation.maxlength', { max: 200 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.branding.isActive')}
                id="branding-isActive"
                name="isActive"
                data-cy="isActive"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('infraMirrorApp.branding.createdAt')}
                id="branding-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('infraMirrorApp.branding.updatedAt')}
                id="branding-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/branding" replace color="info">
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

export default BrandingUpdate;
