import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ApiKey from './api-key';
import ApiKeyDetail from './api-key-detail';
import ApiKeyUpdate from './api-key-update';
import ApiKeyDeleteDialog from './api-key-delete-dialog';

const ApiKeyRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ApiKey />} />
    <Route path="new" element={<ApiKeyUpdate />} />
    <Route path=":id">
      <Route index element={<ApiKeyDetail />} />
      <Route path="edit" element={<ApiKeyUpdate />} />
      <Route path="delete" element={<ApiKeyDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ApiKeyRoutes;
