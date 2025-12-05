import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './agent-lock.reducer';

export const AgentLockDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const agentLockEntity = useAppSelector(state => state.agentLock.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="agentLockDetailsHeading">
          <Translate contentKey="infraMirrorApp.agentLock.detail.title">AgentLock</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{agentLockEntity.id}</dd>
          <dt>
            <span id="agentId">
              <Translate contentKey="infraMirrorApp.agentLock.agentId">Agent Id</Translate>
            </span>
          </dt>
          <dd>{agentLockEntity.agentId}</dd>
          <dt>
            <span id="acquiredAt">
              <Translate contentKey="infraMirrorApp.agentLock.acquiredAt">Acquired At</Translate>
            </span>
          </dt>
          <dd>
            {agentLockEntity.acquiredAt ? <TextFormat value={agentLockEntity.acquiredAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="expiresAt">
              <Translate contentKey="infraMirrorApp.agentLock.expiresAt">Expires At</Translate>
            </span>
          </dt>
          <dd>
            {agentLockEntity.expiresAt ? <TextFormat value={agentLockEntity.expiresAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
        </dl>
        <Button tag={Link} to="/agent-lock" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/agent-lock/${agentLockEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AgentLockDetail;
