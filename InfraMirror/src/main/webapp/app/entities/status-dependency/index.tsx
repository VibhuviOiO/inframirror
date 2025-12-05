import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import StatusDependency from './status-dependency';
import StatusDependencyDetail from './status-dependency-detail';
import StatusDependencyUpdate from './status-dependency-update';
import StatusDependencyDeleteDialog from './status-dependency-delete-dialog';

const StatusDependencyRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<StatusDependency />} />
    <Route path="new" element={<StatusDependencyUpdate />} />
    <Route path=":id">
      <Route index element={<StatusDependencyDetail />} />
      <Route path="edit" element={<StatusDependencyUpdate />} />
      <Route path="delete" element={<StatusDependencyDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default StatusDependencyRoutes;
