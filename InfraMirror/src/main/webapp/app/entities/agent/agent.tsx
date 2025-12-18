import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Input, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp, faPlus } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { AgentEditModal } from './agent-edit-modal';
import { AgentDeleteModal } from './agent-delete-modal';

import { getEntities, searchEntities } from './agent.reducer';

export const Agent = () => {
  const dispatch = useAppDispatch();
  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [editModalOpen, setEditModalOpen] = useState(false);
  const [selectedAgent, setSelectedAgent] = useState(null);
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const agentList = useAppSelector(state => state.agent.entities);
  const loading = useAppSelector(state => state.agent.loading);
  const totalItems = useAppSelector(state => state.agent.totalItems);

  const getAllEntities = () => {
    if (search) {
      dispatch(
        searchEntities({
          query: search,
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        }),
      );
    } else {
      dispatch(
        getEntities({
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        }),
      );
    }
  };

  const startSearching = e => {
    if (search) {
      setPaginationState({ ...paginationState, activePage: 1 });
      dispatch(
        searchEntities({
          query: search,
          page: 0,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        }),
      );
    }
    e.preventDefault();
  };

  const clear = () => {
    setSearch('');
    setPaginationState({ ...paginationState, activePage: 1 });
    dispatch(getEntities({}));
  };

  const handleSearch = event => setSearch(event.target.value);

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort, search]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  const handleDelete = agent => {
    setSelectedAgent(agent);
    setDeleteModalOpen(true);
  };

  const handleDeleteSuccess = () => {
    setDeleteModalOpen(false);
    setSelectedAgent(null);
    sortEntities();
  };

  const handleCreate = () => {
    setSelectedAgent(null);
    setEditModalOpen(true);
  };

  const handleEdit = agent => {
    setSelectedAgent(agent);
    setEditModalOpen(true);
  };

  const handleSaveSuccess = () => {
    setEditModalOpen(false);
    setSelectedAgent(null);
    sortEntities();
  };

  return (
    <div className="row g-3">
      <div className={editModalOpen ? 'col-md-6' : 'col-md-12'}>
        <div className="d-flex justify-content-between align-items-center mb-3">
          <h5 className="mb-0">
            <Translate contentKey="infraMirrorApp.agent.home.title">Agents</Translate>
          </h5>
          <div className="d-flex gap-2">
            <Button color="info" size="sm" onClick={handleSyncList} disabled={loading}>
              <FontAwesomeIcon icon="sync" spin={loading} />
            </Button>
            <Button color="primary" size="sm" onClick={handleCreate}>
              <FontAwesomeIcon icon={faPlus} className="me-1" />
              New Agent
            </Button>
          </div>
        </div>
        <div className="mb-3">
          <div className="d-flex gap-2">
            <Input
              type="text"
              name="search"
              value={search}
              onChange={handleSearch}
              placeholder={translate('infraMirrorApp.agent.home.search')}
              style={{ flex: 1 }}
            />
            <Button color="primary" size="sm" onClick={startSearching} disabled={!search}>
              <FontAwesomeIcon icon="search" />
            </Button>
            {search && (
              <Button color="secondary" size="sm" onClick={clear}>
                <FontAwesomeIcon icon="times" />
              </Button>
            )}
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
          {agentList && agentList.length > 0 ? (
            <Table responsive striped hover>
              <thead>
                <tr>
                  <th className="hand" onClick={sort('name')}>
                    Name <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                  </th>
                  <th>Hostname</th>
                  <th>IP Address</th>
                  <th>OS</th>
                  <th>Version</th>
                  <th>Status</th>
                  <th>Datacenter</th>
                  <th>Region</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {agentList.map((agent, i) => (
                  <tr key={`entity-${i}`} data-cy="entityTable">
                    <td>
                      <strong>{agent.name}</strong>
                    </td>
                    <td>
                      <small className="text-muted">{agent.hostname || '-'}</small>
                    </td>
                    <td>
                      <small className="text-muted">{agent.ipAddress || '-'}</small>
                    </td>
                    <td>
                      <small>{agent.osType || '-'}</small>
                    </td>
                    <td>
                      <small className="text-muted">{agent.agentVersion || '-'}</small>
                    </td>
                    <td>
                      {agent.status === 'ACTIVE' && <span className="badge bg-success">Active</span>}
                      {agent.status === 'INACTIVE' && <span className="badge bg-warning">Inactive</span>}
                      {agent.status === 'OFFLINE' && <span className="badge bg-danger">Offline</span>}
                      {!agent.status && <span className="badge bg-secondary">Unknown</span>}
                    </td>
                    <td>
                      {agent.datacenter ? (
                        <Link to={`/datacenter/${agent.datacenter.id}`}>
                          <small>{agent.datacenter.name}</small>
                        </Link>
                      ) : (
                        '-'
                      )}
                    </td>
                    <td>
                      {agent.region ? (
                        <Link to={`/region/${agent.region.id}`}>
                          <small>{agent.region.name}</small>
                        </Link>
                      ) : (
                        '-'
                      )}
                    </td>
                    <td>
                      <div className="d-flex gap-1">
                        <Button onClick={() => handleEdit(agent)} color="link" size="sm" title="Edit" style={{ padding: 0 }}>
                          <FontAwesomeIcon icon="pencil-alt" />
                        </Button>
                        <Button
                          onClick={() => handleDelete(agent)}
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
                <h5 className="text-muted">No agents available. Create your first agent to get started.</h5>
                <Button color="primary" className="mt-3" onClick={handleCreate}>
                  <FontAwesomeIcon icon="plus" /> Create Agent
                </Button>
              </div>
            )
          )}
        </div>
        {totalItems ? (
          <div className={agentList && agentList.length > 0 ? 'd-flex justify-content-between align-items-center' : 'd-none'}>
            <div>
              <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
            </div>
            <div>
              <JhiPagination
                activePage={paginationState.activePage}
                onSelect={handlePagination}
                maxButtons={5}
                itemsPerPage={paginationState.itemsPerPage}
                totalItems={totalItems}
              />
            </div>
          </div>
        ) : (
          ''
        )}
      </div>
      <div className="col-md-6">
        <AgentEditModal isOpen={editModalOpen} toggle={() => setEditModalOpen(false)} agent={selectedAgent} onSave={handleSaveSuccess} />
      </div>

      <AgentDeleteModal
        isOpen={deleteModalOpen}
        toggle={() => setDeleteModalOpen(false)}
        agent={selectedAgent}
        onDelete={handleDeleteSuccess}
      />
    </div>
  );
};

export default Agent;
