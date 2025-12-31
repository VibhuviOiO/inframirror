import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Col, Input, Row, Card, CardBody, Badge, Dropdown, DropdownToggle, DropdownMenu, DropdownItem } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities, searchEntities } from './status-page.reducer';
import StatusPageSidePanel from './status-page-side-panel';
import './status-page.scss';

export const StatusPage = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [dropdownOpen, setDropdownOpen] = useState<{ [key: number]: boolean }>({});
  const [sidePanelOpen, setSidePanelOpen] = useState(false);
  const [selectedStatusPage, setSelectedStatusPage] = useState(null);
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const statusPageList = useAppSelector(state => state.statusPage.entities);
  const loading = useAppSelector(state => state.statusPage.loading);
  const totalItems = useAppSelector(state => state.statusPage.totalItems);

  const getAllEntities = () => {
    if (search && search.length >= 3) {
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
    if (search && search.length >= 3) {
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

  const handleSearch = event => {
    const value = event.target.value;
    setSearch(value);
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    const timer = setTimeout(() => {
      sortEntities();
    }, 500);
    return () => clearTimeout(timer);
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

  const totalPages = Math.ceil(totalItems / paginationState.itemsPerPage);
  const startItem = (paginationState.activePage - 1) * paginationState.itemsPerPage + 1;
  const endItem = Math.min(paginationState.activePage * paginationState.itemsPerPage, totalItems);

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

  const toggleDropdown = (index: number) => {
    setDropdownOpen(prev => ({ ...prev, [index]: !prev[index] }));
  };

  const openSidePanel = (statusPage = null) => {
    setSelectedStatusPage(statusPage);
    setSidePanelOpen(true);
  };

  const closeSidePanel = () => {
    setSidePanelOpen(false);
    setSelectedStatusPage(null);
  };

  const handleSidePanelSuccess = () => {
    closeSidePanel();
    handleSyncList();
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h4 id="status-page-heading" data-cy="StatusPageHeading" className="mb-0">
          <Translate contentKey="infraMirrorApp.statusPage.home.title">Status Pages</Translate>
        </h4>
        <div className="d-flex gap-2 align-items-center">
          {totalItems > 0 && (
            <div className="d-flex align-items-center gap-2 text-muted small">
              <span>
                {startItem}–{endItem} of {totalItems}
              </span>
              <Button
                color="link"
                size="sm"
                className="p-1 text-muted"
                onClick={() => handlePagination(paginationState.activePage - 1)}
                disabled={paginationState.activePage === 1}
                title="Previous page"
              >
                <FontAwesomeIcon icon="chevron-left" />
              </Button>
              <Button
                color="link"
                size="sm"
                className="p-1 text-muted"
                onClick={() => handlePagination(paginationState.activePage + 1)}
                disabled={paginationState.activePage >= totalPages}
                title="Next page"
              >
                <FontAwesomeIcon icon="chevron-right" />
              </Button>
            </div>
          )}
          <Input
            type="text"
            name="search"
            value={search}
            onChange={handleSearch}
            placeholder={translate('infraMirrorApp.statusPage.home.search')}
            style={{ width: '250px' }}
          />
          <Button
            color="info"
            size="sm"
            onClick={handleSyncList}
            disabled={loading}
            title={translate('infraMirrorApp.statusPage.home.refreshListLabel')}
          >
            <FontAwesomeIcon icon="sync" spin={loading} />
          </Button>
          <Button
            color="primary"
            size="sm"
            onClick={() => openSidePanel()}
            id="jh-create-entity"
            data-cy="entityCreateButton"
            title={translate('infraMirrorApp.statusPage.home.createLabel')}
          >
            <FontAwesomeIcon icon="plus" /> <Translate contentKey="infraMirrorApp.statusPage.home.createLabel">Create</Translate>
          </Button>
        </div>
      </div>
      <hr />
      <div style={{ minHeight: '200px' }}>
        {loading ? (
          <div>
            {[1, 2, 3].map(i => (
              <div key={i} className="skeleton-loader skeleton-card mb-2" />
            ))}
          </div>
        ) : statusPageList && statusPageList.length > 0 ? (
          <div>
            {statusPageList.map((statusPage, i) => (
              <Card key={`entity-${i}`} className="mb-2 shadow-sm status-page-card">
                <CardBody className="py-2">
                  <div className="d-flex justify-content-between align-items-center">
                    <div className="d-flex align-items-center gap-3 flex-grow-1">
                      <div>
                        <strong>{statusPage.name}</strong>
                        {statusPage.description && <span className="text-muted ms-2">— {statusPage.description}</span>}
                      </div>
                      <div className="d-flex gap-1">
                        {statusPage.isPublic && <Badge className="py-0 status-badge-up">Public</Badge>}
                        {statusPage.isActive && <Badge className="py-0 status-badge-up">Active</Badge>}
                        {statusPage.isHomePage && <Badge className="py-0 status-badge-degraded">Home</Badge>}
                      </div>
                      <div className="small">
                        <Link to={`/status-page/view/${statusPage.slug}`} className="text-decoration-none">
                          <code>/s/{statusPage.slug}</code>
                        </Link>
                      </div>
                    </div>
                    <div className="d-flex align-items-center gap-2">
                      {statusPage.itemCount !== undefined && (
                        <Badge color="secondary" className="py-0">
                          <FontAwesomeIcon icon="circle" className="me-1" style={{ fontSize: '0.5em' }} />
                          {statusPage.itemCount}
                        </Badge>
                      )}
                      <Dropdown isOpen={dropdownOpen[i]} toggle={() => toggleDropdown(i)}>
                        <DropdownToggle color="link" className="text-dark p-0">
                          <FontAwesomeIcon icon="ellipsis-v" size="lg" />
                        </DropdownToggle>
                        <DropdownMenu end>
                          <DropdownItem tag={Link} to={`/status-page/view/${statusPage.slug}`}>
                            <FontAwesomeIcon icon="eye" className="me-2" /> View
                          </DropdownItem>
                          {statusPage.isPublic && (
                            <DropdownItem tag="a" href={`/s/${statusPage.slug}`} target="_blank" rel="noopener noreferrer">
                              <FontAwesomeIcon icon="external-link-alt" className="me-2" /> Open Public Page
                            </DropdownItem>
                          )}
                          <DropdownItem onClick={() => openSidePanel(statusPage)}>
                            <FontAwesomeIcon icon="pencil-alt" className="me-2" /> Edit
                          </DropdownItem>
                          <DropdownItem divider />
                          <DropdownItem
                            tag={Link}
                            to={`/status-page/${statusPage.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                            className="text-danger"
                          >
                            <FontAwesomeIcon icon="trash" className="me-2" /> Delete
                          </DropdownItem>
                        </DropdownMenu>
                      </Dropdown>
                    </div>
                  </div>
                </CardBody>
              </Card>
            ))}
          </div>
        ) : (
          <Card className="border-0 shadow-sm">
            <CardBody className="text-center py-5">
              <FontAwesomeIcon icon="clipboard-list" size="4x" className="text-primary mb-4" />
              <h4 className="mb-3">Welcome to Status Pages</h4>
              <p className="text-muted mb-4">
                Create status pages to monitor your infrastructure and share real-time status with your team or customers.
              </p>
              <div className="d-flex justify-content-center gap-3 mb-4">
                <div className="text-start">
                  <h6 className="text-primary">
                    <FontAwesomeIcon icon="check-circle" className="me-2" />
                    Quick Start
                  </h6>
                  <ul className="small text-muted">
                    <li>Create a status page</li>
                    <li>Add monitors (HTTP, Services, Instances)</li>
                    <li>Configure dependencies</li>
                    <li>Share with your team</li>
                  </ul>
                </div>
              </div>
              <Button color="primary" size="lg" onClick={() => openSidePanel()}>
                <FontAwesomeIcon icon="plus" className="me-2" />
                Create Your First Status Page
              </Button>
            </CardBody>
          </Card>
        )}
      </div>
      <StatusPageSidePanel
        isOpen={sidePanelOpen}
        onClose={closeSidePanel}
        statusPage={selectedStatusPage}
        onSuccess={handleSidePanelSuccess}
      />
    </div>
  );
};

export default StatusPage;
