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

import { getEntities, reset, searchEntities } from './ping-heartbeat.reducer';

export const PingHeartbeat = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();

  const [search, setSearch] = useState('');
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );
  const [sorting, setSorting] = useState(false);

  const pingHeartbeatList = useAppSelector(state => state.pingHeartbeat.entities);
  const loading = useAppSelector(state => state.pingHeartbeat.loading);
  const links = useAppSelector(state => state.pingHeartbeat.links);
  const updateSuccess = useAppSelector(state => state.pingHeartbeat.updateSuccess);

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
      <h2 id="ping-heartbeat-heading" data-cy="PingHeartbeatHeading">
        <Translate contentKey="infraMirrorApp.pingHeartbeat.home.title">Ping Heartbeats</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="infraMirrorApp.pingHeartbeat.home.refreshListLabel">Refresh List</Translate>
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
                  placeholder={translate('infraMirrorApp.pingHeartbeat.home.search')}
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
          dataLength={pingHeartbeatList ? pingHeartbeatList.length : 0}
          next={handleLoadMore}
          hasMore={paginationState.activePage - 1 < links.next}
          loader={<div className="loader">Loading ...</div>}
        >
          {pingHeartbeatList && pingHeartbeatList.length > 0 ? (
            <Table responsive>
              <thead>
                <tr>
                  <th className="hand" onClick={sort('id')}>
                    <Translate contentKey="infraMirrorApp.pingHeartbeat.id">ID</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                  </th>
                  <th className="hand" onClick={sort('executedAt')}>
                    <Translate contentKey="infraMirrorApp.pingHeartbeat.executedAt">Executed At</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('executedAt')} />
                  </th>
                  <th className="hand" onClick={sort('heartbeatType')}>
                    <Translate contentKey="infraMirrorApp.pingHeartbeat.heartbeatType">Heartbeat Type</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('heartbeatType')} />
                  </th>
                  <th className="hand" onClick={sort('success')}>
                    <Translate contentKey="infraMirrorApp.pingHeartbeat.success">Success</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('success')} />
                  </th>
                  <th className="hand" onClick={sort('responseTimeMs')}>
                    <Translate contentKey="infraMirrorApp.pingHeartbeat.responseTimeMs">Response Time Ms</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('responseTimeMs')} />
                  </th>
                  <th className="hand" onClick={sort('packetLoss')}>
                    <Translate contentKey="infraMirrorApp.pingHeartbeat.packetLoss">Packet Loss</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('packetLoss')} />
                  </th>
                  <th className="hand" onClick={sort('jitterMs')}>
                    <Translate contentKey="infraMirrorApp.pingHeartbeat.jitterMs">Jitter Ms</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('jitterMs')} />
                  </th>
                  <th className="hand" onClick={sort('cpuUsage')}>
                    <Translate contentKey="infraMirrorApp.pingHeartbeat.cpuUsage">Cpu Usage</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('cpuUsage')} />
                  </th>
                  <th className="hand" onClick={sort('memoryUsage')}>
                    <Translate contentKey="infraMirrorApp.pingHeartbeat.memoryUsage">Memory Usage</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('memoryUsage')} />
                  </th>
                  <th className="hand" onClick={sort('diskUsage')}>
                    <Translate contentKey="infraMirrorApp.pingHeartbeat.diskUsage">Disk Usage</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('diskUsage')} />
                  </th>
                  <th className="hand" onClick={sort('loadAverage')}>
                    <Translate contentKey="infraMirrorApp.pingHeartbeat.loadAverage">Load Average</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('loadAverage')} />
                  </th>
                  <th className="hand" onClick={sort('processCount')}>
                    <Translate contentKey="infraMirrorApp.pingHeartbeat.processCount">Process Count</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('processCount')} />
                  </th>
                  <th className="hand" onClick={sort('networkRxBytes')}>
                    <Translate contentKey="infraMirrorApp.pingHeartbeat.networkRxBytes">Network Rx Bytes</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('networkRxBytes')} />
                  </th>
                  <th className="hand" onClick={sort('networkTxBytes')}>
                    <Translate contentKey="infraMirrorApp.pingHeartbeat.networkTxBytes">Network Tx Bytes</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('networkTxBytes')} />
                  </th>
                  <th className="hand" onClick={sort('uptimeSeconds')}>
                    <Translate contentKey="infraMirrorApp.pingHeartbeat.uptimeSeconds">Uptime Seconds</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('uptimeSeconds')} />
                  </th>
                  <th className="hand" onClick={sort('status')}>
                    <Translate contentKey="infraMirrorApp.pingHeartbeat.status">Status</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                  </th>
                  <th className="hand" onClick={sort('errorMessage')}>
                    <Translate contentKey="infraMirrorApp.pingHeartbeat.errorMessage">Error Message</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('errorMessage')} />
                  </th>
                  <th className="hand" onClick={sort('errorType')}>
                    <Translate contentKey="infraMirrorApp.pingHeartbeat.errorType">Error Type</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('errorType')} />
                  </th>
                  <th className="hand" onClick={sort('metadata')}>
                    <Translate contentKey="infraMirrorApp.pingHeartbeat.metadata">Metadata</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('metadata')} />
                  </th>
                  <th>
                    <Translate contentKey="infraMirrorApp.pingHeartbeat.instance">Instance</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th>
                    <Translate contentKey="infraMirrorApp.pingHeartbeat.agent">Agent</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {pingHeartbeatList.map((pingHeartbeat, i) => (
                  <tr key={`entity-${i}`} data-cy="entityTable">
                    <td>
                      <Button tag={Link} to={`/ping-heartbeat/${pingHeartbeat.id}`} color="link" size="sm">
                        {pingHeartbeat.id}
                      </Button>
                    </td>
                    <td>
                      {pingHeartbeat.executedAt ? (
                        <TextFormat type="date" value={pingHeartbeat.executedAt} format={APP_DATE_FORMAT} />
                      ) : null}
                    </td>
                    <td>{pingHeartbeat.heartbeatType}</td>
                    <td>{pingHeartbeat.success ? 'true' : 'false'}</td>
                    <td>{pingHeartbeat.responseTimeMs}</td>
                    <td>{pingHeartbeat.packetLoss}</td>
                    <td>{pingHeartbeat.jitterMs}</td>
                    <td>{pingHeartbeat.cpuUsage}</td>
                    <td>{pingHeartbeat.memoryUsage}</td>
                    <td>{pingHeartbeat.diskUsage}</td>
                    <td>{pingHeartbeat.loadAverage}</td>
                    <td>{pingHeartbeat.processCount}</td>
                    <td>{pingHeartbeat.networkRxBytes}</td>
                    <td>{pingHeartbeat.networkTxBytes}</td>
                    <td>{pingHeartbeat.uptimeSeconds}</td>
                    <td>{pingHeartbeat.status}</td>
                    <td>{pingHeartbeat.errorMessage}</td>
                    <td>{pingHeartbeat.errorType}</td>
                    <td>{pingHeartbeat.metadata}</td>
                    <td>
                      {pingHeartbeat.instance ? <Link to={`/instance/${pingHeartbeat.instance.id}`}>{pingHeartbeat.instance.id}</Link> : ''}
                    </td>
                    <td>{pingHeartbeat.agent ? <Link to={`/agent/${pingHeartbeat.agent.id}`}>{pingHeartbeat.agent.id}</Link> : ''}</td>
                    <td className="text-end">
                      <div className="btn-group flex-btn-group-container">
                        <Button tag={Link} to={`/ping-heartbeat/${pingHeartbeat.id}`} color="info" size="sm" data-cy="entityDetailsButton">
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
                <Translate contentKey="infraMirrorApp.pingHeartbeat.home.notFound">No Ping Heartbeats found</Translate>
              </div>
            )
          )}
        </InfiniteScroll>
      </div>
    </div>
  );
};

export default PingHeartbeat;
