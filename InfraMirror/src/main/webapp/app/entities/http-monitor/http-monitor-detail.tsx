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
              <Translate contentKey="global.field.id">ID</Translate>
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
            <Translate contentKey="infraMirrorApp.httpMonitor.schedule">Schedule</Translate>
          </dt>
          <dd>{httpMonitorEntity.schedule ? httpMonitorEntity.schedule.id : ''}</dd>
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
