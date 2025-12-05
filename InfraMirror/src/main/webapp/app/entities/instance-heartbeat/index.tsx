import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import InstanceHeartbeat from './instance-heartbeat';
import InstanceHeartbeatDetail from './instance-heartbeat-detail';
import InstanceHeartbeatUpdate from './instance-heartbeat-update';
import InstanceHeartbeatDeleteDialog from './instance-heartbeat-delete-dialog';

const InstanceHeartbeatRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<InstanceHeartbeat />} />
    <Route path="new" element={<InstanceHeartbeatUpdate />} />
    <Route path=":id">
      <Route index element={<InstanceHeartbeatDetail />} />
      <Route path="edit" element={<InstanceHeartbeatUpdate />} />
      <Route path="delete" element={<InstanceHeartbeatDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default InstanceHeartbeatRoutes;
