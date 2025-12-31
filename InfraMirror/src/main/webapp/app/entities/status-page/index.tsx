import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import StatusPage from './status-page';
import StatusPageDeleteDialog from './status-page-delete-dialog';
import StatusPageView from './status-page-view';

const StatusPageRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<StatusPage />} />
    <Route path="view/:slug" element={<StatusPageView />} />
    <Route path=":id">
      <Route path="delete" element={<StatusPageDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default StatusPageRoutes;
