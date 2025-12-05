import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getHttpMonitors } from 'app/entities/http-monitor/http-monitor.reducer';
import { createEntity, getEntity, reset, updateEntity } from './http-monitor.reducer';

export const HttpMonitorUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const httpMonitors = useAppSelector(state => state.httpMonitor.entities);
  const httpMonitorEntity = useAppSelector(state => state.httpMonitor.entity);
  const loading = useAppSelector(state => state.httpMonitor.loading);
  const updating = useAppSelector(state => state.httpMonitor.updating);
  const updateSuccess = useAppSelector(state => state.httpMonitor.updateSuccess);

  const handleClose = () => {
    navigate(`/http-monitor${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

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
    if (values.intervalSeconds !== undefined && typeof values.intervalSeconds !== 'number') {
      values.intervalSeconds = Number(values.intervalSeconds);
    }
    if (values.timeoutSeconds !== undefined && typeof values.timeoutSeconds !== 'number') {
      values.timeoutSeconds = Number(values.timeoutSeconds);
    }
    if (values.retryCount !== undefined && typeof values.retryCount !== 'number') {
      values.retryCount = Number(values.retryCount);
    }
    if (values.retryDelaySeconds !== undefined && typeof values.retryDelaySeconds !== 'number') {
      values.retryDelaySeconds = Number(values.retryDelaySeconds);
    }
    if (values.responseTimeWarningMs !== undefined && typeof values.responseTimeWarningMs !== 'number') {
      values.responseTimeWarningMs = Number(values.responseTimeWarningMs);
    }
    if (values.responseTimeCriticalMs !== undefined && typeof values.responseTimeCriticalMs !== 'number') {
      values.responseTimeCriticalMs = Number(values.responseTimeCriticalMs);
    }
    if (values.uptimeWarningPercent !== undefined && typeof values.uptimeWarningPercent !== 'number') {
      values.uptimeWarningPercent = Number(values.uptimeWarningPercent);
    }
    if (values.uptimeCriticalPercent !== undefined && typeof values.uptimeCriticalPercent !== 'number') {
      values.uptimeCriticalPercent = Number(values.uptimeCriticalPercent);
    }
    if (values.resendNotificationCount !== undefined && typeof values.resendNotificationCount !== 'number') {
      values.resendNotificationCount = Number(values.resendNotificationCount);
    }
    if (values.certificateExpiryDays !== undefined && typeof values.certificateExpiryDays !== 'number') {
      values.certificateExpiryDays = Number(values.certificateExpiryDays);
    }
    if (values.maxRedirects !== undefined && typeof values.maxRedirects !== 'number') {
      values.maxRedirects = Number(values.maxRedirects);
    }
    if (values.performanceBudgetMs !== undefined && typeof values.performanceBudgetMs !== 'number') {
      values.performanceBudgetMs = Number(values.performanceBudgetMs);
    }
    if (values.sizeBudgetKb !== undefined && typeof values.sizeBudgetKb !== 'number') {
      values.sizeBudgetKb = Number(values.sizeBudgetKb);
    }

    const entity = {
      ...httpMonitorEntity,
      ...values,
      parent: httpMonitors.find(it => it.id.toString() === values.parent?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...httpMonitorEntity,
          parent: httpMonitorEntity?.parent?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="infraMirrorApp.httpMonitor.home.createOrEditLabel" data-cy="HttpMonitorCreateUpdateHeading">
            <Translate contentKey="infraMirrorApp.httpMonitor.home.createOrEditLabel">Create or edit a HttpMonitor</Translate>
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
                  id="http-monitor-id"
                  label={translate('infraMirrorApp.httpMonitor.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.name')}
                id="http-monitor-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.method')}
                id="http-monitor-method"
                name="method"
                data-cy="method"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 10, message: translate('entity.validation.maxlength', { max: 10 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.type')}
                id="http-monitor-type"
                name="type"
                data-cy="type"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 10, message: translate('entity.validation.maxlength', { max: 10 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.url')}
                id="http-monitor-url"
                name="url"
                data-cy="url"
                type="textarea"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.headers')}
                id="http-monitor-headers"
                name="headers"
                data-cy="headers"
                type="textarea"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.body')}
                id="http-monitor-body"
                name="body"
                data-cy="body"
                type="textarea"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.intervalSeconds')}
                id="http-monitor-intervalSeconds"
                name="intervalSeconds"
                data-cy="intervalSeconds"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.timeoutSeconds')}
                id="http-monitor-timeoutSeconds"
                name="timeoutSeconds"
                data-cy="timeoutSeconds"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.retryCount')}
                id="http-monitor-retryCount"
                name="retryCount"
                data-cy="retryCount"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.retryDelaySeconds')}
                id="http-monitor-retryDelaySeconds"
                name="retryDelaySeconds"
                data-cy="retryDelaySeconds"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.responseTimeWarningMs')}
                id="http-monitor-responseTimeWarningMs"
                name="responseTimeWarningMs"
                data-cy="responseTimeWarningMs"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.responseTimeCriticalMs')}
                id="http-monitor-responseTimeCriticalMs"
                name="responseTimeCriticalMs"
                data-cy="responseTimeCriticalMs"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.uptimeWarningPercent')}
                id="http-monitor-uptimeWarningPercent"
                name="uptimeWarningPercent"
                data-cy="uptimeWarningPercent"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.uptimeCriticalPercent')}
                id="http-monitor-uptimeCriticalPercent"
                name="uptimeCriticalPercent"
                data-cy="uptimeCriticalPercent"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.includeResponseBody')}
                id="http-monitor-includeResponseBody"
                name="includeResponseBody"
                data-cy="includeResponseBody"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.resendNotificationCount')}
                id="http-monitor-resendNotificationCount"
                name="resendNotificationCount"
                data-cy="resendNotificationCount"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.certificateExpiryDays')}
                id="http-monitor-certificateExpiryDays"
                name="certificateExpiryDays"
                data-cy="certificateExpiryDays"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.ignoreTlsError')}
                id="http-monitor-ignoreTlsError"
                name="ignoreTlsError"
                data-cy="ignoreTlsError"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.checkSslCertificate')}
                id="http-monitor-checkSslCertificate"
                name="checkSslCertificate"
                data-cy="checkSslCertificate"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.checkDnsResolution')}
                id="http-monitor-checkDnsResolution"
                name="checkDnsResolution"
                data-cy="checkDnsResolution"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.upsideDownMode')}
                id="http-monitor-upsideDownMode"
                name="upsideDownMode"
                data-cy="upsideDownMode"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.maxRedirects')}
                id="http-monitor-maxRedirects"
                name="maxRedirects"
                data-cy="maxRedirects"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.description')}
                id="http-monitor-description"
                name="description"
                data-cy="description"
                type="textarea"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.tags')}
                id="http-monitor-tags"
                name="tags"
                data-cy="tags"
                type="text"
                validate={{
                  maxLength: { value: 500, message: translate('entity.validation.maxlength', { max: 500 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.enabled')}
                id="http-monitor-enabled"
                name="enabled"
                data-cy="enabled"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.expectedStatusCodes')}
                id="http-monitor-expectedStatusCodes"
                name="expectedStatusCodes"
                data-cy="expectedStatusCodes"
                type="text"
                validate={{
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.performanceBudgetMs')}
                id="http-monitor-performanceBudgetMs"
                name="performanceBudgetMs"
                data-cy="performanceBudgetMs"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.httpMonitor.sizeBudgetKb')}
                id="http-monitor-sizeBudgetKb"
                name="sizeBudgetKb"
                data-cy="sizeBudgetKb"
                type="text"
              />
              <ValidatedField
                id="http-monitor-parent"
                name="parent"
                data-cy="parent"
                label={translate('infraMirrorApp.httpMonitor.parent')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/http-monitor" replace color="info">
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

export default HttpMonitorUpdate;
