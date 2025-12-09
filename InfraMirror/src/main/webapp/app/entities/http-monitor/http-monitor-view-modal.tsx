import React from 'react';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTimes } from '@fortawesome/free-solid-svg-icons';
import { IHttpMonitor } from 'app/shared/model/http-monitor.model';

interface HttpMonitorViewModalProps {
  isOpen: boolean;
  toggle: () => void;
  monitor?: IHttpMonitor | null;
}

const formatJSON = (value: any) => {
  if (!value) return 'None';
  if (typeof value === 'object') return JSON.stringify(value, null, 2);
  return value;
};

const Field = ({ label, value }: { label: string; value: any }) => (
  <div className="row mb-2">
    <div className="col-sm-4">
      <strong>{label}:</strong>
    </div>
    <div className="col-sm-8">{value || '-'}</div>
  </div>
);

const BasicInfo = ({ monitor }: { monitor?: IHttpMonitor | null }) => (
  <div className="detail-section mb-4">
    <h5 className="section-title border-bottom pb-2 mb-3">Basic Information</h5>
    <Field label="ID" value={<code>{monitor?.id}</code>} />
    <Field label="Name" value={monitor?.name} />
    <Field
      label="URL"
      value={
        <a href={monitor?.url} target="_blank" rel="noopener noreferrer">
          {monitor?.url}
        </a>
      }
    />
    <Field label="Method" value={<span className="badge bg-info">{monitor?.method || 'GET'}</span>} />
    <Field label="Type" value={monitor?.type} />
    <Field label="Description" value={monitor?.description} />
    <Field label="Tags" value={monitor?.tags} />
    <Field
      label="Enabled"
      value={<span className={`badge ${monitor?.enabled ? 'bg-success' : 'bg-secondary'}`}>{monitor?.enabled ? 'Yes' : 'No'}</span>}
    />
    <Field label="Parent ID" value={monitor?.parentId} />
  </div>
);

const TimingConfig = ({ monitor }: { monitor?: IHttpMonitor | null }) => (
  <div className="detail-section mb-4">
    <h5 className="section-title border-bottom pb-2 mb-3">Timing Configuration</h5>
    <Field label="Interval" value={`${monitor?.intervalSeconds}s`} />
    <Field label="Timeout" value={`${monitor?.timeoutSeconds}s`} />
    <Field label="Retry Count" value={monitor?.retryCount} />
    <Field label="Retry Delay" value={`${monitor?.retryDelaySeconds}s`} />
  </div>
);

const Thresholds = ({ monitor }: { monitor?: IHttpMonitor | null }) => (
  <div className="detail-section mb-4">
    <h5 className="section-title border-bottom pb-2 mb-3">Thresholds & Budgets</h5>
    <Field label="Response Time Warning" value={monitor?.responseTimeWarningMs ? `${monitor.responseTimeWarningMs}ms` : '-'} />
    <Field label="Response Time Critical" value={monitor?.responseTimeCriticalMs ? `${monitor.responseTimeCriticalMs}ms` : '-'} />
    <Field label="Uptime Warning" value={monitor?.uptimeWarningPercent ? `${monitor.uptimeWarningPercent}%` : '-'} />
    <Field label="Uptime Critical" value={monitor?.uptimeCriticalPercent ? `${monitor.uptimeCriticalPercent}%` : '-'} />
    <Field label="Performance Budget" value={monitor?.performanceBudgetMs ? `${monitor.performanceBudgetMs}ms` : '-'} />
    <Field label="Size Budget" value={monitor?.sizeBudgetKb ? `${monitor.sizeBudgetKb}KB` : '-'} />
  </div>
);

const ValidationOptions = ({ monitor }: { monitor?: IHttpMonitor | null }) => (
  <div className="detail-section mb-4">
    <h5 className="section-title border-bottom pb-2 mb-3">Validation & Options</h5>
    <Field label="Expected Status Codes" value={monitor?.expectedStatusCodes} />
    <Field label="Include Response Body" value={monitor?.includeResponseBody ? 'Yes' : 'No'} />
    <Field label="Check SSL Certificate" value={monitor?.checkSslCertificate ? 'Yes' : 'No'} />
    <Field label="Ignore TLS Error" value={monitor?.ignoreTlsError ? 'Yes' : 'No'} />
    <Field label="Check DNS Resolution" value={monitor?.checkDnsResolution ? 'Yes' : 'No'} />
    <Field label="Upside Down Mode" value={monitor?.upsideDownMode ? 'Yes' : 'No'} />
    <Field label="Max Redirects" value={monitor?.maxRedirects ?? '-'} />
    <Field label="Certificate Expiry Days" value={monitor?.certificateExpiryDays ?? '-'} />
    <Field label="Resend Notification Count" value={monitor?.resendNotificationCount ?? '-'} />
  </div>
);

const HeadersSection = ({ monitor }: { monitor?: IHttpMonitor | null }) => (
  <div className="detail-section mb-4">
    <h5 className="section-title border-bottom pb-2 mb-3">Headers</h5>
    {monitor?.headers ? (
      <pre
        style={{
          backgroundColor: '#f5f5f5',
          padding: '12px',
          borderRadius: '4px',
          overflow: 'auto',
          maxHeight: '200px',
          fontSize: '12px',
          lineHeight: '1.4',
        }}
      >
        {formatJSON(monitor.headers)}
      </pre>
    ) : (
      <span className="text-muted">No headers</span>
    )}
  </div>
);

const BodySection = ({ monitor }: { monitor?: IHttpMonitor | null }) => (
  <div className="detail-section">
    <h5 className="section-title border-bottom pb-2 mb-3">Request Body</h5>
    {monitor?.body ? (
      <pre
        style={{
          backgroundColor: '#f5f5f5',
          padding: '12px',
          borderRadius: '4px',
          overflow: 'auto',
          maxHeight: '200px',
          fontSize: '12px',
          lineHeight: '1.4',
        }}
      >
        {formatJSON(monitor.body)}
      </pre>
    ) : (
      <span className="text-muted">No body</span>
    )}
  </div>
);

export const HttpMonitorViewModal: React.FC<HttpMonitorViewModalProps> = ({ isOpen, toggle, monitor }) => (
  <Modal isOpen={isOpen} toggle={toggle} size="lg" centered>
    <ModalHeader toggle={toggle}>Monitor Details: {monitor?.name}</ModalHeader>
    <ModalBody style={{ maxHeight: '70vh', overflowY: 'auto' }}>
      <div className="view-modal-content">
        <BasicInfo monitor={monitor} />
        <TimingConfig monitor={monitor} />
        <Thresholds monitor={monitor} />
        <ValidationOptions monitor={monitor} />
        <HeadersSection monitor={monitor} />
        <BodySection monitor={monitor} />
      </div>
    </ModalBody>
    <ModalFooter>
      <Button color="secondary" onClick={toggle}>
        <FontAwesomeIcon icon={faTimes} /> Close
      </Button>
    </ModalFooter>
  </Modal>
);

export default HttpMonitorViewModal;
