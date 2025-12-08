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

import { getEntities, searchEntities } from './instance.reducer';
import 'app/custom.scss';

export const Instance = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const instanceList = useAppSelector(state => state.instance.entities);
  const loading = useAppSelector(state => state.instance.loading);
  const totalItems = useAppSelector(state => state.instance.totalItems);

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
      <h2 id="instance-heading" data-cy="InstanceHeading">
        <Translate contentKey="infraMirrorApp.instance.home.title">Instances</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="infraMirrorApp.instance.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/instance/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="infraMirrorApp.instance.home.createLabel">Create new Instance</Translate>
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
                  placeholder={translate('infraMirrorApp.instance.home.search')}
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
        {instanceList && instanceList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="infraMirrorApp.instance.id">Id</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('name')}>
                  <Translate contentKey="infraMirrorApp.instance.name">Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th className="hand" onClick={sort('hostname')}>
                  <Translate contentKey="infraMirrorApp.instance.hostname">Hostname</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('hostname')} />
                </th>
                <th className="hand" onClick={sort('description')}>
                  <Translate contentKey="infraMirrorApp.instance.description">Description</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('description')} />
                </th>
                <th className="hand" onClick={sort('instanceType')}>
                  <Translate contentKey="infraMirrorApp.instance.instanceType">Instance Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('instanceType')} />
                </th>
                <th className="hand" onClick={sort('monitoringType')}>
                  <Translate contentKey="infraMirrorApp.instance.monitoringType">Monitoring Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('monitoringType')} />
                </th>
                <th className="hand" onClick={sort('operatingSystem')}>
                  <Translate contentKey="infraMirrorApp.instance.operatingSystem">Operating System</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('operatingSystem')} />
                </th>
                <th className="hand" onClick={sort('platform')}>
                  <Translate contentKey="infraMirrorApp.instance.platform">Platform</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('platform')} />
                </th>
                <th className="hand" onClick={sort('privateIpAddress')}>
                  <Translate contentKey="infraMirrorApp.instance.privateIpAddress">Private Ip Address</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('privateIpAddress')} />
                </th>
                <th className="hand" onClick={sort('publicIpAddress')}>
                  <Translate contentKey="infraMirrorApp.instance.publicIpAddress">Public Ip Address</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('publicIpAddress')} />
                </th>
                <th className="hand" onClick={sort('tags')}>
                  <Translate contentKey="infraMirrorApp.instance.tags">Tags</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('tags')} />
                </th>
                <th className="hand" onClick={sort('pingEnabled')}>
                  <Translate contentKey="infraMirrorApp.instance.pingEnabled">Ping Enabled</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('pingEnabled')} />
                </th>
                <th className="hand" onClick={sort('pingInterval')}>
                  <Translate contentKey="infraMirrorApp.instance.pingInterval">Ping Interval</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('pingInterval')} />
                </th>
                <th className="hand" onClick={sort('pingTimeoutMs')}>
                  <Translate contentKey="infraMirrorApp.instance.pingTimeoutMs">Ping Timeout Ms</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('pingTimeoutMs')} />
                </th>
                <th className="hand" onClick={sort('pingRetryCount')}>
                  <Translate contentKey="infraMirrorApp.instance.pingRetryCount">Ping Retry Count</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('pingRetryCount')} />
                </th>
                <th className="hand" onClick={sort('hardwareMonitoringEnabled')}>
                  <Translate contentKey="infraMirrorApp.instance.hardwareMonitoringEnabled">Hardware Monitoring Enabled</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('hardwareMonitoringEnabled')} />
                </th>
                <th className="hand" onClick={sort('hardwareMonitoringInterval')}>
                  <Translate contentKey="infraMirrorApp.instance.hardwareMonitoringInterval">Hardware Monitoring Interval</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('hardwareMonitoringInterval')} />
                </th>
                <th className="hand" onClick={sort('cpuWarningThreshold')}>
                  <Translate contentKey="infraMirrorApp.instance.cpuWarningThreshold">Cpu Warning Threshold</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('cpuWarningThreshold')} />
                </th>
                <th className="hand" onClick={sort('cpuDangerThreshold')}>
                  <Translate contentKey="infraMirrorApp.instance.cpuDangerThreshold">Cpu Danger Threshold</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('cpuDangerThreshold')} />
                </th>
                <th className="hand" onClick={sort('memoryWarningThreshold')}>
                  <Translate contentKey="infraMirrorApp.instance.memoryWarningThreshold">Memory Warning Threshold</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('memoryWarningThreshold')} />
                </th>
                <th className="hand" onClick={sort('memoryDangerThreshold')}>
                  <Translate contentKey="infraMirrorApp.instance.memoryDangerThreshold">Memory Danger Threshold</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('memoryDangerThreshold')} />
                </th>
                <th className="hand" onClick={sort('diskWarningThreshold')}>
                  <Translate contentKey="infraMirrorApp.instance.diskWarningThreshold">Disk Warning Threshold</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('diskWarningThreshold')} />
                </th>
                <th className="hand" onClick={sort('diskDangerThreshold')}>
                  <Translate contentKey="infraMirrorApp.instance.diskDangerThreshold">Disk Danger Threshold</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('diskDangerThreshold')} />
                </th>
                <th className="hand" onClick={sort('createdAt')}>
                  <Translate contentKey="infraMirrorApp.instance.createdAt">Created At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('createdAt')} />
                </th>
                <th className="hand" onClick={sort('updatedAt')}>
                  <Translate contentKey="infraMirrorApp.instance.updatedAt">Updated At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('updatedAt')} />
                </th>
                <th className="hand" onClick={sort('lastPingAt')}>
                  <Translate contentKey="infraMirrorApp.instance.lastPingAt">Last Ping At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('lastPingAt')} />
                </th>
                <th className="hand" onClick={sort('lastHardwareCheckAt')}>
                  <Translate contentKey="infraMirrorApp.instance.lastHardwareCheckAt">Last Hardware Check At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('lastHardwareCheckAt')} />
                </th>
                <th>
                  <Translate contentKey="infraMirrorApp.instance.datacenter">Datacenter</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="infraMirrorApp.instance.agent">Agent</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {instanceList.map((instance, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/instance/${instance.id}`} color="link" size="sm">
                      {instance.id}
                    </Button>
                  </td>
                  <td>{instance.name}</td>
                  <td>{instance.hostname}</td>
                  <td>{instance.description}</td>
                  <td>{instance.instanceType}</td>
                  <td>{instance.monitoringType}</td>
                  <td>{instance.operatingSystem}</td>
                  <td>{instance.platform}</td>
                  <td>{instance.privateIpAddress}</td>
                  <td>{instance.publicIpAddress}</td>
                  <td>{instance.tags}</td>
                  <td>{instance.pingEnabled ? 'true' : 'false'}</td>
                  <td>{instance.pingInterval}</td>
                  <td>{instance.pingTimeoutMs}</td>
                  <td>{instance.pingRetryCount}</td>
                  <td>{instance.hardwareMonitoringEnabled ? 'true' : 'false'}</td>
                  <td>{instance.hardwareMonitoringInterval}</td>
                  <td>{instance.cpuWarningThreshold}</td>
                  <td>{instance.cpuDangerThreshold}</td>
                  <td>{instance.memoryWarningThreshold}</td>
                  <td>{instance.memoryDangerThreshold}</td>
                  <td>{instance.diskWarningThreshold}</td>
                  <td>{instance.diskDangerThreshold}</td>
                  <td>{instance.createdAt ? <TextFormat type="date" value={instance.createdAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{instance.updatedAt ? <TextFormat type="date" value={instance.updatedAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{instance.lastPingAt ? <TextFormat type="date" value={instance.lastPingAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>
                    {instance.lastHardwareCheckAt ? (
                      <TextFormat type="date" value={instance.lastHardwareCheckAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{instance.datacenter ? <Link to={`/datacenter/${instance.datacenter.id}`}>{instance.datacenter.id}</Link> : ''}</td>
                  <td>{instance.agent ? <Link to={`/agent/${instance.agent.id}`}>{instance.agent.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/instance/${instance.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/instance/${instance.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/instance/${instance.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="infraMirrorApp.instance.home.notFound">No Instances found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={instanceList && instanceList.length > 0 ? '' : 'd-none'}>
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

export default Instance;
