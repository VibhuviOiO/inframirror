import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import MonitoredService from './monitored-service';

const MonitoredServiceRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<MonitoredService />} />
  </ErrorBoundaryRoutes>
);

export default MonitoredServiceRoutes;
