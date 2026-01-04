import React, { useEffect, useState, useCallback } from 'react';
import { Button, Form, FormGroup, Label, Input, FormFeedback } from 'reactstrap';
import { Translate, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { toast } from 'react-toastify';
import { IHttpMonitor } from 'app/shared/model/http-monitor.model';
import { useAppDispatch } from 'app/config/store';
import { createEntity, updateEntity } from './http-monitor.reducer';

interface HttpMonitorSidePanelProps {
  isOpen: boolean;
  onClose: () => void;
  monitor: IHttpMonitor | null;
  onSuccess: () => void;
}

const HttpMonitorSidePanel: React.FC<HttpMonitorSidePanelProps> = ({ isOpen, onClose, monitor, onSuccess }) => {
  const dispatch = useAppDispatch();
  const [formData, setFormData] = useState<IHttpMonitor>({
    name: '',
    url: '',
    method: 'GET',
    type: 'HTTPS',
    intervalSeconds: 60,
    timeoutSeconds: 30,
    retryCount: 2,
    retryDelaySeconds: 5,
    responseTimeWarningMs: 500,
    responseTimeCriticalMs: 1000,
    description: '',
    expectedStatusCodes: '200',
    maxRedirects: 5,
    checkSslCertificate: true,
    ignoreTlsError: false,
    enabled: true,
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (monitor) {
      setFormData(monitor);
    } else {
      setFormData({
        name: '',
        url: '',
        method: 'GET',
        type: 'HTTPS',
        intervalSeconds: 60,
        timeoutSeconds: 30,
        retryCount: 2,
        retryDelaySeconds: 5,
        responseTimeWarningMs: 500,
        responseTimeCriticalMs: 1000,
        description: '',
        expectedStatusCodes: '200',
        maxRedirects: 5,
        checkSslCertificate: true,
        ignoreTlsError: false,
        enabled: true,
      });
    }
    setErrors({});
  }, [monitor, isOpen]);

  useEffect(() => {
    const handleEscape = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && isOpen) {
        onClose();
      }
    };
    document.addEventListener('keydown', handleEscape);
    return () => document.removeEventListener('keydown', handleEscape);
  }, [isOpen, onClose]);

  const handleChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({ ...prev, [name]: type === 'checkbox' ? checked : value }));
    setErrors(prev => ({ ...prev, [name]: '' }));
  }, []);

  const validate = useCallback(() => {
    const newErrors: Record<string, string> = {};
    if (!formData.name?.trim()) newErrors.name = translate('entity.validation.required');
    if (!formData.url?.trim()) newErrors.url = translate('entity.validation.required');
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  }, [formData.name, formData.url]);

  const handleSubmit = useCallback(
    async (e: React.FormEvent) => {
      e.preventDefault();
      if (!validate()) return;

      setLoading(true);
      try {
        const action = monitor?.id ? updateEntity(formData) : createEntity(formData);
        await dispatch(action).unwrap();
        toast.success(
          translate(monitor?.id ? 'infraMirrorApp.httpMonitor.updated' : 'infraMirrorApp.httpMonitor.created', { param: formData.name }),
        );
        onClose();
        onSuccess();
      } catch (error) {
        toast.error(translate('error.http.500'));
      } finally {
        setLoading(false);
      }
    },
    [dispatch, formData, monitor, validate, onClose, onSuccess],
  );

  if (!isOpen) return null;

  return (
    <>
      <div className="side-panel-overlay" onClick={onClose} />
      <div className={`side-panel ${isOpen ? 'open' : ''}`}>
        <div className="side-panel-header">
          <h5>
            {monitor ? (
              <Translate contentKey="infraMirrorApp.httpMonitor.home.editLabel">Edit HTTP Monitor</Translate>
            ) : (
              <Translate contentKey="infraMirrorApp.httpMonitor.home.createLabel">Create HTTP Monitor</Translate>
            )}
          </h5>
          <Button close onClick={onClose} />
        </div>
        <div className="side-panel-body">
          <Form onSubmit={handleSubmit}>
            <FormGroup>
              <Label for="name">
                <Translate contentKey="infraMirrorApp.httpMonitor.name">Name</Translate> <span className="text-danger">*</span>
              </Label>
              <Input type="text" name="name" id="name" value={formData.name} onChange={handleChange} invalid={!!errors.name} />
              {errors.name && <FormFeedback>{errors.name}</FormFeedback>}
            </FormGroup>

            <FormGroup>
              <Label for="url">
                <Translate contentKey="infraMirrorApp.httpMonitor.url">URL</Translate> <span className="text-danger">*</span>
              </Label>
              <Input type="text" name="url" id="url" value={formData.url} onChange={handleChange} invalid={!!errors.url} />
              {errors.url && <FormFeedback>{errors.url}</FormFeedback>}
            </FormGroup>

            <FormGroup>
              <Label for="description">
                <Translate contentKey="infraMirrorApp.httpMonitor.description">Description</Translate>
              </Label>
              <Input type="textarea" name="description" id="description" value={formData.description} onChange={handleChange} rows={2} />
            </FormGroup>

            <div className="row">
              <div className="col-md-6">
                <FormGroup>
                  <Label for="method">
                    <Translate contentKey="infraMirrorApp.httpMonitor.method">Method</Translate>
                  </Label>
                  <Input type="select" name="method" id="method" value={formData.method} onChange={handleChange}>
                    <option value="GET">GET</option>
                    <option value="POST">POST</option>
                    <option value="PUT">PUT</option>
                    <option value="DELETE">DELETE</option>
                    <option value="PATCH">PATCH</option>
                    <option value="HEAD">HEAD</option>
                  </Input>
                </FormGroup>
              </div>
              <div className="col-md-6">
                <FormGroup>
                  <Label for="type">
                    <Translate contentKey="infraMirrorApp.httpMonitor.type">Type</Translate>
                  </Label>
                  <Input type="select" name="type" id="type" value={formData.type} onChange={handleChange}>
                    <option value="HTTP">HTTP</option>
                    <option value="HTTPS">HTTPS</option>
                  </Input>
                </FormGroup>
              </div>
            </div>

            <div className="row">
              <div className="col-md-6">
                <FormGroup>
                  <Label for="intervalSeconds">
                    <Translate contentKey="infraMirrorApp.httpMonitor.intervalSeconds">Interval (seconds)</Translate>
                  </Label>
                  <Input
                    type="number"
                    name="intervalSeconds"
                    id="intervalSeconds"
                    value={formData.intervalSeconds}
                    onChange={handleChange}
                  />
                </FormGroup>
              </div>
              <div className="col-md-6">
                <FormGroup>
                  <Label for="timeoutSeconds">
                    <Translate contentKey="infraMirrorApp.httpMonitor.timeoutSeconds">Timeout (seconds)</Translate>
                  </Label>
                  <Input type="number" name="timeoutSeconds" id="timeoutSeconds" value={formData.timeoutSeconds} onChange={handleChange} />
                </FormGroup>
              </div>
            </div>

            <div className="row">
              <div className="col-md-6">
                <FormGroup>
                  <Label for="retryCount">
                    <Translate contentKey="infraMirrorApp.httpMonitor.retryCount">Retry Count</Translate>
                  </Label>
                  <Input type="number" name="retryCount" id="retryCount" value={formData.retryCount} onChange={handleChange} />
                </FormGroup>
              </div>
              <div className="col-md-6">
                <FormGroup>
                  <Label for="retryDelaySeconds">
                    <Translate contentKey="infraMirrorApp.httpMonitor.retryDelaySeconds">Retry Delay (seconds)</Translate>
                  </Label>
                  <Input
                    type="number"
                    name="retryDelaySeconds"
                    id="retryDelaySeconds"
                    value={formData.retryDelaySeconds}
                    onChange={handleChange}
                  />
                </FormGroup>
              </div>
            </div>

            <div className="row">
              <div className="col-md-6">
                <FormGroup>
                  <Label for="responseTimeWarningMs">
                    <Translate contentKey="infraMirrorApp.httpMonitor.responseTimeWarningMs">Warning Threshold (ms)</Translate>
                  </Label>
                  <Input
                    type="number"
                    name="responseTimeWarningMs"
                    id="responseTimeWarningMs"
                    value={formData.responseTimeWarningMs}
                    onChange={handleChange}
                  />
                </FormGroup>
              </div>
              <div className="col-md-6">
                <FormGroup>
                  <Label for="responseTimeCriticalMs">
                    <Translate contentKey="infraMirrorApp.httpMonitor.responseTimeCriticalMs">Critical Threshold (ms)</Translate>
                  </Label>
                  <Input
                    type="number"
                    name="responseTimeCriticalMs"
                    id="responseTimeCriticalMs"
                    value={formData.responseTimeCriticalMs}
                    onChange={handleChange}
                  />
                </FormGroup>
              </div>
            </div>

            <div className="row">
              <div className="col-md-6">
                <FormGroup>
                  <Label for="expectedStatusCodes">
                    <Translate contentKey="infraMirrorApp.httpMonitor.expectedStatusCodes">Expected Status Codes</Translate>
                  </Label>
                  <Input
                    type="text"
                    name="expectedStatusCodes"
                    id="expectedStatusCodes"
                    value={formData.expectedStatusCodes}
                    onChange={handleChange}
                    placeholder="200,201,204"
                  />
                </FormGroup>
              </div>
              <div className="col-md-6">
                <FormGroup>
                  <Label for="maxRedirects">
                    <Translate contentKey="infraMirrorApp.httpMonitor.maxRedirects">Max Redirects</Translate>
                  </Label>
                  <Input type="number" name="maxRedirects" id="maxRedirects" value={formData.maxRedirects} onChange={handleChange} />
                </FormGroup>
              </div>
            </div>

            <div className="row">
              <div className="col-md-6">
                <FormGroup check>
                  <Label check>
                    <Input
                      type="checkbox"
                      name="checkSslCertificate"
                      id="checkSslCertificate"
                      checked={formData.checkSslCertificate}
                      onChange={handleChange}
                    />{' '}
                    <Translate contentKey="infraMirrorApp.httpMonitor.checkSslCertificate">Check SSL Certificate</Translate>
                  </Label>
                </FormGroup>
              </div>
              <div className="col-md-6">
                <FormGroup check>
                  <Label check>
                    <Input
                      type="checkbox"
                      name="ignoreTlsError"
                      id="ignoreTlsError"
                      checked={formData.ignoreTlsError}
                      onChange={handleChange}
                    />{' '}
                    <Translate contentKey="infraMirrorApp.httpMonitor.ignoreTlsError">Ignore TLS Errors</Translate>
                  </Label>
                </FormGroup>
              </div>
            </div>
          </Form>
        </div>
        <div className="side-panel-footer">
          <Button color="secondary" onClick={onClose} disabled={loading}>
            <Translate contentKey="entity.action.cancel">Cancel</Translate>
          </Button>
          <Button color="primary" onClick={handleSubmit} disabled={loading} data-cy="entityCreateSaveButton">
            {loading ? <FontAwesomeIcon icon="spinner" spin /> : <FontAwesomeIcon icon="save" />}{' '}
            <Translate contentKey="entity.action.save">Save</Translate>
          </Button>
        </div>
      </div>
    </>
  );
};

export default HttpMonitorSidePanel;
