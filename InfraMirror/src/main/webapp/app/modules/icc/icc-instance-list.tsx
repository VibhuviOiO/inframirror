import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Table, Badge, Spinner } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faArrowLeft } from '@fortawesome/free-solid-svg-icons';
import axios from 'axios';

const ICCInstanceList = () => {
  const { code } = useParams<{ code: string }>();
  const [instances, setInstances] = useState<any[]>([]);
  const [integration, setIntegration] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [intRes, instRes] = await Promise.all([
          axios.get(`/api/control-integrations?code.equals=${code}`),
          axios.get(`/api/integration-instances?controlIntegrationCode.equals=${code}`),
        ]);
        setIntegration(intRes.data[0]);
        setInstances(instRes.data);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [code]);

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
          <Link to="/icc" className="btn btn-sm btn-secondary mb-2">
            <FontAwesomeIcon icon={faArrowLeft} /> Back
          </Link>
          <h2>{integration?.name} - Instances</h2>
          <p className="text-muted">{instances.length} configured instances</p>
        </div>
        <hr className="my-0" />
        <div className="table-responsive">
          <Table className="table-hover mb-0 entity-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Type</th>
                <th>Environment</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {instances.map(instance => (
                <tr key={instance.id}>
                  <td>
                    <strong>{instance.name}</strong>
                  </td>
                  <td>
                    <Badge color="info">{instance.instanceType}</Badge>
                  </td>
                  <td>
                    <Badge color={instance.environment === 'PROD' ? 'danger' : 'secondary'}>{instance.environment}</Badge>
                  </td>
                  <td>
                    <Badge color={instance.isActive ? 'success' : 'secondary'}>{instance.isActive ? 'Active' : 'Inactive'}</Badge>
                  </td>
                  <td>
                    <Link to={`/icc/${code}/apps`} className="btn btn-sm btn-primary">
                      View Apps →
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

export default ICCInstanceList;
