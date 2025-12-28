import React, { useState } from 'react';
import { Button, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IAgentMonitor } from 'app/shared/model/agent-monitor.model';
import axios from 'axios';
import { toast } from 'react-toastify';

interface AgentMonitorDeleteModalProps {
  isOpen: boolean;
  toggle: () => void;
  agentMonitor?: IAgentMonitor | null;
  onDelete?: () => void;
}

export const AgentMonitorDeleteModal: React.FC<AgentMonitorDeleteModalProps> = ({ isOpen, toggle, agentMonitor, onDelete }) => {
  const [deleting, setDeleting] = useState(false);

  const handleDelete = async () => {
    if (!agentMonitor?.id) return;

    setDeleting(true);
    try {
      await axios.delete(`/api/agent-monitors/${agentMonitor.id}`);
      toast.success('Agent monitor deleted successfully');
      onDelete?.();
      toggle();
    } catch (error) {
      toast.error('Failed to delete agent monitor');
    } finally {
      setDeleting(false);
    }
  };

  return (
    <Modal isOpen={isOpen} toggle={toggle}>
      <ModalHeader toggle={toggle}>Confirm Delete</ModalHeader>
      <ModalBody>Are you sure you want to delete this agent monitor assignment?</ModalBody>
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

export default AgentMonitorDeleteModal;
