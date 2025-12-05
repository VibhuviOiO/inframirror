import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './status-page-item.reducer';

export const StatusPageItemDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const statusPageItemEntity = useAppSelector(state => state.statusPageItem.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="statusPageItemDetailsHeading">
          <Translate contentKey="infraMirrorApp.statusPageItem.detail.title">StatusPageItem</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="infraMirrorApp.statusPageItem.id">Id</Translate>
            </span>
          </dt>
          <dd>{statusPageItemEntity.id}</dd>
          <dt>
            <span id="itemType">
              <Translate contentKey="infraMirrorApp.statusPageItem.itemType">Item Type</Translate>
            </span>
          </dt>
          <dd>{statusPageItemEntity.itemType}</dd>
          <dt>
            <span id="itemId">
              <Translate contentKey="infraMirrorApp.statusPageItem.itemId">Item Id</Translate>
            </span>
          </dt>
          <dd>{statusPageItemEntity.itemId}</dd>
          <dt>
            <span id="displayOrder">
              <Translate contentKey="infraMirrorApp.statusPageItem.displayOrder">Display Order</Translate>
            </span>
          </dt>
          <dd>{statusPageItemEntity.displayOrder}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="infraMirrorApp.statusPageItem.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {statusPageItemEntity.createdAt ? (
              <TextFormat value={statusPageItemEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="infraMirrorApp.statusPageItem.statusPage">Status Page</Translate>
          </dt>
          <dd>{statusPageItemEntity.statusPage ? statusPageItemEntity.statusPage.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/status-page-item" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/status-page-item/${statusPageItemEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default StatusPageItemDetail;
