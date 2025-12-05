import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './audit-trail.reducer';

export const AuditTrailDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const auditTrailEntity = useAppSelector(state => state.auditTrail.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="auditTrailDetailsHeading">
          <Translate contentKey="infraMirrorApp.auditTrail.detail.title">AuditTrail</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="infraMirrorApp.auditTrail.id">Id</Translate>
            </span>
          </dt>
          <dd>{auditTrailEntity.id}</dd>
          <dt>
            <span id="action">
              <Translate contentKey="infraMirrorApp.auditTrail.action">Action</Translate>
            </span>
          </dt>
          <dd>{auditTrailEntity.action}</dd>
          <dt>
            <span id="entityName">
              <Translate contentKey="infraMirrorApp.auditTrail.entityName">Entity Name</Translate>
            </span>
          </dt>
          <dd>{auditTrailEntity.entityName}</dd>
          <dt>
            <span id="entityId">
              <Translate contentKey="infraMirrorApp.auditTrail.entityId">Entity Id</Translate>
            </span>
          </dt>
          <dd>{auditTrailEntity.entityId}</dd>
          <dt>
            <span id="oldValue">
              <Translate contentKey="infraMirrorApp.auditTrail.oldValue">Old Value</Translate>
            </span>
          </dt>
          <dd>{auditTrailEntity.oldValue}</dd>
          <dt>
            <span id="newValue">
              <Translate contentKey="infraMirrorApp.auditTrail.newValue">New Value</Translate>
            </span>
          </dt>
          <dd>{auditTrailEntity.newValue}</dd>
          <dt>
            <span id="timestamp">
              <Translate contentKey="infraMirrorApp.auditTrail.timestamp">Timestamp</Translate>
            </span>
          </dt>
          <dd>
            {auditTrailEntity.timestamp ? <TextFormat value={auditTrailEntity.timestamp} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="ipAddress">
              <Translate contentKey="infraMirrorApp.auditTrail.ipAddress">Ip Address</Translate>
            </span>
          </dt>
          <dd>{auditTrailEntity.ipAddress}</dd>
          <dt>
            <span id="userAgent">
              <Translate contentKey="infraMirrorApp.auditTrail.userAgent">User Agent</Translate>
            </span>
          </dt>
          <dd>{auditTrailEntity.userAgent}</dd>
        </dl>
        <Button tag={Link} to="/audit-trail" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/audit-trail/${auditTrailEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AuditTrailDetail;
