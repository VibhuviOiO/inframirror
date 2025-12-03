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

import { getEntities, reset, searchEntities } from './http-heartbeat.reducer';

export const HttpHeartbeat = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();

  const [search, setSearch] = useState('');
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );
  const [sorting, setSorting] = useState(false);

  const httpHeartbeatList = useAppSelector(state => state.httpHeartbeat.entities);
  const loading = useAppSelector(state => state.httpHeartbeat.loading);
  const links = useAppSelector(state => state.httpHeartbeat.links);
  const updateSuccess = useAppSelector(state => state.httpHeartbeat.updateSuccess);

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
      <h2 id="http-heartbeat-heading" data-cy="HttpHeartbeatHeading">
        <Translate contentKey="infraMirrorApp.httpHeartbeat.home.title">Http Heartbeats</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="infraMirrorApp.httpHeartbeat.home.refreshListLabel">Refresh List</Translate>
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
                  placeholder={translate('infraMirrorApp.httpHeartbeat.home.search')}
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
          dataLength={httpHeartbeatList ? httpHeartbeatList.length : 0}
          next={handleLoadMore}
          hasMore={paginationState.activePage - 1 < links.next}
          loader={<div className="loader">Loading ...</div>}
        >
          {httpHeartbeatList && httpHeartbeatList.length > 0 ? (
            <Table responsive>
              <thead>
                <tr>
                  <th className="hand" onClick={sort('id')}>
                    <Translate contentKey="infraMirrorApp.httpHeartbeat.id">ID</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                  </th>
                  <th className="hand" onClick={sort('executedAt')}>
                    <Translate contentKey="infraMirrorApp.httpHeartbeat.executedAt">Executed At</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('executedAt')} />
                  </th>
                  <th className="hand" onClick={sort('success')}>
                    <Translate contentKey="infraMirrorApp.httpHeartbeat.success">Success</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('success')} />
                  </th>
                  <th className="hand" onClick={sort('responseTimeMs')}>
                    <Translate contentKey="infraMirrorApp.httpHeartbeat.responseTimeMs">Response Time Ms</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('responseTimeMs')} />
                  </th>
                  <th className="hand" onClick={sort('responseSizeBytes')}>
                    <Translate contentKey="infraMirrorApp.httpHeartbeat.responseSizeBytes">Response Size Bytes</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('responseSizeBytes')} />
                  </th>
                  <th className="hand" onClick={sort('responseStatusCode')}>
                    <Translate contentKey="infraMirrorApp.httpHeartbeat.responseStatusCode">Response Status Code</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('responseStatusCode')} />
                  </th>
                  <th className="hand" onClick={sort('responseContentType')}>
                    <Translate contentKey="infraMirrorApp.httpHeartbeat.responseContentType">Response Content Type</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('responseContentType')} />
                  </th>
                  <th className="hand" onClick={sort('responseServer')}>
                    <Translate contentKey="infraMirrorApp.httpHeartbeat.responseServer">Response Server</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('responseServer')} />
                  </th>
                  <th className="hand" onClick={sort('responseCacheStatus')}>
                    <Translate contentKey="infraMirrorApp.httpHeartbeat.responseCacheStatus">Response Cache Status</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('responseCacheStatus')} />
                  </th>
                  <th className="hand" onClick={sort('dnsLookupMs')}>
                    <Translate contentKey="infraMirrorApp.httpHeartbeat.dnsLookupMs">Dns Lookup Ms</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('dnsLookupMs')} />
                  </th>
                  <th className="hand" onClick={sort('tcpConnectMs')}>
                    <Translate contentKey="infraMirrorApp.httpHeartbeat.tcpConnectMs">Tcp Connect Ms</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('tcpConnectMs')} />
                  </th>
                  <th className="hand" onClick={sort('tlsHandshakeMs')}>
                    <Translate contentKey="infraMirrorApp.httpHeartbeat.tlsHandshakeMs">Tls Handshake Ms</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('tlsHandshakeMs')} />
                  </th>
                  <th className="hand" onClick={sort('timeToFirstByteMs')}>
                    <Translate contentKey="infraMirrorApp.httpHeartbeat.timeToFirstByteMs">Time To First Byte Ms</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('timeToFirstByteMs')} />
                  </th>
                  <th className="hand" onClick={sort('warningThresholdMs')}>
                    <Translate contentKey="infraMirrorApp.httpHeartbeat.warningThresholdMs">Warning Threshold Ms</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('warningThresholdMs')} />
                  </th>
                  <th className="hand" onClick={sort('criticalThresholdMs')}>
                    <Translate contentKey="infraMirrorApp.httpHeartbeat.criticalThresholdMs">Critical Threshold Ms</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('criticalThresholdMs')} />
                  </th>
                  <th className="hand" onClick={sort('errorType')}>
                    <Translate contentKey="infraMirrorApp.httpHeartbeat.errorType">Error Type</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('errorType')} />
                  </th>
                  <th className="hand" onClick={sort('errorMessage')}>
                    <Translate contentKey="infraMirrorApp.httpHeartbeat.errorMessage">Error Message</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('errorMessage')} />
                  </th>
                  <th className="hand" onClick={sort('rawRequestHeaders')}>
                    <Translate contentKey="infraMirrorApp.httpHeartbeat.rawRequestHeaders">Raw Request Headers</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('rawRequestHeaders')} />
                  </th>
                  <th className="hand" onClick={sort('rawResponseHeaders')}>
                    <Translate contentKey="infraMirrorApp.httpHeartbeat.rawResponseHeaders">Raw Response Headers</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('rawResponseHeaders')} />
                  </th>
                  <th className="hand" onClick={sort('rawResponseBody')}>
                    <Translate contentKey="infraMirrorApp.httpHeartbeat.rawResponseBody">Raw Response Body</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('rawResponseBody')} />
                  </th>
                  <th>
                    <Translate contentKey="infraMirrorApp.httpHeartbeat.monitor">Monitor</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th>
                    <Translate contentKey="infraMirrorApp.httpHeartbeat.agent">Agent</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {httpHeartbeatList.map((httpHeartbeat, i) => (
                  <tr key={`entity-${i}`} data-cy="entityTable">
                    <td>
                      <Button tag={Link} to={`/http-heartbeat/${httpHeartbeat.id}`} color="link" size="sm">
                        {httpHeartbeat.id}
                      </Button>
                    </td>
                    <td>
                      {httpHeartbeat.executedAt ? (
                        <TextFormat type="date" value={httpHeartbeat.executedAt} format={APP_DATE_FORMAT} />
                      ) : null}
                    </td>
                    <td>{httpHeartbeat.success ? 'true' : 'false'}</td>
                    <td>{httpHeartbeat.responseTimeMs}</td>
                    <td>{httpHeartbeat.responseSizeBytes}</td>
                    <td>{httpHeartbeat.responseStatusCode}</td>
                    <td>{httpHeartbeat.responseContentType}</td>
                    <td>{httpHeartbeat.responseServer}</td>
                    <td>{httpHeartbeat.responseCacheStatus}</td>
                    <td>{httpHeartbeat.dnsLookupMs}</td>
                    <td>{httpHeartbeat.tcpConnectMs}</td>
                    <td>{httpHeartbeat.tlsHandshakeMs}</td>
                    <td>{httpHeartbeat.timeToFirstByteMs}</td>
                    <td>{httpHeartbeat.warningThresholdMs}</td>
                    <td>{httpHeartbeat.criticalThresholdMs}</td>
                    <td>{httpHeartbeat.errorType}</td>
                    <td>{httpHeartbeat.errorMessage}</td>
                    <td>{httpHeartbeat.rawRequestHeaders}</td>
                    <td>{httpHeartbeat.rawResponseHeaders}</td>
                    <td>{httpHeartbeat.rawResponseBody}</td>
                    <td>
                      {httpHeartbeat.monitor ? (
                        <Link to={`/http-monitor/${httpHeartbeat.monitor.id}`}>{httpHeartbeat.monitor.id}</Link>
                      ) : (
                        ''
                      )}
                    </td>
                    <td>{httpHeartbeat.agent ? <Link to={`/agent/${httpHeartbeat.agent.id}`}>{httpHeartbeat.agent.id}</Link> : ''}</td>
                    <td className="text-end">
                      <div className="btn-group flex-btn-group-container">
                        <Button tag={Link} to={`/http-heartbeat/${httpHeartbeat.id}`} color="info" size="sm" data-cy="entityDetailsButton">
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
                <Translate contentKey="infraMirrorApp.httpHeartbeat.home.notFound">No Http Heartbeats found</Translate>
              </div>
            )
          )}
        </InfiniteScroll>
      </div>
    </div>
  );
};

export default HttpHeartbeat;
