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

import { getEntities, searchEntities } from './branding.reducer';

export const Branding = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const brandingList = useAppSelector(state => state.branding.entities);
  const loading = useAppSelector(state => state.branding.loading);
  const totalItems = useAppSelector(state => state.branding.totalItems);

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
      <h2 id="branding-heading" data-cy="BrandingHeading">
        <Translate contentKey="infraMirrorApp.branding.home.title">Brandings</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="infraMirrorApp.branding.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/branding/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="infraMirrorApp.branding.home.createLabel">Create new Branding</Translate>
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
                  placeholder={translate('infraMirrorApp.branding.home.search')}
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
        {brandingList && brandingList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="infraMirrorApp.branding.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('title')}>
                  <Translate contentKey="infraMirrorApp.branding.title">Title</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('title')} />
                </th>
                <th className="hand" onClick={sort('description')}>
                  <Translate contentKey="infraMirrorApp.branding.description">Description</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('description')} />
                </th>
                <th className="hand" onClick={sort('keywords')}>
                  <Translate contentKey="infraMirrorApp.branding.keywords">Keywords</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('keywords')} />
                </th>
                <th className="hand" onClick={sort('author')}>
                  <Translate contentKey="infraMirrorApp.branding.author">Author</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('author')} />
                </th>
                <th className="hand" onClick={sort('faviconPath')}>
                  <Translate contentKey="infraMirrorApp.branding.faviconPath">Favicon Path</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('faviconPath')} />
                </th>
                <th className="hand" onClick={sort('logoPath')}>
                  <Translate contentKey="infraMirrorApp.branding.logoPath">Logo Path</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('logoPath')} />
                </th>
                <th className="hand" onClick={sort('logoWidth')}>
                  <Translate contentKey="infraMirrorApp.branding.logoWidth">Logo Width</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('logoWidth')} />
                </th>
                <th className="hand" onClick={sort('logoHeight')}>
                  <Translate contentKey="infraMirrorApp.branding.logoHeight">Logo Height</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('logoHeight')} />
                </th>
                <th className="hand" onClick={sort('footerTitle')}>
                  <Translate contentKey="infraMirrorApp.branding.footerTitle">Footer Title</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('footerTitle')} />
                </th>
                <th className="hand" onClick={sort('isActive')}>
                  <Translate contentKey="infraMirrorApp.branding.isActive">Is Active</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isActive')} />
                </th>
                <th className="hand" onClick={sort('createdAt')}>
                  <Translate contentKey="infraMirrorApp.branding.createdAt">Created At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('createdAt')} />
                </th>
                <th className="hand" onClick={sort('updatedAt')}>
                  <Translate contentKey="infraMirrorApp.branding.updatedAt">Updated At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('updatedAt')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {brandingList.map((branding, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/branding/${branding.id}`} color="link" size="sm">
                      {branding.id}
                    </Button>
                  </td>
                  <td>{branding.title}</td>
                  <td>{branding.description}</td>
                  <td>{branding.keywords}</td>
                  <td>{branding.author}</td>
                  <td>{branding.faviconPath}</td>
                  <td>{branding.logoPath}</td>
                  <td>{branding.logoWidth}</td>
                  <td>{branding.logoHeight}</td>
                  <td>{branding.footerTitle}</td>
                  <td>{branding.isActive ? 'true' : 'false'}</td>
                  <td>{branding.createdAt ? <TextFormat type="date" value={branding.createdAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{branding.updatedAt ? <TextFormat type="date" value={branding.updatedAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/branding/${branding.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/branding/${branding.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/branding/${branding.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="infraMirrorApp.branding.home.notFound">No Brandings found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={brandingList && brandingList.length > 0 ? '' : 'd-none'}>
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

export default Branding;
