import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ServiceHeartbeat from './service-heartbeat';
import ServiceHeartbeatDetail from './service-heartbeat-detail';
import ServiceHeartbeatUpdate from './service-heartbeat-update';
import ServiceHeartbeatDeleteDialog from './service-heartbeat-delete-dialog';

const ServiceHeartbeatRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ServiceHeartbeat />} />
    <Route path="new" element={<ServiceHeartbeatUpdate />} />
    <Route path=":id">
      <Route index element={<ServiceHeartbeatDetail />} />
      <Route path="edit" element={<ServiceHeartbeatUpdate />} />
      <Route path="delete" element={<ServiceHeartbeatDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ServiceHeartbeatRoutes;
