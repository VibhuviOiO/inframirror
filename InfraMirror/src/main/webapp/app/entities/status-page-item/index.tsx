import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import StatusPageItem from './status-page-item';
import StatusPageItemDetail from './status-page-item-detail';
import StatusPageItemUpdate from './status-page-item-update';
import StatusPageItemDeleteDialog from './status-page-item-delete-dialog';

const StatusPageItemRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<StatusPageItem />} />
    <Route path="new" element={<StatusPageItemUpdate />} />
    <Route path=":id">
      <Route index element={<StatusPageItemDetail />} />
      <Route path="edit" element={<StatusPageItemUpdate />} />
      <Route path="delete" element={<StatusPageItemDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default StatusPageItemRoutes;
