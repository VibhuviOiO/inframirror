import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getInstances } from 'app/entities/instance/instance.reducer';
import { getEntities as getServices } from 'app/entities/service/service.reducer';
import { createEntity, getEntity, reset, updateEntity } from './service-instance.reducer';

export const ServiceInstanceUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const instances = useAppSelector(state => state.instance.entities);
  const services = useAppSelector(state => state.service.entities);
  const serviceInstanceEntity = useAppSelector(state => state.serviceInstance.entity);
  const loading = useAppSelector(state => state.serviceInstance.loading);
  const updating = useAppSelector(state => state.serviceInstance.updating);
  const updateSuccess = useAppSelector(state => state.serviceInstance.updateSuccess);

  const handleClose = () => {
    navigate(`/service-instance${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getInstances({}));
    dispatch(getServices({}));
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
    if (values.port !== undefined && typeof values.port !== 'number') {
      values.port = Number(values.port);
    }
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...serviceInstanceEntity,
      ...values,
      instance: instances.find(it => it.id.toString() === values.instance?.toString()),
      service: services.find(it => it.id.toString() === values.service?.toString()),
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
          ...serviceInstanceEntity,
          createdAt: convertDateTimeFromServer(serviceInstanceEntity.createdAt),
          updatedAt: convertDateTimeFromServer(serviceInstanceEntity.updatedAt),
          instance: serviceInstanceEntity?.instance?.id,
          service: serviceInstanceEntity?.service?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="infraMirrorApp.serviceInstance.home.createOrEditLabel" data-cy="ServiceInstanceCreateUpdateHeading">
            <Translate contentKey="infraMirrorApp.serviceInstance.home.createOrEditLabel">Create or edit a ServiceInstance</Translate>
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
                  id="service-instance-id"
                  label={translate('infraMirrorApp.serviceInstance.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('infraMirrorApp.serviceInstance.port')}
                id="service-instance-port"
                name="port"
                data-cy="port"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.serviceInstance.isActive')}
                id="service-instance-isActive"
                name="isActive"
                data-cy="isActive"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('infraMirrorApp.serviceInstance.createdAt')}
                id="service-instance-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('infraMirrorApp.serviceInstance.updatedAt')}
                id="service-instance-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="service-instance-instance"
                name="instance"
                data-cy="instance"
                label={translate('infraMirrorApp.serviceInstance.instance')}
                type="select"
                required
              >
                <option value="" key="0" />
                {instances
                  ? instances.map(otherEntity => (
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
                id="service-instance-service"
                name="service"
                data-cy="service"
                label={translate('infraMirrorApp.serviceInstance.service')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/service-instance" replace color="info">
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

export default ServiceInstanceUpdate;
