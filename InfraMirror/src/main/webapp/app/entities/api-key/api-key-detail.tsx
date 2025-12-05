import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './api-key.reducer';

export const ApiKeyDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const apiKeyEntity = useAppSelector(state => state.apiKey.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="apiKeyDetailsHeading">
          <Translate contentKey="infraMirrorApp.apiKey.detail.title">ApiKey</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="infraMirrorApp.apiKey.id">Id</Translate>
            </span>
          </dt>
          <dd>{apiKeyEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="infraMirrorApp.apiKey.name">Name</Translate>
            </span>
          </dt>
          <dd>{apiKeyEntity.name}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="infraMirrorApp.apiKey.description">Description</Translate>
            </span>
          </dt>
          <dd>{apiKeyEntity.description}</dd>
          <dt>
            <span id="keyHash">
              <Translate contentKey="infraMirrorApp.apiKey.keyHash">Key Hash</Translate>
            </span>
          </dt>
          <dd>{apiKeyEntity.keyHash}</dd>
          <dt>
            <span id="active">
              <Translate contentKey="infraMirrorApp.apiKey.active">Active</Translate>
            </span>
          </dt>
          <dd>{apiKeyEntity.active ? 'true' : 'false'}</dd>
          <dt>
            <span id="lastUsedDate">
              <Translate contentKey="infraMirrorApp.apiKey.lastUsedDate">Last Used Date</Translate>
            </span>
          </dt>
          <dd>
            {apiKeyEntity.lastUsedDate ? <TextFormat value={apiKeyEntity.lastUsedDate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="expiresAt">
              <Translate contentKey="infraMirrorApp.apiKey.expiresAt">Expires At</Translate>
            </span>
          </dt>
          <dd>{apiKeyEntity.expiresAt ? <TextFormat value={apiKeyEntity.expiresAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="createdBy">
              <Translate contentKey="infraMirrorApp.apiKey.createdBy">Created By</Translate>
            </span>
          </dt>
          <dd>{apiKeyEntity.createdBy}</dd>
          <dt>
            <span id="createdDate">
              <Translate contentKey="infraMirrorApp.apiKey.createdDate">Created Date</Translate>
            </span>
          </dt>
          <dd>{apiKeyEntity.createdDate ? <TextFormat value={apiKeyEntity.createdDate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="lastModifiedBy">
              <Translate contentKey="infraMirrorApp.apiKey.lastModifiedBy">Last Modified By</Translate>
            </span>
          </dt>
          <dd>{apiKeyEntity.lastModifiedBy}</dd>
          <dt>
            <span id="lastModifiedDate">
              <Translate contentKey="infraMirrorApp.apiKey.lastModifiedDate">Last Modified Date</Translate>
            </span>
          </dt>
          <dd>
            {apiKeyEntity.lastModifiedDate ? (
              <TextFormat value={apiKeyEntity.lastModifiedDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
        </dl>
        <Button tag={Link} to="/api-key" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/api-key/${apiKeyEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ApiKeyDetail;
