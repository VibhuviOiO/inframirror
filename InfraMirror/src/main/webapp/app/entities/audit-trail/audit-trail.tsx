import React, { useEffect, useState } from 'react';
import InfiniteScroll from 'react-infinite-scroll-component';
import { Link, useLocation } from 'react-router-dom';
import { Button, Col, Form, FormGroup, Input, InputGroup, Row, Table } from 'reactstrap';
import { TextFormat, Translate, getPaginationState, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities, reset, searchEntities } from './audit-trail.reducer';

export const AuditTrail = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();

  const [search, setSearch] = useState('');
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );
  const [sorting, setSorting] = useState(false);

  const auditTrailList = useAppSelector(state => state.auditTrail.entities);
  const loading = useAppSelector(state => state.auditTrail.loading);
  const links = useAppSelector(state => state.auditTrail.links);
  const updateSuccess = useAppSelector(state => state.auditTrail.updateSuccess);

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

  const resetAll = () => {
    dispatch(reset());
    setPaginationState({
      ...paginationState,
      activePage: 1,
    });
    dispatch(getEntities({}));
  };

  useEffect(() => {
    resetAll();
  }, []);

  const startSearching = e => {
    if (search) {
      dispatch(reset());
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
    dispatch(reset());
    setSearch('');
    setPaginationState({
      ...paginationState,
      activePage: 1,
    });
    dispatch(getEntities({}));
  };

  const handleSearch = event => setSearch(event.target.value);

  useEffect(() => {
    if (updateSuccess) {
      resetAll();
    }
  }, [updateSuccess]);

  useEffect(() => {
    getAllEntities();
  }, [paginationState.activePage]);

  const handleLoadMore = () => {
    if ((window as any).pageYOffset > 0) {
      setPaginationState({
        ...paginationState,
        activePage: paginationState.activePage + 1,
      });
    }
  };

  useEffect(() => {
    if (sorting) {
      getAllEntities();
      setSorting(false);
    }
  }, [sorting, search]);

  const sort = p => () => {
    dispatch(reset());
    setPaginationState({
      ...paginationState,
      activePage: 1,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
    setSorting(true);
  };

  const handleSyncList = () => {
    resetAll();
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
      <h2 id="audit-trail-heading" data-cy="AuditTrailHeading">
        <Translate contentKey="infraMirrorApp.auditTrail.home.title">Audit Trails</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="infraMirrorApp.auditTrail.home.refreshListLabel">Refresh List</Translate>
          </Button>
        </div>
      </h2>
      <Row>
        <Col sm="12">
          <Form onSubmit={startSearching}>
            <FormGroup>
              <InputGroup>
                <Input
                  type="text"
                  name="search"
                  defaultValue={search}
                  onChange={handleSearch}
                  placeholder={translate('infraMirrorApp.auditTrail.home.search')}
                />
                <Button className="input-group-addon">
                  <FontAwesomeIcon icon="search" />
                </Button>
                <Button type="reset" className="input-group-addon" onClick={clear}>
                  <FontAwesomeIcon icon="trash" />
                </Button>
              </InputGroup>
            </FormGroup>
          </Form>
        </Col>
      </Row>
      <div className="table-responsive">
        <InfiniteScroll
          dataLength={auditTrailList ? auditTrailList.length : 0}
          next={handleLoadMore}
          hasMore={paginationState.activePage - 1 < links.next}
          loader={<div className="loader">Loading ...</div>}
        >
          {auditTrailList && auditTrailList.length > 0 ? (
            <Table responsive>
              <thead>
                <tr>
                  <th className="hand" onClick={sort('id')}>
                    <Translate contentKey="infraMirrorApp.auditTrail.id">ID</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                  </th>
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
                  <th>
                    <Translate contentKey="infraMirrorApp.auditTrail.user">User</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {auditTrailList.map((auditTrail, i) => (
                  <tr key={`entity-${i}`} data-cy="entityTable">
                    <td>
                      <Button tag={Link} to={`/audit-trail/${auditTrail.id}`} color="link" size="sm">
                        {auditTrail.id}
                      </Button>
                    </td>
                    <td>{auditTrail.action}</td>
                    <td>{auditTrail.entityName}</td>
                    <td>{auditTrail.entityId}</td>
                    <td>{auditTrail.oldValue}</td>
                    <td>{auditTrail.newValue}</td>
                    <td>
                      {auditTrail.timestamp ? <TextFormat type="date" value={auditTrail.timestamp} format={APP_DATE_FORMAT} /> : null}
                    </td>
                    <td>{auditTrail.ipAddress}</td>
                    <td>{auditTrail.userAgent}</td>
                    <td>{auditTrail.user ? auditTrail.user.id : ''}</td>
                    <td className="text-end">
                      <div className="btn-group flex-btn-group-container">
                        <Button tag={Link} to={`/audit-trail/${auditTrail.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                          <FontAwesomeIcon icon="eye" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.view">View</Translate>
                          </span>
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          ) : (
            !loading && (
              <div className="alert alert-warning">
                <Translate contentKey="infraMirrorApp.auditTrail.home.notFound">No Audit Trails found</Translate>
              </div>
            )
          )}
        </InfiniteScroll>
      </div>
    </div>
  );
};

export default AuditTrail;
