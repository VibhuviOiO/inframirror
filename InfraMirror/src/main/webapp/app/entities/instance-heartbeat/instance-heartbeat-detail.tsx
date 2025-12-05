import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './instance-heartbeat.reducer';

export const InstanceHeartbeatDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const instanceHeartbeatEntity = useAppSelector(state => state.instanceHeartbeat.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="instanceHeartbeatDetailsHeading">
          <Translate contentKey="infraMirrorApp.instanceHeartbeat.detail.title">InstanceHeartbeat</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="infraMirrorApp.instanceHeartbeat.id">Id</Translate>
            </span>
          </dt>
          <dd>{instanceHeartbeatEntity.id}</dd>
          <dt>
            <span id="executedAt">
              <Translate contentKey="infraMirrorApp.instanceHeartbeat.executedAt">Executed At</Translate>
            </span>
          </dt>
          <dd>
            {instanceHeartbeatEntity.executedAt ? (
              <TextFormat value={instanceHeartbeatEntity.executedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="heartbeatType">
              <Translate contentKey="infraMirrorApp.instanceHeartbeat.heartbeatType">Heartbeat Type</Translate>
            </span>
          </dt>
          <dd>{instanceHeartbeatEntity.heartbeatType}</dd>
          <dt>
            <span id="success">
              <Translate contentKey="infraMirrorApp.instanceHeartbeat.success">Success</Translate>
            </span>
          </dt>
          <dd>{instanceHeartbeatEntity.success ? 'true' : 'false'}</dd>
          <dt>
            <span id="responseTimeMs">
              <Translate contentKey="infraMirrorApp.instanceHeartbeat.responseTimeMs">Response Time Ms</Translate>
            </span>
          </dt>
          <dd>{instanceHeartbeatEntity.responseTimeMs}</dd>
          <dt>
            <span id="packetLoss">
              <Translate contentKey="infraMirrorApp.instanceHeartbeat.packetLoss">Packet Loss</Translate>
            </span>
          </dt>
          <dd>{instanceHeartbeatEntity.packetLoss}</dd>
          <dt>
            <span id="jitterMs">
              <Translate contentKey="infraMirrorApp.instanceHeartbeat.jitterMs">Jitter Ms</Translate>
            </span>
          </dt>
          <dd>{instanceHeartbeatEntity.jitterMs}</dd>
          <dt>
            <span id="cpuUsage">
              <Translate contentKey="infraMirrorApp.instanceHeartbeat.cpuUsage">Cpu Usage</Translate>
            </span>
          </dt>
          <dd>{instanceHeartbeatEntity.cpuUsage}</dd>
          <dt>
            <span id="memoryUsage">
              <Translate contentKey="infraMirrorApp.instanceHeartbeat.memoryUsage">Memory Usage</Translate>
            </span>
          </dt>
          <dd>{instanceHeartbeatEntity.memoryUsage}</dd>
          <dt>
            <span id="diskUsage">
              <Translate contentKey="infraMirrorApp.instanceHeartbeat.diskUsage">Disk Usage</Translate>
            </span>
          </dt>
          <dd>{instanceHeartbeatEntity.diskUsage}</dd>
          <dt>
            <span id="loadAverage">
              <Translate contentKey="infraMirrorApp.instanceHeartbeat.loadAverage">Load Average</Translate>
            </span>
          </dt>
          <dd>{instanceHeartbeatEntity.loadAverage}</dd>
          <dt>
            <span id="processCount">
              <Translate contentKey="infraMirrorApp.instanceHeartbeat.processCount">Process Count</Translate>
            </span>
          </dt>
          <dd>{instanceHeartbeatEntity.processCount}</dd>
          <dt>
            <span id="networkRxBytes">
              <Translate contentKey="infraMirrorApp.instanceHeartbeat.networkRxBytes">Network Rx Bytes</Translate>
            </span>
          </dt>
          <dd>{instanceHeartbeatEntity.networkRxBytes}</dd>
          <dt>
            <span id="networkTxBytes">
              <Translate contentKey="infraMirrorApp.instanceHeartbeat.networkTxBytes">Network Tx Bytes</Translate>
            </span>
          </dt>
          <dd>{instanceHeartbeatEntity.networkTxBytes}</dd>
          <dt>
            <span id="uptimeSeconds">
              <Translate contentKey="infraMirrorApp.instanceHeartbeat.uptimeSeconds">Uptime Seconds</Translate>
            </span>
          </dt>
          <dd>{instanceHeartbeatEntity.uptimeSeconds}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="infraMirrorApp.instanceHeartbeat.status">Status</Translate>
            </span>
          </dt>
          <dd>{instanceHeartbeatEntity.status}</dd>
          <dt>
            <span id="errorMessage">
              <Translate contentKey="infraMirrorApp.instanceHeartbeat.errorMessage">Error Message</Translate>
            </span>
          </dt>
          <dd>{instanceHeartbeatEntity.errorMessage}</dd>
          <dt>
            <span id="errorType">
              <Translate contentKey="infraMirrorApp.instanceHeartbeat.errorType">Error Type</Translate>
            </span>
          </dt>
          <dd>{instanceHeartbeatEntity.errorType}</dd>
          <dt>
            <span id="metadata">
              <Translate contentKey="infraMirrorApp.instanceHeartbeat.metadata">Metadata</Translate>
            </span>
          </dt>
          <dd>{instanceHeartbeatEntity.metadata}</dd>
          <dt>
            <Translate contentKey="infraMirrorApp.instanceHeartbeat.agent">Agent</Translate>
          </dt>
          <dd>{instanceHeartbeatEntity.agent ? instanceHeartbeatEntity.agent.id : ''}</dd>
          <dt>
            <Translate contentKey="infraMirrorApp.instanceHeartbeat.instance">Instance</Translate>
          </dt>
          <dd>{instanceHeartbeatEntity.instance ? instanceHeartbeatEntity.instance.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/instance-heartbeat" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/instance-heartbeat/${instanceHeartbeatEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default InstanceHeartbeatDetail;
