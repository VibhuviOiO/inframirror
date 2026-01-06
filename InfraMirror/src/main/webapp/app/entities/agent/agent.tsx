import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Button, Input, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faSort,
  faSortDown,
  faSortUp,
  faPlus,
  faUserSecret,
  faTimes,
  faSync,
  faSpinner,
  faPencilAlt,
  faTrash,
  faChevronLeft,
  faChevronRight,
} from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { AgentEditModal } from './agent-edit-modal';
import { AgentDeleteModal } from './agent-delete-modal';
import { IAgent } from 'app/shared/model/agent.model';

import { getEntities, searchEntities } from './agent.reducer';

export const Agent = () => {
  const dispatch = useAppDispatch();
  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [editModalOpen, setEditModalOpen] = useState(false);
  const [selectedAgent, setSelectedAgent] = useState<IAgent | null>(null);
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

  const startSearching = (e: React.FormEvent) => {
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

  const handleSearch = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newSearch = event.target.value;
    setSearch(newSearch);

    // Auto-search as user types (with debounce effect)
    if (newSearch.trim()) {
      setPaginationState({ ...paginationState, activePage: 1 });
      dispatch(
        searchEntities({
          query: newSearch,
          page: 0,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        }),
      );
    } else {
      // If search is cleared, get all entities
      dispatch(
        getEntities({
          page: 0,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        }),
      );
    }
  };

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

  const sort = (p: string) => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = (currentPage: number) =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    // Always show the up-down arrows symbol for all sortable columns
    return faSort;
  };

  const handleDelete = (agent: IAgent) => {
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

  const handleEdit = (agent: IAgent) => {
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
        {/* Single line: Icon + Title on left, Pagination + Search + Actions on right */}
        <div className="d-flex justify-content-between align-items-center mb-2 mt-5">
          {/* Left side: Icon + Title */}
          <div className="d-flex align-items-center">
            <FontAwesomeIcon icon={faUserSecret} className="me-2" style={{ color: '#007bff', fontSize: '1.5rem' }} />
            <h4 className="mb-0" style={{ fontWeight: 'bold', color: '#212529' }}>
              <Translate contentKey="infraMirrorApp.agent.home.title">Agents</Translate>
            </h4>
          </div>

          {/* Right side: Pagination + Search + Actions */}
          <div className="d-flex align-items-center gap-2">
            {/* Pagination Info with Navigation */}
            {totalItems > 0 && (
              <div className="d-flex align-items-center gap-2">
                <span className="text-muted" style={{ fontSize: '0.875rem' }}>
                  {paginationState.activePage === 1 ? 1 : (paginationState.activePage - 1) * paginationState.itemsPerPage + 1}–
                  {Math.min(paginationState.activePage * paginationState.itemsPerPage, totalItems)} of {totalItems}
                </span>
                <div className="d-flex">
                  <Button
                    color="outline-secondary"
                    size="sm"
                    onClick={() => handlePagination(paginationState.activePage - 1)}
                    disabled={paginationState.activePage <= 1}
                    style={{ padding: '4px 8px', border: 'none' }}
                  >
                    <FontAwesomeIcon icon={faChevronLeft} />
                  </Button>
                  <Button
                    color="outline-secondary"
                    size="sm"
                    onClick={() => handlePagination(paginationState.activePage + 1)}
                    disabled={paginationState.activePage >= Math.ceil(totalItems / paginationState.itemsPerPage)}
                    style={{ padding: '4px 8px', border: 'none' }}
                  >
                    <FontAwesomeIcon icon={faChevronRight} />
                  </Button>
                </div>
              </div>
            )}

            {/* Compact Search Bar */}
            <div className="d-flex align-items-center position-relative" style={{ minWidth: '250px' }}>
              <Input
                type="text"
                name="search"
                value={search}
                onChange={handleSearch}
                placeholder="Search for Agent"
                style={{
                  fontSize: '0.875rem',
                  borderRadius: '20px',
                  border: '1px solid #ced4da',
                  paddingLeft: '15px',
                  paddingRight: search ? '40px' : '15px',
                  width: '100%',
                }}
                className="form-control-sm"
              />
              {search && (
                <Button
                  color="link"
                  size="sm"
                  onClick={clear}
                  style={{
                    position: 'absolute',
                    right: '10px',
                    zIndex: 5,
                    padding: '2px 6px',
                    color: '#6c757d',
                    border: 'none',
                    background: 'none',
                  }}
                  title="Clear search"
                >
                  <FontAwesomeIcon icon={faTimes} />
                </Button>
              )}
            </div>

            {/* Action Buttons */}
            <Button color="outline-primary" size="sm" onClick={handleSyncList} disabled={loading}>
              <FontAwesomeIcon icon={faSync} spin={loading} />
            </Button>
            <Button color="primary" size="sm" onClick={handleCreate}>
              <FontAwesomeIcon icon={faPlus} className="me-1" />
              Create
            </Button>
          </div>
        </div>

        {/* Horizontal line above table headers */}
        <hr style={{ margin: '1rem 0 0 0', border: 'none', borderTop: '1px solid #dee2e6' }} />

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
              <FontAwesomeIcon icon={faSpinner} spin size="2x" />
            </div>
          )}
          {agentList && agentList.length > 0 ? (
            <>
              <hr style={{ margin: '1rem 0', border: 'none', borderTop: '1px solid #adb5bd', opacity: 1 }} />
              <Table responsive striped hover>
                <thead>
                  <tr>
                    <th
                      className="hand"
                      onClick={sort('name')}
                      style={{ color: '#6c757d', fontWeight: '500', fontSize: '0.75rem', textTransform: 'uppercase', letterSpacing: '0.5px' }}
                    >
                      NAME <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                    </th>
                    <th
                      className="hand"
                      onClick={sort('status')}
                      style={{ color: '#6c757d', fontWeight: '500', fontSize: '0.75rem', textTransform: 'uppercase', letterSpacing: '0.5px' }}
                    >
                      STATUS <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                    </th>
                    <th
                      className="hand"
                      onClick={sort('lastSeenAt')}
                      style={{ color: '#6c757d', fontWeight: '500', fontSize: '0.75rem', textTransform: 'uppercase', letterSpacing: '0.5px' }}
                    >
                      LAST SEEN <FontAwesomeIcon icon={getSortIconByFieldName('lastSeenAt')} />
                    </th>
                    <th
                      style={{ color: '#6c757d', fontWeight: '500', fontSize: '0.75rem', textTransform: 'uppercase', letterSpacing: '0.5px' }}
                    >
                      ACTIONS
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {agentList.map((agent, i) => (
                    <tr key={`entity-${i}`} data-cy="entityTable" style={{ height: '60px' }}>
                      <td 
                        style={{ 
                          verticalAlign: 'middle', 
                          padding: '1rem 0.75rem',
                          borderLeft: '4px solid transparent',
                          transition: 'all 0.2s ease',
                          cursor: 'pointer'
                        }}
                        onMouseEnter={(e) => {
                          e.currentTarget.style.borderLeft = '4px solid #007bff';
                          const span = e.currentTarget.querySelector('span');
                          if (span) span.style.color = '#007bff';
                        }}
                        onMouseLeave={(e) => {
                          e.currentTarget.style.borderLeft = '4px solid transparent';
                          const span = e.currentTarget.querySelector('span');
                          if (span) span.style.color = '#212529';
                        }}
                      >
                        <span style={{ fontSize: '0.875rem', transition: 'color 0.2s ease' }}>{agent.name}</span>
                      </td>
                      <td style={{ verticalAlign: 'middle', padding: '1rem 0.75rem' }}>
                        {agent.status === 'ACTIVE' && <span className="badge bg-success">Active</span>}
                        {agent.status === 'INACTIVE' && <span className="badge bg-warning">Inactive</span>}
                        {agent.status === 'OFFLINE' && <span className="badge bg-danger">Offline</span>}
                        {!agent.status && <span className="badge bg-secondary">Unknown</span>}
                      </td>
                      <td style={{ verticalAlign: 'middle', padding: '1rem 0.75rem' }}>
                        <small className="text-muted">{agent.lastSeenAt || '-'}</small>
                      </td>
                      <td style={{ verticalAlign: 'middle', padding: '1rem 0.75rem' }}>
                        <div className="d-flex gap-1">
                          <Button onClick={() => handleEdit(agent)} color="link" size="sm" title="Edit" style={{ padding: 0 }}>
                            <FontAwesomeIcon icon={faPencilAlt} />
                          </Button>
                          <Button
                            onClick={() => handleDelete(agent)}
                            color="link"
                            size="sm"
                            title="Delete"
                            style={{ padding: 0, color: '#dc3545', marginLeft: '0.5rem' }}
                          >
                            <FontAwesomeIcon icon={faTrash} />
                          </Button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </>
          ) : (
            !loading && (
              <div className="text-center py-5">
                <FontAwesomeIcon icon={faUserSecret} size="3x" className="text-muted mb-3" />
                <h5 className="text-muted">No agents available. Create your first agent to get started.</h5>
                <Button color="primary" className="mt-3" onClick={handleCreate}>
                  <FontAwesomeIcon icon={faPlus} /> Create Agent
                </Button>
              </div>
            )
          )}
        </div>
      </div>
      {editModalOpen && (
        <div className="col-md-6">
          <AgentEditModal isOpen={editModalOpen} toggle={() => setEditModalOpen(false)} agent={selectedAgent} onSave={handleSaveSuccess} />
        </div>
      )}

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
