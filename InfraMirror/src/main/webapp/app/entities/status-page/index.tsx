import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import StatusPage from './status-page';
import StatusPageDetail from './status-page-detail';
import StatusPageUpdate from './status-page-update';
import StatusPageDeleteDialog from './status-page-delete-dialog';

const StatusPageRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<StatusPage />} />
    <Route path="new" element={<StatusPageUpdate />} />
    <Route path=":id">
      <Route index element={<StatusPageDetail />} />
      <Route path="edit" element={<StatusPageUpdate />} />
      <Route path="delete" element={<StatusPageDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default StatusPageRoutes;
