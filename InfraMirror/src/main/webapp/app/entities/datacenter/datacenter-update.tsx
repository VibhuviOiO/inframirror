import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Card, CardBody, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getRegions } from 'app/entities/region/region.reducer';
import { createEntity, getEntity, reset, updateEntity } from './datacenter.reducer';

export const DatacenterUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const regions = useAppSelector(state => state.region.entities);
  const datacenterEntity = useAppSelector(state => state.datacenter.entity);
  const loading = useAppSelector(state => state.datacenter.loading);
  const updating = useAppSelector(state => state.datacenter.updating);
  const updateSuccess = useAppSelector(state => state.datacenter.updateSuccess);

  const handleClose = () => {
    navigate(`/datacenter${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getRegions({}));
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
      ...datacenterEntity,
      ...values,
      region: regions.find(it => it.id.toString() === values.region?.toString()),
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
          ...datacenterEntity,
          region: datacenterEntity?.region?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h4>
            <Translate contentKey="infraMirrorApp.datacenter.home.createOrEditLabel">Create or edit a Datacenter</Translate>
          </h4>
          <hr />
          <Card>
            <CardBody>
              {loading ? (
                <p>Loading...</p>
              ) : (
                <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
                  <ValidatedField
                    label={translate('infraMirrorApp.datacenter.code')}
                    id="datacenter-code"
                    name="code"
                    data-cy="code"
                    type="text"
                    validate={{
                      required: { value: true, message: translate('entity.validation.required') },
                      maxLength: { value: 10, message: translate('entity.validation.maxlength', { max: 10 }) },
                    }}
                  />
                  <ValidatedField
                    label={translate('infraMirrorApp.datacenter.name')}
                    id="datacenter-name"
                    name="name"
                    data-cy="name"
                    type="text"
                    validate={{
                      required: { value: true, message: translate('entity.validation.required') },
                      maxLength: { value: 50, message: translate('entity.validation.maxlength', { max: 50 }) },
                    }}
                  />
                  <ValidatedField
                    id="datacenter-region"
                    name="region"
                    data-cy="region"
                    label={translate('infraMirrorApp.datacenter.region')}
                    type="select"
                  >
                    <option value="" key="0" />
                    {regions
                      ? regions.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.name}
                          </option>
                        ))
                      : null}
                  </ValidatedField>
                  <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/datacenter" replace color="info">
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

export default DatacenterUpdate;
