import React from 'react';

import { DropdownMenu, DropdownToggle, UncontrolledDropdown } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

export const NavDropdown = props => {
  const hasAvatar = Boolean(props.avatar);
  const toggleClass = props.toggleClassName ?? 'd-flex align-items-center gap-1';

  return (
    <UncontrolledDropdown nav inNavbar id={props.id} data-cy={props['data-cy']} className={props.className}>
      <DropdownToggle nav caret className={toggleClass}>
        {hasAvatar ? <span className="avatar-initial">{props.avatar}</span> : <FontAwesomeIcon icon={props.icon} />}
        <span>{props.name}</span>
      </DropdownToggle>
      <DropdownMenu end style={props.style}>
        {props.children}
      </DropdownMenu>
    </UncontrolledDropdown>
  );
};
