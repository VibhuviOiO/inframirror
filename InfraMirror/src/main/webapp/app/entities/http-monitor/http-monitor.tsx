import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Col, Form, FormGroup, Input, InputGroup, Row, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities, searchEntities } from './http-monitor.reducer';

export const HttpMonitor = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const httpMonitorList = useAppSelector(state => state.httpMonitor.entities);
  const loading = useAppSelector(state => state.httpMonitor.loading);
  const totalItems = useAppSelector(state => state.httpMonitor.totalItems);

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
      <h2 id="http-monitor-heading" data-cy="HttpMonitorHeading">
        <Translate contentKey="infraMirrorApp.httpMonitor.home.title">Http Monitors</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="infraMirrorApp.httpMonitor.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/http-monitor/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="infraMirrorApp.httpMonitor.home.createLabel">Create new Http Monitor</Translate>
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
                  placeholder={translate('infraMirrorApp.httpMonitor.home.search')}
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
        {httpMonitorList && httpMonitorList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.id">Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('name')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.name">Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th className="hand" onClick={sort('method')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.method">Method</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('method')} />
                </th>
                <th className="hand" onClick={sort('type')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.type">Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('type')} />
                </th>
                <th className="hand" onClick={sort('url')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.url">Url</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('url')} />
                </th>
                <th className="hand" onClick={sort('headers')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.headers">Headers</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('headers')} />
                </th>
                <th className="hand" onClick={sort('body')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.body">Body</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('body')} />
                </th>
                <th className="hand" onClick={sort('intervalSeconds')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.intervalSeconds">Interval Seconds</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('intervalSeconds')} />
                </th>
                <th className="hand" onClick={sort('timeoutSeconds')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.timeoutSeconds">Timeout Seconds</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('timeoutSeconds')} />
                </th>
                <th className="hand" onClick={sort('retryCount')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.retryCount">Retry Count</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('retryCount')} />
                </th>
                <th className="hand" onClick={sort('retryDelaySeconds')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.retryDelaySeconds">Retry Delay Seconds</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('retryDelaySeconds')} />
                </th>
                <th className="hand" onClick={sort('responseTimeWarningMs')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.responseTimeWarningMs">Response Time Warning Ms</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('responseTimeWarningMs')} />
                </th>
                <th className="hand" onClick={sort('responseTimeCriticalMs')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.responseTimeCriticalMs">Response Time Critical Ms</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('responseTimeCriticalMs')} />
                </th>
                <th className="hand" onClick={sort('uptimeWarningPercent')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.uptimeWarningPercent">Uptime Warning Percent</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('uptimeWarningPercent')} />
                </th>
                <th className="hand" onClick={sort('uptimeCriticalPercent')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.uptimeCriticalPercent">Uptime Critical Percent</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('uptimeCriticalPercent')} />
                </th>
                <th className="hand" onClick={sort('includeResponseBody')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.includeResponseBody">Include Response Body</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('includeResponseBody')} />
                </th>
                <th className="hand" onClick={sort('resendNotificationCount')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.resendNotificationCount">Resend Notification Count</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('resendNotificationCount')} />
                </th>
                <th className="hand" onClick={sort('certificateExpiryDays')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.certificateExpiryDays">Certificate Expiry Days</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('certificateExpiryDays')} />
                </th>
                <th className="hand" onClick={sort('ignoreTlsError')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.ignoreTlsError">Ignore Tls Error</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('ignoreTlsError')} />
                </th>
                <th className="hand" onClick={sort('checkSslCertificate')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.checkSslCertificate">Check Ssl Certificate</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('checkSslCertificate')} />
                </th>
                <th className="hand" onClick={sort('checkDnsResolution')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.checkDnsResolution">Check Dns Resolution</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('checkDnsResolution')} />
                </th>
                <th className="hand" onClick={sort('upsideDownMode')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.upsideDownMode">Upside Down Mode</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('upsideDownMode')} />
                </th>
                <th className="hand" onClick={sort('maxRedirects')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.maxRedirects">Max Redirects</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('maxRedirects')} />
                </th>
                <th className="hand" onClick={sort('description')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.description">Description</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('description')} />
                </th>
                <th className="hand" onClick={sort('tags')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.tags">Tags</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('tags')} />
                </th>
                <th className="hand" onClick={sort('enabled')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.enabled">Enabled</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('enabled')} />
                </th>
                <th className="hand" onClick={sort('expectedStatusCodes')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.expectedStatusCodes">Expected Status Codes</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('expectedStatusCodes')} />
                </th>
                <th className="hand" onClick={sort('performanceBudgetMs')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.performanceBudgetMs">Performance Budget Ms</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('performanceBudgetMs')} />
                </th>
                <th className="hand" onClick={sort('sizeBudgetKb')}>
                  <Translate contentKey="infraMirrorApp.httpMonitor.sizeBudgetKb">Size Budget Kb</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sizeBudgetKb')} />
                </th>
                <th>
                  <Translate contentKey="infraMirrorApp.httpMonitor.parent">Parent</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {httpMonitorList.map((httpMonitor, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/http-monitor/${httpMonitor.id}`} color="link" size="sm">
                      {httpMonitor.id}
                    </Button>
                  </td>
                  <td>{httpMonitor.name}</td>
                  <td>{httpMonitor.method}</td>
                  <td>{httpMonitor.type}</td>
                  <td>{httpMonitor.url}</td>
                  <td>{httpMonitor.headers}</td>
                  <td>{httpMonitor.body}</td>
                  <td>{httpMonitor.intervalSeconds}</td>
                  <td>{httpMonitor.timeoutSeconds}</td>
                  <td>{httpMonitor.retryCount}</td>
                  <td>{httpMonitor.retryDelaySeconds}</td>
                  <td>{httpMonitor.responseTimeWarningMs}</td>
                  <td>{httpMonitor.responseTimeCriticalMs}</td>
                  <td>{httpMonitor.uptimeWarningPercent}</td>
                  <td>{httpMonitor.uptimeCriticalPercent}</td>
                  <td>{httpMonitor.includeResponseBody ? 'true' : 'false'}</td>
                  <td>{httpMonitor.resendNotificationCount}</td>
                  <td>{httpMonitor.certificateExpiryDays}</td>
                  <td>{httpMonitor.ignoreTlsError ? 'true' : 'false'}</td>
                  <td>{httpMonitor.checkSslCertificate ? 'true' : 'false'}</td>
                  <td>{httpMonitor.checkDnsResolution ? 'true' : 'false'}</td>
                  <td>{httpMonitor.upsideDownMode ? 'true' : 'false'}</td>
                  <td>{httpMonitor.maxRedirects}</td>
                  <td>{httpMonitor.description}</td>
                  <td>{httpMonitor.tags}</td>
                  <td>{httpMonitor.enabled ? 'true' : 'false'}</td>
                  <td>{httpMonitor.expectedStatusCodes}</td>
                  <td>{httpMonitor.performanceBudgetMs}</td>
                  <td>{httpMonitor.sizeBudgetKb}</td>
                  <td>{httpMonitor.parent ? <Link to={`/http-monitor/${httpMonitor.parent.id}`}>{httpMonitor.parent.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/http-monitor/${httpMonitor.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/http-monitor/${httpMonitor.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/http-monitor/${httpMonitor.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="infraMirrorApp.httpMonitor.home.notFound">No Http Monitors found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={httpMonitorList && httpMonitorList.length > 0 ? '' : 'd-none'}>
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

export default HttpMonitor;
