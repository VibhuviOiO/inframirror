import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './datacenter.reducer';

export const DatacenterDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const datacenterEntity = useAppSelector(state => state.datacenter.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="datacenterDetailsHeading">
          <Translate contentKey="infraMirrorApp.datacenter.detail.title">Datacenter</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="infraMirrorApp.datacenter.id">Id</Translate>
            </span>
          </dt>
          <dd>{datacenterEntity.id}</dd>
          <dt>
            <span id="code">
              <Translate contentKey="infraMirrorApp.datacenter.code">Code</Translate>
            </span>
          </dt>
          <dd>{datacenterEntity.code}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="infraMirrorApp.datacenter.name">Name</Translate>
            </span>
          </dt>
          <dd>{datacenterEntity.name}</dd>
          <dt>
            <Translate contentKey="infraMirrorApp.datacenter.region">Region</Translate>
          </dt>
          <dd>{datacenterEntity.region ? datacenterEntity.region.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/datacenter" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/datacenter/${datacenterEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default DatacenterDetail;
