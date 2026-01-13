import React, { useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Table, Badge } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faArrowLeft } from '@fortawesome/free-solid-svg-icons';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntitiesByIntegrationCode } from './integration-instance.reducer';
import { getEntityByCode } from '../control-integration/control-integration.reducer';

const IntegrationInstance = () => {
  const dispatch = useAppDispatch();
  const { code } = useParams<{ code: string }>();
  const instanceList = useAppSelector(state => state.integrationInstance.entities);
  const integration = useAppSelector(state => state.controlIntegration.entity);
  const loading = useAppSelector(state => state.integrationInstance.loading);

  useEffect(() => {
    if (code) {
      dispatch(getEntityByCode(code));
      dispatch(getEntitiesByIntegrationCode(code));
    }
  }, [code]);

  const getEnvColor = (env: string | null) => {
    switch (env) {
      case 'PROD':
        return 'danger';
      case 'STAGE':
        return 'warning';
      case 'QA':
        return 'info';
      case 'DEV':
        return 'secondary';
      default:
        return 'secondary';
    }
  };

  return (
    <div className="entity-page">
      <div className="card shadow-sm border-0 entity-card">
        <div className="card-body">
          <div className="d-flex justify-content-between align-items-center">
            <div>
              <h2 className="mb-0">{integration.name} Instances</h2>
              <p className="text-muted mb-0">
                {instanceList.length} instance{instanceList.length !== 1 ? 's' : ''} configured
              </p>
            </div>
            <Link to="/icc" className="btn btn-secondary">
              <FontAwesomeIcon icon={faArrowLeft} className="me-1" />
              Back to Integrations
            </Link>
          </div>
        </div>
        <hr className="my-0" />
        <div className="table-responsive">
          <Table className="table-hover mb-0 entity-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Type</th>
                <th>Datacenter</th>
                <th>Environment</th>
                <th>Linked Entity</th>
                <th>Status</th>
                <th style={{ width: '150px' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {instanceList && instanceList.length > 0
                ? instanceList.map(instance => (
                    <tr key={instance.id}>
                      <td>
                        <strong>{instance.name}</strong>
                      </td>
                      <td>
                        <Badge color="secondary">{instance.instanceType}</Badge>
                      </td>
                      <td>{instance.datacenterName || '—'}</td>
                      <td>{instance.environment && <Badge color={getEnvColor(instance.environment)}>{instance.environment}</Badge>}</td>
                      <td>
                        {instance.monitoredServiceName && <small className="text-muted">Service: {instance.monitoredServiceName}</small>}
                        {instance.httpMonitorName && <small className="text-muted">Monitor: {instance.httpMonitorName}</small>}
                        {instance.baseUrl && <small className="text-muted">{instance.baseUrl}</small>}
                      </td>
                      <td>
                        <Badge color={instance.isActive ? 'success' : 'secondary'}>{instance.isActive ? 'Active' : 'Inactive'}</Badge>
                      </td>
                      <td>
                        <Link to={`/icc/view/${code}/apps`} className="btn btn-sm btn-primary">
                          View Data →
                        </Link>
                      </td>
                    </tr>
                  ))
                : !loading && (
                    <tr>
                      <td colSpan={7} className="text-center text-muted py-4">
                        No instances found
                      </td>
                    </tr>
                  )}
            </tbody>
          </Table>
        </div>
      </div>
    </div>
  );
};

export default IntegrationInstance;
