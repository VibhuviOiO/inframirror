import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import MonitoredService from './monitored-service';
import MonitoredServiceDetail from './monitored-service-detail';
import MonitoredServiceUpdate from './monitored-service-update';
import MonitoredServiceDeleteDialog from './monitored-service-delete-dialog';

const MonitoredServiceRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<MonitoredService />} />
    <Route path="new" element={<MonitoredServiceUpdate />} />
    <Route path=":id">
      <Route index element={<MonitoredServiceDetail />} />
      <Route path="edit" element={<MonitoredServiceUpdate />} />
      <Route path="delete" element={<MonitoredServiceDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default MonitoredServiceRoutes;
