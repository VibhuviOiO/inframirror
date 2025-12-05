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

import { getEntities, searchEntities } from './service.reducer';

export const Service = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const serviceList = useAppSelector(state => state.service.entities);
  const loading = useAppSelector(state => state.service.loading);
  const totalItems = useAppSelector(state => state.service.totalItems);

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
      <h2 id="service-heading" data-cy="ServiceHeading">
        <Translate contentKey="infraMirrorApp.service.home.title">Services</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="infraMirrorApp.service.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/service/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="infraMirrorApp.service.home.createLabel">Create new Service</Translate>
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
                  placeholder={translate('infraMirrorApp.service.home.search')}
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
        {serviceList && serviceList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="infraMirrorApp.service.id">Id</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('name')}>
                  <Translate contentKey="infraMirrorApp.service.name">Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th className="hand" onClick={sort('description')}>
                  <Translate contentKey="infraMirrorApp.service.description">Description</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('description')} />
                </th>
                <th className="hand" onClick={sort('serviceType')}>
                  <Translate contentKey="infraMirrorApp.service.serviceType">Service Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('serviceType')} />
                </th>
                <th className="hand" onClick={sort('environment')}>
                  <Translate contentKey="infraMirrorApp.service.environment">Environment</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('environment')} />
                </th>
                <th className="hand" onClick={sort('monitoringEnabled')}>
                  <Translate contentKey="infraMirrorApp.service.monitoringEnabled">Monitoring Enabled</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('monitoringEnabled')} />
                </th>
                <th className="hand" onClick={sort('clusterMonitoringEnabled')}>
                  <Translate contentKey="infraMirrorApp.service.clusterMonitoringEnabled">Cluster Monitoring Enabled</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('clusterMonitoringEnabled')} />
                </th>
                <th className="hand" onClick={sort('intervalSeconds')}>
                  <Translate contentKey="infraMirrorApp.service.intervalSeconds">Interval Seconds</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('intervalSeconds')} />
                </th>
                <th className="hand" onClick={sort('timeoutMs')}>
                  <Translate contentKey="infraMirrorApp.service.timeoutMs">Timeout Ms</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('timeoutMs')} />
                </th>
                <th className="hand" onClick={sort('retryCount')}>
                  <Translate contentKey="infraMirrorApp.service.retryCount">Retry Count</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('retryCount')} />
                </th>
                <th className="hand" onClick={sort('latencyWarningMs')}>
                  <Translate contentKey="infraMirrorApp.service.latencyWarningMs">Latency Warning Ms</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('latencyWarningMs')} />
                </th>
                <th className="hand" onClick={sort('latencyCriticalMs')}>
                  <Translate contentKey="infraMirrorApp.service.latencyCriticalMs">Latency Critical Ms</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('latencyCriticalMs')} />
                </th>
                <th className="hand" onClick={sort('advancedConfig')}>
                  <Translate contentKey="infraMirrorApp.service.advancedConfig">Advanced Config</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('advancedConfig')} />
                </th>
                <th className="hand" onClick={sort('isActive')}>
                  <Translate contentKey="infraMirrorApp.service.isActive">Is Active</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isActive')} />
                </th>
                <th className="hand" onClick={sort('createdAt')}>
                  <Translate contentKey="infraMirrorApp.service.createdAt">Created At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('createdAt')} />
                </th>
                <th className="hand" onClick={sort('updatedAt')}>
                  <Translate contentKey="infraMirrorApp.service.updatedAt">Updated At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('updatedAt')} />
                </th>
                <th>
                  <Translate contentKey="infraMirrorApp.service.datacenter">Datacenter</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {serviceList.map((service, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/service/${service.id}`} color="link" size="sm">
                      {service.id}
                    </Button>
                  </td>
                  <td>{service.name}</td>
                  <td>{service.description}</td>
                  <td>{service.serviceType}</td>
                  <td>{service.environment}</td>
                  <td>{service.monitoringEnabled ? 'true' : 'false'}</td>
                  <td>{service.clusterMonitoringEnabled ? 'true' : 'false'}</td>
                  <td>{service.intervalSeconds}</td>
                  <td>{service.timeoutMs}</td>
                  <td>{service.retryCount}</td>
                  <td>{service.latencyWarningMs}</td>
                  <td>{service.latencyCriticalMs}</td>
                  <td>{service.advancedConfig}</td>
                  <td>{service.isActive ? 'true' : 'false'}</td>
                  <td>{service.createdAt ? <TextFormat type="date" value={service.createdAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{service.updatedAt ? <TextFormat type="date" value={service.updatedAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{service.datacenter ? <Link to={`/datacenter/${service.datacenter.id}`}>{service.datacenter.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/service/${service.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/service/${service.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/service/${service.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="infraMirrorApp.service.home.notFound">No Services found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={serviceList && serviceList.length > 0 ? '' : 'd-none'}>
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

export default Service;
