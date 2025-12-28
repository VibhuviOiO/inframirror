import React, { useEffect, useState } from 'react';
import { Table, Badge } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { APP_TIMESTAMP_FORMAT } from 'app/config/constants';
import axios from 'axios';

export const AgentLock = () => {
  const [locks, setLocks] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchLocks = async () => {
    try {
      const response = await axios.get('/api/agent-locks');
      setLocks(response.data);
    } catch (error) {
      console.error('Error fetching agent locks:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLocks();
    const interval = setInterval(fetchLocks, 10000);
    return () => clearInterval(interval);
  }, []);

  const isExpired = lock => new Date(lock.expiresAt) < new Date();

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h5 className="mb-0">Agent Locks (HA Status)</h5>
        <FontAwesomeIcon icon="sync" spin={loading} className="text-muted" />
      </div>

      {locks.length > 0 ? (
        <Table responsive striped>
          <thead>
            <tr>
              <th>Agent ID</th>
              <th>Lock ID</th>
              <th>Acquired At</th>
              <th>Expires At</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {locks.map(lock => (
              <tr key={lock.id}>
                <td>
                  <strong>Agent #{lock.agentId}</strong>
                </td>
                <td>{lock.id}</td>
                <td>
                  <TextFormat type="date" value={lock.acquiredAt} format={APP_TIMESTAMP_FORMAT} />
                </td>
                <td>
                  <TextFormat type="date" value={lock.expiresAt} format={APP_TIMESTAMP_FORMAT} />
                </td>
                <td>{isExpired(lock) ? <Badge color="danger">Expired</Badge> : <Badge color="success">Active</Badge>}</td>
              </tr>
            ))}
          </tbody>
        </Table>
      ) : (
        <div className="text-center py-5 text-muted">
          <FontAwesomeIcon icon="lock" size="3x" className="mb-3" />
          <h5>No active agent locks</h5>
          <p>Locks will appear when agents acquire them for monitoring</p>
        </div>
      )}
    </div>
  );
};

export default AgentLock;
