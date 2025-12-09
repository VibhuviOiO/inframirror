import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Col, Form, FormGroup, Input, InputGroup, Row, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities, searchEntities } from './agent.reducer';

export const Agent = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
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

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h4 id="agent-heading" data-cy="AgentHeading" className="mb-0">
          <Translate contentKey="infraMirrorApp.agent.home.title">Agents</Translate>
        </h4>
        <div className="d-flex">
          <Button
            className="me-2"
            color="info"
            size="sm"
            onClick={handleSyncList}
            disabled={loading}
            title={translate('infraMirrorApp.agent.home.refreshListLabel')}
          >
            <FontAwesomeIcon icon="sync" spin={loading} />
          </Button>
          <Link
            to="/agent/new"
            className="btn btn-primary btn-sm jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            title={translate('infraMirrorApp.agent.home.createLabel')}
          >
            <Translate contentKey="infraMirrorApp.agent.home.createLabel">Create</Translate>
          </Link>
        </div>
      </div>
      <hr />
      <Row className="mb-3">
        <Col sm="12">
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
              <Translate contentKey="infraMirrorApp.agent.home.searchButton">Search</Translate>
            </Button>
            {search && (
              <Button color="secondary" size="sm" onClick={clear}>
                <Translate contentKey="infraMirrorApp.agent.home.clearSearch">Clear</Translate>
              </Button>
            )}
          </div>
        </Col>
      </Row>
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
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('name')}>
                  <Translate contentKey="infraMirrorApp.agent.name">Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th>
                  <Translate contentKey="infraMirrorApp.agent.region">Region</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {agentList.map((agent, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>{agent.name}</td>
                  <td>{agent.region ? <Link to={`/region/${agent.region.id}`}>{agent.region.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/agent/${agent.id}`}
                        color="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                        title={translate('entity.action.view')}
                      >
                        <FontAwesomeIcon icon="eye" size="sm" />
                      </Button>
                      <Button
                        tag={Link}
                        to={`/agent/${agent.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                        title={translate('entity.action.edit')}
                      >
                        <FontAwesomeIcon icon="pencil-alt" size="sm" />
                      </Button>
                      <Button
                        onClick={() =>
                          (window.location.href = `/agent/${agent.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                        title={translate('entity.action.delete')}
                      >
                        <FontAwesomeIcon icon="trash" size="sm" />
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
              <h5 className="text-muted">
                <Translate contentKey="infraMirrorApp.agent.home.emptyState">
                  No agents available. Create your first agent to get started.
                </Translate>
              </h5>
              <Link to="/agent/new" className="btn btn-primary mt-3">
                <FontAwesomeIcon icon="plus" /> <Translate contentKey="infraMirrorApp.agent.home.createLabel">Create</Translate>
              </Link>
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
  );
};

export default Agent;
