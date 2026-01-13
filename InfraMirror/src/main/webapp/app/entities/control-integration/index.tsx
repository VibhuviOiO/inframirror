import React, { useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Table, Badge } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faPlug, faServer } from '@fortawesome/free-solid-svg-icons';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities } from './control-integration.reducer';

const ControlIntegration = () => {
  const dispatch = useAppDispatch();
  const controlIntegrationList = useAppSelector(state => state.controlIntegration.entities);
  const loading = useAppSelector(state => state.controlIntegration.loading);

  useEffect(() => {
    dispatch(getEntities());
  }, []);

  const getIcon = (iconName: string | null) => {
    switch (iconName) {
      case 'faServer':
        return faServer;
      default:
        return faPlug;
    }
  };

  return (
    <div className="entity-page">
      <div className="card shadow-sm border-0 entity-card">
        <div className="card-body">
          <h2 className="mb-0">
            <FontAwesomeIcon icon={faPlug} className="me-2" />
            Integration Control Console
          </h2>
          <p className="text-muted mb-0">Manage distributed systems through unified UI</p>
        </div>
        <hr className="my-0" />
        <div className="table-responsive">
          <Table className="table-hover mb-0 entity-table">
            <thead>
              <tr>
                <th style={{ width: '60px' }}>Icon</th>
                <th>Name</th>
                <th>Code</th>
                <th>Category</th>
                <th style={{ width: '100px' }}>Multi-DC</th>
                <th style={{ width: '100px' }}>Status</th>
                <th style={{ width: '150px' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {controlIntegrationList && controlIntegrationList.length > 0
                ? controlIntegrationList.map(integration => (
                    <tr key={integration.id}>
                      <td className="text-center">
                        <FontAwesomeIcon icon={getIcon(integration.icon)} size="lg" />
                      </td>
                      <td>
                        <strong>{integration.name}</strong>
                        {integration.description && (
                          <>
                            <br />
                            <small className="text-muted">{integration.description}</small>
                          </>
                        )}
                      </td>
                      <td>
                        <code>{integration.code}</code>
                      </td>
                      <td>{integration.category && <Badge color="info">{integration.category}</Badge>}</td>
                      <td className="text-center">{integration.supportsMultiDc ? '✓' : '—'}</td>
                      <td>
                        <Badge color={integration.isActive ? 'success' : 'secondary'}>{integration.isActive ? 'Active' : 'Inactive'}</Badge>
                      </td>
                      <td>
                        <Link to={`/icc/instances/${integration.code}`} className="btn btn-sm btn-primary">
                          View Instances →
                        </Link>
                      </td>
                    </tr>
                  ))
                : !loading && (
                    <tr>
                      <td colSpan={7} className="text-center text-muted py-4">
                        No integrations found
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

export default ControlIntegration;
