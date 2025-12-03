import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './tag.reducer';

export const TagDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const tagEntity = useAppSelector(state => state.tag.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="tagDetailsHeading">
          <Translate contentKey="infraMirrorApp.tag.detail.title">Tag</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{tagEntity.id}</dd>
          <dt>
            <span id="key">
              <Translate contentKey="infraMirrorApp.tag.key">Key</Translate>
            </span>
          </dt>
          <dd>{tagEntity.key}</dd>
          <dt>
            <span id="value">
              <Translate contentKey="infraMirrorApp.tag.value">Value</Translate>
            </span>
          </dt>
          <dd>{tagEntity.value}</dd>
          <dt>
            <span id="entityType">
              <Translate contentKey="infraMirrorApp.tag.entityType">Entity Type</Translate>
            </span>
          </dt>
          <dd>{tagEntity.entityType}</dd>
          <dt>
            <span id="entityId">
              <Translate contentKey="infraMirrorApp.tag.entityId">Entity Id</Translate>
            </span>
          </dt>
          <dd>{tagEntity.entityId}</dd>
          <dt>
            <span id="createdBy">
              <Translate contentKey="infraMirrorApp.tag.createdBy">Created By</Translate>
            </span>
          </dt>
          <dd>{tagEntity.createdBy}</dd>
          <dt>
            <span id="createdDate">
              <Translate contentKey="infraMirrorApp.tag.createdDate">Created Date</Translate>
            </span>
          </dt>
          <dd>{tagEntity.createdDate ? <TextFormat value={tagEntity.createdDate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/tag" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/tag/${tagEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TagDetail;
