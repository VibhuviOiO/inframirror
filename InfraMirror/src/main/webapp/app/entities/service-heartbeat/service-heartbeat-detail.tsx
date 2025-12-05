import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './service-heartbeat.reducer';

export const ServiceHeartbeatDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const serviceHeartbeatEntity = useAppSelector(state => state.serviceHeartbeat.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="serviceHeartbeatDetailsHeading">
          <Translate contentKey="infraMirrorApp.serviceHeartbeat.detail.title">ServiceHeartbeat</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="infraMirrorApp.serviceHeartbeat.id">Id</Translate>
            </span>
          </dt>
          <dd>{serviceHeartbeatEntity.id}</dd>
          <dt>
            <span id="executedAt">
              <Translate contentKey="infraMirrorApp.serviceHeartbeat.executedAt">Executed At</Translate>
            </span>
          </dt>
          <dd>
            {serviceHeartbeatEntity.executedAt ? (
              <TextFormat value={serviceHeartbeatEntity.executedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="success">
              <Translate contentKey="infraMirrorApp.serviceHeartbeat.success">Success</Translate>
            </span>
          </dt>
          <dd>{serviceHeartbeatEntity.success ? 'true' : 'false'}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="infraMirrorApp.serviceHeartbeat.status">Status</Translate>
            </span>
          </dt>
          <dd>{serviceHeartbeatEntity.status}</dd>
          <dt>
            <span id="responseTimeMs">
              <Translate contentKey="infraMirrorApp.serviceHeartbeat.responseTimeMs">Response Time Ms</Translate>
            </span>
          </dt>
          <dd>{serviceHeartbeatEntity.responseTimeMs}</dd>
          <dt>
            <span id="errorMessage">
              <Translate contentKey="infraMirrorApp.serviceHeartbeat.errorMessage">Error Message</Translate>
            </span>
          </dt>
          <dd>{serviceHeartbeatEntity.errorMessage}</dd>
          <dt>
            <span id="metadata">
              <Translate contentKey="infraMirrorApp.serviceHeartbeat.metadata">Metadata</Translate>
            </span>
          </dt>
          <dd>{serviceHeartbeatEntity.metadata}</dd>
          <dt>
            <Translate contentKey="infraMirrorApp.serviceHeartbeat.agent">Agent</Translate>
          </dt>
          <dd>{serviceHeartbeatEntity.agent ? serviceHeartbeatEntity.agent.id : ''}</dd>
          <dt>
            <Translate contentKey="infraMirrorApp.serviceHeartbeat.monitoredService">Monitored Service</Translate>
          </dt>
          <dd>{serviceHeartbeatEntity.monitoredService ? serviceHeartbeatEntity.monitoredService.id : ''}</dd>
          <dt>
            <Translate contentKey="infraMirrorApp.serviceHeartbeat.serviceInstance">Service Instance</Translate>
          </dt>
          <dd>{serviceHeartbeatEntity.serviceInstance ? serviceHeartbeatEntity.serviceInstance.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/service-heartbeat" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/service-heartbeat/${serviceHeartbeatEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ServiceHeartbeatDetail;
