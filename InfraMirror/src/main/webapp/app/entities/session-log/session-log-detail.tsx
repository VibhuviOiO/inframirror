import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './session-log.reducer';

export const SessionLogDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const sessionLogEntity = useAppSelector(state => state.sessionLog.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="sessionLogDetailsHeading">
          <Translate contentKey="infraMirrorApp.sessionLog.detail.title">SessionLog</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{sessionLogEntity.id}</dd>
          <dt>
            <span id="sessionType">
              <Translate contentKey="infraMirrorApp.sessionLog.sessionType">Session Type</Translate>
            </span>
          </dt>
          <dd>{sessionLogEntity.sessionType}</dd>
          <dt>
            <span id="startTime">
              <Translate contentKey="infraMirrorApp.sessionLog.startTime">Start Time</Translate>
            </span>
          </dt>
          <dd>
            {sessionLogEntity.startTime ? <TextFormat value={sessionLogEntity.startTime} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="endTime">
              <Translate contentKey="infraMirrorApp.sessionLog.endTime">End Time</Translate>
            </span>
          </dt>
          <dd>{sessionLogEntity.endTime ? <TextFormat value={sessionLogEntity.endTime} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="duration">
              <Translate contentKey="infraMirrorApp.sessionLog.duration">Duration</Translate>
            </span>
          </dt>
          <dd>{sessionLogEntity.duration}</dd>
          <dt>
            <span id="sourceIpAddress">
              <Translate contentKey="infraMirrorApp.sessionLog.sourceIpAddress">Source Ip Address</Translate>
            </span>
          </dt>
          <dd>{sessionLogEntity.sourceIpAddress}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="infraMirrorApp.sessionLog.status">Status</Translate>
            </span>
          </dt>
          <dd>{sessionLogEntity.status}</dd>
          <dt>
            <span id="terminationReason">
              <Translate contentKey="infraMirrorApp.sessionLog.terminationReason">Termination Reason</Translate>
            </span>
          </dt>
          <dd>{sessionLogEntity.terminationReason}</dd>
          <dt>
            <span id="commandsExecuted">
              <Translate contentKey="infraMirrorApp.sessionLog.commandsExecuted">Commands Executed</Translate>
            </span>
          </dt>
          <dd>{sessionLogEntity.commandsExecuted}</dd>
          <dt>
            <span id="bytesTransferred">
              <Translate contentKey="infraMirrorApp.sessionLog.bytesTransferred">Bytes Transferred</Translate>
            </span>
          </dt>
          <dd>{sessionLogEntity.bytesTransferred}</dd>
          <dt>
            <span id="sessionId">
              <Translate contentKey="infraMirrorApp.sessionLog.sessionId">Session Id</Translate>
            </span>
          </dt>
          <dd>{sessionLogEntity.sessionId}</dd>
          <dt>
            <span id="metadata">
              <Translate contentKey="infraMirrorApp.sessionLog.metadata">Metadata</Translate>
            </span>
          </dt>
          <dd>{sessionLogEntity.metadata}</dd>
          <dt>
            <Translate contentKey="infraMirrorApp.sessionLog.instance">Instance</Translate>
          </dt>
          <dd>{sessionLogEntity.instance ? sessionLogEntity.instance.id : ''}</dd>
          <dt>
            <Translate contentKey="infraMirrorApp.sessionLog.agent">Agent</Translate>
          </dt>
          <dd>{sessionLogEntity.agent ? sessionLogEntity.agent.id : ''}</dd>
          <dt>
            <Translate contentKey="infraMirrorApp.sessionLog.user">User</Translate>
          </dt>
          <dd>{sessionLogEntity.user ? sessionLogEntity.user.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/session-log" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/session-log/${sessionLogEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default SessionLogDetail;
