import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Agent from './agent';
import AgentDetail from './agent-detail';
import AgentDeleteDialog from './agent-delete-dialog';

const AgentRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Agent />} />
    <Route path=":id">
      <Route index element={<AgentDetail />} />
      <Route path="delete" element={<AgentDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AgentRoutes;
