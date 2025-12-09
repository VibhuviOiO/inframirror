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

import { getEntities, searchEntities } from './status-page.reducer';

export const StatusPage = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const statusPageList = useAppSelector(state => state.statusPage.entities);
  const loading = useAppSelector(state => state.statusPage.loading);
  const totalItems = useAppSelector(state => state.statusPage.totalItems);

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
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h4 id="status-page-heading" data-cy="StatusPageHeading" className="mb-0">
          <Translate contentKey="infraMirrorApp.statusPage.home.title">Status Pages</Translate>
        </h4>
        <div className="d-flex">
          <Button
            className="me-2"
            color="info"
            size="sm"
            onClick={handleSyncList}
            disabled={loading}
            title={translate('infraMirrorApp.statusPage.home.refreshListLabel')}
          >
            <FontAwesomeIcon icon="sync" spin={loading} />
          </Button>
          <Link
            to="/status-page/new"
            className="btn btn-primary btn-sm jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            title={translate('infraMirrorApp.statusPage.home.createLabel')}
          >
            <Translate contentKey="infraMirrorApp.statusPage.home.createLabel">Create</Translate>
          </Link>
        </div>
      </div>
      <hr />
      <Row className="mb-3">
        <Col sm="12">
          <div className="d-flex gap-2">
            <Input
              type="text"
              name="search"
              value={search}
              onChange={handleSearch}
              placeholder={translate('infraMirrorApp.statusPage.home.search')}
              style={{ flex: 1 }}
            />
            <Button color="primary" size="sm" onClick={startSearching} disabled={!search}>
              <Translate contentKey="infraMirrorApp.statusPage.home.searchButton">Search</Translate>
            </Button>
            {search && (
              <Button color="secondary" size="sm" onClick={clear}>
                <Translate contentKey="infraMirrorApp.statusPage.home.clearSearch">Clear</Translate>
              </Button>
            )}
          </div>
        </Col>
      </Row>
      <div className="table-responsive" style={{ position: 'relative', minHeight: '200px' }}>
        {loading && (
          <div
            style={{
              position: 'absolute',
              top: 0,
              left: 0,
              right: 0,
              bottom: 0,
              backgroundColor: 'rgba(255, 255, 255, 0.8)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              zIndex: 10,
            }}
          >
            <FontAwesomeIcon icon="spinner" spin size="2x" />
          </div>
        )}
        {statusPageList && statusPageList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('name')}>
                  <Translate contentKey="infraMirrorApp.statusPage.name">Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th className="hand" onClick={sort('slug')}>
                  <Translate contentKey="infraMirrorApp.statusPage.slug">Slug</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('slug')} />
                </th>
                <th className="hand" onClick={sort('description')}>
                  <Translate contentKey="infraMirrorApp.statusPage.description">Description</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('description')} />
                </th>
                <th className="hand" onClick={sort('isPublic')}>
                  <Translate contentKey="infraMirrorApp.statusPage.isPublic">Is Public</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isPublic')} />
                </th>
                <th className="hand" onClick={sort('customDomain')}>
                  <Translate contentKey="infraMirrorApp.statusPage.customDomain">Custom Domain</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('customDomain')} />
                </th>
                <th className="hand" onClick={sort('logoUrl')}>
                  <Translate contentKey="infraMirrorApp.statusPage.logoUrl">Logo Url</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('logoUrl')} />
                </th>
                <th className="hand" onClick={sort('themeColor')}>
                  <Translate contentKey="infraMirrorApp.statusPage.themeColor">Theme Color</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('themeColor')} />
                </th>
                <th className="hand" onClick={sort('headerText')}>
                  <Translate contentKey="infraMirrorApp.statusPage.headerText">Header Text</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('headerText')} />
                </th>
                <th className="hand" onClick={sort('footerText')}>
                  <Translate contentKey="infraMirrorApp.statusPage.footerText">Footer Text</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('footerText')} />
                </th>
                <th className="hand" onClick={sort('showResponseTimes')}>
                  <Translate contentKey="infraMirrorApp.statusPage.showResponseTimes">Show Response Times</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('showResponseTimes')} />
                </th>
                <th className="hand" onClick={sort('showUptimePercentage')}>
                  <Translate contentKey="infraMirrorApp.statusPage.showUptimePercentage">Show Uptime Percentage</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('showUptimePercentage')} />
                </th>
                <th className="hand" onClick={sort('autoRefreshSeconds')}>
                  <Translate contentKey="infraMirrorApp.statusPage.autoRefreshSeconds">Auto Refresh Seconds</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('autoRefreshSeconds')} />
                </th>
                <th className="hand" onClick={sort('monitorSelection')}>
                  <Translate contentKey="infraMirrorApp.statusPage.monitorSelection">Monitor Selection</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('monitorSelection')} />
                </th>
                <th className="hand" onClick={sort('isActive')}>
                  <Translate contentKey="infraMirrorApp.statusPage.isActive">Is Active</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isActive')} />
                </th>
                <th className="hand" onClick={sort('isHomePage')}>
                  <Translate contentKey="infraMirrorApp.statusPage.isHomePage">Is Home Page</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isHomePage')} />
                </th>
                <th className="hand" onClick={sort('allowedRoles')}>
                  <Translate contentKey="infraMirrorApp.statusPage.allowedRoles">Allowed Roles</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('allowedRoles')} />
                </th>
                <th className="hand" onClick={sort('createdAt')}>
                  <Translate contentKey="infraMirrorApp.statusPage.createdAt">Created At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('createdAt')} />
                </th>
                <th className="hand" onClick={sort('updatedAt')}>
                  <Translate contentKey="infraMirrorApp.statusPage.updatedAt">Updated At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('updatedAt')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {statusPageList.map((statusPage, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>{statusPage.name}</td>
                  <td>{statusPage.slug}</td>
                  <td>{statusPage.description}</td>
                  <td>{statusPage.isPublic ? 'true' : 'false'}</td>
                  <td>{statusPage.customDomain}</td>
                  <td>{statusPage.logoUrl}</td>
                  <td>{statusPage.themeColor}</td>
                  <td>{statusPage.headerText}</td>
                  <td>{statusPage.footerText}</td>
                  <td>{statusPage.showResponseTimes ? 'true' : 'false'}</td>
                  <td>{statusPage.showUptimePercentage ? 'true' : 'false'}</td>
                  <td>{statusPage.autoRefreshSeconds}</td>
                  <td>{statusPage.monitorSelection}</td>
                  <td>{statusPage.isActive ? 'true' : 'false'}</td>
                  <td>{statusPage.isHomePage ? 'true' : 'false'}</td>
                  <td>{statusPage.allowedRoles}</td>
                  <td>{statusPage.createdAt ? <TextFormat type="date" value={statusPage.createdAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{statusPage.updatedAt ? <TextFormat type="date" value={statusPage.updatedAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/status-page/${statusPage.id}`}
                        color="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                        title={translate('entity.action.view')}
                      >
                        <FontAwesomeIcon icon="eye" size="sm" />
                      </Button>
                      <Button
                        tag={Link}
                        to={`/status-page/${statusPage.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                        title={translate('entity.action.edit')}
                      >
                        <FontAwesomeIcon icon="pencil-alt" size="sm" />
                      </Button>
                      <Button
                        onClick={() =>
                          (window.location.href = `/status-page/${statusPage.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                        title={translate('entity.action.delete')}
                      >
                        <FontAwesomeIcon icon="trash" size="sm" />
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="text-center py-5">
              <FontAwesomeIcon icon="inbox" size="3x" className="text-muted mb-3" />
              <h5 className="text-muted">
                <Translate contentKey="infraMirrorApp.statusPage.home.emptyState">
                  No status pages available. Create your first status page to get started.
                </Translate>
              </h5>
              <Link to="/status-page/new" className="btn btn-primary mt-3">
                <FontAwesomeIcon icon="plus" /> <Translate contentKey="infraMirrorApp.statusPage.home.createLabel">Create</Translate>
              </Link>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={statusPageList && statusPageList.length > 0 ? 'd-flex justify-content-between align-items-center' : 'd-none'}>
          <div>
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div>
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

export default StatusPage;
