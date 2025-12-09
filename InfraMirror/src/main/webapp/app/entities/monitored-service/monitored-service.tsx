import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Badge, Button, Input, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp, faServer, faPlus, faCaretRight, faCaretDown } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { MonitoredServiceDeleteModal } from './monitored-service-delete-modal';
import { MonitoredServiceEditModal } from './monitored-service-edit-modal';
import { MonitoredServiceViewModal } from './monitored-service-view-modal';
import { ServiceInstanceManager } from './service-instance-manager';

import { getEntities, searchEntities } from './monitored-service.reducer';

export const MonitoredService = () => {
  const dispatch = useAppDispatch();
  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [editModalOpen, setEditModalOpen] = useState(false);
  const [viewModalOpen, setViewModalOpen] = useState(false);
  const [selectedService, setSelectedService] = useState(null);
  const [expandedRows, setExpandedRows] = useState<Set<number>>(new Set());
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const monitoredServiceList = useAppSelector(state => state.monitoredService.entities);
  const loading = useAppSelector(state => state.monitoredService.loading);
  const totalItems = useAppSelector(state => state.monitoredService.totalItems);

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

  const handleDelete = service => {
    setSelectedService(service);
    setDeleteModalOpen(true);
  };

  const handleDeleteSuccess = () => {
    setDeleteModalOpen(false);
    setSelectedService(null);
    sortEntities();
  };

  const handleCreate = () => {
    setSelectedService(null);
    setEditModalOpen(true);
  };

  const handleEdit = service => {
    setSelectedService(service);
    setEditModalOpen(true);
  };

  const handleSaveSuccess = () => {
    setEditModalOpen(false);
    setSelectedService(null);
    sortEntities();
  };

  const handleView = service => {
    setSelectedService(service);
    setViewModalOpen(true);
  };

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
    <div className="row g-3">
      <div className={editModalOpen ? 'col-md-6' : 'col-md-12'}>
        <div className="d-flex justify-content-between align-items-center mb-3">
          <h5 className="mb-0">
            <FontAwesomeIcon icon={faServer} className="me-2" />
            <Translate contentKey="infraMirrorApp.monitoredService.home.title">Monitored Services</Translate>
          </h5>
          <div className="d-flex gap-2">
            <Button color="info" size="sm" onClick={handleSyncList} disabled={loading}>
              <FontAwesomeIcon icon="sync" spin={loading} />
            </Button>
            <Button color="primary" size="sm" onClick={handleCreate}>
              <FontAwesomeIcon icon={faPlus} className="me-1" />
              New Service
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
              placeholder={translate('infraMirrorApp.monitoredService.home.search')}
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
          {monitoredServiceList && monitoredServiceList.length > 0 ? (
            <Table responsive striped hover>
              <thead>
                <tr>
                  <th className="hand" onClick={sort('name')}>
                    Name <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                  </th>
                  <th>Type & Environment</th>
                  <th>Configuration</th>
                  <th>Instances</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {monitoredServiceList.map((service, i) => (
                  <React.Fragment key={`entity-${i}`}>
                    <tr>
                      <td>
                        <div>
                          <strong>{service.name}</strong>
                          {service.description && <div style={{ fontSize: '0.85rem', color: '#6c757d' }}>{service.description}</div>}
                        </div>
                      </td>
                      <td>
                        <div className="d-flex gap-1 flex-wrap">
                          <Badge color="info" style={{ fontSize: '0.7rem' }}>
                            {service.serviceType}
                          </Badge>
                          <Badge color="secondary" style={{ fontSize: '0.7rem' }}>
                            {service.environment}
                          </Badge>
                          {service.datacenter && (
                            <Badge color="light" style={{ fontSize: '0.7rem' }}>
                              {service.datacenter.name}
                            </Badge>
                          )}
                        </div>
                      </td>
                      <td>
                        <div style={{ fontSize: '0.85rem' }}>
                          <div>Interval: {service.intervalSeconds}s</div>
                          <div style={{ fontSize: '0.75rem', color: '#6c757d' }}>
                            Timeout: {service.timeoutMs}ms | Retry: {service.retryCount}
                          </div>
                        </div>
                      </td>
                      <td>
                        <div className="d-flex gap-1">
                          <Button
                            color="link"
                            size="sm"
                            onClick={() => toggleRow(service.id)}
                            title="Toggle instances"
                            style={{ padding: 0 }}
                          >
                            <FontAwesomeIcon icon={expandedRows.has(service.id) ? faCaretDown : faCaretRight} />
                          </Button>
                        </div>
                      </td>
                      <td>
                        <div className="d-flex gap-1 flex-wrap">
                          {service.monitoringEnabled ? (
                            <Badge color="success" style={{ fontSize: '0.7rem' }}>
                              Monitoring
                            </Badge>
                          ) : (
                            <Badge color="secondary" style={{ fontSize: '0.7rem' }}>
                              Disabled
                            </Badge>
                          )}
                          {service.clusterMonitoringEnabled && (
                            <Badge color="info" style={{ fontSize: '0.7rem' }}>
                              Cluster
                            </Badge>
                          )}
                          {!service.isActive && (
                            <Badge color="warning" style={{ fontSize: '0.7rem' }}>
                              Inactive
                            </Badge>
                          )}
                        </div>
                      </td>
                      <td>
                        <div className="d-flex gap-1">
                          <Button onClick={() => handleView(service)} color="link" size="sm" title="View" style={{ padding: 0 }}>
                            <FontAwesomeIcon icon="eye" />
                          </Button>
                          <Button onClick={() => handleEdit(service)} color="link" size="sm" title="Edit" style={{ padding: 0 }}>
                            <FontAwesomeIcon icon="pencil-alt" />
                          </Button>
                          <Button
                            onClick={() => handleDelete(service)}
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
                    {expandedRows.has(service.id) && (
                      <tr>
                        <td colSpan={6} style={{ padding: 0 }}>
                          <ServiceInstanceManager serviceId={service.id} />
                        </td>
                      </tr>
                    )}
                  </React.Fragment>
                ))}
              </tbody>
            </Table>
          ) : (
            !loading && (
              <div className="text-center py-5">
                <FontAwesomeIcon icon="inbox" size="3x" className="text-muted mb-3" />
                <h5 className="text-muted">No monitored services available. Create your first service to get started.</h5>
                <Button color="primary" className="mt-3" onClick={handleCreate}>
                  <FontAwesomeIcon icon="plus" /> Create Service
                </Button>
              </div>
            )
          )}
        </div>
        {totalItems ? (
          <div
            className={
              monitoredServiceList && monitoredServiceList.length > 0 ? 'd-flex justify-content-between align-items-center' : 'd-none'
            }
          >
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
        <MonitoredServiceEditModal
          isOpen={editModalOpen}
          toggle={() => setEditModalOpen(false)}
          service={selectedService}
          onSave={handleSaveSuccess}
        />
      </div>

      <MonitoredServiceDeleteModal
        isOpen={deleteModalOpen}
        toggle={() => setDeleteModalOpen(false)}
        service={selectedService}
        onDelete={handleDeleteSuccess}
      />

      <MonitoredServiceViewModal isOpen={viewModalOpen} toggle={() => setViewModalOpen(false)} service={selectedService} />
    </div>
  );
};

export default MonitoredService;
