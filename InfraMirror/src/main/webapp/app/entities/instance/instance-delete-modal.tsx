import React, { useState } from 'react';
import { Button, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IInstance } from 'app/shared/model/instance.model';
import axios from 'axios';
import { toast } from 'react-toastify';

interface InstanceDeleteModalProps {
  isOpen: boolean;
  toggle: () => void;
  instance?: IInstance | null;
  onDelete?: () => void;
}

export const InstanceDeleteModal: React.FC<InstanceDeleteModalProps> = ({ isOpen, toggle, instance, onDelete }) => {
  const [deleting, setDeleting] = useState(false);

  const handleDelete = async () => {
    if (!instance?.id) return;

    setDeleting(true);
    try {
      await axios.delete(`/api/instances/${instance.id}`);
      toast.success('Instance deleted successfully');
      onDelete?.();
      toggle();
    } catch (error) {
      toast.error('Failed to delete instance');
    } finally {
      setDeleting(false);
    }
  };

  return (
    <Modal isOpen={isOpen} toggle={toggle}>
      <ModalHeader toggle={toggle}>Confirm Delete</ModalHeader>
      <ModalBody>
        Are you sure you want to delete instance <strong>{instance?.name}</strong>?
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={toggle} disabled={deleting}>
          Cancel
        </Button>
        <Button color="danger" onClick={handleDelete} disabled={deleting}>
          <FontAwesomeIcon icon="trash" /> {deleting ? 'Deleting...' : 'Delete'}
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default InstanceDeleteModal;
