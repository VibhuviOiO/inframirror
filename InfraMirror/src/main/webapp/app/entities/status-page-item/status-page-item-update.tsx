import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getStatusPages } from 'app/entities/status-page/status-page.reducer';
import { createEntity, getEntity, reset, updateEntity } from './status-page-item.reducer';

export const StatusPageItemUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const statusPages = useAppSelector(state => state.statusPage.entities);
  const statusPageItemEntity = useAppSelector(state => state.statusPageItem.entity);
  const loading = useAppSelector(state => state.statusPageItem.loading);
  const updating = useAppSelector(state => state.statusPageItem.updating);
  const updateSuccess = useAppSelector(state => state.statusPageItem.updateSuccess);

  const handleClose = () => {
    navigate(`/status-page-item${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getStatusPages({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.itemId !== undefined && typeof values.itemId !== 'number') {
      values.itemId = Number(values.itemId);
    }
    if (values.displayOrder !== undefined && typeof values.displayOrder !== 'number') {
      values.displayOrder = Number(values.displayOrder);
    }
    values.createdAt = convertDateTimeToServer(values.createdAt);

    const entity = {
      ...statusPageItemEntity,
      ...values,
      statusPage: statusPages.find(it => it.id.toString() === values.statusPage?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          createdAt: displayDefaultDateTime(),
        }
      : {
          ...statusPageItemEntity,
          createdAt: convertDateTimeFromServer(statusPageItemEntity.createdAt),
          statusPage: statusPageItemEntity?.statusPage?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="infraMirrorApp.statusPageItem.home.createOrEditLabel" data-cy="StatusPageItemCreateUpdateHeading">
            <Translate contentKey="infraMirrorApp.statusPageItem.home.createOrEditLabel">Create or edit a StatusPageItem</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="status-page-item-id"
                  label={translate('infraMirrorApp.statusPageItem.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('infraMirrorApp.statusPageItem.itemType')}
                id="status-page-item-itemType"
                name="itemType"
                data-cy="itemType"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 50, message: translate('entity.validation.maxlength', { max: 50 }) },
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.statusPageItem.itemId')}
                id="status-page-item-itemId"
                name="itemId"
                data-cy="itemId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('infraMirrorApp.statusPageItem.displayOrder')}
                id="status-page-item-displayOrder"
                name="displayOrder"
                data-cy="displayOrder"
                type="text"
              />
              <ValidatedField
                label={translate('infraMirrorApp.statusPageItem.createdAt')}
                id="status-page-item-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="status-page-item-statusPage"
                name="statusPage"
                data-cy="statusPage"
                label={translate('infraMirrorApp.statusPageItem.statusPage')}
                type="select"
                required
              >
                <option value="" key="0" />
                {statusPages
                  ? statusPages.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/status-page-item" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default StatusPageItemUpdate;
