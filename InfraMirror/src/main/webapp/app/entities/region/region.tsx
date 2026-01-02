import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Button, Input, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp, faPlus } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { RegionEditModal } from './region-edit-modal';
import { RegionDeleteModal } from './region-delete-modal';
import { getEntities, searchEntities } from './region.reducer';
import './region.scss';

export const Region = () => {
  const dispatch = useAppDispatch();
  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [editModalOpen, setEditModalOpen] = useState(false);
  const [selectedRegion, setSelectedRegion] = useState(null);
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const regionList = useAppSelector(state => state.region.entities);
  const loading = useAppSelector(state => state.region.loading);
  const totalItems = useAppSelector(state => state.region.totalItems);

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

  const clear = () => {
    setSearch('');
    setPaginationState({ ...paginationState, activePage: 1 });
    dispatch(getEntities({}));
  };

  const handleSearch = event => {
    const value = event.target.value;
    setSearch(value);
    if (value) {
      setPaginationState({ ...paginationState, activePage: 1 });
      dispatch(
        searchEntities({
          query: value,
          page: 0,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        }),
      );
    } else {
      dispatch(getEntities({}));
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

  const handleDelete = region => {
    setSelectedRegion(region);
    setDeleteModalOpen(true);
  };

  const handleDeleteSuccess = () => {
    setDeleteModalOpen(false);
    setSelectedRegion(null);
    sortEntities();
  };

  const handleCreate = () => {
    setSelectedRegion(null);
    setEditModalOpen(true);
  };

  const handleEdit = region => {
    setSelectedRegion(region);
    setEditModalOpen(true);
  };

  const handleSaveSuccess = () => {
    setEditModalOpen(false);
    setSelectedRegion(null);
    sortEntities();
  };

  return (
    <div className="row g-3 region-page">
      <div className={editModalOpen ? 'col-md-6' : 'col-md-12'}>
        <div className="card shadow-sm border-0 region-card">
          <div className="card-body">
            <div className="d-flex justify-content-between align-items-center">
              <h4 className="mb-0 fw-bold">
                <FontAwesomeIcon icon="globe" className="me-2 text-primary" />
                <Translate contentKey="infraMirrorApp.region.home.title">Regions</Translate>
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
                        <FontAwesomeIcon icon="chevron-left" />
                      </Button>
                      <Button
                        color="link"
                        size="sm"
                        className="p-1 text-muted"
                        onClick={() => handlePagination(paginationState.activePage + 1)}
                        disabled={paginationState.activePage >= Math.ceil(totalItems / paginationState.itemsPerPage)}
                        title="Next page"
                      >
                        <FontAwesomeIcon icon="chevron-right" />
                      </Button>
                    </div>
                  </div>
                )}
                <Input
                  type="text"
                  name="search"
                  value={search}
                  onChange={handleSearch}
                  placeholder={translate('infraMirrorApp.region.home.search')}
                  className="search-input"
                  style={{ width: '250px' }}
                />
                {search && (
                  <Button color="light" size="sm" onClick={clear} className="border">
                    <FontAwesomeIcon icon="times" />
                  </Button>
                )}
                <Button color="light" size="sm" onClick={handleSyncList} disabled={loading} className="border action-btn">
                  <FontAwesomeIcon icon="sync" spin={loading} />
                </Button>
                <Button color="primary" size="sm" onClick={handleCreate} className="action-btn">
                  <FontAwesomeIcon icon={faPlus} className="me-1" />
                  Create
                </Button>
              </div>
            </div>
          </div>
          <hr className="my-0" />
          <div className="card-body p-0">
            <div style={{ position: 'relative', minHeight: '200px' }}>
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
                  <FontAwesomeIcon icon="spinner" spin size="2x" className="text-primary" />
                </div>
              )}
              {regionList && regionList.length > 0 ? (
                <div className="table-responsive">
                  <Table className="table-hover mb-0 region-table">
                    <thead className="bg-light">
                      <tr>
                        <th className="hand border-0" onClick={sort('name')}>
                          Name <FontAwesomeIcon icon={getSortIconByFieldName('name')} className="ms-1" />
                        </th>
                        <th className="hand border-0" onClick={sort('regionCode')}>
                          Region Code <FontAwesomeIcon icon={getSortIconByFieldName('regionCode')} className="ms-1" />
                        </th>
                        <th className="hand border-0" onClick={sort('groupName')}>
                          Group Name <FontAwesomeIcon icon={getSortIconByFieldName('groupName')} className="ms-1" />
                        </th>
                        <th className="border-0 text-end">Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {regionList.map((region, i) => (
                        <tr key={`entity-${i}`} data-cy="entityTable">
                          <td className="fw-semibold">{region.name}</td>
                          <td>
                            <span className="badge bg-light text-dark region-badge">{region.regionCode}</span>
                          </td>
                          <td>{region.groupName}</td>
                          <td className="text-end">
                            <div className="d-flex gap-2 justify-content-end">
                              <Button onClick={() => handleEdit(region)} color="light" size="sm" className="border action-btn" title="Edit">
                                <FontAwesomeIcon icon="pencil-alt" />
                              </Button>
                              <Button
                                onClick={() => handleDelete(region)}
                                color="danger"
                                size="sm"
                                className="border-0 action-btn"
                                title="Delete"
                              >
                                <FontAwesomeIcon icon="trash" />
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
                      <FontAwesomeIcon icon="globe" size="4x" className="text-muted opacity-50" />
                    </div>
                    <h5 className="text-muted mb-2">No regions found</h5>
                    <p className="text-muted small mb-4">Create your first region to get started</p>
                    <Button color="primary" onClick={handleCreate}>
                      <FontAwesomeIcon icon="plus" className="me-1" /> Create Region
                    </Button>
                  </div>
                )
              )}
            </div>
          </div>
        </div>
      </div>
      <div className="col-md-6">
        <RegionEditModal isOpen={editModalOpen} toggle={() => setEditModalOpen(false)} region={selectedRegion} onSave={handleSaveSuccess} />
      </div>

      <RegionDeleteModal
        isOpen={deleteModalOpen}
        toggle={() => setDeleteModalOpen(false)}
        region={selectedRegion}
        onDelete={handleDeleteSuccess}
      />
    </div>
  );
};

export default Region;
