import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './service.reducer';

export const ServiceDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const serviceEntity = useAppSelector(state => state.service.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="serviceDetailsHeading">
          <Translate contentKey="infraMirrorApp.service.detail.title">Service</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="infraMirrorApp.service.id">Id</Translate>
            </span>
          </dt>
          <dd>{serviceEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="infraMirrorApp.service.name">Name</Translate>
            </span>
          </dt>
          <dd>{serviceEntity.name}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="infraMirrorApp.service.description">Description</Translate>
            </span>
          </dt>
          <dd>{serviceEntity.description}</dd>
          <dt>
            <span id="serviceType">
              <Translate contentKey="infraMirrorApp.service.serviceType">Service Type</Translate>
            </span>
          </dt>
          <dd>{serviceEntity.serviceType}</dd>
          <dt>
            <span id="environment">
              <Translate contentKey="infraMirrorApp.service.environment">Environment</Translate>
            </span>
          </dt>
          <dd>{serviceEntity.environment}</dd>
          <dt>
            <span id="monitoringEnabled">
              <Translate contentKey="infraMirrorApp.service.monitoringEnabled">Monitoring Enabled</Translate>
            </span>
          </dt>
          <dd>{serviceEntity.monitoringEnabled ? 'true' : 'false'}</dd>
          <dt>
            <span id="clusterMonitoringEnabled">
              <Translate contentKey="infraMirrorApp.service.clusterMonitoringEnabled">Cluster Monitoring Enabled</Translate>
            </span>
          </dt>
          <dd>{serviceEntity.clusterMonitoringEnabled ? 'true' : 'false'}</dd>
          <dt>
            <span id="intervalSeconds">
              <Translate contentKey="infraMirrorApp.service.intervalSeconds">Interval Seconds</Translate>
            </span>
          </dt>
          <dd>{serviceEntity.intervalSeconds}</dd>
          <dt>
            <span id="timeoutMs">
              <Translate contentKey="infraMirrorApp.service.timeoutMs">Timeout Ms</Translate>
            </span>
          </dt>
          <dd>{serviceEntity.timeoutMs}</dd>
          <dt>
            <span id="retryCount">
              <Translate contentKey="infraMirrorApp.service.retryCount">Retry Count</Translate>
            </span>
          </dt>
          <dd>{serviceEntity.retryCount}</dd>
          <dt>
            <span id="latencyWarningMs">
              <Translate contentKey="infraMirrorApp.service.latencyWarningMs">Latency Warning Ms</Translate>
            </span>
          </dt>
          <dd>{serviceEntity.latencyWarningMs}</dd>
          <dt>
            <span id="latencyCriticalMs">
              <Translate contentKey="infraMirrorApp.service.latencyCriticalMs">Latency Critical Ms</Translate>
            </span>
          </dt>
          <dd>{serviceEntity.latencyCriticalMs}</dd>
          <dt>
            <span id="advancedConfig">
              <Translate contentKey="infraMirrorApp.service.advancedConfig">Advanced Config</Translate>
            </span>
          </dt>
          <dd>{serviceEntity.advancedConfig}</dd>
          <dt>
            <span id="isActive">
              <Translate contentKey="infraMirrorApp.service.isActive">Is Active</Translate>
            </span>
          </dt>
          <dd>{serviceEntity.isActive ? 'true' : 'false'}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="infraMirrorApp.service.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{serviceEntity.createdAt ? <TextFormat value={serviceEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="infraMirrorApp.service.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>{serviceEntity.updatedAt ? <TextFormat value={serviceEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <Translate contentKey="infraMirrorApp.service.datacenter">Datacenter</Translate>
          </dt>
          <dd>{serviceEntity.datacenter ? serviceEntity.datacenter.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/service" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/service/${serviceEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ServiceDetail;
