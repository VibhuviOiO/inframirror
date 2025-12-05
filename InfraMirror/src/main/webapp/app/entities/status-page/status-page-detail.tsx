import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './status-page.reducer';

export const StatusPageDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const statusPageEntity = useAppSelector(state => state.statusPage.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="statusPageDetailsHeading">
          <Translate contentKey="infraMirrorApp.statusPage.detail.title">StatusPage</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="infraMirrorApp.statusPage.id">Id</Translate>
            </span>
          </dt>
          <dd>{statusPageEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="infraMirrorApp.statusPage.name">Name</Translate>
            </span>
          </dt>
          <dd>{statusPageEntity.name}</dd>
          <dt>
            <span id="slug">
              <Translate contentKey="infraMirrorApp.statusPage.slug">Slug</Translate>
            </span>
          </dt>
          <dd>{statusPageEntity.slug}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="infraMirrorApp.statusPage.description">Description</Translate>
            </span>
          </dt>
          <dd>{statusPageEntity.description}</dd>
          <dt>
            <span id="isPublic">
              <Translate contentKey="infraMirrorApp.statusPage.isPublic">Is Public</Translate>
            </span>
          </dt>
          <dd>{statusPageEntity.isPublic ? 'true' : 'false'}</dd>
          <dt>
            <span id="customDomain">
              <Translate contentKey="infraMirrorApp.statusPage.customDomain">Custom Domain</Translate>
            </span>
          </dt>
          <dd>{statusPageEntity.customDomain}</dd>
          <dt>
            <span id="logoUrl">
              <Translate contentKey="infraMirrorApp.statusPage.logoUrl">Logo Url</Translate>
            </span>
          </dt>
          <dd>{statusPageEntity.logoUrl}</dd>
          <dt>
            <span id="themeColor">
              <Translate contentKey="infraMirrorApp.statusPage.themeColor">Theme Color</Translate>
            </span>
          </dt>
          <dd>{statusPageEntity.themeColor}</dd>
          <dt>
            <span id="headerText">
              <Translate contentKey="infraMirrorApp.statusPage.headerText">Header Text</Translate>
            </span>
          </dt>
          <dd>{statusPageEntity.headerText}</dd>
          <dt>
            <span id="footerText">
              <Translate contentKey="infraMirrorApp.statusPage.footerText">Footer Text</Translate>
            </span>
          </dt>
          <dd>{statusPageEntity.footerText}</dd>
          <dt>
            <span id="showResponseTimes">
              <Translate contentKey="infraMirrorApp.statusPage.showResponseTimes">Show Response Times</Translate>
            </span>
          </dt>
          <dd>{statusPageEntity.showResponseTimes ? 'true' : 'false'}</dd>
          <dt>
            <span id="showUptimePercentage">
              <Translate contentKey="infraMirrorApp.statusPage.showUptimePercentage">Show Uptime Percentage</Translate>
            </span>
          </dt>
          <dd>{statusPageEntity.showUptimePercentage ? 'true' : 'false'}</dd>
          <dt>
            <span id="autoRefreshSeconds">
              <Translate contentKey="infraMirrorApp.statusPage.autoRefreshSeconds">Auto Refresh Seconds</Translate>
            </span>
          </dt>
          <dd>{statusPageEntity.autoRefreshSeconds}</dd>
          <dt>
            <span id="monitorSelection">
              <Translate contentKey="infraMirrorApp.statusPage.monitorSelection">Monitor Selection</Translate>
            </span>
          </dt>
          <dd>{statusPageEntity.monitorSelection}</dd>
          <dt>
            <span id="isActive">
              <Translate contentKey="infraMirrorApp.statusPage.isActive">Is Active</Translate>
            </span>
          </dt>
          <dd>{statusPageEntity.isActive ? 'true' : 'false'}</dd>
          <dt>
            <span id="isHomePage">
              <Translate contentKey="infraMirrorApp.statusPage.isHomePage">Is Home Page</Translate>
            </span>
          </dt>
          <dd>{statusPageEntity.isHomePage ? 'true' : 'false'}</dd>
          <dt>
            <span id="allowedRoles">
              <Translate contentKey="infraMirrorApp.statusPage.allowedRoles">Allowed Roles</Translate>
            </span>
          </dt>
          <dd>{statusPageEntity.allowedRoles}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="infraMirrorApp.statusPage.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {statusPageEntity.createdAt ? <TextFormat value={statusPageEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="infraMirrorApp.statusPage.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>
            {statusPageEntity.updatedAt ? <TextFormat value={statusPageEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
        </dl>
        <Button tag={Link} to="/status-page" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/status-page/${statusPageEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default StatusPageDetail;
