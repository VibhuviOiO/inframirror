import React from 'react';
import { Button, Modal, ModalHeader, ModalBody, ModalFooter, Badge } from 'reactstrap';
import { IInstance } from 'app/shared/model/instance.model';
import { TextFormat } from 'react-jhipster';
import { APP_DATE_FORMAT } from 'app/config/constants';

interface InstanceViewModalProps {
  isOpen: boolean;
  toggle: () => void;
  instance?: IInstance | null;
}

export const InstanceViewModal: React.FC<InstanceViewModalProps> = ({ isOpen, toggle, instance }) => {
  if (!instance) return null;

  return (
    <Modal isOpen={isOpen} toggle={toggle} size="lg">
      <ModalHeader toggle={toggle}>Instance Details</ModalHeader>
      <ModalBody>
        <div className="row mb-3">
          <div className="col-md-6">
            <strong>Name:</strong> {instance.name}
          </div>
          <div className="col-md-6">
            <strong>Hostname:</strong> {instance.hostname}
          </div>
        </div>
        {instance.description && (
          <div className="mb-3">
            <strong>Description:</strong> {instance.description}
          </div>
        )}
        <div className="row mb-3">
          <div className="col-md-6">
            <strong>Type:</strong> <Badge color="info">{instance.instanceType}</Badge>
          </div>
          <div className="col-md-6">
            <strong>Monitoring:</strong> <Badge color="secondary">{instance.monitoringType}</Badge>
          </div>
        </div>
        <div className="row mb-3">
          <div className="col-md-6">
            <strong>OS:</strong> {instance.operatingSystem || 'N/A'}
          </div>
          <div className="col-md-6">
            <strong>Platform:</strong> {instance.platform || 'N/A'}
          </div>
        </div>
        <div className="row mb-3">
          <div className="col-md-6">
            <strong>Private IP:</strong> {instance.privateIpAddress || 'N/A'}
          </div>
          <div className="col-md-6">
            <strong>Public IP:</strong> {instance.publicIpAddress || 'N/A'}
          </div>
        </div>
        <div className="row mb-3">
          <div className="col-md-6">
            <strong>Datacenter:</strong> {instance.datacenter?.name || 'N/A'}
          </div>
          <div className="col-md-6">
            <strong>Agent:</strong> {instance.agent?.name || 'N/A'}
          </div>
        </div>
        <hr />
        <h6>Ping Monitoring</h6>
        <div className="row mb-3">
          <div className="col-md-12">
            <strong>Enabled:</strong>{' '}
            <Badge color={instance.pingEnabled ? 'success' : 'secondary'}>{instance.pingEnabled ? 'Yes' : 'No'}</Badge>
          </div>
        </div>
        {instance.pingEnabled && (
          <div className="row mb-3">
            <div className="col-md-4">
              <strong>Interval:</strong> {instance.pingInterval}s
            </div>
            <div className="col-md-4">
              <strong>Timeout:</strong> {instance.pingTimeoutMs}ms
            </div>
            <div className="col-md-4">
              <strong>Retry:</strong> {instance.pingRetryCount}
            </div>
          </div>
        )}
        <hr />
        <h6>Hardware Monitoring</h6>
        <div className="row mb-3">
          <div className="col-md-12">
            <strong>Enabled:</strong>{' '}
            <Badge color={instance.hardwareMonitoringEnabled ? 'success' : 'secondary'}>
              {instance.hardwareMonitoringEnabled ? 'Yes' : 'No'}
            </Badge>
          </div>
        </div>
        {instance.hardwareMonitoringEnabled && (
          <>
            <div className="row mb-3">
              <div className="col-md-12">
                <strong>Interval:</strong> {instance.hardwareMonitoringInterval}s
              </div>
            </div>
            <div className="row mb-3">
              <div className="col-md-6">
                <strong>CPU Thresholds:</strong> {instance.cpuWarningThreshold}% / {instance.cpuDangerThreshold}%
              </div>
              <div className="col-md-6">
                <strong>Memory Thresholds:</strong> {instance.memoryWarningThreshold}% / {instance.memoryDangerThreshold}%
              </div>
            </div>
            <div className="row mb-3">
              <div className="col-md-6">
                <strong>Disk Thresholds:</strong> {instance.diskWarningThreshold}% / {instance.diskDangerThreshold}%
              </div>
            </div>
          </>
        )}
        {instance.createdAt && (
          <div className="mt-3">
            <small className="text-muted">Created: {instance.createdAt.toString()}</small>
          </div>
        )}
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={toggle}>
          Close
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default InstanceViewModal;
