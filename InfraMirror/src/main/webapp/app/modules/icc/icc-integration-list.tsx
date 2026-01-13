import React, { useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Table, Badge, Spinner } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faPlug, faServer, faSearch, faCube } from '@fortawesome/free-solid-svg-icons';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getIntegrations } from './icc.reducer';

const iconMap = {
  faServer,
  faSearch,
  faPlug,
  faCube,
};

const ICCIntegrationList = () => {
  const dispatch = useAppDispatch();
  const integrations = useAppSelector(state => state.icc.integrations);
  const loading = useAppSelector(state => state.icc.loading);

  useEffect(() => {
    dispatch(getIntegrations());
  }, []);

  if (loading) {
    return (
      <div className="text-center p-5">
        <Spinner color="primary" />
      </div>
    );
  }

  return (
    <div className="entity-page">
      <div className="card shadow-sm border-0 entity-card">
        <div className="card-body">
          <h2>
            <FontAwesomeIcon icon={faPlug} className="me-2" />
            Integration Control Console
          </h2>
          <p className="text-muted">Manage distributed systems through unified UI</p>
        </div>
        <hr className="my-0" />
        <div className="table-responsive">
          <Table className="table-hover mb-0 entity-table">
            <thead>
              <tr>
                <th>Icon</th>
                <th>Name</th>
                <th>Category</th>
                <th>Multi-DC</th>
                <th>Write Support</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {integrations.map(integration => (
                <tr key={integration.id}>
                  <td>
                    <FontAwesomeIcon icon={iconMap[integration.icon] || faPlug} size="lg" />
                  </td>
                  <td>
                    <strong>{integration.name}</strong>
                    <br />
                    <small className="text-muted">{integration.description}</small>
                  </td>
                  <td>
                    <Badge color="info">{integration.category}</Badge>
                  </td>
                  <td>{integration.supportsMultiDc ? '✓' : '—'}</td>
                  <td>{integration.supportsWrite ? '✓' : '—'}</td>
                  <td>
                    <Badge color={integration.isActive ? 'success' : 'secondary'}>{integration.isActive ? 'Active' : 'Inactive'}</Badge>
                  </td>
                  <td>
                    <Link to={`/icc/instances/${integration.code}`} className="btn btn-sm btn-primary">
                      View Instances →
                    </Link>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        </div>
      </div>
    </div>
  );
};

export default ICCIntegrationList;
