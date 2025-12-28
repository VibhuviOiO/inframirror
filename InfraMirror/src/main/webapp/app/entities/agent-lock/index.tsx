import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import AgentLock from './agent-lock';

const AgentLockRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<AgentLock />} />
  </ErrorBoundaryRoutes>
);

export default AgentLockRoutes;
