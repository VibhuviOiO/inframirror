import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';
import { DropdownItem } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Translate, translate } from 'react-jhipster';
import { getLoginUrl } from 'app/shared/util/url-utils';
import { useLocation, useNavigate } from 'react-router';
import { NavDropdown } from './menu-components';

const accountMenuItemsAuthenticated = () => (
  <>
    <MenuItem icon="lock" to="/account/password" data-cy="passwordItem">
      <Translate contentKey="global.menu.account.password">Password</Translate>
    </MenuItem>
    <MenuItem icon="sign-out-alt" to="/logout" data-cy="logout">
      <Translate contentKey="global.menu.account.logout">Sign out</Translate>
    </MenuItem>
  </>
);

const accountMenuItems = () => {
  const navigate = useNavigate();
  const pageLocation = useLocation();

  return (
    <>
      <DropdownItem
        id="login-item"
        tag="a"
        data-cy="login"
        onClick={() =>
          navigate(getLoginUrl(), {
            state: { from: pageLocation },
          })
        }
      >
        <FontAwesomeIcon icon="sign-in-alt" /> <Translate contentKey="global.menu.account.login">Sign in</Translate>
      </DropdownItem>
    </>
  );
};

const computeInitials = (name?: string) => {
  if (!name) return undefined;
  const parts = name.trim().split(/\s+/).filter(Boolean);
  if (parts.length >= 2) {
    return `${parts[0][0]}${parts[1][0]}`.toUpperCase();
  }
  const single = parts[0];
  return single.slice(0, 2).toUpperCase();
};

export const AccountMenu = ({ isAuthenticated = false, displayName }: { isAuthenticated?: boolean; displayName?: string }) => {
  const initial = computeInitials(displayName);

  return (
    <NavDropdown
      icon="user"
      name={translate('global.menu.account.main')}
      id="account-menu"
      data-cy="accountMenu"
      className="account-nav"
      toggleClassName="account-toggle"
      avatar={initial}
    >
      {isAuthenticated && accountMenuItemsAuthenticated()}
      {!isAuthenticated && accountMenuItems()}
    </NavDropdown>
  );
};

export default AccountMenu;
