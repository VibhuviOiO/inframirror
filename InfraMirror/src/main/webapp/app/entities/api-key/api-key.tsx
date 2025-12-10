import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Button, Input, Table, Badge } from 'reactstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp, faPlus } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { ApiKeyCreateModal } from './api-key-create-modal';
import { ApiKeyShowModal } from './api-key-show-modal';
import axios from 'axios';
import { toast } from 'react-toastify';

import { getEntities, searchEntities } from './api-key.reducer';

export const ApiKey = () => {
  const dispatch = useAppDispatch();
  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [createModalOpen, setCreateModalOpen] = useState(false);
  const [showKeyModal, setShowKeyModal] = useState(false);
  const [newApiKey, setNewApiKey] = useState(null);
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const apiKeyList = useAppSelector(state => state.apiKey.entities);
  const loading = useAppSelector(state => state.apiKey.loading);
  const totalItems = useAppSelector(state => state.apiKey.totalItems);

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

  const handleCreate = () => {
    setCreateModalOpen(true);
  };

  const handleCreateSuccess = apiKey => {
    setNewApiKey(apiKey);
    setShowKeyModal(true);
    sortEntities();
  };

  const handleDeactivate = async (id: number) => {
    try {
      await axios.put(`/api/api-keys/${id}/deactivate`);
      toast.success('API Key deactivated successfully');
      sortEntities();
    } catch (error) {
      toast.error('Failed to deactivate API key');
    }
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this API key? This action cannot be undone.')) {
      try {
        await axios.delete(`/api/api-keys/${id}`);
        toast.success('API Key deleted successfully');
        sortEntities();
      } catch (error) {
        toast.error('Failed to delete API key');
      }
    }
  };

  return (
    <div className="row g-3">
      <div className={createModalOpen ? 'col-md-6' : 'col-md-12'}>
        <div className="d-flex justify-content-between align-items-center mb-3">
          <h5 className="mb-0">
            <Translate contentKey="infraMirrorApp.apiKey.home.title">API Keys</Translate>
          </h5>
          <div className="d-flex gap-2">
            <Button color="info" size="sm" onClick={handleSyncList} disabled={loading}>
              <FontAwesomeIcon icon="sync" spin={loading} />
            </Button>
            <Button color="primary" size="sm" onClick={handleCreate}>
              <FontAwesomeIcon icon={faPlus} className="me-1" />
              New API Key
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
              placeholder={translate('infraMirrorApp.apiKey.home.search')}
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
          {apiKeyList && apiKeyList.length > 0 ? (
            <Table responsive striped hover>
              <thead>
                <tr>
                  <th className="hand" onClick={sort('name')}>
                    Name <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                  </th>
                  <th>Description</th>
                  <th>Status</th>
                  <th>Last Used</th>
                  <th>Expires At</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {apiKeyList.map((apiKey, i) => (
                  <tr key={`entity-${i}`} data-cy="entityTable">
                    <td>
                      <strong>{apiKey.name}</strong>
                    </td>
                    <td>{apiKey.description}</td>
                    <td>
                      <Badge color={apiKey.active ? 'success' : 'secondary'}>{apiKey.active ? 'Active' : 'Inactive'}</Badge>
                    </td>
                    <td>{apiKey.lastUsedDate ? new Date(apiKey.lastUsedDate).toLocaleString() : 'Never'}</td>
                    <td>{apiKey.expiresAt ? new Date(apiKey.expiresAt).toLocaleString() : 'Never'}</td>
                    <td>
                      <div className="d-flex gap-1">
                        {apiKey.active && (
                          <Button
                            onClick={() => handleDeactivate(apiKey.id)}
                            color="link"
                            size="sm"
                            title="Deactivate"
                            style={{ padding: 0, color: '#ffc107' }}
                          >
                            <FontAwesomeIcon icon="ban" />
                          </Button>
                        )}
                        <Button
                          onClick={() => handleDelete(apiKey.id)}
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
                <FontAwesomeIcon icon="key" size="3x" className="text-muted mb-3" />
                <h5 className="text-muted">No API keys available. Create your first API key to get started.</h5>
                <Button color="primary" className="mt-3" onClick={handleCreate}>
                  <FontAwesomeIcon icon="plus" /> Create API Key
                </Button>
              </div>
            )
          )}
        </div>
        {totalItems ? (
          <div className={apiKeyList && apiKeyList.length > 0 ? 'd-flex justify-content-between align-items-center' : 'd-none'}>
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
        <ApiKeyCreateModal isOpen={createModalOpen} toggle={() => setCreateModalOpen(false)} onSuccess={handleCreateSuccess} />
      </div>

      <ApiKeyShowModal isOpen={showKeyModal} toggle={() => setShowKeyModal(false)} apiKey={newApiKey} />
    </div>
  );
};

export default ApiKey;
