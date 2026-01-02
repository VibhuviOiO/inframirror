import React, { useState, useCallback } from 'react';
import { Button, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Translate } from 'react-jhipster';
import { toast } from 'react-toastify';
import { useAppDispatch } from 'app/config/store';

interface EntityDeleteModalProps {
  isOpen: boolean;
  toggle: () => void;
  entityName: string;
  entityDisplayName: string;
  entityId?: number | string | null;
  deleteAction: (id: number | string) => any;
  onDeleteSuccess?: () => void;
}

export const EntityDeleteModal: React.FC<EntityDeleteModalProps> = ({
  isOpen,
  toggle,
  entityName,
  entityDisplayName,
  entityId,
  deleteAction,
  onDeleteSuccess,
}) => {
  const dispatch = useAppDispatch();
  const [deleting, setDeleting] = useState(false);

  const handleDelete = useCallback(async () => {
    if (!entityId) return;

    setDeleting(true);
    try {
      await dispatch(deleteAction(entityId)).unwrap();
      toast.success(`${entityName} deleted successfully`);
      onDeleteSuccess?.();
      toggle();
    } catch (error) {
      toast.error(`Failed to delete ${entityName}`);
    } finally {
      setDeleting(false);
    }
  }, [dispatch, entityId, entityName, deleteAction, onDeleteSuccess, toggle]);

  return (
    <Modal isOpen={isOpen} toggle={toggle}>
      <ModalHeader toggle={toggle}>
        <Translate contentKey="entity.delete.title">Confirm delete operation</Translate>
      </ModalHeader>
      <ModalBody>
        Are you sure you want to delete <strong>{entityDisplayName}</strong>?
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={toggle} disabled={deleting}>
          <Translate contentKey="entity.action.cancel">Cancel</Translate>
        </Button>
        <Button color="danger" onClick={handleDelete} disabled={deleting} data-cy="entityConfirmDeleteButton">
          <FontAwesomeIcon icon="trash" />{' '}
          {deleting ? (
            <Translate contentKey="entity.action.deleting">Deleting...</Translate>
          ) : (
            <Translate contentKey="entity.action.delete">Delete</Translate>
          )}
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default EntityDeleteModal;
