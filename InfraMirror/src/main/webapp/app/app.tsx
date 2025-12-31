import 'react-toastify/dist/ReactToastify.css';
import './app.scss';
import 'app/config/dayjs';

import React, { useEffect } from 'react';
import { Card } from 'reactstrap';
import { BrowserRouter, useLocation } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getSession } from 'app/shared/reducers/authentication';
import { getProfile } from 'app/shared/reducers/application-profile';
import Header from 'app/shared/layout/header/header';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import ErrorBoundary from 'app/shared/error/error-boundary';
import { AUTHORITIES } from 'app/config/constants';
import AppRoutes from 'app/routes';
import { setTextDirection } from './config/translation';
import Sidebar from 'app/components/Sidebar';

const baseHref = document.querySelector('base').getAttribute('href').replace(/\/$/, '');

const AppContent = () => {
  const location = useLocation();
  const dispatch = useAppDispatch();
  const [sidebarCollapsed, setSidebarCollapsed] = React.useState(true);
  const [hasSecondarySidebar, setHasSecondarySidebar] = React.useState(false);

  const currentLocale = useAppSelector(state => state.locale.currentLocale);
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);
  const isAdmin = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));
  const ribbonEnv = useAppSelector(state => state.applicationProfile.ribbonEnv);
  const isInProduction = useAppSelector(state => state.applicationProfile.inProduction);
  const isOpenAPIEnabled = useAppSelector(state => state.applicationProfile.isOpenAPIEnabled);

  const isPublicStatusPage = location.pathname.startsWith('/s/');

  if (isPublicStatusPage) {
    return (
      <div className="app-container">
        <ToastContainer position="top-left" className="toastify-container" toastClassName="toastify-toast" />
        <ErrorBoundary>
          <AppRoutes />
        </ErrorBoundary>
      </div>
    );
  }

  const paddingTop = '60px';
  return (
    <div className="app-container" style={{ paddingTop }}>
      <ToastContainer position="top-left" className="toastify-container" toastClassName="toastify-toast" />
      <ErrorBoundary>
        <Header
          isAuthenticated={isAuthenticated}
          isAdmin={isAdmin}
          currentLocale={currentLocale}
          ribbonEnv={ribbonEnv}
          isInProduction={isInProduction}
          isOpenAPIEnabled={isOpenAPIEnabled}
          onSidebarToggle={() => setSidebarCollapsed(!sidebarCollapsed)}
        />
      </ErrorBoundary>
      <div className="container-fluid view-container" id="app-view-container">
        <Sidebar isAuthenticated={isAuthenticated} isAdmin={isAdmin} isCollapsed={sidebarCollapsed} />
        <div
          style={{
            marginLeft: isAuthenticated ? (sidebarCollapsed ? (hasSecondarySidebar ? '310px' : '60px') : '250px') : '0',
            transition: 'margin-left 0.3s ease',
          }}
        >
          <ErrorBoundary>
            <AppRoutes />
          </ErrorBoundary>
        </div>
      </div>
    </div>
  );
};

export const App = () => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getSession());
    dispatch(getProfile());
  }, []);

  const currentLocale = useAppSelector(state => state.locale.currentLocale);

  useEffect(() => {
    setTextDirection(currentLocale);
  }, [currentLocale]);

  return (
    <BrowserRouter basename={baseHref}>
      <AppContent />
    </BrowserRouter>
  );
};

export default App;
