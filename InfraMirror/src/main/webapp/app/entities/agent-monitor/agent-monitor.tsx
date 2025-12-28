import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Input, Table } from 'reactstrap';
import { Translate, getSortState, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp, faPlus } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { AgentMonitorEditModal } from './agent-monitor-edit-modal';
import { AgentMonitorDeleteModal } from './agent-monitor-delete-modal';

import { getEntities } from './agent-monitor.reducer';
import { getEntities as getAgents } from '../agent/agent.reducer';

export const AgentMonitor = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [filterAgentId, setFilterAgentId] = useState('');
  const [filterMonitorType, setFilterMonitorType] = useState('');
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [editModalOpen, setEditModalOpen] = useState(false);
  const [selectedAgentMonitor, setSelectedAgentMonitor] = useState(null);
  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const agentMonitorList = useAppSelector(state => state.agentMonitor.entities);
  const loading = useAppSelector(state => state.agentMonitor.loading);
  const agents = useAppSelector(state => state.agent.entities);

  useEffect(() => {
    dispatch(getAgents({}));
  }, []);

  const getAllEntities = () => {
    const params: any = {
      sort: `${sortState.sort},${sortState.order}`,
    };

    if (filterAgentId) {
      params['agentId.equals'] = filterAgentId;
    }
    if (filterMonitorType) {
      params['monitorType.equals'] = filterMonitorType;
    }

    dispatch(getEntities(params));
  };

  const clear = () => {
    setSearch('');
    setFilterAgentId('');
    setFilterMonitorType('');
    dispatch(getEntities({}));
  };

  const handleSearch = event => setSearch(event.target.value);

  const filteredList = search
    ? agentMonitorList.filter(am => am.monitorName?.toLowerCase().includes(search.toLowerCase()))
    : agentMonitorList;

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort, filterAgentId, filterMonitorType]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const order = sortState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  const handleDelete = agentMonitor => {
    setSelectedAgentMonitor(agentMonitor);
    setDeleteModalOpen(true);
  };

  const handleDeleteSuccess = () => {
    setDeleteModalOpen(false);
    setSelectedAgentMonitor(null);
    sortEntities();
  };

  const handleCreate = () => {
    setSelectedAgentMonitor(null);
    setEditModalOpen(true);
  };

  const handleEdit = agentMonitor => {
    setSelectedAgentMonitor(agentMonitor);
    setEditModalOpen(true);
  };

  const handleSaveSuccess = () => {
    setEditModalOpen(false);
    setSelectedAgentMonitor(null);
    sortEntities();
  };

  return (
    <div className="row g-3">
      <div className={editModalOpen ? 'col-md-6' : 'col-md-12'}>
        <div className="d-flex justify-content-between align-items-center mb-3">
          <h5 className="mb-0">
            <Translate contentKey="infraMirrorApp.agentMonitor.home.title">Agent Monitors</Translate>
          </h5>
          <div className="d-flex gap-2">
            <Button color="info" size="sm" onClick={handleSyncList} disabled={loading}>
              <FontAwesomeIcon icon="sync" spin={loading} />
            </Button>
            <Button color="primary" size="sm" onClick={handleCreate}>
              <FontAwesomeIcon icon={faPlus} className="me-1" />
              Assign Monitor
            </Button>
          </div>
        </div>
        <div className="mb-3">
          <div className="row g-2">
            <div className="col-md-4">
              <Input type="select" value={filterAgentId} onChange={e => setFilterAgentId(e.target.value)}>
                <option value="">All Agents</option>
                {agents?.map(agent => (
                  <option key={agent.id} value={agent.id}>
                    {agent.name}
                  </option>
                ))}
              </Input>
            </div>
            <div className="col-md-4">
              <Input type="select" value={filterMonitorType} onChange={e => setFilterMonitorType(e.target.value)}>
                <option value="">All Monitor Types</option>
                <option value="HTTP">HTTP</option>
                <option value="INSTANCE">Instance</option>
                <option value="SERVICE">Service</option>
              </Input>
            </div>
            <div className="col-md-4">
              <div className="d-flex gap-2">
                <Input type="text" name="search" value={search} onChange={handleSearch} placeholder="Search..." style={{ flex: 1 }} />
                {(search || filterAgentId || filterMonitorType) && (
                  <Button color="secondary" size="sm" onClick={clear}>
                    <FontAwesomeIcon icon="times" />
                  </Button>
                )}
              </div>
            </div>
          </div>
        </div>
        <div className="table-responsive" style={{ position: 'relative', minHeight: '200px' }}>
          {loading && (
            <div
              style={{
                position: 'absolute',
                top: 0,
                left: 0,
                right: 0,
                bottom: 0,
                backgroundColor: 'rgba(255, 255, 255, 0.8)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                zIndex: 10,
              }}
            >
              <FontAwesomeIcon icon="spinner" spin size="2x" />
            </div>
          )}
          {filteredList && filteredList.length > 0 ? (
            <Table responsive striped hover>
              <thead>
                <tr>
                  <th className="hand" onClick={sort('agent.name')}>
                    Agent <FontAwesomeIcon icon={getSortIconByFieldName('agent.name')} />
                  </th>
                  <th>Monitor Type</th>
                  <th>Monitor Name</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredList.map((agentMonitor, i) => (
                  <tr key={`entity-${i}`} data-cy="entityTable">
                    <td>
                      <strong>{agentMonitor.agent?.name || agentMonitor.agent?.id}</strong>
                    </td>
                    <td>
                      {agentMonitor.monitorType === 'HTTP' && <span className="badge bg-info">HTTP</span>}
                      {agentMonitor.monitorType === 'INSTANCE' && <span className="badge bg-primary">Instance</span>}
                      {agentMonitor.monitorType === 'SERVICE' && <span className="badge bg-secondary">Service</span>}
                    </td>
                    <td>{agentMonitor.monitorName || `#${agentMonitor.monitorId}`}</td>
                    <td>
                      {agentMonitor.active ? (
                        <span className="badge bg-success">Active</span>
                      ) : (
                        <span className="badge bg-warning">Inactive</span>
                      )}
                    </td>
                    <td>
                      <div className="d-flex gap-1">
                        <Button onClick={() => handleEdit(agentMonitor)} color="link" size="sm" title="Edit" style={{ padding: 0 }}>
                          <FontAwesomeIcon icon="pencil-alt" />
                        </Button>
                        <Button
                          onClick={() => handleDelete(agentMonitor)}
                          color="link"
                          size="sm"
                          title="Delete"
                          style={{ padding: 0, color: '#dc3545', marginLeft: '0.5rem' }}
                        >
                          <FontAwesomeIcon icon="trash" />
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          ) : (
            !loading && (
              <div className="text-center py-5">
                <FontAwesomeIcon icon="inbox" size="3x" className="text-muted mb-3" />
                <h5 className="text-muted">No agent monitors assigned. Assign monitors to agents to get started.</h5>
                <Button color="primary" className="mt-3" onClick={handleCreate}>
                  <FontAwesomeIcon icon="plus" /> Assign Monitor
                </Button>
              </div>
            )
          )}
        </div>
      </div>
      <div className="col-md-6">
        <AgentMonitorEditModal
          isOpen={editModalOpen}
          toggle={() => setEditModalOpen(false)}
          agentMonitor={selectedAgentMonitor}
          onSave={handleSaveSuccess}
        />
      </div>

      <AgentMonitorDeleteModal
        isOpen={deleteModalOpen}
        toggle={() => setDeleteModalOpen(false)}
        agentMonitor={selectedAgentMonitor}
        onDelete={handleDeleteSuccess}
      />
    </div>
  );
};

export default AgentMonitor;
