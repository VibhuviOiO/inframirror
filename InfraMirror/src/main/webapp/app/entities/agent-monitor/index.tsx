import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import AgentMonitor from './agent-monitor';

const AgentMonitorRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<AgentMonitor />} />
  </ErrorBoundaryRoutes>
);

export default AgentMonitorRoutes;
