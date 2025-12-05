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
              <Translate contentKey="infraMirrorApp.httpHeartbeat.id">Id</Translate>
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
            <span id="dnsResolvedIp">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.dnsResolvedIp">Dns Resolved Ip</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.dnsResolvedIp}</dd>
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
            <span id="sslCertificateValid">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.sslCertificateValid">Ssl Certificate Valid</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.sslCertificateValid ? 'true' : 'false'}</dd>
          <dt>
            <span id="sslCertificateExpiry">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.sslCertificateExpiry">Ssl Certificate Expiry</Translate>
            </span>
          </dt>
          <dd>
            {httpHeartbeatEntity.sslCertificateExpiry ? (
              <TextFormat value={httpHeartbeatEntity.sslCertificateExpiry} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="sslCertificateIssuer">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.sslCertificateIssuer">Ssl Certificate Issuer</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.sslCertificateIssuer}</dd>
          <dt>
            <span id="sslDaysUntilExpiry">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.sslDaysUntilExpiry">Ssl Days Until Expiry</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.sslDaysUntilExpiry}</dd>
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
            <span id="dnsDetails">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.dnsDetails">Dns Details</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.dnsDetails}</dd>
          <dt>
            <span id="tlsDetails">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.tlsDetails">Tls Details</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.tlsDetails}</dd>
          <dt>
            <span id="httpVersion">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.httpVersion">Http Version</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.httpVersion}</dd>
          <dt>
            <span id="contentEncoding">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.contentEncoding">Content Encoding</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.contentEncoding}</dd>
          <dt>
            <span id="compressionRatio">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.compressionRatio">Compression Ratio</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.compressionRatio}</dd>
          <dt>
            <span id="transferEncoding">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.transferEncoding">Transfer Encoding</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.transferEncoding}</dd>
          <dt>
            <span id="responseBodyHash">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.responseBodyHash">Response Body Hash</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.responseBodyHash}</dd>
          <dt>
            <span id="responseBodySample">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.responseBodySample">Response Body Sample</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.responseBodySample}</dd>
          <dt>
            <span id="responseBodyValid">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.responseBodyValid">Response Body Valid</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.responseBodyValid ? 'true' : 'false'}</dd>
          <dt>
            <span id="responseBodyUncompressedBytes">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.responseBodyUncompressedBytes">
                Response Body Uncompressed Bytes
              </Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.responseBodyUncompressedBytes}</dd>
          <dt>
            <span id="redirectDetails">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.redirectDetails">Redirect Details</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.redirectDetails}</dd>
          <dt>
            <span id="cacheControl">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.cacheControl">Cache Control</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.cacheControl}</dd>
          <dt>
            <span id="etag">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.etag">Etag</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.etag}</dd>
          <dt>
            <span id="cacheAge">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.cacheAge">Cache Age</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.cacheAge}</dd>
          <dt>
            <span id="cdnProvider">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.cdnProvider">Cdn Provider</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.cdnProvider}</dd>
          <dt>
            <span id="cdnPop">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.cdnPop">Cdn Pop</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.cdnPop}</dd>
          <dt>
            <span id="rateLimitDetails">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.rateLimitDetails">Rate Limit Details</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.rateLimitDetails}</dd>
          <dt>
            <span id="networkPath">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.networkPath">Network Path</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.networkPath}</dd>
          <dt>
            <span id="agentMetrics">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.agentMetrics">Agent Metrics</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.agentMetrics}</dd>
          <dt>
            <span id="phaseLatencies">
              <Translate contentKey="infraMirrorApp.httpHeartbeat.phaseLatencies">Phase Latencies</Translate>
            </span>
          </dt>
          <dd>{httpHeartbeatEntity.phaseLatencies}</dd>
          <dt>
            <Translate contentKey="infraMirrorApp.httpHeartbeat.agent">Agent</Translate>
          </dt>
          <dd>{httpHeartbeatEntity.agent ? httpHeartbeatEntity.agent.id : ''}</dd>
          <dt>
            <Translate contentKey="infraMirrorApp.httpHeartbeat.monitor">Monitor</Translate>
          </dt>
          <dd>{httpHeartbeatEntity.monitor ? httpHeartbeatEntity.monitor.id : ''}</dd>
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
