import React, { useState } from 'react';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button, Alert, FormGroup, Label, Input } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCopy, faCheck } from '@fortawesome/free-solid-svg-icons';

interface ApiKeyShowModalProps {
  isOpen: boolean;
  toggle: () => void;
  apiKey: any;
}

export const ApiKeyShowModal: React.FC<ApiKeyShowModalProps> = ({ isOpen, toggle, apiKey }) => {
  const [copied, setCopied] = useState(false);

  const copyToClipboard = () => {
    if (apiKey?.plainTextKey) {
      navigator.clipboard.writeText(apiKey.plainTextKey);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    }
  };

  return (
    <Modal isOpen={isOpen} toggle={toggle} size="lg" centered>
      <ModalHeader toggle={toggle}>API Key Created Successfully</ModalHeader>
      <ModalBody>
        <Alert color="warning">
          <strong>Important!</strong> This is the only time you will see this API key. Please copy it now and store it securely.
        </Alert>

        <FormGroup>
          <Label>API Key</Label>
          <div className="input-group">
            <Input type="text" value={apiKey?.plainTextKey || ''} readOnly className="font-monospace" />
            <Button color="primary" onClick={copyToClipboard}>
              <FontAwesomeIcon icon={copied ? faCheck : faCopy} /> {copied ? 'Copied!' : 'Copy'}
            </Button>
          </div>
        </FormGroup>
      </ModalBody>
      <ModalFooter>
        <Button color="primary" onClick={toggle}>
          Close
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default ApiKeyShowModal;
