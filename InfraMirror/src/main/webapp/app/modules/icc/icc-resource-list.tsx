import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Table, Spinner } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faArrowLeft } from '@fortawesome/free-solid-svg-icons';
import axios from 'axios';

const ICCResourceList = () => {
  const { instanceId } = useParams<{ instanceId: string }>();
  const [resources, setResources] = useState<any[]>([]);
  const [instance, setInstance] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [instRes, resRes] = await Promise.all([
          axios.get(`/api/integration-instances/${instanceId}`),
          axios.get(`/api/integration-resources?integrationInstanceId.equals=${instanceId}`),
        ]);
        setInstance(instRes.data);
        setResources(resRes.data);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [instanceId]);

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
          <Link to={`/icc/instances/${instance?.controlIntegrationCode}`} className="btn btn-sm btn-secondary mb-2">
            <FontAwesomeIcon icon={faArrowLeft} /> Back
          </Link>
          <h2>{instance?.name} - Resources</h2>
          <p className="text-muted">{resources.length} available resources</p>
        </div>
        <hr className="my-0" />
        <div className="table-responsive">
          <Table className="table-hover mb-0 entity-table">
            <thead>
              <tr>
                <th>Resource</th>
                <th>Display Name</th>
                <th>API Path</th>
                <th>Refresh Interval</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {resources.map(resource => (
                <tr key={resource.id}>
                  <td>
                    <code>{resource.name}</code>
                  </td>
                  <td>{resource.displayName}</td>
                  <td>
                    <code>{resource.apiPath}</code>
                  </td>
                  <td>{resource.refreshIntervalSec}s</td>
                  <td>
                    <Link to={`/icc/instances/${instanceId}/${resource.name}`} className="btn btn-sm btn-primary">
                      View Data →
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

export default ICCResourceList;
