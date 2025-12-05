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

import { getEntities, searchEntities } from './instance-heartbeat.reducer';

export const InstanceHeartbeat = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const instanceHeartbeatList = useAppSelector(state => state.instanceHeartbeat.entities);
  const loading = useAppSelector(state => state.instanceHeartbeat.loading);
  const totalItems = useAppSelector(state => state.instanceHeartbeat.totalItems);

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
      <h2 id="instance-heartbeat-heading" data-cy="InstanceHeartbeatHeading">
        <Translate contentKey="infraMirrorApp.instanceHeartbeat.home.title">Instance Heartbeats</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="infraMirrorApp.instanceHeartbeat.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/instance-heartbeat/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="infraMirrorApp.instanceHeartbeat.home.createLabel">Create new Instance Heartbeat</Translate>
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
                  placeholder={translate('infraMirrorApp.instanceHeartbeat.home.search')}
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
        {instanceHeartbeatList && instanceHeartbeatList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="infraMirrorApp.instanceHeartbeat.id">Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('executedAt')}>
                  <Translate contentKey="infraMirrorApp.instanceHeartbeat.executedAt">Executed At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('executedAt')} />
                </th>
                <th className="hand" onClick={sort('heartbeatType')}>
                  <Translate contentKey="infraMirrorApp.instanceHeartbeat.heartbeatType">Heartbeat Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('heartbeatType')} />
                </th>
                <th className="hand" onClick={sort('success')}>
                  <Translate contentKey="infraMirrorApp.instanceHeartbeat.success">Success</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('success')} />
                </th>
                <th className="hand" onClick={sort('responseTimeMs')}>
                  <Translate contentKey="infraMirrorApp.instanceHeartbeat.responseTimeMs">Response Time Ms</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('responseTimeMs')} />
                </th>
                <th className="hand" onClick={sort('packetLoss')}>
                  <Translate contentKey="infraMirrorApp.instanceHeartbeat.packetLoss">Packet Loss</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('packetLoss')} />
                </th>
                <th className="hand" onClick={sort('jitterMs')}>
                  <Translate contentKey="infraMirrorApp.instanceHeartbeat.jitterMs">Jitter Ms</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('jitterMs')} />
                </th>
                <th className="hand" onClick={sort('cpuUsage')}>
                  <Translate contentKey="infraMirrorApp.instanceHeartbeat.cpuUsage">Cpu Usage</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('cpuUsage')} />
                </th>
                <th className="hand" onClick={sort('memoryUsage')}>
                  <Translate contentKey="infraMirrorApp.instanceHeartbeat.memoryUsage">Memory Usage</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('memoryUsage')} />
                </th>
                <th className="hand" onClick={sort('diskUsage')}>
                  <Translate contentKey="infraMirrorApp.instanceHeartbeat.diskUsage">Disk Usage</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('diskUsage')} />
                </th>
                <th className="hand" onClick={sort('loadAverage')}>
                  <Translate contentKey="infraMirrorApp.instanceHeartbeat.loadAverage">Load Average</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('loadAverage')} />
                </th>
                <th className="hand" onClick={sort('processCount')}>
                  <Translate contentKey="infraMirrorApp.instanceHeartbeat.processCount">Process Count</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('processCount')} />
                </th>
                <th className="hand" onClick={sort('networkRxBytes')}>
                  <Translate contentKey="infraMirrorApp.instanceHeartbeat.networkRxBytes">Network Rx Bytes</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('networkRxBytes')} />
                </th>
                <th className="hand" onClick={sort('networkTxBytes')}>
                  <Translate contentKey="infraMirrorApp.instanceHeartbeat.networkTxBytes">Network Tx Bytes</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('networkTxBytes')} />
                </th>
                <th className="hand" onClick={sort('uptimeSeconds')}>
                  <Translate contentKey="infraMirrorApp.instanceHeartbeat.uptimeSeconds">Uptime Seconds</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('uptimeSeconds')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="infraMirrorApp.instanceHeartbeat.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('errorMessage')}>
                  <Translate contentKey="infraMirrorApp.instanceHeartbeat.errorMessage">Error Message</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('errorMessage')} />
                </th>
                <th className="hand" onClick={sort('errorType')}>
                  <Translate contentKey="infraMirrorApp.instanceHeartbeat.errorType">Error Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('errorType')} />
                </th>
                <th className="hand" onClick={sort('metadata')}>
                  <Translate contentKey="infraMirrorApp.instanceHeartbeat.metadata">Metadata</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('metadata')} />
                </th>
                <th>
                  <Translate contentKey="infraMirrorApp.instanceHeartbeat.agent">Agent</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="infraMirrorApp.instanceHeartbeat.instance">Instance</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {instanceHeartbeatList.map((instanceHeartbeat, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/instance-heartbeat/${instanceHeartbeat.id}`} color="link" size="sm">
                      {instanceHeartbeat.id}
                    </Button>
                  </td>
                  <td>
                    {instanceHeartbeat.executedAt ? (
                      <TextFormat type="date" value={instanceHeartbeat.executedAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{instanceHeartbeat.heartbeatType}</td>
                  <td>{instanceHeartbeat.success ? 'true' : 'false'}</td>
                  <td>{instanceHeartbeat.responseTimeMs}</td>
                  <td>{instanceHeartbeat.packetLoss}</td>
                  <td>{instanceHeartbeat.jitterMs}</td>
                  <td>{instanceHeartbeat.cpuUsage}</td>
                  <td>{instanceHeartbeat.memoryUsage}</td>
                  <td>{instanceHeartbeat.diskUsage}</td>
                  <td>{instanceHeartbeat.loadAverage}</td>
                  <td>{instanceHeartbeat.processCount}</td>
                  <td>{instanceHeartbeat.networkRxBytes}</td>
                  <td>{instanceHeartbeat.networkTxBytes}</td>
                  <td>{instanceHeartbeat.uptimeSeconds}</td>
                  <td>{instanceHeartbeat.status}</td>
                  <td>{instanceHeartbeat.errorMessage}</td>
                  <td>{instanceHeartbeat.errorType}</td>
                  <td>{instanceHeartbeat.metadata}</td>
                  <td>
                    {instanceHeartbeat.agent ? <Link to={`/agent/${instanceHeartbeat.agent.id}`}>{instanceHeartbeat.agent.id}</Link> : ''}
                  </td>
                  <td>
                    {instanceHeartbeat.instance ? (
                      <Link to={`/instance/${instanceHeartbeat.instance.id}`}>{instanceHeartbeat.instance.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/instance-heartbeat/${instanceHeartbeat.id}`}
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
                        to={`/instance-heartbeat/${instanceHeartbeat.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/instance-heartbeat/${instanceHeartbeat.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="infraMirrorApp.instanceHeartbeat.home.notFound">No Instance Heartbeats found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={instanceHeartbeatList && instanceHeartbeatList.length > 0 ? '' : 'd-none'}>
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

export default InstanceHeartbeat;
