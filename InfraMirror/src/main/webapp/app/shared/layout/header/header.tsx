import './header.scss';

import React, { useState } from 'react';
import { theme } from 'app/config/theme';
import { Storage, Translate } from 'react-jhipster';
import { Collapse, Nav, Navbar, NavbarToggler } from 'reactstrap';
import LoadingBar from 'react-redux-loading-bar';

import { useAppDispatch } from 'app/config/store';
import { setLocale } from 'app/shared/reducers/locale';
import { AccountMenu, LocaleMenu } from '../menus';
import { Brand, Home } from './header-components';
import { FaBars } from 'react-icons/fa';

export interface IHeaderProps {
  isAuthenticated: boolean;
  isAdmin: boolean;
  ribbonEnv: string;
  isInProduction: boolean;
  isOpenAPIEnabled: boolean;
  currentLocale: string;
  onSidebarToggle?: () => void;
}

const Header = (props: IHeaderProps) => {
  const [menuOpen, setMenuOpen] = useState(false);

  const dispatch = useAppDispatch();

  const handleLocaleChange = event => {
    const langKey = event.target.value;
    Storage.session.set('locale', langKey);
    dispatch(setLocale(langKey));
  };

  const renderDevRibbon = () =>
    props.isInProduction === false ? (
      <div className="ribbon dev">
        <a href="">
          <Translate contentKey={`global.ribbon.${props.ribbonEnv}`} />
        </a>
      </div>
    ) : null;

  const toggleMenu = () => setMenuOpen(!menuOpen);

  /* jhipster-needle-add-element-to-menu - JHipster will add new menu items here */

  return (
    <div id="app-header">
      {renderDevRibbon()}
      <LoadingBar className="loading-bar" />
      <div className="navbar-wrapper">
        {props.isAuthenticated && props.onSidebarToggle && (
          <button onClick={props.onSidebarToggle} aria-label="Toggle Sidebar" className="sidebar-toggle-btn">
            <FaBars />
          </button>
        )}
        <Navbar data-cy="navbar" dark expand="md" className="jh-navbar">
          <NavbarToggler aria-label="Menu" onClick={toggleMenu} />
          <Brand />
          <Collapse isOpen={menuOpen} navbar>
            <Nav id="header-tabs" className="ms-auto" navbar>
              {/* <Home /> */}
              {/* <LocaleMenu currentLocale={props.currentLocale} onClick={handleLocaleChange} /> */}
              <AccountMenu isAuthenticated={props.isAuthenticated} />
            </Nav>
          </Collapse>
        </Navbar>
      </div>
    </div>
  );
};

export default Header;
