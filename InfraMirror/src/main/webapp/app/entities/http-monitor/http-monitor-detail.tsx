import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './http-monitor.reducer';

export const HttpMonitorDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const httpMonitorEntity = useAppSelector(state => state.httpMonitor.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="httpMonitorDetailsHeading">
          <Translate contentKey="infraMirrorApp.httpMonitor.detail.title">HttpMonitor</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="infraMirrorApp.httpMonitor.id">Id</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="infraMirrorApp.httpMonitor.name">Name</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.name}</dd>
          <dt>
            <span id="method">
              <Translate contentKey="infraMirrorApp.httpMonitor.method">Method</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.method}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="infraMirrorApp.httpMonitor.type">Type</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.type}</dd>
          <dt>
            <span id="url">
              <Translate contentKey="infraMirrorApp.httpMonitor.url">Url</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.url}</dd>
          <dt>
            <span id="headers">
              <Translate contentKey="infraMirrorApp.httpMonitor.headers">Headers</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.headers}</dd>
          <dt>
            <span id="body">
              <Translate contentKey="infraMirrorApp.httpMonitor.body">Body</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.body}</dd>
          <dt>
            <span id="intervalSeconds">
              <Translate contentKey="infraMirrorApp.httpMonitor.intervalSeconds">Interval Seconds</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.intervalSeconds}</dd>
          <dt>
            <span id="timeoutSeconds">
              <Translate contentKey="infraMirrorApp.httpMonitor.timeoutSeconds">Timeout Seconds</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.timeoutSeconds}</dd>
          <dt>
            <span id="retryCount">
              <Translate contentKey="infraMirrorApp.httpMonitor.retryCount">Retry Count</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.retryCount}</dd>
          <dt>
            <span id="retryDelaySeconds">
              <Translate contentKey="infraMirrorApp.httpMonitor.retryDelaySeconds">Retry Delay Seconds</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.retryDelaySeconds}</dd>
          <dt>
            <span id="responseTimeWarningMs">
              <Translate contentKey="infraMirrorApp.httpMonitor.responseTimeWarningMs">Response Time Warning Ms</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.responseTimeWarningMs}</dd>
          <dt>
            <span id="responseTimeCriticalMs">
              <Translate contentKey="infraMirrorApp.httpMonitor.responseTimeCriticalMs">Response Time Critical Ms</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.responseTimeCriticalMs}</dd>
          <dt>
            <span id="uptimeWarningPercent">
              <Translate contentKey="infraMirrorApp.httpMonitor.uptimeWarningPercent">Uptime Warning Percent</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.uptimeWarningPercent}</dd>
          <dt>
            <span id="uptimeCriticalPercent">
              <Translate contentKey="infraMirrorApp.httpMonitor.uptimeCriticalPercent">Uptime Critical Percent</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.uptimeCriticalPercent}</dd>
          <dt>
            <span id="includeResponseBody">
              <Translate contentKey="infraMirrorApp.httpMonitor.includeResponseBody">Include Response Body</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.includeResponseBody ? 'true' : 'false'}</dd>
          <dt>
            <span id="resendNotificationCount">
              <Translate contentKey="infraMirrorApp.httpMonitor.resendNotificationCount">Resend Notification Count</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.resendNotificationCount}</dd>
          <dt>
            <span id="certificateExpiryDays">
              <Translate contentKey="infraMirrorApp.httpMonitor.certificateExpiryDays">Certificate Expiry Days</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.certificateExpiryDays}</dd>
          <dt>
            <span id="ignoreTlsError">
              <Translate contentKey="infraMirrorApp.httpMonitor.ignoreTlsError">Ignore Tls Error</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.ignoreTlsError ? 'true' : 'false'}</dd>
          <dt>
            <span id="checkSslCertificate">
              <Translate contentKey="infraMirrorApp.httpMonitor.checkSslCertificate">Check Ssl Certificate</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.checkSslCertificate ? 'true' : 'false'}</dd>
          <dt>
            <span id="checkDnsResolution">
              <Translate contentKey="infraMirrorApp.httpMonitor.checkDnsResolution">Check Dns Resolution</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.checkDnsResolution ? 'true' : 'false'}</dd>
          <dt>
            <span id="upsideDownMode">
              <Translate contentKey="infraMirrorApp.httpMonitor.upsideDownMode">Upside Down Mode</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.upsideDownMode ? 'true' : 'false'}</dd>
          <dt>
            <span id="maxRedirects">
              <Translate contentKey="infraMirrorApp.httpMonitor.maxRedirects">Max Redirects</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.maxRedirects}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="infraMirrorApp.httpMonitor.description">Description</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.description}</dd>
          <dt>
            <span id="tags">
              <Translate contentKey="infraMirrorApp.httpMonitor.tags">Tags</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.tags}</dd>
          <dt>
            <span id="enabled">
              <Translate contentKey="infraMirrorApp.httpMonitor.enabled">Enabled</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.enabled ? 'true' : 'false'}</dd>
          <dt>
            <span id="expectedStatusCodes">
              <Translate contentKey="infraMirrorApp.httpMonitor.expectedStatusCodes">Expected Status Codes</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.expectedStatusCodes}</dd>
          <dt>
            <span id="performanceBudgetMs">
              <Translate contentKey="infraMirrorApp.httpMonitor.performanceBudgetMs">Performance Budget Ms</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.performanceBudgetMs}</dd>
          <dt>
            <span id="sizeBudgetKb">
              <Translate contentKey="infraMirrorApp.httpMonitor.sizeBudgetKb">Size Budget Kb</Translate>
            </span>
          </dt>
          <dd>{httpMonitorEntity.sizeBudgetKb}</dd>
          <dt>
            <Translate contentKey="infraMirrorApp.httpMonitor.parent">Parent</Translate>
          </dt>
          <dd>{httpMonitorEntity.parent ? httpMonitorEntity.parent.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/http-monitor" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/http-monitor/${httpMonitorEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default HttpMonitorDetail;
