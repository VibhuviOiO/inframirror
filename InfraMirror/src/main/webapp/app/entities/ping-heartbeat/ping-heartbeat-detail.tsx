import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './ping-heartbeat.reducer';

export const PingHeartbeatDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const pingHeartbeatEntity = useAppSelector(state => state.pingHeartbeat.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="pingHeartbeatDetailsHeading">
          <Translate contentKey="infraMirrorApp.pingHeartbeat.detail.title">PingHeartbeat</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{pingHeartbeatEntity.id}</dd>
          <dt>
            <span id="executedAt">
              <Translate contentKey="infraMirrorApp.pingHeartbeat.executedAt">Executed At</Translate>
            </span>
          </dt>
          <dd>
            {pingHeartbeatEntity.executedAt ? (
              <TextFormat value={pingHeartbeatEntity.executedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="heartbeatType">
              <Translate contentKey="infraMirrorApp.pingHeartbeat.heartbeatType">Heartbeat Type</Translate>
            </span>
          </dt>
          <dd>{pingHeartbeatEntity.heartbeatType}</dd>
          <dt>
            <span id="success">
              <Translate contentKey="infraMirrorApp.pingHeartbeat.success">Success</Translate>
            </span>
          </dt>
          <dd>{pingHeartbeatEntity.success ? 'true' : 'false'}</dd>
          <dt>
            <span id="responseTimeMs">
              <Translate contentKey="infraMirrorApp.pingHeartbeat.responseTimeMs">Response Time Ms</Translate>
            </span>
          </dt>
          <dd>{pingHeartbeatEntity.responseTimeMs}</dd>
          <dt>
            <span id="packetLoss">
              <Translate contentKey="infraMirrorApp.pingHeartbeat.packetLoss">Packet Loss</Translate>
            </span>
          </dt>
          <dd>{pingHeartbeatEntity.packetLoss}</dd>
          <dt>
            <span id="jitterMs">
              <Translate contentKey="infraMirrorApp.pingHeartbeat.jitterMs">Jitter Ms</Translate>
            </span>
          </dt>
          <dd>{pingHeartbeatEntity.jitterMs}</dd>
          <dt>
            <span id="cpuUsage">
              <Translate contentKey="infraMirrorApp.pingHeartbeat.cpuUsage">Cpu Usage</Translate>
            </span>
          </dt>
          <dd>{pingHeartbeatEntity.cpuUsage}</dd>
          <dt>
            <span id="memoryUsage">
              <Translate contentKey="infraMirrorApp.pingHeartbeat.memoryUsage">Memory Usage</Translate>
            </span>
          </dt>
          <dd>{pingHeartbeatEntity.memoryUsage}</dd>
          <dt>
            <span id="diskUsage">
              <Translate contentKey="infraMirrorApp.pingHeartbeat.diskUsage">Disk Usage</Translate>
            </span>
          </dt>
          <dd>{pingHeartbeatEntity.diskUsage}</dd>
          <dt>
            <span id="loadAverage">
              <Translate contentKey="infraMirrorApp.pingHeartbeat.loadAverage">Load Average</Translate>
            </span>
          </dt>
          <dd>{pingHeartbeatEntity.loadAverage}</dd>
          <dt>
            <span id="processCount">
              <Translate contentKey="infraMirrorApp.pingHeartbeat.processCount">Process Count</Translate>
            </span>
          </dt>
          <dd>{pingHeartbeatEntity.processCount}</dd>
          <dt>
            <span id="networkRxBytes">
              <Translate contentKey="infraMirrorApp.pingHeartbeat.networkRxBytes">Network Rx Bytes</Translate>
            </span>
          </dt>
          <dd>{pingHeartbeatEntity.networkRxBytes}</dd>
          <dt>
            <span id="networkTxBytes">
              <Translate contentKey="infraMirrorApp.pingHeartbeat.networkTxBytes">Network Tx Bytes</Translate>
            </span>
          </dt>
          <dd>{pingHeartbeatEntity.networkTxBytes}</dd>
          <dt>
            <span id="uptimeSeconds">
              <Translate contentKey="infraMirrorApp.pingHeartbeat.uptimeSeconds">Uptime Seconds</Translate>
            </span>
          </dt>
          <dd>{pingHeartbeatEntity.uptimeSeconds}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="infraMirrorApp.pingHeartbeat.status">Status</Translate>
            </span>
          </dt>
          <dd>{pingHeartbeatEntity.status}</dd>
          <dt>
            <span id="errorMessage">
              <Translate contentKey="infraMirrorApp.pingHeartbeat.errorMessage">Error Message</Translate>
            </span>
          </dt>
          <dd>{pingHeartbeatEntity.errorMessage}</dd>
          <dt>
            <span id="errorType">
              <Translate contentKey="infraMirrorApp.pingHeartbeat.errorType">Error Type</Translate>
            </span>
          </dt>
          <dd>{pingHeartbeatEntity.errorType}</dd>
          <dt>
            <span id="metadata">
              <Translate contentKey="infraMirrorApp.pingHeartbeat.metadata">Metadata</Translate>
            </span>
          </dt>
          <dd>{pingHeartbeatEntity.metadata}</dd>
          <dt>
            <Translate contentKey="infraMirrorApp.pingHeartbeat.instance">Instance</Translate>
          </dt>
          <dd>{pingHeartbeatEntity.instance ? pingHeartbeatEntity.instance.id : ''}</dd>
          <dt>
            <Translate contentKey="infraMirrorApp.pingHeartbeat.agent">Agent</Translate>
          </dt>
          <dd>{pingHeartbeatEntity.agent ? pingHeartbeatEntity.agent.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/ping-heartbeat" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/ping-heartbeat/${pingHeartbeatEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default PingHeartbeatDetail;
