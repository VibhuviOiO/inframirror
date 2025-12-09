import React, { useState } from 'react';
import { Button, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRegion } from 'app/shared/model/region.model';
import axios from 'axios';
import { toast } from 'react-toastify';

interface RegionDeleteModalProps {
  isOpen: boolean;
  toggle: () => void;
  region?: IRegion | null;
  onDelete?: () => void;
}

export const RegionDeleteModal: React.FC<RegionDeleteModalProps> = ({ isOpen, toggle, region, onDelete }) => {
  const [deleting, setDeleting] = useState(false);

  const handleDelete = async () => {
    if (!region?.id) return;

    setDeleting(true);
    try {
      await axios.delete(`/api/regions/${region.id}`);
      toast.success('Region deleted successfully');
      onDelete?.();
      toggle();
    } catch (error) {
      toast.error('Failed to delete region');
    } finally {
      setDeleting(false);
    }
  };

  return (
    <Modal isOpen={isOpen} toggle={toggle}>
      <ModalHeader toggle={toggle}>Confirm Delete</ModalHeader>
      <ModalBody>
        Are you sure you want to delete region <strong>{region?.name}</strong>?
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

export default RegionDeleteModal;
