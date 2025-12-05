import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './status-dependency.reducer';

export const StatusDependencyDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const statusDependencyEntity = useAppSelector(state => state.statusDependency.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="statusDependencyDetailsHeading">
          <Translate contentKey="infraMirrorApp.statusDependency.detail.title">StatusDependency</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="infraMirrorApp.statusDependency.id">Id</Translate>
            </span>
          </dt>
          <dd>{statusDependencyEntity.id}</dd>
          <dt>
            <span id="parentType">
              <Translate contentKey="infraMirrorApp.statusDependency.parentType">Parent Type</Translate>
            </span>
          </dt>
          <dd>{statusDependencyEntity.parentType}</dd>
          <dt>
            <span id="parentId">
              <Translate contentKey="infraMirrorApp.statusDependency.parentId">Parent Id</Translate>
            </span>
          </dt>
          <dd>{statusDependencyEntity.parentId}</dd>
          <dt>
            <span id="childType">
              <Translate contentKey="infraMirrorApp.statusDependency.childType">Child Type</Translate>
            </span>
          </dt>
          <dd>{statusDependencyEntity.childType}</dd>
          <dt>
            <span id="childId">
              <Translate contentKey="infraMirrorApp.statusDependency.childId">Child Id</Translate>
            </span>
          </dt>
          <dd>{statusDependencyEntity.childId}</dd>
          <dt>
            <span id="metadata">
              <Translate contentKey="infraMirrorApp.statusDependency.metadata">Metadata</Translate>
            </span>
          </dt>
          <dd>{statusDependencyEntity.metadata}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="infraMirrorApp.statusDependency.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {statusDependencyEntity.createdAt ? (
              <TextFormat value={statusDependencyEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="infraMirrorApp.statusDependency.statusPage">Status Page</Translate>
          </dt>
          <dd>{statusDependencyEntity.statusPage ? statusDependencyEntity.statusPage.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/status-dependency" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/status-dependency/${statusDependencyEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default StatusDependencyDetail;
