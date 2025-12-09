import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Col, Form, FormGroup, Input, InputGroup, Row, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities, searchEntities } from './audit-trail.reducer';

export const AuditTrail = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const auditTrailList = useAppSelector(state => state.auditTrail.entities);
  const loading = useAppSelector(state => state.auditTrail.loading);
  const totalItems = useAppSelector(state => state.auditTrail.totalItems);

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
        <h4 id="audit-trail-heading" data-cy="AuditTrailHeading" className="mb-0">
          <Translate contentKey="infraMirrorApp.auditTrail.home.title">Audit Trails</Translate>
        </h4>
        <div className="d-flex">
          <Button
            className="me-2"
            color="info"
            size="sm"
            onClick={handleSyncList}
            disabled={loading}
            title={translate('infraMirrorApp.auditTrail.home.refreshListLabel')}
          >
            <FontAwesomeIcon icon="sync" spin={loading} />
          </Button>
          <Link
            to="/audit-trail/new"
            className="btn btn-primary btn-sm jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            title={translate('infraMirrorApp.auditTrail.home.createLabel')}
          >
            <Translate contentKey="infraMirrorApp.auditTrail.home.createLabel">Create</Translate>
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
              placeholder={translate('infraMirrorApp.auditTrail.home.search')}
              style={{ flex: 1 }}
            />
            <Button color="primary" size="sm" onClick={startSearching} disabled={!search}>
              <Translate contentKey="infraMirrorApp.auditTrail.home.searchButton">Search</Translate>
            </Button>
            {search && (
              <Button color="secondary" size="sm" onClick={clear}>
                <Translate contentKey="infraMirrorApp.auditTrail.home.clearSearch">Clear</Translate>
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
        {auditTrailList && auditTrailList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('action')}>
                  <Translate contentKey="infraMirrorApp.auditTrail.action">Action</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('action')} />
                </th>
                <th className="hand" onClick={sort('entityName')}>
                  <Translate contentKey="infraMirrorApp.auditTrail.entityName">Entity Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('entityName')} />
                </th>
                <th className="hand" onClick={sort('entityId')}>
                  <Translate contentKey="infraMirrorApp.auditTrail.entityId">Entity Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('entityId')} />
                </th>
                <th className="hand" onClick={sort('oldValue')}>
                  <Translate contentKey="infraMirrorApp.auditTrail.oldValue">Old Value</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('oldValue')} />
                </th>
                <th className="hand" onClick={sort('newValue')}>
                  <Translate contentKey="infraMirrorApp.auditTrail.newValue">New Value</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('newValue')} />
                </th>
                <th className="hand" onClick={sort('timestamp')}>
                  <Translate contentKey="infraMirrorApp.auditTrail.timestamp">Timestamp</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('timestamp')} />
                </th>
                <th className="hand" onClick={sort('ipAddress')}>
                  <Translate contentKey="infraMirrorApp.auditTrail.ipAddress">Ip Address</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('ipAddress')} />
                </th>
                <th className="hand" onClick={sort('userAgent')}>
                  <Translate contentKey="infraMirrorApp.auditTrail.userAgent">User Agent</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('userAgent')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {auditTrailList.map((auditTrail, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>{auditTrail.action}</td>
                  <td>{auditTrail.entityName}</td>
                  <td>{auditTrail.entityId}</td>
                  <td>{auditTrail.oldValue}</td>
                  <td>{auditTrail.newValue}</td>
                  <td>{auditTrail.timestamp ? <TextFormat type="date" value={auditTrail.timestamp} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{auditTrail.ipAddress}</td>
                  <td>{auditTrail.userAgent}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/audit-trail/${auditTrail.id}`}
                        color="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                        title={translate('entity.action.view')}
                      >
                        <FontAwesomeIcon icon="eye" size="sm" />
                      </Button>
                      <Button
                        tag={Link}
                        to={`/audit-trail/${auditTrail.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                        title={translate('entity.action.edit')}
                      >
                        <FontAwesomeIcon icon="pencil-alt" size="sm" />
                      </Button>
                      <Button
                        onClick={() =>
                          (window.location.href = `/audit-trail/${auditTrail.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
                <Translate contentKey="infraMirrorApp.auditTrail.home.emptyState">
                  No audit trails available. Create your first audit trail to get started.
                </Translate>
              </h5>
              <Link to="/audit-trail/new" className="btn btn-primary mt-3">
                <FontAwesomeIcon icon="plus" /> <Translate contentKey="infraMirrorApp.auditTrail.home.createLabel">Create</Translate>
              </Link>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={auditTrailList && auditTrailList.length > 0 ? 'd-flex justify-content-between align-items-center' : 'd-none'}>
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

export default AuditTrail;
