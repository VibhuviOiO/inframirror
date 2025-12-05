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

import { getEntities, searchEntities } from './monitored-service.reducer';

export const MonitoredService = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
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
      <h2 id="monitored-service-heading" data-cy="MonitoredServiceHeading">
        <Translate contentKey="infraMirrorApp.monitoredService.home.title">Monitored Services</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="infraMirrorApp.monitoredService.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/monitored-service/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="infraMirrorApp.monitoredService.home.createLabel">Create new Monitored Service</Translate>
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
                  placeholder={translate('infraMirrorApp.monitoredService.home.search')}
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
        {monitoredServiceList && monitoredServiceList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="infraMirrorApp.monitoredService.id">Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('name')}>
                  <Translate contentKey="infraMirrorApp.monitoredService.name">Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th className="hand" onClick={sort('description')}>
                  <Translate contentKey="infraMirrorApp.monitoredService.description">Description</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('description')} />
                </th>
                <th className="hand" onClick={sort('serviceType')}>
                  <Translate contentKey="infraMirrorApp.monitoredService.serviceType">Service Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('serviceType')} />
                </th>
                <th className="hand" onClick={sort('environment')}>
                  <Translate contentKey="infraMirrorApp.monitoredService.environment">Environment</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('environment')} />
                </th>
                <th className="hand" onClick={sort('monitoringEnabled')}>
                  <Translate contentKey="infraMirrorApp.monitoredService.monitoringEnabled">Monitoring Enabled</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('monitoringEnabled')} />
                </th>
                <th className="hand" onClick={sort('clusterMonitoringEnabled')}>
                  <Translate contentKey="infraMirrorApp.monitoredService.clusterMonitoringEnabled">Cluster Monitoring Enabled</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('clusterMonitoringEnabled')} />
                </th>
                <th className="hand" onClick={sort('intervalSeconds')}>
                  <Translate contentKey="infraMirrorApp.monitoredService.intervalSeconds">Interval Seconds</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('intervalSeconds')} />
                </th>
                <th className="hand" onClick={sort('timeoutMs')}>
                  <Translate contentKey="infraMirrorApp.monitoredService.timeoutMs">Timeout Ms</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('timeoutMs')} />
                </th>
                <th className="hand" onClick={sort('retryCount')}>
                  <Translate contentKey="infraMirrorApp.monitoredService.retryCount">Retry Count</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('retryCount')} />
                </th>
                <th className="hand" onClick={sort('latencyWarningMs')}>
                  <Translate contentKey="infraMirrorApp.monitoredService.latencyWarningMs">Latency Warning Ms</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('latencyWarningMs')} />
                </th>
                <th className="hand" onClick={sort('latencyCriticalMs')}>
                  <Translate contentKey="infraMirrorApp.monitoredService.latencyCriticalMs">Latency Critical Ms</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('latencyCriticalMs')} />
                </th>
                <th className="hand" onClick={sort('advancedConfig')}>
                  <Translate contentKey="infraMirrorApp.monitoredService.advancedConfig">Advanced Config</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('advancedConfig')} />
                </th>
                <th className="hand" onClick={sort('isActive')}>
                  <Translate contentKey="infraMirrorApp.monitoredService.isActive">Is Active</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isActive')} />
                </th>
                <th className="hand" onClick={sort('createdAt')}>
                  <Translate contentKey="infraMirrorApp.monitoredService.createdAt">Created At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('createdAt')} />
                </th>
                <th className="hand" onClick={sort('updatedAt')}>
                  <Translate contentKey="infraMirrorApp.monitoredService.updatedAt">Updated At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('updatedAt')} />
                </th>
                <th>
                  <Translate contentKey="infraMirrorApp.monitoredService.datacenter">Datacenter</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {monitoredServiceList.map((monitoredService, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/monitored-service/${monitoredService.id}`} color="link" size="sm">
                      {monitoredService.id}
                    </Button>
                  </td>
                  <td>{monitoredService.name}</td>
                  <td>{monitoredService.description}</td>
                  <td>{monitoredService.serviceType}</td>
                  <td>{monitoredService.environment}</td>
                  <td>{monitoredService.monitoringEnabled ? 'true' : 'false'}</td>
                  <td>{monitoredService.clusterMonitoringEnabled ? 'true' : 'false'}</td>
                  <td>{monitoredService.intervalSeconds}</td>
                  <td>{monitoredService.timeoutMs}</td>
                  <td>{monitoredService.retryCount}</td>
                  <td>{monitoredService.latencyWarningMs}</td>
                  <td>{monitoredService.latencyCriticalMs}</td>
                  <td>{monitoredService.advancedConfig}</td>
                  <td>{monitoredService.isActive ? 'true' : 'false'}</td>
                  <td>
                    {monitoredService.createdAt ? (
                      <TextFormat type="date" value={monitoredService.createdAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {monitoredService.updatedAt ? (
                      <TextFormat type="date" value={monitoredService.updatedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {monitoredService.datacenter ? (
                      <Link to={`/datacenter/${monitoredService.datacenter.id}`}>{monitoredService.datacenter.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/monitored-service/${monitoredService.id}`}
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
                        to={`/monitored-service/${monitoredService.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/monitored-service/${monitoredService.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="infraMirrorApp.monitoredService.home.notFound">No Monitored Services found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={monitoredServiceList && monitoredServiceList.length > 0 ? '' : 'd-none'}>
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

export default MonitoredService;
