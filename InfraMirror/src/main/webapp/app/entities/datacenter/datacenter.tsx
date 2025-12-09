import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Input, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp, faPlus } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { DatacenterEditModal } from './datacenter-edit-modal';
import { DatacenterDeleteModal } from './datacenter-delete-modal';

import { getEntities, searchEntities } from './datacenter.reducer';

export const Datacenter = () => {
  const dispatch = useAppDispatch();
  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [editModalOpen, setEditModalOpen] = useState(false);
  const [selectedDatacenter, setSelectedDatacenter] = useState(null);
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const datacenterList = useAppSelector(state => state.datacenter.entities);
  const loading = useAppSelector(state => state.datacenter.loading);
  const totalItems = useAppSelector(state => state.datacenter.totalItems);

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

  const handleDelete = datacenter => {
    setSelectedDatacenter(datacenter);
    setDeleteModalOpen(true);
  };

  const handleDeleteSuccess = () => {
    setDeleteModalOpen(false);
    setSelectedDatacenter(null);
    sortEntities();
  };

  const handleCreate = () => {
    setSelectedDatacenter(null);
    setEditModalOpen(true);
  };

  const handleEdit = datacenter => {
    setSelectedDatacenter(datacenter);
    setEditModalOpen(true);
  };

  const handleSaveSuccess = () => {
    setEditModalOpen(false);
    setSelectedDatacenter(null);
    sortEntities();
  };

  return (
    <div className="row g-3">
      <div className={editModalOpen ? 'col-md-6' : 'col-md-12'}>
        <div className="d-flex justify-content-between align-items-center mb-3">
          <h5 className="mb-0">
            <Translate contentKey="infraMirrorApp.datacenter.home.title">Datacenters</Translate>
          </h5>
          <div className="d-flex gap-2">
            <Button color="info" size="sm" onClick={handleSyncList} disabled={loading}>
              <FontAwesomeIcon icon="sync" spin={loading} />
            </Button>
            <Button color="primary" size="sm" onClick={handleCreate}>
              <FontAwesomeIcon icon={faPlus} className="me-1" />
              New Datacenter
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
              placeholder={translate('infraMirrorApp.datacenter.home.search')}
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
          {datacenterList && datacenterList.length > 0 ? (
            <Table responsive striped hover>
              <thead>
                <tr>
                  <th className="hand" onClick={sort('code')}>
                    Code <FontAwesomeIcon icon={getSortIconByFieldName('code')} />
                  </th>
                  <th className="hand" onClick={sort('name')}>
                    Name <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                  </th>
                  <th className="hand" onClick={sort('region')}>
                    Region <FontAwesomeIcon icon={getSortIconByFieldName('region')} />
                  </th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {datacenterList.map((datacenter, i) => (
                  <tr key={`entity-${i}`} data-cy="entityTable">
                    <td>{datacenter.code}</td>
                    <td>{datacenter.name}</td>
                    <td>{datacenter.region ? <Link to={`/region/${datacenter.region.id}`}>{datacenter.region.name}</Link> : ''}</td>
                    <td>
                      <div className="d-flex gap-1">
                        <Button onClick={() => handleEdit(datacenter)} color="link" size="sm" title="Edit" style={{ padding: 0 }}>
                          <FontAwesomeIcon icon="pencil-alt" />
                        </Button>
                        <Button
                          onClick={() => handleDelete(datacenter)}
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
                <h5 className="text-muted">No datacenters available. Create your first datacenter to get started.</h5>
                <Button color="primary" className="mt-3" onClick={handleCreate}>
                  <FontAwesomeIcon icon="plus" /> Create Datacenter
                </Button>
              </div>
            )
          )}
        </div>
        {totalItems ? (
          <div className={datacenterList && datacenterList.length > 0 ? 'd-flex justify-content-between align-items-center' : 'd-none'}>
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
        <DatacenterEditModal
          isOpen={editModalOpen}
          toggle={() => setEditModalOpen(false)}
          datacenter={selectedDatacenter}
          onSave={handleSaveSuccess}
        />
      </div>

      <DatacenterDeleteModal
        isOpen={deleteModalOpen}
        toggle={() => setDeleteModalOpen(false)}
        datacenter={selectedDatacenter}
        onDelete={handleDeleteSuccess}
      />
    </div>
  );
};

export default Datacenter;
