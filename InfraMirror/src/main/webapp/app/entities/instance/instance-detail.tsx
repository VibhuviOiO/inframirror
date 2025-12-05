import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './instance.reducer';

export const InstanceDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const instanceEntity = useAppSelector(state => state.instance.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="instanceDetailsHeading">
          <Translate contentKey="infraMirrorApp.instance.detail.title">Instance</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="infraMirrorApp.instance.id">Id</Translate>
            </span>
          </dt>
          <dd>{instanceEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="infraMirrorApp.instance.name">Name</Translate>
            </span>
          </dt>
          <dd>{instanceEntity.name}</dd>
          <dt>
            <span id="hostname">
              <Translate contentKey="infraMirrorApp.instance.hostname">Hostname</Translate>
            </span>
          </dt>
          <dd>{instanceEntity.hostname}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="infraMirrorApp.instance.description">Description</Translate>
            </span>
          </dt>
          <dd>{instanceEntity.description}</dd>
          <dt>
            <span id="instanceType">
              <Translate contentKey="infraMirrorApp.instance.instanceType">Instance Type</Translate>
            </span>
          </dt>
          <dd>{instanceEntity.instanceType}</dd>
          <dt>
            <span id="monitoringType">
              <Translate contentKey="infraMirrorApp.instance.monitoringType">Monitoring Type</Translate>
            </span>
          </dt>
          <dd>{instanceEntity.monitoringType}</dd>
          <dt>
            <span id="operatingSystem">
              <Translate contentKey="infraMirrorApp.instance.operatingSystem">Operating System</Translate>
            </span>
          </dt>
          <dd>{instanceEntity.operatingSystem}</dd>
          <dt>
            <span id="platform">
              <Translate contentKey="infraMirrorApp.instance.platform">Platform</Translate>
            </span>
          </dt>
          <dd>{instanceEntity.platform}</dd>
          <dt>
            <span id="privateIpAddress">
              <Translate contentKey="infraMirrorApp.instance.privateIpAddress">Private Ip Address</Translate>
            </span>
          </dt>
          <dd>{instanceEntity.privateIpAddress}</dd>
          <dt>
            <span id="publicIpAddress">
              <Translate contentKey="infraMirrorApp.instance.publicIpAddress">Public Ip Address</Translate>
            </span>
          </dt>
          <dd>{instanceEntity.publicIpAddress}</dd>
          <dt>
            <span id="tags">
              <Translate contentKey="infraMirrorApp.instance.tags">Tags</Translate>
            </span>
          </dt>
          <dd>{instanceEntity.tags}</dd>
          <dt>
            <span id="pingEnabled">
              <Translate contentKey="infraMirrorApp.instance.pingEnabled">Ping Enabled</Translate>
            </span>
          </dt>
          <dd>{instanceEntity.pingEnabled ? 'true' : 'false'}</dd>
          <dt>
            <span id="pingInterval">
              <Translate contentKey="infraMirrorApp.instance.pingInterval">Ping Interval</Translate>
            </span>
          </dt>
          <dd>{instanceEntity.pingInterval}</dd>
          <dt>
            <span id="pingTimeoutMs">
              <Translate contentKey="infraMirrorApp.instance.pingTimeoutMs">Ping Timeout Ms</Translate>
            </span>
          </dt>
          <dd>{instanceEntity.pingTimeoutMs}</dd>
          <dt>
            <span id="pingRetryCount">
              <Translate contentKey="infraMirrorApp.instance.pingRetryCount">Ping Retry Count</Translate>
            </span>
          </dt>
          <dd>{instanceEntity.pingRetryCount}</dd>
          <dt>
            <span id="hardwareMonitoringEnabled">
              <Translate contentKey="infraMirrorApp.instance.hardwareMonitoringEnabled">Hardware Monitoring Enabled</Translate>
            </span>
          </dt>
          <dd>{instanceEntity.hardwareMonitoringEnabled ? 'true' : 'false'}</dd>
          <dt>
            <span id="hardwareMonitoringInterval">
              <Translate contentKey="infraMirrorApp.instance.hardwareMonitoringInterval">Hardware Monitoring Interval</Translate>
            </span>
          </dt>
          <dd>{instanceEntity.hardwareMonitoringInterval}</dd>
          <dt>
            <span id="cpuWarningThreshold">
              <Translate contentKey="infraMirrorApp.instance.cpuWarningThreshold">Cpu Warning Threshold</Translate>
            </span>
          </dt>
          <dd>{instanceEntity.cpuWarningThreshold}</dd>
          <dt>
            <span id="cpuDangerThreshold">
              <Translate contentKey="infraMirrorApp.instance.cpuDangerThreshold">Cpu Danger Threshold</Translate>
            </span>
          </dt>
          <dd>{instanceEntity.cpuDangerThreshold}</dd>
          <dt>
            <span id="memoryWarningThreshold">
              <Translate contentKey="infraMirrorApp.instance.memoryWarningThreshold">Memory Warning Threshold</Translate>
            </span>
          </dt>
          <dd>{instanceEntity.memoryWarningThreshold}</dd>
          <dt>
            <span id="memoryDangerThreshold">
              <Translate contentKey="infraMirrorApp.instance.memoryDangerThreshold">Memory Danger Threshold</Translate>
            </span>
          </dt>
          <dd>{instanceEntity.memoryDangerThreshold}</dd>
          <dt>
            <span id="diskWarningThreshold">
              <Translate contentKey="infraMirrorApp.instance.diskWarningThreshold">Disk Warning Threshold</Translate>
            </span>
          </dt>
          <dd>{instanceEntity.diskWarningThreshold}</dd>
          <dt>
            <span id="diskDangerThreshold">
              <Translate contentKey="infraMirrorApp.instance.diskDangerThreshold">Disk Danger Threshold</Translate>
            </span>
          </dt>
          <dd>{instanceEntity.diskDangerThreshold}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="infraMirrorApp.instance.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{instanceEntity.createdAt ? <TextFormat value={instanceEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="infraMirrorApp.instance.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>{instanceEntity.updatedAt ? <TextFormat value={instanceEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="lastPingAt">
              <Translate contentKey="infraMirrorApp.instance.lastPingAt">Last Ping At</Translate>
            </span>
          </dt>
          <dd>
            {instanceEntity.lastPingAt ? <TextFormat value={instanceEntity.lastPingAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="lastHardwareCheckAt">
              <Translate contentKey="infraMirrorApp.instance.lastHardwareCheckAt">Last Hardware Check At</Translate>
            </span>
          </dt>
          <dd>
            {instanceEntity.lastHardwareCheckAt ? (
              <TextFormat value={instanceEntity.lastHardwareCheckAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="infraMirrorApp.instance.datacenter">Datacenter</Translate>
          </dt>
          <dd>{instanceEntity.datacenter ? instanceEntity.datacenter.id : ''}</dd>
          <dt>
            <Translate contentKey="infraMirrorApp.instance.agent">Agent</Translate>
          </dt>
          <dd>{instanceEntity.agent ? instanceEntity.agent.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/instance" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/instance/${instanceEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default InstanceDetail;
