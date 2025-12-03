import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './http-heartbeat.reducer';

export const HttpHeartbeatDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const httpHeartbeatEntity = useAppSelector(state => state.httpHeartbeat.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="httpHeartbeatDetailsHeading">
          <Translate contentKey="infraMirrorApp.httpHeartbeat.detail.title">HttpHeartbeat</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.id}</dd>
          <dt>
            <span id="executedAt">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.executedAt">Executed At</Translate>
            </span>
          </dt>
          <dd>
            {httpHeartbeatEntity.executedAt ? (
              <TextFormat value={httpHeartbeatEntity.executedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="success">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.success">Success</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.success ? 'true' : 'false'}</dd>
          <dt>
            <span id="responseTimeMs">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.responseTimeMs">Response Time Ms</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.responseTimeMs}</dd>
          <dt>
            <span id="responseSizeBytes">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.responseSizeBytes">Response Size Bytes</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.responseSizeBytes}</dd>
          <dt>
            <span id="responseStatusCode">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.responseStatusCode">Response Status Code</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.responseStatusCode}</dd>
          <dt>
            <span id="responseContentType">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.responseContentType">Response Content Type</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.responseContentType}</dd>
          <dt>
            <span id="responseServer">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.responseServer">Response Server</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.responseServer}</dd>
          <dt>
            <span id="responseCacheStatus">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.responseCacheStatus">Response Cache Status</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.responseCacheStatus}</dd>
          <dt>
            <span id="dnsLookupMs">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.dnsLookupMs">Dns Lookup Ms</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.dnsLookupMs}</dd>
          <dt>
            <span id="tcpConnectMs">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.tcpConnectMs">Tcp Connect Ms</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.tcpConnectMs}</dd>
          <dt>
            <span id="tlsHandshakeMs">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.tlsHandshakeMs">Tls Handshake Ms</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.tlsHandshakeMs}</dd>
          <dt>
            <span id="timeToFirstByteMs">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.timeToFirstByteMs">Time To First Byte Ms</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.timeToFirstByteMs}</dd>
          <dt>
            <span id="warningThresholdMs">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.warningThresholdMs">Warning Threshold Ms</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.warningThresholdMs}</dd>
          <dt>
            <span id="criticalThresholdMs">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.criticalThresholdMs">Critical Threshold Ms</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.criticalThresholdMs}</dd>
          <dt>
            <span id="errorType">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.errorType">Error Type</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.errorType}</dd>
          <dt>
            <span id="errorMessage">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.errorMessage">Error Message</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.errorMessage}</dd>
          <dt>
            <span id="rawRequestHeaders">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.rawRequestHeaders">Raw Request Headers</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.rawRequestHeaders}</dd>
          <dt>
            <span id="rawResponseHeaders">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.rawResponseHeaders">Raw Response Headers</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.rawResponseHeaders}</dd>
          <dt>
            <span id="rawResponseBody">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.rawResponseBody">Raw Response Body</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.rawResponseBody}</dd>
          <dt>
            <Translate contentKey="infraMirrorApp.httpHeartbeat.monitor">Monitor</Translate>
          </dt>
          <dd>{httpHeartbeatEntity.monitor ? httpHeartbeatEntity.monitor.id : ''}</dd>
          <dt>
            <Translate contentKey="infraMirrorApp.httpHeartbeat.agent">Agent</Translate>
          </dt>
          <dd>{httpHeartbeatEntity.agent ? httpHeartbeatEntity.agent.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/http-heartbeat" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/http-heartbeat/${httpHeartbeatEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default HttpHeartbeatDetail;
