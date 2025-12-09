import React, { useState } from 'react';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBan, faTrash } from '@fortawesome/free-solid-svg-icons';
import { IMonitoredService } from 'app/shared/model/monitored-service.model';
import axios from 'axios';
import { toast } from 'react-toastify';

interface MonitoredServiceDeleteModalProps {
  isOpen: boolean;
  toggle: () => void;
  service?: IMonitoredService | null;
  onDelete?: () => void;
}

export const MonitoredServiceDeleteModal: React.FC<MonitoredServiceDeleteModalProps> = ({ isOpen, toggle, service, onDelete }) => {
  const [deleting, setDeleting] = useState(false);

  const handleConfirmDelete = async () => {
    if (service && service.id) {
      setDeleting(true);
      try {
        await axios.delete(`/api/monitored-services/${service.id}`);
        toast.success('Service deleted successfully');
        toggle();
        onDelete?.();
      } catch (error) {
        toast.error('Failed to delete service');
      } finally {
        setDeleting(false);
      }
    }
  };

  return (
    <Modal isOpen={isOpen} toggle={toggle}>
      <ModalHeader toggle={toggle}>Confirm delete operation</ModalHeader>
      <ModalBody>
        Are you sure you want to delete Service <strong>{service?.name || service?.id}</strong>?
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={toggle} disabled={deleting}>
          <FontAwesomeIcon icon={faBan} /> Cancel
        </Button>
        <Button color="danger" onClick={handleConfirmDelete} disabled={deleting}>
          <FontAwesomeIcon icon={faTrash} /> Delete
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default MonitoredServiceDeleteModal;
