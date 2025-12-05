import React, { useMemo } from 'react';
import { Translate } from 'react-jhipster';

import { NavItem, NavLink, NavbarBrand, UncontrolledDropdown, DropdownToggle, DropdownMenu } from 'reactstrap';
import { NavLink as Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

export const BrandIcon = props => (
  <div {...props} className="brand-icon">
    <img src="content/images/logo-jhipster.png" alt="Logo" />
  </div>
);

export const Brand = () => (
  <NavbarBrand tag={Link} to="/" className="brand-logo">
    <BrandIcon />
    <span className="brand-title">
      <Translate contentKey="global.title">InfraMirror</Translate>
    </span>
    <span className="navbar-version">{VERSION.toLowerCase().startsWith('v') ? VERSION : `v${VERSION}`}</span>
  </NavbarBrand>
);

export const Home = () => (
  <NavItem>
    <NavLink tag={Link} to="/" className="d-flex align-items-center">
      <FontAwesomeIcon icon="home" />
      <span>
        <Translate contentKey="global.menu.home">Home</Translate>
      </span>
    </NavLink>
  </NavItem>
);

type AppLauncherProps = {
  apps: string[];
};

export const AppLauncher = ({ apps }: AppLauncherProps) => {
  const dotGrid = useMemo(() => new Array(9).fill(0), []);

  return (
    <UncontrolledDropdown nav inNavbar className="app-launcher" aria-label="apps menu">
      <DropdownToggle nav className="launcher-toggle" caret={false}>
        <div className="launcher-dots" aria-hidden>
          {dotGrid.map((_, idx) => (
            <span key={idx} className="dot" />
          ))}
        </div>
      </DropdownToggle>
      <DropdownMenu end className="launcher-menu">
        <div className="launcher-title">Monitored services</div>
        <div className="launcher-grid">
          {apps.map(app => (
            <div key={app} className="launcher-tile">
              <div className="tile-dot" />
              <span>{app}</span>
            </div>
          ))}
        </div>
      </DropdownMenu>
    </UncontrolledDropdown>
  );
};
