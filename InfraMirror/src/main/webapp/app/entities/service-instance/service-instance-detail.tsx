import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './service-instance.reducer';

export const ServiceInstanceDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const serviceInstanceEntity = useAppSelector(state => state.serviceInstance.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="serviceInstanceDetailsHeading">
          <Translate contentKey="infraMirrorApp.serviceInstance.detail.title">ServiceInstance</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="infraMirrorApp.serviceInstance.id">Id</Translate>
            </span>
          </dt>
          <dd>{serviceInstanceEntity.id}</dd>
          <dt>
            <span id="port">
              <Translate contentKey="infraMirrorApp.serviceInstance.port">Port</Translate>
            </span>
          </dt>
          <dd>{serviceInstanceEntity.port}</dd>
          <dt>
            <span id="isActive">
              <Translate contentKey="infraMirrorApp.serviceInstance.isActive">Is Active</Translate>
            </span>
          </dt>
          <dd>{serviceInstanceEntity.isActive ? 'true' : 'false'}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="infraMirrorApp.serviceInstance.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {serviceInstanceEntity.createdAt ? (
              <TextFormat value={serviceInstanceEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="infraMirrorApp.serviceInstance.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>
            {serviceInstanceEntity.updatedAt ? (
              <TextFormat value={serviceInstanceEntity.updatedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="infraMirrorApp.serviceInstance.instance">Instance</Translate>
          </dt>
          <dd>{serviceInstanceEntity.instance ? serviceInstanceEntity.instance.id : ''}</dd>
          <dt>
            <Translate contentKey="infraMirrorApp.serviceInstance.service">Service</Translate>
          </dt>
          <dd>{serviceInstanceEntity.service ? serviceInstanceEntity.service.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/service-instance" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/service-instance/${serviceInstanceEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ServiceInstanceDetail;
