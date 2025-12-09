import React, { useState } from 'react';
import { Button, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IDatacenter } from 'app/shared/model/datacenter.model';
import axios from 'axios';
import { toast } from 'react-toastify';

interface DatacenterDeleteModalProps {
  isOpen: boolean;
  toggle: () => void;
  datacenter?: IDatacenter | null;
  onDelete?: () => void;
}

export const DatacenterDeleteModal: React.FC<DatacenterDeleteModalProps> = ({ isOpen, toggle, datacenter, onDelete }) => {
  const [deleting, setDeleting] = useState(false);

  const handleDelete = async () => {
    if (!datacenter?.id) return;

    setDeleting(true);
    try {
      await axios.delete(`/api/datacenters/${datacenter.id}`);
      toast.success('Datacenter deleted successfully');
      onDelete?.();
      toggle();
    } catch (error) {
      toast.error('Failed to delete datacenter');
    } finally {
      setDeleting(false);
    }
  };

  return (
    <Modal isOpen={isOpen} toggle={toggle}>
      <ModalHeader toggle={toggle}>Confirm Delete</ModalHeader>
      <ModalBody>
        Are you sure you want to delete datacenter <strong>{datacenter?.name}</strong>?
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

export default DatacenterDeleteModal;
