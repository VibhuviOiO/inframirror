import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Badge, Button, Col, Input, Row, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp, faGlobe, faPlus, faCode } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { HttpMonitorDeleteModal } from './http-monitor-delete-modal';
import { HttpMonitorEditModal } from './http-monitor-edit-modal';
import { HttpMonitorViewModal } from './http-monitor-view-modal';

import { getEntities, searchEntities } from './http-monitor.reducer';

export const HttpMonitor = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [editModalOpen, setEditModalOpen] = useState(false);
  const [viewModalOpen, setViewModalOpen] = useState(false);
  const [clickedHeaders, setClickedHeaders] = useState(null);
  const [clickedBody, setClickedBody] = useState(null);
  const [selectedMonitor, setSelectedMonitor] = useState(null);
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const httpMonitorList = useAppSelector(state => state.httpMonitor.entities);
  const loading = useAppSelector(state => state.httpMonitor.loading);
  const totalItems = useAppSelector(state => state.httpMonitor.totalItems);

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
      setPaginationState({
        ...paginationState,
        activePage: 1,
      });
      dispatch(
        searchEntities({
          query: search,
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        }),
      );
    }
    e.preventDefault();
  };

  const clear = () => {
    setSearch('');
    setPaginationState({
      ...paginationState,
      activePage: 1,
    });
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

  const handleDelete = monitor => {
    setSelectedMonitor(monitor);
    setDeleteModalOpen(true);
  };

  const handleDeleteSuccess = () => {
    setDeleteModalOpen(false);
    setSelectedMonitor(null);
    sortEntities();
  };

  const handleCreate = () => {
    setSelectedMonitor(null);
    setEditModalOpen(true);
  };

  const handleEdit = monitor => {
    setSelectedMonitor(monitor);
    setEditModalOpen(true);
  };

  const handleSaveSuccess = () => {
    setEditModalOpen(false);
    setSelectedMonitor(null);
    sortEntities();
  };

  const handleView = monitor => {
    setSelectedMonitor(monitor);
    setViewModalOpen(true);
  };

  return (
    <div
      className="row g-3"
      onClick={() => {
        setClickedHeaders(null);
        setClickedBody(null);
      }}
    >
      <div className={editModalOpen ? 'col-md-6' : 'col-md-12'}>
        <div className="d-flex justify-content-between align-items-center mb-3">
          <h5 className="mb-0">
            <FontAwesomeIcon icon={faGlobe} className="me-2" />
            <Translate contentKey="infraMirrorApp.httpMonitor.home.title">Http Monitors</Translate>
          </h5>
          <div className="d-flex gap-2">
            <Button color="info" size="sm" onClick={handleSyncList} disabled={loading}>
              <FontAwesomeIcon icon="sync" spin={loading} />
            </Button>
            <Button color="primary" size="sm" onClick={handleCreate}>
              <FontAwesomeIcon icon={faPlus} className="me-1" />
              New Monitor
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
              placeholder={translate('infraMirrorApp.httpMonitor.home.search')}
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
          {httpMonitorList && httpMonitorList.length > 0 ? (
            <Table responsive striped hover>
              <thead>
                <tr>
                  <th className="hand" onClick={sort('name')}>
                    Name <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                  </th>
                  <th>URL</th>
                  <th>Interval</th>
                  <th>Headers</th>
                  <th>Body</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {httpMonitorList.map((httpMonitor, i) => (
                  <tr key={`entity-${i}`}>
                    <td>
                      <div>
                        <strong>{httpMonitor.name}</strong>
                        <div className="d-flex gap-1 mt-1 flex-wrap">
                          <Badge color="info" style={{ fontSize: '0.65rem' }}>
                            {httpMonitor.method || 'GET'}
                          </Badge>
                          {httpMonitor.type && (
                            <Badge color="secondary" style={{ fontSize: '0.65rem' }}>
                              {httpMonitor.type}
                            </Badge>
                          )}
                          {!httpMonitor.enabled && (
                            <Badge color="warning" style={{ fontSize: '0.65rem' }}>
                              Disabled
                            </Badge>
                          )}
                        </div>
                      </div>
                    </td>
                    <td>
                      {httpMonitor.url ? (
                        <a href={httpMonitor.url} target="_blank" rel="noopener noreferrer" title={httpMonitor.url}>
                          {httpMonitor.url.length > 40 ? `${httpMonitor.url.substring(0, 40)}...` : httpMonitor.url}
                        </a>
                      ) : (
                        '-'
                      )}
                    </td>
                    <td>
                      <div style={{ fontSize: '0.85rem' }}>
                        <div>Interval: {httpMonitor.intervalSeconds}s</div>
                        <div style={{ fontSize: '0.75rem', color: '#6c757d' }}>Timeout: {httpMonitor.timeoutSeconds}s</div>
                      </div>
                    </td>
                    <td>
                      {httpMonitor.headers ? (
                        <div style={{ position: 'relative', display: 'inline-block' }}>
                          <Button
                            color="link"
                            size="sm"
                            onClick={e => {
                              e.stopPropagation();
                              setClickedHeaders(clickedHeaders === httpMonitor.id ? null : httpMonitor.id);
                            }}
                            title="View Headers"
                            style={{ padding: 0 }}
                          >
                            <FontAwesomeIcon icon={faCode} />
                          </Button>
                          {clickedHeaders === httpMonitor.id && (
                            <div
                              style={{
                                position: 'absolute',
                                bottom: i > httpMonitorList.length / 2 ? '100%' : 'auto',
                                top: i > httpMonitorList.length / 2 ? 'auto' : '100%',
                                left: 0,
                                zIndex: 1000,
                                backgroundColor: '#fff',
                                border: '1px solid #ddd',
                                borderRadius: '4px',
                                padding: '0.5rem',
                                minWidth: '300px',
                                maxWidth: '500px',
                                boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
                                marginTop: i > httpMonitorList.length / 2 ? 0 : '4px',
                                marginBottom: i > httpMonitorList.length / 2 ? '4px' : 0,
                              }}
                              onClick={e => e.stopPropagation()}
                            >
                              <pre style={{ margin: 0, fontSize: '0.75rem', maxHeight: '200px', overflow: 'auto' }}>
                                {JSON.stringify(httpMonitor.headers, null, 2)}
                              </pre>
                            </div>
                          )}
                        </div>
                      ) : (
                        '-'
                      )}
                    </td>
                    <td>
                      {httpMonitor.body ? (
                        <div style={{ position: 'relative', display: 'inline-block' }}>
                          <Button
                            color="link"
                            size="sm"
                            onClick={e => {
                              e.stopPropagation();
                              setClickedBody(clickedBody === httpMonitor.id ? null : httpMonitor.id);
                            }}
                            title="View Body"
                            style={{ padding: 0, color: '#198754' }}
                          >
                            <FontAwesomeIcon icon={faCode} />
                          </Button>
                          {clickedBody === httpMonitor.id && (
                            <div
                              style={{
                                position: 'absolute',
                                bottom: i > httpMonitorList.length / 2 ? '100%' : 'auto',
                                top: i > httpMonitorList.length / 2 ? 'auto' : '100%',
                                left: 0,
                                zIndex: 1000,
                                backgroundColor: '#fff',
                                border: '1px solid #ddd',
                                borderRadius: '4px',
                                padding: '0.5rem',
                                minWidth: '300px',
                                maxWidth: '500px',
                                boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
                                marginTop: i > httpMonitorList.length / 2 ? 0 : '4px',
                                marginBottom: i > httpMonitorList.length / 2 ? '4px' : 0,
                              }}
                              onClick={e => e.stopPropagation()}
                            >
                              <pre style={{ margin: 0, fontSize: '0.75rem', maxHeight: '200px', overflow: 'auto' }}>
                                {JSON.stringify(httpMonitor.body, null, 2)}
                              </pre>
                            </div>
                          )}
                        </div>
                      ) : (
                        '-'
                      )}
                    </td>
                    <td>
                      <div className="d-flex gap-1">
                        <Button onClick={() => handleView(httpMonitor)} color="link" size="sm" title="View" style={{ padding: 0 }}>
                          <FontAwesomeIcon icon="eye" />
                        </Button>
                        <Button onClick={() => handleEdit(httpMonitor)} color="link" size="sm" title="Edit" style={{ padding: 0 }}>
                          <FontAwesomeIcon icon="pencil-alt" />
                        </Button>
                        <Button
                          onClick={() => handleDelete(httpMonitor)}
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
                <h5 className="text-muted">No HTTP monitors available. Create your first monitor to get started.</h5>
                <Button color="primary" className="mt-3" onClick={handleCreate}>
                  <FontAwesomeIcon icon="plus" /> Create Monitor
                </Button>
              </div>
            )
          )}
        </div>
        {totalItems ? (
          <div className={httpMonitorList && httpMonitorList.length > 0 ? 'd-flex justify-content-between align-items-center' : 'd-none'}>
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
      {editModalOpen && (
        <div className="col-md-6">
          <HttpMonitorEditModal
            isOpen={editModalOpen}
            toggle={() => setEditModalOpen(false)}
            monitor={selectedMonitor}
            onSave={handleSaveSuccess}
          />
        </div>
      )}

      <HttpMonitorDeleteModal
        isOpen={deleteModalOpen}
        toggle={() => setDeleteModalOpen(false)}
        monitor={selectedMonitor}
        onDelete={handleDeleteSuccess}
      />

      <HttpMonitorViewModal isOpen={viewModalOpen} toggle={() => setViewModalOpen(false)} monitor={selectedMonitor} />
    </div>
  );
};

export default HttpMonitor;
