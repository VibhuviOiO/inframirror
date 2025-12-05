import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getAgents } from 'app/entities/agent/agent.reducer';
import { getEntities as getHttpMonitors } from 'app/entities/http-monitor/http-monitor.reducer';
import { createEntity, getEntity, reset, updateEntity } from './http-heartbeat.reducer';

export const HttpHeartbeatUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const agents = useAppSelector(state => state.agent.entities);
  const httpMonitors = useAppSelector(state => state.httpMonitor.entities);
  const httpHeartbeatEntity = useAppSelector(state => state.httpHeartbeat.entity);
  const loading = useAppSelector(state => state.httpHeartbeat.loading);
  const updating = useAppSelector(state => state.httpHeartbeat.updating);
  const updateSuccess = useAppSelector(state => state.httpHeartbeat.updateSuccess);

  const handleClose = () => {
    navigate(`/http-heartbeat${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getAgents({}));
    dispatch(getHttpMonitors({}));
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
    values.executedAt = convertDateTimeToServer(values.executedAt);
    if (values.responseTimeMs !== undefined && typeof values.responseTimeMs !== 'number') {
      values.responseTimeMs = Number(values.responseTimeMs);
    }
    if (values.responseSizeBytes !== undefined && typeof values.responseSizeBytes !== 'number') {
      values.responseSizeBytes = Number(values.responseSizeBytes);
    }
    if (values.responseStatusCode !== undefined && typeof values.responseStatusCode !== 'number') {
      values.responseStatusCode = Number(values.responseStatusCode);
    }
    if (values.dnsLookupMs !== undefined && typeof values.dnsLookupMs !== 'number') {
      values.dnsLookupMs = Number(values.dnsLookupMs);
    }
    if (values.tcpConnectMs !== undefined && typeof values.tcpConnectMs !== 'number') {
      values.tcpConnectMs = Number(values.tcpConnectMs);
    }
    if (values.tlsHandshakeMs !== undefined && typeof values.tlsHandshakeMs !== 'number') {
      values.tlsHandshakeMs = Number(values.tlsHandshakeMs);
    }
    values.sslCertificateExpiry = convertDateTimeToServer(values.sslCertificateExpiry);
    if (values.sslDaysUntilExpiry !== undefined && typeof values.sslDaysUntilExpiry !== 'number') {
      values.sslDaysUntilExpiry = Number(values.sslDaysUntilExpiry);
    }
    if (values.timeToFirstByteMs !== undefined && typeof values.timeToFirstByteMs !== 'number') {
      values.timeToFirstByteMs = Number(values.timeToFirstByteMs);
    }
    if (values.warningThresholdMs !== undefined && typeof values.warningThresholdMs !== 'number') {
      values.warningThresholdMs = Number(values.warningThresholdMs);
    }
    if (values.criticalThresholdMs !== undefined && typeof values.criticalThresholdMs !== 'number') {
      values.criticalThresholdMs = Number(values.criticalThresholdMs);
    }
    if (values.compressionRatio !== undefined && typeof values.compressionRatio !== 'number') {
      values.compressionRatio = Number(values.compressionRatio);
    }
    if (values.responseBodyUncompressedBytes !== undefined && typeof values.responseBodyUncompressedBytes !== 'number') {
      values.responseBodyUncompressedBytes = Number(values.responseBodyUncompressedBytes);
    }
    if (values.cacheAge !== undefined && typeof values.cacheAge !== 'number') {
      values.cacheAge = Number(values.cacheAge);
    }

    const entity = {
      ...httpHeartbeatEntity,
      ...values,
      agent: agents.find(it => it.id.toString() === values.agent?.toString()),
      monitor: httpMonitors.find(it => it.id.toString() === values.monitor?.toString()),
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
          executedAt: displayDefaultDateTime(),
          sslCertificateExpiry: displayDefaultDateTime(),
        }
      : {
          ...httpHeartbeatEntity,
          executedAt: convertDateTimeFromServer(httpHeartbeatEntity.executedAt),
          sslCertificateExpiry: convertDateTimeFromServer(httpHeartbeatEntity.sslCertificateExpiry),
          agent: httpHeartbeatEntity?.agent?.id,
          monitor: httpHeartbeatEntity?.monitor?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="infraMirrorApp.httpHeartbeat.home.createOrEditLabel" data-cy="HttpHeartbeatCreateUpdateHeading">
            <Translate contentKey="infraMirrorApp.httpHeartbeat.home.createOrEditLabel">Create or edit a HttpHeartbeat</Translate>
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
                  id="http-heartbeat-id"
                  label={translate('infraMirrorApp.httpHeartbeat.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.executedAt')}
                id="http-heartbeat-executedAt"
                name="executedAt"
                data-cy="executedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.success')}
                id="http-heartbeat-success"
                name="success"
                data-cy="success"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.responseTimeMs')}
                id="http-heartbeat-responseTimeMs"
                name="responseTimeMs"
                data-cy="responseTimeMs"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.responseSizeBytes')}
                id="http-heartbeat-responseSizeBytes"
                name="responseSizeBytes"
                data-cy="responseSizeBytes"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.responseStatusCode')}
                id="http-heartbeat-responseStatusCode"
                name="responseStatusCode"
                data-cy="responseStatusCode"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.responseContentType')}
                id="http-heartbeat-responseContentType"
                name="responseContentType"
                data-cy="responseContentType"
                type="text"
                validate={{
                  maxLength: { value: 50, message: translate('entity.validation.maxlength', { max: 50 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.responseServer')}
                id="http-heartbeat-responseServer"
                name="responseServer"
                data-cy="responseServer"
                type="text"
                validate={{
                  maxLength: { value: 50, message: translate('entity.validation.maxlength', { max: 50 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.responseCacheStatus')}
                id="http-heartbeat-responseCacheStatus"
                name="responseCacheStatus"
                data-cy="responseCacheStatus"
                type="text"
                validate={{
                  maxLength: { value: 50, message: translate('entity.validation.maxlength', { max: 50 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.dnsLookupMs')}
                id="http-heartbeat-dnsLookupMs"
                name="dnsLookupMs"
                data-cy="dnsLookupMs"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.dnsResolvedIp')}
                id="http-heartbeat-dnsResolvedIp"
                name="dnsResolvedIp"
                data-cy="dnsResolvedIp"
                type="text"
                validate={{
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.tcpConnectMs')}
                id="http-heartbeat-tcpConnectMs"
                name="tcpConnectMs"
                data-cy="tcpConnectMs"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.tlsHandshakeMs')}
                id="http-heartbeat-tlsHandshakeMs"
                name="tlsHandshakeMs"
                data-cy="tlsHandshakeMs"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.sslCertificateValid')}
                id="http-heartbeat-sslCertificateValid"
                name="sslCertificateValid"
                data-cy="sslCertificateValid"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.sslCertificateExpiry')}
                id="http-heartbeat-sslCertificateExpiry"
                name="sslCertificateExpiry"
                data-cy="sslCertificateExpiry"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.sslCertificateIssuer')}
                id="http-heartbeat-sslCertificateIssuer"
                name="sslCertificateIssuer"
                data-cy="sslCertificateIssuer"
                type="text"
                validate={{
                  maxLength: { value: 500, message: translate('entity.validation.maxlength', { max: 500 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.sslDaysUntilExpiry')}
                id="http-heartbeat-sslDaysUntilExpiry"
                name="sslDaysUntilExpiry"
                data-cy="sslDaysUntilExpiry"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.timeToFirstByteMs')}
                id="http-heartbeat-timeToFirstByteMs"
                name="timeToFirstByteMs"
                data-cy="timeToFirstByteMs"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.warningThresholdMs')}
                id="http-heartbeat-warningThresholdMs"
                name="warningThresholdMs"
                data-cy="warningThresholdMs"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.criticalThresholdMs')}
                id="http-heartbeat-criticalThresholdMs"
                name="criticalThresholdMs"
                data-cy="criticalThresholdMs"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.errorType')}
                id="http-heartbeat-errorType"
                name="errorType"
                data-cy="errorType"
                type="text"
                validate={{
                  maxLength: { value: 50, message: translate('entity.validation.maxlength', { max: 50 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.errorMessage')}
                id="http-heartbeat-errorMessage"
                name="errorMessage"
                data-cy="errorMessage"
                type="textarea"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.rawRequestHeaders')}
                id="http-heartbeat-rawRequestHeaders"
                name="rawRequestHeaders"
                data-cy="rawRequestHeaders"
                type="textarea"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.rawResponseHeaders')}
                id="http-heartbeat-rawResponseHeaders"
                name="rawResponseHeaders"
                data-cy="rawResponseHeaders"
                type="textarea"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.rawResponseBody')}
                id="http-heartbeat-rawResponseBody"
                name="rawResponseBody"
                data-cy="rawResponseBody"
                type="textarea"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.dnsDetails')}
                id="http-heartbeat-dnsDetails"
                name="dnsDetails"
                data-cy="dnsDetails"
                type="textarea"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.tlsDetails')}
                id="http-heartbeat-tlsDetails"
                name="tlsDetails"
                data-cy="tlsDetails"
                type="textarea"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.httpVersion')}
                id="http-heartbeat-httpVersion"
                name="httpVersion"
                data-cy="httpVersion"
                type="text"
                validate={{
                  maxLength: { value: 10, message: translate('entity.validation.maxlength', { max: 10 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.contentEncoding')}
                id="http-heartbeat-contentEncoding"
                name="contentEncoding"
                data-cy="contentEncoding"
                type="text"
                validate={{
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.compressionRatio')}
                id="http-heartbeat-compressionRatio"
                name="compressionRatio"
                data-cy="compressionRatio"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.transferEncoding')}
                id="http-heartbeat-transferEncoding"
                name="transferEncoding"
                data-cy="transferEncoding"
                type="text"
                validate={{
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.responseBodyHash')}
                id="http-heartbeat-responseBodyHash"
                name="responseBodyHash"
                data-cy="responseBodyHash"
                type="text"
                validate={{
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.responseBodySample')}
                id="http-heartbeat-responseBodySample"
                name="responseBodySample"
                data-cy="responseBodySample"
                type="textarea"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.responseBodyValid')}
                id="http-heartbeat-responseBodyValid"
                name="responseBodyValid"
                data-cy="responseBodyValid"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.responseBodyUncompressedBytes')}
                id="http-heartbeat-responseBodyUncompressedBytes"
                name="responseBodyUncompressedBytes"
                data-cy="responseBodyUncompressedBytes"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.redirectDetails')}
                id="http-heartbeat-redirectDetails"
                name="redirectDetails"
                data-cy="redirectDetails"
                type="textarea"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.cacheControl')}
                id="http-heartbeat-cacheControl"
                name="cacheControl"
                data-cy="cacheControl"
                type="text"
                validate={{
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.etag')}
                id="http-heartbeat-etag"
                name="etag"
                data-cy="etag"
                type="text"
                validate={{
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.cacheAge')}
                id="http-heartbeat-cacheAge"
                name="cacheAge"
                data-cy="cacheAge"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.cdnProvider')}
                id="http-heartbeat-cdnProvider"
                name="cdnProvider"
                data-cy="cdnProvider"
                type="text"
                validate={{
                  maxLength: { value: 50, message: translate('entity.validation.maxlength', { max: 50 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.cdnPop')}
                id="http-heartbeat-cdnPop"
                name="cdnPop"
                data-cy="cdnPop"
                type="text"
                validate={{
                  maxLength: { value: 10, message: translate('entity.validation.maxlength', { max: 10 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.rateLimitDetails')}
                id="http-heartbeat-rateLimitDetails"
                name="rateLimitDetails"
                data-cy="rateLimitDetails"
                type="textarea"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.networkPath')}
                id="http-heartbeat-networkPath"
                name="networkPath"
                data-cy="networkPath"
                type="textarea"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.agentMetrics')}
                id="http-heartbeat-agentMetrics"
                name="agentMetrics"
                data-cy="agentMetrics"
                type="textarea"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpHeartbeat.phaseLatencies')}
                id="http-heartbeat-phaseLatencies"
                name="phaseLatencies"
                data-cy="phaseLatencies"
                type="textarea"
              />
              <ValidatedField
                id="http-heartbeat-agent"
                name="agent"
                data-cy="agent"
                label={translate('infraMirrorApp.httpHeartbeat.agent')}
                type="select"
              >
                <option value="" key="0" />
                {agents
                  ? agents.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="http-heartbeat-monitor"
                name="monitor"
                data-cy="monitor"
                label={translate('infraMirrorApp.httpHeartbeat.monitor')}
                type="select"
              >
                <option value="" key="0" />
                {httpMonitors
                  ? httpMonitors.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/http-heartbeat" replace color="info">
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

export default HttpHeartbeatUpdate;
