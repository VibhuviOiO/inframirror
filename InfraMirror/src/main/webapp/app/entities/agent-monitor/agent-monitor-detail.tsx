import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './agent-monitor.reducer';

export const AgentMonitorDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const agentMonitorEntity = useAppSelector(state => state.agentMonitor.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="agentMonitorDetailsHeading">
          <Translate contentKey="infraMirrorApp.agentMonitor.detail.title">AgentMonitor</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="infraMirrorApp.agentMonitor.id">Id</Translate>
            </span>
          </dt>
          <dd>{agentMonitorEntity.id}</dd>
          <dt>
            <span id="active">
              <Translate contentKey="infraMirrorApp.agentMonitor.active">Active</Translate>
            </span>
          </dt>
          <dd>{agentMonitorEntity.active ? 'true' : 'false'}</dd>
          <dt>
            <span id="createdBy">
              <Translate contentKey="infraMirrorApp.agentMonitor.createdBy">Created By</Translate>
            </span>
          </dt>
          <dd>{agentMonitorEntity.createdBy}</dd>
          <dt>
            <span id="createdDate">
              <Translate contentKey="infraMirrorApp.agentMonitor.createdDate">Created Date</Translate>
            </span>
          </dt>
          <dd>
            {agentMonitorEntity.createdDate ? (
              <TextFormat value={agentMonitorEntity.createdDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="lastModifiedBy">
              <Translate contentKey="infraMirrorApp.agentMonitor.lastModifiedBy">Last Modified By</Translate>
            </span>
          </dt>
          <dd>{agentMonitorEntity.lastModifiedBy}</dd>
          <dt>
            <span id="lastModifiedDate">
              <Translate contentKey="infraMirrorApp.agentMonitor.lastModifiedDate">Last Modified Date</Translate>
            </span>
          </dt>
          <dd>
            {agentMonitorEntity.lastModifiedDate ? (
              <TextFormat value={agentMonitorEntity.lastModifiedDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="infraMirrorApp.agentMonitor.agent">Agent</Translate>
          </dt>
          <dd>{agentMonitorEntity.agent ? agentMonitorEntity.agent.id : ''}</dd>
          <dt>
            <Translate contentKey="infraMirrorApp.agentMonitor.monitor">Monitor</Translate>
          </dt>
          <dd>{agentMonitorEntity.monitor ? agentMonitorEntity.monitor.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/agent-monitor" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/agent-monitor/${agentMonitorEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AgentMonitorDetail;
