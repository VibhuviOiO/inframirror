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

import { getEntities, searchEntities } from './service-heartbeat.reducer';

export const ServiceHeartbeat = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const serviceHeartbeatList = useAppSelector(state => state.serviceHeartbeat.entities);
  const loading = useAppSelector(state => state.serviceHeartbeat.loading);
  const totalItems = useAppSelector(state => state.serviceHeartbeat.totalItems);

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
      <h2 id="service-heartbeat-heading" data-cy="ServiceHeartbeatHeading">
        <Translate contentKey="infraMirrorApp.serviceHeartbeat.home.title">Service Heartbeats</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="infraMirrorApp.serviceHeartbeat.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/service-heartbeat/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="infraMirrorApp.serviceHeartbeat.home.createLabel">Create new Service Heartbeat</Translate>
          </Link>
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
                  placeholder={translate('infraMirrorApp.serviceHeartbeat.home.search')}
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
        {serviceHeartbeatList && serviceHeartbeatList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="infraMirrorApp.serviceHeartbeat.id">Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('executedAt')}>
                  <Translate contentKey="infraMirrorApp.serviceHeartbeat.executedAt">Executed At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('executedAt')} />
                </th>
                <th className="hand" onClick={sort('success')}>
                  <Translate contentKey="infraMirrorApp.serviceHeartbeat.success">Success</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('success')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="infraMirrorApp.serviceHeartbeat.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('responseTimeMs')}>
                  <Translate contentKey="infraMirrorApp.serviceHeartbeat.responseTimeMs">Response Time Ms</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('responseTimeMs')} />
                </th>
                <th className="hand" onClick={sort('errorMessage')}>
                  <Translate contentKey="infraMirrorApp.serviceHeartbeat.errorMessage">Error Message</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('errorMessage')} />
                </th>
                <th className="hand" onClick={sort('metadata')}>
                  <Translate contentKey="infraMirrorApp.serviceHeartbeat.metadata">Metadata</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('metadata')} />
                </th>
                <th>
                  <Translate contentKey="infraMirrorApp.serviceHeartbeat.agent">Agent</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="infraMirrorApp.serviceHeartbeat.monitoredService">Monitored Service</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="infraMirrorApp.serviceHeartbeat.serviceInstance">Service Instance</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {serviceHeartbeatList.map((serviceHeartbeat, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/service-heartbeat/${serviceHeartbeat.id}`} color="link" size="sm">
                      {serviceHeartbeat.id}
                    </Button>
                  </td>
                  <td>
                    {serviceHeartbeat.executedAt ? (
                      <TextFormat type="date" value={serviceHeartbeat.executedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{serviceHeartbeat.success ? 'true' : 'false'}</td>
                  <td>{serviceHeartbeat.status}</td>
                  <td>{serviceHeartbeat.responseTimeMs}</td>
                  <td>{serviceHeartbeat.errorMessage}</td>
                  <td>{serviceHeartbeat.metadata}</td>
                  <td>
                    {serviceHeartbeat.agent ? <Link to={`/agent/${serviceHeartbeat.agent.id}`}>{serviceHeartbeat.agent.id}</Link> : ''}
                  </td>
                  <td>
                    {serviceHeartbeat.monitoredService ? (
                      <Link to={`/monitored-service/${serviceHeartbeat.monitoredService.id}`}>{serviceHeartbeat.monitoredService.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {serviceHeartbeat.serviceInstance ? (
                      <Link to={`/service-instance/${serviceHeartbeat.serviceInstance.id}`}>{serviceHeartbeat.serviceInstance.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/service-heartbeat/${serviceHeartbeat.id}`}
                        color="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/service-heartbeat/${serviceHeartbeat.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() =>
                          (window.location.href = `/service-heartbeat/${serviceHeartbeat.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
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
              <Translate contentKey="infraMirrorApp.serviceHeartbeat.home.notFound">No Service Heartbeats found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={serviceHeartbeatList && serviceHeartbeatList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
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

export default ServiceHeartbeat;
