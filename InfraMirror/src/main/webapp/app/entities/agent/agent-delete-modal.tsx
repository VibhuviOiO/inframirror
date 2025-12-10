import React, { useState } from 'react';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTrash, faTimes } from '@fortawesome/free-solid-svg-icons';
import { IAgent } from 'app/shared/model/agent.model';
import axios from 'axios';
import { toast } from 'react-toastify';

interface AgentDeleteModalProps {
  isOpen: boolean;
  toggle: () => void;
  agent?: IAgent | null;
  onDelete?: () => void;
}

export const AgentDeleteModal: React.FC<AgentDeleteModalProps> = ({ isOpen, toggle, agent, onDelete }) => {
  const [deleting, setDeleting] = useState(false);

  const handleDelete = async () => {
    if (!agent?.id) return;

    setDeleting(true);
    try {
      await axios.delete(`/api/agents/${agent.id}`);
      toast.success('Agent deleted successfully');
      onDelete?.();
      toggle();
    } catch (error) {
      toast.error('Failed to delete agent');
    } finally {
      setDeleting(false);
    }
  };

  return (
    <Modal isOpen={isOpen} toggle={toggle} centered>
      <ModalHeader toggle={toggle}>Confirm Delete</ModalHeader>
      <ModalBody>
        Are you sure you want to delete agent <strong>{agent?.name}</strong>?
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" size="sm" onClick={toggle} disabled={deleting}>
          <FontAwesomeIcon icon={faTimes} /> Cancel
        </Button>
        <Button color="danger" size="sm" onClick={handleDelete} disabled={deleting}>
          <FontAwesomeIcon icon={faTrash} /> {deleting ? 'Deleting...' : 'Delete'}
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default AgentDeleteModal;
