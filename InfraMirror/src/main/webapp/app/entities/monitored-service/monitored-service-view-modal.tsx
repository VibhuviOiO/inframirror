import React from 'react';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTimes } from '@fortawesome/free-solid-svg-icons';
import { IMonitoredService } from 'app/shared/model/monitored-service.model';

interface MonitoredServiceViewModalProps {
  isOpen: boolean;
  toggle: () => void;
  service?: IMonitoredService | null;
}

const Field = ({ label, value }: { label: string; value: any }) => (
  <div className="row mb-2">
    <div className="col-sm-4">
      <strong>{label}:</strong>
    </div>
    <div className="col-sm-8">{value || '-'}</div>
  </div>
);

export const MonitoredServiceViewModal: React.FC<MonitoredServiceViewModalProps> = ({ isOpen, toggle, service }) => (
  <Modal isOpen={isOpen} toggle={toggle} size="lg" centered>
    <ModalHeader toggle={toggle}>Service Details: {service?.name}</ModalHeader>
    <ModalBody style={{ maxHeight: '70vh', overflowY: 'auto' }}>
      <div className="detail-section mb-4">
        <h5 className="section-title border-bottom pb-2 mb-3">Basic Information</h5>
        <Field label="ID" value={<code>{service?.id}</code>} />
        <Field label="Name" value={service?.name} />
        <Field label="Description" value={service?.description} />
        <Field label="Service Type" value={<span className="badge bg-info">{service?.serviceType}</span>} />
        <Field label="Environment" value={<span className="badge bg-secondary">{service?.environment}</span>} />
        <Field label="Datacenter" value={service?.datacenter?.name} />
        <Field
          label="Active"
          value={<span className={`badge ${service?.isActive ? 'bg-success' : 'bg-secondary'}`}>{service?.isActive ? 'Yes' : 'No'}</span>}
        />
      </div>

      <div className="detail-section mb-4">
        <h5 className="section-title border-bottom pb-2 mb-3">Monitoring Configuration</h5>
        <Field label="Monitoring Enabled" value={service?.monitoringEnabled ? 'Yes' : 'No'} />
        <Field label="Cluster Monitoring" value={service?.clusterMonitoringEnabled ? 'Yes' : 'No'} />
        <Field label="Interval" value={`${service?.intervalSeconds}s`} />
        <Field label="Timeout" value={`${service?.timeoutMs}ms`} />
        <Field label="Retry Count" value={service?.retryCount} />
      </div>

      <div className="detail-section mb-4">
        <h5 className="section-title border-bottom pb-2 mb-3">Latency Thresholds</h5>
        <Field label="Warning Latency" value={service?.latencyWarningMs ? `${service.latencyWarningMs}ms` : '-'} />
        <Field label="Critical Latency" value={service?.latencyCriticalMs ? `${service.latencyCriticalMs}ms` : '-'} />
      </div>

      {service?.advancedConfig && (
        <div className="detail-section">
          <h5 className="section-title border-bottom pb-2 mb-3">Advanced Configuration</h5>
          <pre
            style={{
              backgroundColor: '#f5f5f5',
              padding: '12px',
              borderRadius: '4px',
              overflow: 'auto',
              maxHeight: '200px',
              fontSize: '12px',
            }}
          >
            {typeof service.advancedConfig === 'object' ? JSON.stringify(service.advancedConfig, null, 2) : service.advancedConfig}
          </pre>
        </div>
      )}
    </ModalBody>
    <ModalFooter>
      <Button color="secondary" onClick={toggle}>
        <FontAwesomeIcon icon={faTimes} /> Close
      </Button>
    </ModalFooter>
  </Modal>
);

export default MonitoredServiceViewModal;
