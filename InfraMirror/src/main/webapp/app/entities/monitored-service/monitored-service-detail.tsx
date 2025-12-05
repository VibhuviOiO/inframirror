import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './monitored-service.reducer';

export const MonitoredServiceDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const monitoredServiceEntity = useAppSelector(state => state.monitoredService.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="monitoredServiceDetailsHeading">
          <Translate contentKey="infraMirrorApp.monitoredService.detail.title">MonitoredService</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="infraMirrorApp.monitoredService.id">Id</Translate>
            </span>
          </dt>
          <dd>{monitoredServiceEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="infraMirrorApp.monitoredService.name">Name</Translate>
            </span>
          </dt>
          <dd>{monitoredServiceEntity.name}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="infraMirrorApp.monitoredService.description">Description</Translate>
            </span>
          </dt>
          <dd>{monitoredServiceEntity.description}</dd>
          <dt>
            <span id="serviceType">
              <Translate contentKey="infraMirrorApp.monitoredService.serviceType">Service Type</Translate>
            </span>
          </dt>
          <dd>{monitoredServiceEntity.serviceType}</dd>
          <dt>
            <span id="environment">
              <Translate contentKey="infraMirrorApp.monitoredService.environment">Environment</Translate>
            </span>
          </dt>
          <dd>{monitoredServiceEntity.environment}</dd>
          <dt>
            <span id="monitoringEnabled">
              <Translate contentKey="infraMirrorApp.monitoredService.monitoringEnabled">Monitoring Enabled</Translate>
            </span>
          </dt>
          <dd>{monitoredServiceEntity.monitoringEnabled ? 'true' : 'false'}</dd>
          <dt>
            <span id="clusterMonitoringEnabled">
              <Translate contentKey="infraMirrorApp.monitoredService.clusterMonitoringEnabled">Cluster Monitoring Enabled</Translate>
            </span>
          </dt>
          <dd>{monitoredServiceEntity.clusterMonitoringEnabled ? 'true' : 'false'}</dd>
          <dt>
            <span id="intervalSeconds">
              <Translate contentKey="infraMirrorApp.monitoredService.intervalSeconds">Interval Seconds</Translate>
            </span>
          </dt>
          <dd>{monitoredServiceEntity.intervalSeconds}</dd>
          <dt>
            <span id="timeoutMs">
              <Translate contentKey="infraMirrorApp.monitoredService.timeoutMs">Timeout Ms</Translate>
            </span>
          </dt>
          <dd>{monitoredServiceEntity.timeoutMs}</dd>
          <dt>
            <span id="retryCount">
              <Translate contentKey="infraMirrorApp.monitoredService.retryCount">Retry Count</Translate>
            </span>
          </dt>
          <dd>{monitoredServiceEntity.retryCount}</dd>
          <dt>
            <span id="latencyWarningMs">
              <Translate contentKey="infraMirrorApp.monitoredService.latencyWarningMs">Latency Warning Ms</Translate>
            </span>
          </dt>
          <dd>{monitoredServiceEntity.latencyWarningMs}</dd>
          <dt>
            <span id="latencyCriticalMs">
              <Translate contentKey="infraMirrorApp.monitoredService.latencyCriticalMs">Latency Critical Ms</Translate>
            </span>
          </dt>
          <dd>{monitoredServiceEntity.latencyCriticalMs}</dd>
          <dt>
            <span id="advancedConfig">
              <Translate contentKey="infraMirrorApp.monitoredService.advancedConfig">Advanced Config</Translate>
            </span>
          </dt>
          <dd>{monitoredServiceEntity.advancedConfig}</dd>
          <dt>
            <span id="isActive">
              <Translate contentKey="infraMirrorApp.monitoredService.isActive">Is Active</Translate>
            </span>
          </dt>
          <dd>{monitoredServiceEntity.isActive ? 'true' : 'false'}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="infraMirrorApp.monitoredService.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {monitoredServiceEntity.createdAt ? (
              <TextFormat value={monitoredServiceEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="infraMirrorApp.monitoredService.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>
            {monitoredServiceEntity.updatedAt ? (
              <TextFormat value={monitoredServiceEntity.updatedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="infraMirrorApp.monitoredService.datacenter">Datacenter</Translate>
          </dt>
          <dd>{monitoredServiceEntity.datacenter ? monitoredServiceEntity.datacenter.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/monitored-service" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/monitored-service/${monitoredServiceEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default MonitoredServiceDetail;
