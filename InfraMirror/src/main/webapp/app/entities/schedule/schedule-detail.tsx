import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './schedule.reducer';

export const ScheduleDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const scheduleEntity = useAppSelector(state => state.schedule.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="scheduleDetailsHeading">
          <Translate contentKey="infraMirrorApp.schedule.detail.title">Schedule</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{scheduleEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="infraMirrorApp.schedule.name">Name</Translate>
            </span>
          </dt>
          <dd>{scheduleEntity.name}</dd>
          <dt>
            <span id="interval">
              <Translate contentKey="infraMirrorApp.schedule.interval">Interval</Translate>
            </span>
          </dt>
          <dd>{scheduleEntity.interval}</dd>
          <dt>
            <span id="includeResponseBody">
              <Translate contentKey="infraMirrorApp.schedule.includeResponseBody">Include Response Body</Translate>
            </span>
          </dt>
          <dd>{scheduleEntity.includeResponseBody ? 'true' : 'false'}</dd>
          <dt>
            <span id="thresholdsWarning">
              <Translate contentKey="infraMirrorApp.schedule.thresholdsWarning">Thresholds Warning</Translate>
            </span>
          </dt>
          <dd>{scheduleEntity.thresholdsWarning}</dd>
          <dt>
            <span id="thresholdsCritical">
              <Translate contentKey="infraMirrorApp.schedule.thresholdsCritical">Thresholds Critical</Translate>
            </span>
          </dt>
          <dd>{scheduleEntity.thresholdsCritical}</dd>
        </dl>
        <Button tag={Link} to="/schedule" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/schedule/${scheduleEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ScheduleDetail;
