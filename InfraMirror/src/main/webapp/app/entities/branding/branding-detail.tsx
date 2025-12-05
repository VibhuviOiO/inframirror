import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './branding.reducer';

export const BrandingDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const brandingEntity = useAppSelector(state => state.branding.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="brandingDetailsHeading">
          <Translate contentKey="infraMirrorApp.branding.detail.title">Branding</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{brandingEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="infraMirrorApp.branding.title">Title</Translate>
            </span>
          </dt>
          <dd>{brandingEntity.title}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="infraMirrorApp.branding.description">Description</Translate>
            </span>
          </dt>
          <dd>{brandingEntity.description}</dd>
          <dt>
            <span id="keywords">
              <Translate contentKey="infraMirrorApp.branding.keywords">Keywords</Translate>
            </span>
          </dt>
          <dd>{brandingEntity.keywords}</dd>
          <dt>
            <span id="author">
              <Translate contentKey="infraMirrorApp.branding.author">Author</Translate>
            </span>
          </dt>
          <dd>{brandingEntity.author}</dd>
          <dt>
            <span id="faviconPath">
              <Translate contentKey="infraMirrorApp.branding.faviconPath">Favicon Path</Translate>
            </span>
          </dt>
          <dd>{brandingEntity.faviconPath}</dd>
          <dt>
            <span id="logoPath">
              <Translate contentKey="infraMirrorApp.branding.logoPath">Logo Path</Translate>
            </span>
          </dt>
          <dd>{brandingEntity.logoPath}</dd>
          <dt>
            <span id="logoWidth">
              <Translate contentKey="infraMirrorApp.branding.logoWidth">Logo Width</Translate>
            </span>
          </dt>
          <dd>{brandingEntity.logoWidth}</dd>
          <dt>
            <span id="logoHeight">
              <Translate contentKey="infraMirrorApp.branding.logoHeight">Logo Height</Translate>
            </span>
          </dt>
          <dd>{brandingEntity.logoHeight}</dd>
          <dt>
            <span id="footerTitle">
              <Translate contentKey="infraMirrorApp.branding.footerTitle">Footer Title</Translate>
            </span>
          </dt>
          <dd>{brandingEntity.footerTitle}</dd>
          <dt>
            <span id="isActive">
              <Translate contentKey="infraMirrorApp.branding.isActive">Is Active</Translate>
            </span>
          </dt>
          <dd>{brandingEntity.isActive ? 'true' : 'false'}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="infraMirrorApp.branding.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{brandingEntity.createdAt ? <TextFormat value={brandingEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="infraMirrorApp.branding.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>{brandingEntity.updatedAt ? <TextFormat value={brandingEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/branding" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/branding/${brandingEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default BrandingDetail;
