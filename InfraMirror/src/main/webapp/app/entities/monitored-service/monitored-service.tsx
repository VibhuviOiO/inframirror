import React, { useEffect, useState, useCallback } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Badge, Button, Input, Table } from 'reactstrap';
import { Translate, getPaginationState, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faSort,
  faSortDown,
  faSortUp,
  faServer,
  faPlus,
  faSpinner,
  faTimes,
  faChevronLeft,
  faChevronRight,
  faPencilAlt,
  faTrash,
  faCaretRight,
  faCaretDown,
} from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import MonitoredServiceSidePanel from './monitored-service-side-panel';
import EntityDeleteModal from 'app/shared/components/entity-delete-modal';
import { ServiceInstanceManager } from './service-instance-manager';
import { getEntities, searchEntities, deleteEntity } from './monitored-service.reducer';

export const MonitoredService = () => {
  const dispatch = useAppDispatch();
  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [sidePanelOpen, setSidePanelOpen] = useState(false);
  const [selectedService, setSelectedService] = useState(null);
  const [expandedRows, setExpandedRows] = useState<Set<number>>(new Set());
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const monitoredServiceList = useAppSelector(state => state.monitoredService.entities);
  const loading = useAppSelector(state => state.monitoredService.loading);
  const totalItems = useAppSelector(state => state.monitoredService.totalItems);

  const getAllEntities = useCallback(() => {
    const params = {
      page: paginationState.activePage - 1,
      size: paginationState.itemsPerPage,
      sort: `${paginationState.sort},${paginationState.order}`,
    };
    dispatch(search ? searchEntities({ ...params, query: search }) : getEntities(params));
  }, [dispatch, search, paginationState.activePage, paginationState.itemsPerPage, paginationState.sort, paginationState.order]);

  const clear = useCallback(() => {
    setSearch('');
    setPaginationState(prev => ({ ...prev, activePage: 1 }));
  }, []);

  const handleSearch = useCallback(event => {
    setSearch(event.target.value);
    setPaginationState(prev => ({ ...prev, activePage: 1 }));
  }, []);

  useEffect(() => {
    const timer = setTimeout(() => {
      getAllEntities();
      const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
      if (pageLocation.search !== endURL) {
        navigate(`${pageLocation.pathname}${endURL}`);
      }
    }, 300);
    return () => clearTimeout(timer);
  }, [paginationState.activePage, paginationState.order, paginationState.sort, search]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sortParam = params.get(SORT);
    if (page && sortParam) {
      const [sortField, sortOrder] = sortParam.split(',');
      setPaginationState(prev => ({
        ...prev,
        activePage: +page,
        sort: sortField,
        order: sortOrder,
      }));
    }
  }, [pageLocation.search]);

  const handleSort = useCallback(
    field => () => {
      setPaginationState(prev => ({
        ...prev,
        order: prev.order === ASC ? DESC : ASC,
        sort: field,
      }));
    },
    [],
  );

  const handlePagination = useCallback(currentPage => {
    setPaginationState(prev => ({ ...prev, activePage: currentPage }));
  }, []);

  const getSortIconByFieldName = useCallback(
    (fieldName: string) => {
      if (paginationState.sort !== fieldName) return faSort;
      return paginationState.order === ASC ? faSortUp : faSortDown;
    },
    [paginationState.sort, paginationState.order],
  );

  const handleDelete = useCallback(service => {
    setSelectedService(service);
    setDeleteModalOpen(true);
  }, []);

  const handleDeleteSuccess = useCallback(() => {
    setDeleteModalOpen(false);
    setSelectedService(null);
    getAllEntities();
  }, [getAllEntities]);

  const handleCreate = useCallback(() => {
    setSelectedService(null);
    setSidePanelOpen(true);
  }, []);

  const handleEdit = useCallback(service => {
    setSelectedService(service);
    setSidePanelOpen(true);
  }, []);

  const handleSaveSuccess = useCallback(() => {
    setSidePanelOpen(false);
    setSelectedService(null);
    getAllEntities();
  }, [getAllEntities]);

  const toggleRow = (serviceId: number) => {
    const newExpanded = new Set(expandedRows);
    if (newExpanded.has(serviceId)) {
      newExpanded.delete(serviceId);
    } else {
      newExpanded.add(serviceId);
    }
    setExpandedRows(newExpanded);
  };

  return (
    <>
      <div className="entity-page">
        <div className="card shadow-sm border-0 entity-card">
          <div className="card-body">
            <div className="d-flex justify-content-between align-items-center">
              <h4 className="mb-0 fw-bold">
                <FontAwesomeIcon icon={faServer} className="me-2 text-primary" />
                <Translate contentKey="infraMirrorApp.monitoredService.home.title">Monitored Services</Translate>
                {loading && <FontAwesomeIcon icon={faSpinner} spin className="ms-2 text-primary loading-icon" />}
              </h4>
              <div className="d-flex gap-2 align-items-center">
                {totalItems > 0 && (
                  <div className="d-flex align-items-center gap-2 pagination-info">
                    <span>
                      {(paginationState.activePage - 1) * paginationState.itemsPerPage + 1}–
                      {Math.min(paginationState.activePage * paginationState.itemsPerPage, totalItems)} of {totalItems}
                    </span>
                    <div className="d-flex gap-1">
                      <Button
                        color="link"
                        size="sm"
                        className="p-1 text-muted"
                        onClick={() => handlePagination(paginationState.activePage - 1)}
                        disabled={paginationState.activePage === 1}
                        title="Previous page"
                      >
                        <FontAwesomeIcon icon={faChevronLeft} />
                      </Button>
                      <Button
                        color="link"
                        size="sm"
                        className="p-1 text-muted"
                        onClick={() => handlePagination(paginationState.activePage + 1)}
                        disabled={paginationState.activePage >= Math.ceil(totalItems / paginationState.itemsPerPage)}
                        title="Next page"
                      >
                        <FontAwesomeIcon icon={faChevronRight} />
                      </Button>
                    </div>
                  </div>
                )}
                <Input
                  type="text"
                  name="search"
                  value={search}
                  onChange={handleSearch}
                  placeholder={translate('infraMirrorApp.monitoredService.home.search')}
                  className="search-input"
                  style={{ width: '250px' }}
                />
                {search && (
                  <Button color="light" size="sm" onClick={clear} className="border">
                    <FontAwesomeIcon icon={faTimes} />
                  </Button>
                )}
                <Button color="primary" size="sm" onClick={handleCreate} className="action-btn" data-cy="entityCreateButton">
                  <FontAwesomeIcon icon={faPlus} className="me-1" />
                  Create
                </Button>
              </div>
            </div>
          </div>
          <hr className="my-0" />
          <div className="position-relative">
            {monitoredServiceList && monitoredServiceList.length > 0 ? (
              <div className="table-responsive">
                <Table className="table-hover mb-0 entity-table">
                  <thead className="bg-light">
                    <tr>
                      <th className="hand border-0" onClick={handleSort('name')}>
                        Name <FontAwesomeIcon icon={getSortIconByFieldName('name')} className="ms-1" />
                      </th>
                      <th className="border-0">Type & Environment</th>
                      <th className="border-0">Configuration</th>
                      <th className="border-0">Instances</th>
                      <th className="border-0">Status</th>
                      <th className="border-0 text-end">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {monitoredServiceList.map(service => (
                      <React.Fragment key={service.id}>
                        <tr data-cy="entityTable">
                          <td className="fw-semibold">
                            <div>{service.name}</div>
                            {service.description && <div className="text-muted small">{service.description}</div>}
                          </td>
                          <td>
                            <div className="d-flex gap-1 flex-wrap">
                              <Badge color="light" className="entity-badge text-dark border">
                                {service.serviceType}
                              </Badge>
                              <Badge color="light" className="entity-badge text-dark border">
                                {service.environment}
                              </Badge>
                              {service.datacenter && (
                                <Badge color="light" className="entity-badge text-dark border">
                                  {service.datacenter.name}
                                </Badge>
                              )}
                            </div>
                          </td>
                          <td>
                            <div className="small">
                              <div>Interval: {service.intervalSeconds}s</div>
                              <div className="text-muted">
                                Timeout: {service.timeoutMs}ms | Retry: {service.retryCount}
                              </div>
                            </div>
                          </td>
                          <td>
                            <Button color="link" size="sm" onClick={() => toggleRow(service.id)} title="Toggle instances" className="p-0">
                              <FontAwesomeIcon icon={expandedRows.has(service.id) ? faCaretDown : faCaretRight} />
                            </Button>
                          </td>
                          <td>
                            <div className="d-flex gap-1 flex-wrap">
                              {service.monitoringEnabled ? (
                                <Badge color="light" className="entity-badge text-success border">
                                  Monitoring
                                </Badge>
                              ) : (
                                <Badge color="light" className="entity-badge text-dark border">
                                  Disabled
                                </Badge>
                              )}
                              {service.clusterMonitoringEnabled && (
                                <Badge color="light" className="entity-badge text-dark border">
                                  Cluster
                                </Badge>
                              )}
                              {!service.isActive && (
                                <Badge color="light" className="entity-badge text-warning border">
                                  Inactive
                                </Badge>
                              )}
                            </div>
                          </td>
                          <td className="text-end">
                            <div className="d-flex gap-2 justify-content-end">
                              <Button
                                onClick={() => handleEdit(service)}
                                color="light"
                                size="sm"
                                className="border action-btn"
                                title="Edit"
                                data-cy="entityEditButton"
                              >
                                <FontAwesomeIcon icon={faPencilAlt} />
                              </Button>
                              <Button
                                onClick={() => handleDelete(service)}
                                color="danger"
                                size="sm"
                                className="border-0 action-btn"
                                title="Delete"
                                data-cy="entityDeleteButton"
                              >
                                <FontAwesomeIcon icon={faTrash} />
                              </Button>
                            </div>
                          </td>
                        </tr>
                        {expandedRows.has(service.id) && (
                          <tr>
                            <td colSpan={6} className="p-0">
                              <ServiceInstanceManager serviceId={service.id} />
                            </td>
                          </tr>
                        )}
                      </React.Fragment>
                    ))}
                  </tbody>
                </Table>
              </div>
            ) : (
              !loading && (
                <div className="text-center py-5">
                  <div className="mb-4">
                    <FontAwesomeIcon icon={faServer} size="4x" className="text-muted opacity-50" />
                  </div>
                  <h5 className="text-muted mb-2">No monitored services found</h5>
                  <p className="text-muted small mb-4">Create your first service to get started</p>
                  <Button color="primary" onClick={handleCreate}>
                    <FontAwesomeIcon icon={faPlus} className="me-1" /> Create Service
                  </Button>
                </div>
              )
            )}
          </div>
        </div>
      </div>

      <MonitoredServiceSidePanel
        isOpen={sidePanelOpen}
        onClose={() => setSidePanelOpen(false)}
        service={selectedService}
        onSuccess={handleSaveSuccess}
      />

      <EntityDeleteModal
        isOpen={deleteModalOpen}
        toggle={() => setDeleteModalOpen(false)}
        entityName="Monitored Service"
        entityDisplayName={selectedService?.name || ''}
        entityId={selectedService?.id}
        deleteAction={deleteEntity}
        onDeleteSuccess={handleDeleteSuccess}
      />
    </>
  );
};

export default MonitoredService;
