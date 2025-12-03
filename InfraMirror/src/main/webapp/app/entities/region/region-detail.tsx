import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './region.reducer';

export const RegionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const regionEntity = useAppSelector(state => state.region.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="regionDetailsHeading">
          <Translate contentKey="infraMirrorApp.region.detail.title">Region</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{regionEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="infraMirrorApp.region.name">Name</Translate>
            </span>
          </dt>
          <dd>{regionEntity.name}</dd>
          <dt>
            <span id="regionCode">
              <Translate contentKey="infraMirrorApp.region.regionCode">Region Code</Translate>
            </span>
          </dt>
          <dd>{regionEntity.regionCode}</dd>
          <dt>
            <span id="groupName">
              <Translate contentKey="infraMirrorApp.region.groupName">Group Name</Translate>
            </span>
          </dt>
          <dd>{regionEntity.groupName}</dd>
        </dl>
        <Button tag={Link} to="/region" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/region/${regionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default RegionDetail;
