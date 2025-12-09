import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Card, CardBody, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { createEntity, getEntity, reset, updateEntity } from './region.reducer';

export const RegionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const regionEntity = useAppSelector(state => state.region.entity);
  const loading = useAppSelector(state => state.region.loading);
  const updating = useAppSelector(state => state.region.updating);
  const updateSuccess = useAppSelector(state => state.region.updateSuccess);

  const handleClose = () => {
    navigate(`/region${location.search}`);
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

    const entity = {
      ...regionEntity,
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
          ...regionEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h4>
            <Translate contentKey="infraMirrorApp.region.home.createOrEditLabel">Create or edit a Region</Translate>
          </h4>
          <hr />
          <Card>
            <CardBody>
              {loading ? (
                <p>Loading...</p>
              ) : (
                <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
                  <ValidatedField
                    label={translate('infraMirrorApp.region.name')}
                    id="region-name"
                    name="name"
                    data-cy="name"
                    type="text"
                    validate={{
                      required: { value: true, message: translate('entity.validation.required') },
                      maxLength: { value: 50, message: translate('entity.validation.maxlength', { max: 50 }) },
                    }}
                  />
                  <ValidatedField
                    label={translate('infraMirrorApp.region.regionCode')}
                    id="region-regionCode"
                    name="regionCode"
                    data-cy="regionCode"
                    type="text"
                    validate={{
                      maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                    }}
                  />
                  <ValidatedField
                    label={translate('infraMirrorApp.region.groupName')}
                    id="region-groupName"
                    name="groupName"
                    data-cy="groupName"
                    type="text"
                    validate={{
                      maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                    }}
                  />
                  <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/region" replace color="info">
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
            </CardBody>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default RegionUpdate;
