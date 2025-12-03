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

import { getEntities, reset, searchEntities } from './session-log.reducer';

export const SessionLog = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();

  const [search, setSearch] = useState('');
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );
  const [sorting, setSorting] = useState(false);

  const sessionLogList = useAppSelector(state => state.sessionLog.entities);
  const loading = useAppSelector(state => state.sessionLog.loading);
  const links = useAppSelector(state => state.sessionLog.links);
  const updateSuccess = useAppSelector(state => state.sessionLog.updateSuccess);

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
      <h2 id="session-log-heading" data-cy="SessionLogHeading">
        <Translate contentKey="infraMirrorApp.sessionLog.home.title">Session Logs</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="infraMirrorApp.sessionLog.home.refreshListLabel">Refresh List</Translate>
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
                  placeholder={translate('infraMirrorApp.sessionLog.home.search')}
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
          dataLength={sessionLogList ? sessionLogList.length : 0}
          next={handleLoadMore}
          hasMore={paginationState.activePage - 1 < links.next}
          loader={<div className="loader">Loading ...</div>}
        >
          {sessionLogList && sessionLogList.length > 0 ? (
            <Table responsive>
              <thead>
                <tr>
                  <th className="hand" onClick={sort('id')}>
                    <Translate contentKey="infraMirrorApp.sessionLog.id">ID</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                  </th>
                  <th className="hand" onClick={sort('sessionType')}>
                    <Translate contentKey="infraMirrorApp.sessionLog.sessionType">Session Type</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('sessionType')} />
                  </th>
                  <th className="hand" onClick={sort('startTime')}>
                    <Translate contentKey="infraMirrorApp.sessionLog.startTime">Start Time</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('startTime')} />
                  </th>
                  <th className="hand" onClick={sort('endTime')}>
                    <Translate contentKey="infraMirrorApp.sessionLog.endTime">End Time</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('endTime')} />
                  </th>
                  <th className="hand" onClick={sort('duration')}>
                    <Translate contentKey="infraMirrorApp.sessionLog.duration">Duration</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('duration')} />
                  </th>
                  <th className="hand" onClick={sort('sourceIpAddress')}>
                    <Translate contentKey="infraMirrorApp.sessionLog.sourceIpAddress">Source Ip Address</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('sourceIpAddress')} />
                  </th>
                  <th className="hand" onClick={sort('status')}>
                    <Translate contentKey="infraMirrorApp.sessionLog.status">Status</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                  </th>
                  <th className="hand" onClick={sort('terminationReason')}>
                    <Translate contentKey="infraMirrorApp.sessionLog.terminationReason">Termination Reason</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('terminationReason')} />
                  </th>
                  <th className="hand" onClick={sort('commandsExecuted')}>
                    <Translate contentKey="infraMirrorApp.sessionLog.commandsExecuted">Commands Executed</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('commandsExecuted')} />
                  </th>
                  <th className="hand" onClick={sort('bytesTransferred')}>
                    <Translate contentKey="infraMirrorApp.sessionLog.bytesTransferred">Bytes Transferred</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('bytesTransferred')} />
                  </th>
                  <th className="hand" onClick={sort('sessionId')}>
                    <Translate contentKey="infraMirrorApp.sessionLog.sessionId">Session Id</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('sessionId')} />
                  </th>
                  <th className="hand" onClick={sort('metadata')}>
                    <Translate contentKey="infraMirrorApp.sessionLog.metadata">Metadata</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('metadata')} />
                  </th>
                  <th>
                    <Translate contentKey="infraMirrorApp.sessionLog.instance">Instance</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th>
                    <Translate contentKey="infraMirrorApp.sessionLog.agent">Agent</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th>
                    <Translate contentKey="infraMirrorApp.sessionLog.user">User</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {sessionLogList.map((sessionLog, i) => (
                  <tr key={`entity-${i}`} data-cy="entityTable">
                    <td>
                      <Button tag={Link} to={`/session-log/${sessionLog.id}`} color="link" size="sm">
                        {sessionLog.id}
                      </Button>
                    </td>
                    <td>{sessionLog.sessionType}</td>
                    <td>
                      {sessionLog.startTime ? <TextFormat type="date" value={sessionLog.startTime} format={APP_DATE_FORMAT} /> : null}
                    </td>
                    <td>{sessionLog.endTime ? <TextFormat type="date" value={sessionLog.endTime} format={APP_DATE_FORMAT} /> : null}</td>
                    <td>{sessionLog.duration}</td>
                    <td>{sessionLog.sourceIpAddress}</td>
                    <td>{sessionLog.status}</td>
                    <td>{sessionLog.terminationReason}</td>
                    <td>{sessionLog.commandsExecuted}</td>
                    <td>{sessionLog.bytesTransferred}</td>
                    <td>{sessionLog.sessionId}</td>
                    <td>{sessionLog.metadata}</td>
                    <td>{sessionLog.instance ? <Link to={`/instance/${sessionLog.instance.id}`}>{sessionLog.instance.id}</Link> : ''}</td>
                    <td>{sessionLog.agent ? <Link to={`/agent/${sessionLog.agent.id}`}>{sessionLog.agent.id}</Link> : ''}</td>
                    <td>{sessionLog.user ? sessionLog.user.id : ''}</td>
                    <td className="text-end">
                      <div className="btn-group flex-btn-group-container">
                        <Button tag={Link} to={`/session-log/${sessionLog.id}`} color="info" size="sm" data-cy="entityDetailsButton">
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
                <Translate contentKey="infraMirrorApp.sessionLog.home.notFound">No Session Logs found</Translate>
              </div>
            )
          )}
        </InfiniteScroll>
      </div>
    </div>
  );
};

export default SessionLog;
