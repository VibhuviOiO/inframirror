import React, { useState } from 'react';
import axios from 'axios';
import { Translate, translate } from 'react-jhipster';
import { Alert, Button, Form, FormGroup, Input, Label } from 'reactstrap';

import PasswordStrengthBar from 'app/shared/layout/password/password-strength-bar';
import './password.scss';

const Password = () => {
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setSuccess(false);
    setError(null);

    if (newPassword !== confirmPassword) {
      setError(translate('global.messages.error.dontmatch'));
      return;
    }

    try {
      await axios.post('api/account/change-password', { currentPassword, newPassword });
      setSuccess(true);
      setCurrentPassword('');
      setNewPassword('');
      setConfirmPassword('');
    } catch (e: any) {
      if (e?.response?.status === 404) {
        setError(translate('password.notSupported'));
        return;
      }
      setError('Could not update password. Please check your current password.');
    }
  };

  return (
    <div className="password-page">
      <div className="password-card">
        <div className="password-card__header">
          <h2>
            <Translate contentKey="global.menu.account.password">Password</Translate>
          </h2>
          <p className="password-card__subtitle">
            <Translate contentKey="password.subtitle">Update your InfraMirror password.</Translate>
          </p>
        </div>

        {success && <Alert color="success">Password updated successfully.</Alert>}
        {error && <Alert color="danger">{error}</Alert>}

        <Form onSubmit={handleSubmit} data-cy="passwordForm">
          <FormGroup>
            <Label for="currentPassword">
              <Translate contentKey="global.form.currentpassword.label">Current password</Translate>
            </Label>
            <Input
              id="currentPassword"
              name="currentPassword"
              type="password"
              autoComplete="current-password"
              value={currentPassword}
              onChange={e => setCurrentPassword(e.target.value)}
              required
              data-cy="currentPassword"
            />
          </FormGroup>

          <FormGroup>
            <Label for="newPassword">
              <Translate contentKey="global.form.newpassword.label">New password</Translate>
            </Label>
            <Input
              id="newPassword"
              name="newPassword"
              type="password"
              autoComplete="new-password"
              value={newPassword}
              onChange={e => setNewPassword(e.target.value)}
              required
              data-cy="newPassword"
            />
            <PasswordStrengthBar password={newPassword} />
          </FormGroup>

          <FormGroup>
            <Label for="confirmPassword">
              <Translate contentKey="global.form.confirmpassword.label">Confirm new password</Translate>
            </Label>
            <Input
              id="confirmPassword"
              name="confirmPassword"
              type="password"
              autoComplete="new-password"
              value={confirmPassword}
              onChange={e => setConfirmPassword(e.target.value)}
              required
              data-cy="confirmPassword"
            />
          </FormGroup>

          <div className="password-card__actions">
            <Button color="primary" type="submit" data-cy="submitPassword">
              <Translate contentKey="entity.action.save">Save</Translate>
            </Button>
          </div>
        </Form>
      </div>
    </div>
  );
};

export default Password;
