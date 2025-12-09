import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Instance from './instance';

const InstanceRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Instance />} />
  </ErrorBoundaryRoutes>
);

export default InstanceRoutes;
