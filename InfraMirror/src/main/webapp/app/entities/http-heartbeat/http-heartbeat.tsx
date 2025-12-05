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

import { getEntities, searchEntities } from './http-heartbeat.reducer';

export const HttpHeartbeat = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const httpHeartbeatList = useAppSelector(state => state.httpHeartbeat.entities);
  const loading = useAppSelector(state => state.httpHeartbeat.loading);
  const totalItems = useAppSelector(state => state.httpHeartbeat.totalItems);

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
      <h2 id="http-heartbeat-heading" data-cy="HttpHeartbeatHeading">
        <Translate contentKey="infraMirrorApp.httpHeartbeat.home.title">Http Heartbeats</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="infraMirrorApp.httpHeartbeat.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/http-heartbeat/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="infraMirrorApp.httpHeartbeat.home.createLabel">Create new Http Heartbeat</Translate>
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
        {httpHeartbeatList && httpHeartbeatList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.id">Id</Translate>{' '}
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
                <th className="hand" onClick={sort('dnsResolvedIp')}>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.dnsResolvedIp">Dns Resolved Ip</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('dnsResolvedIp')} />
                </th>
                <th className="hand" onClick={sort('tcpConnectMs')}>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.tcpConnectMs">Tcp Connect Ms</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('tcpConnectMs')} />
                </th>
                <th className="hand" onClick={sort('tlsHandshakeMs')}>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.tlsHandshakeMs">Tls Handshake Ms</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('tlsHandshakeMs')} />
                </th>
                <th className="hand" onClick={sort('sslCertificateValid')}>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.sslCertificateValid">Ssl Certificate Valid</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sslCertificateValid')} />
                </th>
                <th className="hand" onClick={sort('sslCertificateExpiry')}>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.sslCertificateExpiry">Ssl Certificate Expiry</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sslCertificateExpiry')} />
                </th>
                <th className="hand" onClick={sort('sslCertificateIssuer')}>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.sslCertificateIssuer">Ssl Certificate Issuer</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sslCertificateIssuer')} />
                </th>
                <th className="hand" onClick={sort('sslDaysUntilExpiry')}>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.sslDaysUntilExpiry">Ssl Days Until Expiry</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sslDaysUntilExpiry')} />
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
                <th className="hand" onClick={sort('dnsDetails')}>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.dnsDetails">Dns Details</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('dnsDetails')} />
                </th>
                <th className="hand" onClick={sort('tlsDetails')}>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.tlsDetails">Tls Details</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('tlsDetails')} />
                </th>
                <th className="hand" onClick={sort('httpVersion')}>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.httpVersion">Http Version</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('httpVersion')} />
                </th>
                <th className="hand" onClick={sort('contentEncoding')}>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.contentEncoding">Content Encoding</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('contentEncoding')} />
                </th>
                <th className="hand" onClick={sort('compressionRatio')}>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.compressionRatio">Compression Ratio</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('compressionRatio')} />
                </th>
                <th className="hand" onClick={sort('transferEncoding')}>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.transferEncoding">Transfer Encoding</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('transferEncoding')} />
                </th>
                <th className="hand" onClick={sort('responseBodyHash')}>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.responseBodyHash">Response Body Hash</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('responseBodyHash')} />
                </th>
                <th className="hand" onClick={sort('responseBodySample')}>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.responseBodySample">Response Body Sample</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('responseBodySample')} />
                </th>
                <th className="hand" onClick={sort('responseBodyValid')}>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.responseBodyValid">Response Body Valid</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('responseBodyValid')} />
                </th>
                <th className="hand" onClick={sort('responseBodyUncompressedBytes')}>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.responseBodyUncompressedBytes">
                    Response Body Uncompressed Bytes
                  </Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('responseBodyUncompressedBytes')} />
                </th>
                <th className="hand" onClick={sort('redirectDetails')}>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.redirectDetails">Redirect Details</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('redirectDetails')} />
                </th>
                <th className="hand" onClick={sort('cacheControl')}>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.cacheControl">Cache Control</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('cacheControl')} />
                </th>
                <th className="hand" onClick={sort('etag')}>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.etag">Etag</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('etag')} />
                </th>
                <th className="hand" onClick={sort('cacheAge')}>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.cacheAge">Cache Age</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('cacheAge')} />
                </th>
                <th className="hand" onClick={sort('cdnProvider')}>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.cdnProvider">Cdn Provider</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('cdnProvider')} />
                </th>
                <th className="hand" onClick={sort('cdnPop')}>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.cdnPop">Cdn Pop</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('cdnPop')} />
                </th>
                <th className="hand" onClick={sort('rateLimitDetails')}>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.rateLimitDetails">Rate Limit Details</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('rateLimitDetails')} />
                </th>
                <th className="hand" onClick={sort('networkPath')}>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.networkPath">Network Path</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('networkPath')} />
                </th>
                <th className="hand" onClick={sort('agentMetrics')}>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.agentMetrics">Agent Metrics</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('agentMetrics')} />
                </th>
                <th className="hand" onClick={sort('phaseLatencies')}>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.phaseLatencies">Phase Latencies</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('phaseLatencies')} />
                </th>
                <th>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.agent">Agent</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="infraMirrorApp.httpHeartbeat.monitor">Monitor</Translate> <FontAwesomeIcon icon="sort" />
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
                    {httpHeartbeat.executedAt ? <TextFormat type="date" value={httpHeartbeat.executedAt} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>{httpHeartbeat.success ? 'true' : 'false'}</td>
                  <td>{httpHeartbeat.responseTimeMs}</td>
                  <td>{httpHeartbeat.responseSizeBytes}</td>
                  <td>{httpHeartbeat.responseStatusCode}</td>
                  <td>{httpHeartbeat.responseContentType}</td>
                  <td>{httpHeartbeat.responseServer}</td>
                  <td>{httpHeartbeat.responseCacheStatus}</td>
                  <td>{httpHeartbeat.dnsLookupMs}</td>
                  <td>{httpHeartbeat.dnsResolvedIp}</td>
                  <td>{httpHeartbeat.tcpConnectMs}</td>
                  <td>{httpHeartbeat.tlsHandshakeMs}</td>
                  <td>{httpHeartbeat.sslCertificateValid ? 'true' : 'false'}</td>
                  <td>
                    {httpHeartbeat.sslCertificateExpiry ? (
                      <TextFormat type="date" value={httpHeartbeat.sslCertificateExpiry} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{httpHeartbeat.sslCertificateIssuer}</td>
                  <td>{httpHeartbeat.sslDaysUntilExpiry}</td>
                  <td>{httpHeartbeat.timeToFirstByteMs}</td>
                  <td>{httpHeartbeat.warningThresholdMs}</td>
                  <td>{httpHeartbeat.criticalThresholdMs}</td>
                  <td>{httpHeartbeat.errorType}</td>
                  <td>{httpHeartbeat.errorMessage}</td>
                  <td>{httpHeartbeat.rawRequestHeaders}</td>
                  <td>{httpHeartbeat.rawResponseHeaders}</td>
                  <td>{httpHeartbeat.rawResponseBody}</td>
                  <td>{httpHeartbeat.dnsDetails}</td>
                  <td>{httpHeartbeat.tlsDetails}</td>
                  <td>{httpHeartbeat.httpVersion}</td>
                  <td>{httpHeartbeat.contentEncoding}</td>
                  <td>{httpHeartbeat.compressionRatio}</td>
                  <td>{httpHeartbeat.transferEncoding}</td>
                  <td>{httpHeartbeat.responseBodyHash}</td>
                  <td>{httpHeartbeat.responseBodySample}</td>
                  <td>{httpHeartbeat.responseBodyValid ? 'true' : 'false'}</td>
                  <td>{httpHeartbeat.responseBodyUncompressedBytes}</td>
                  <td>{httpHeartbeat.redirectDetails}</td>
                  <td>{httpHeartbeat.cacheControl}</td>
                  <td>{httpHeartbeat.etag}</td>
                  <td>{httpHeartbeat.cacheAge}</td>
                  <td>{httpHeartbeat.cdnProvider}</td>
                  <td>{httpHeartbeat.cdnPop}</td>
                  <td>{httpHeartbeat.rateLimitDetails}</td>
                  <td>{httpHeartbeat.networkPath}</td>
                  <td>{httpHeartbeat.agentMetrics}</td>
                  <td>{httpHeartbeat.phaseLatencies}</td>
                  <td>{httpHeartbeat.agent ? <Link to={`/agent/${httpHeartbeat.agent.id}`}>{httpHeartbeat.agent.id}</Link> : ''}</td>
                  <td>
                    {httpHeartbeat.monitor ? <Link to={`/http-monitor/${httpHeartbeat.monitor.id}`}>{httpHeartbeat.monitor.id}</Link> : ''}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/http-heartbeat/${httpHeartbeat.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/http-heartbeat/${httpHeartbeat.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/http-heartbeat/${httpHeartbeat.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="infraMirrorApp.httpHeartbeat.home.notFound">No Http Heartbeats found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={httpHeartbeatList && httpHeartbeatList.length > 0 ? '' : 'd-none'}>
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

export default HttpHeartbeat;
