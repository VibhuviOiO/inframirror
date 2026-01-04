import React, { useEffect, useState, useCallback } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Button, Input, Table } from 'reactstrap';
import { Translate, getPaginationState, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faSort,
  faSortDown,
  faSortUp,
  faPlus,
  faServer,
  faSpinner,
  faTimes,
  faChevronLeft,
  faChevronRight,
  faPencilAlt,
  faTrash,
} from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import InstanceSidePanel from './instance-side-panel';
import EntityDeleteModal from 'app/shared/components/entity-delete-modal';
import { getEntities, searchEntities, deleteEntity } from './instance.reducer';

export const Instance = () => {
  const dispatch = useAppDispatch();
  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [sidePanelOpen, setSidePanelOpen] = useState(false);
  const [selectedInstance, setSelectedInstance] = useState(null);
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const instanceList = useAppSelector(state => state.instance.entities);
  const loading = useAppSelector(state => state.instance.loading);
  const totalItems = useAppSelector(state => state.instance.totalItems);

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

  const handleDelete = useCallback(instance => {
    setSelectedInstance(instance);
    setDeleteModalOpen(true);
  }, []);

  const handleDeleteSuccess = useCallback(() => {
    setDeleteModalOpen(false);
    setSelectedInstance(null);
    getAllEntities();
  }, [getAllEntities]);

  const handleCreate = useCallback(() => {
    setSelectedInstance(null);
    setSidePanelOpen(true);
  }, []);

  const handleEdit = useCallback(instance => {
    setSelectedInstance(instance);
    setSidePanelOpen(true);
  }, []);

  const handleSaveSuccess = useCallback(() => {
    setSidePanelOpen(false);
    setSelectedInstance(null);
    getAllEntities();
  }, [getAllEntities]);

  return (
    <>
      <div className="entity-page">
        <div className="card shadow-sm border-0 entity-card">
          <div className="card-body">
            <div className="d-flex justify-content-between align-items-center">
              <h4 className="mb-0 fw-bold">
                <FontAwesomeIcon icon={faServer} className="me-2 text-primary" />
                <Translate contentKey="infraMirrorApp.instance.home.title">Instances</Translate>
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
                  placeholder={translate('infraMirrorApp.instance.home.search')}
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
            {instanceList && instanceList.length > 0 ? (
              <div className="table-responsive">
                <Table className="table-hover mb-0 entity-table">
                  <thead className="bg-light">
                    <tr>
                      <th className="hand border-0" onClick={handleSort('name')}>
                        Name <FontAwesomeIcon icon={getSortIconByFieldName('name')} className="ms-1" />
                      </th>
                      <th className="hand border-0" onClick={handleSort('hostname')}>
                        Hostname <FontAwesomeIcon icon={getSortIconByFieldName('hostname')} className="ms-1" />
                      </th>
                      <th className="border-0">Type</th>
                      <th className="border-0">Datacenter</th>
                      <th className="border-0 text-end">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {instanceList.map(instance => (
                      <tr key={instance.id} data-cy="entityTable">
                        <td className="fw-semibold">{instance.name}</td>
                        <td>{instance.hostname}</td>
                        <td>
                          <span className="badge bg-light text-dark entity-badge">{instance.instanceType}</span>
                        </td>
                        <td>
                          {instance.datacenter ? (
                            <span className="badge bg-light text-dark entity-badge border">{instance.datacenter.name}</span>
                          ) : (
                            <span className="text-muted small">-</span>
                          )}
                        </td>
                        <td className="text-end">
                          <div className="d-flex gap-2 justify-content-end">
                            <Button
                              onClick={() => handleEdit(instance)}
                              color="light"
                              size="sm"
                              className="border action-btn"
                              title="Edit"
                              data-cy="entityEditButton"
                            >
                              <FontAwesomeIcon icon={faPencilAlt} />
                            </Button>
                            <Button
                              onClick={() => handleDelete(instance)}
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
                  <h5 className="text-muted mb-2">No instances found</h5>
                  <p className="text-muted small mb-4">Create your first instance to get started</p>
                  <Button color="primary" onClick={handleCreate}>
                    <FontAwesomeIcon icon={faPlus} className="me-1" /> Create Instance
                  </Button>
                </div>
              )
            )}
          </div>
        </div>
      </div>

      <InstanceSidePanel
        isOpen={sidePanelOpen}
        onClose={() => setSidePanelOpen(false)}
        instance={selectedInstance}
        onSuccess={handleSaveSuccess}
      />

      <EntityDeleteModal
        isOpen={deleteModalOpen}
        toggle={() => setDeleteModalOpen(false)}
        entityName="Instance"
        entityDisplayName={selectedInstance?.name || ''}
        entityId={selectedInstance?.id}
        deleteAction={deleteEntity}
        onDeleteSuccess={handleDeleteSuccess}
      />
    </>
  );
};

export default Instance;
