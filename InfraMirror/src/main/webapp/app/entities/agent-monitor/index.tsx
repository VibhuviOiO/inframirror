import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import AgentMonitor from './agent-monitor';
import AgentMonitorDetail from './agent-monitor-detail';
import AgentMonitorUpdate from './agent-monitor-update';
import AgentMonitorDeleteDialog from './agent-monitor-delete-dialog';

const AgentMonitorRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<AgentMonitor />} />
    <Route path="new" element={<AgentMonitorUpdate />} />
    <Route path=":id">
      <Route index element={<AgentMonitorDetail />} />
      <Route path="edit" element={<AgentMonitorUpdate />} />
      <Route path="delete" element={<AgentMonitorDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AgentMonitorRoutes;
