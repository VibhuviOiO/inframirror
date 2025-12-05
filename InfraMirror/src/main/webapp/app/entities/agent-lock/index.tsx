import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import AgentLock from './agent-lock';
import AgentLockDetail from './agent-lock-detail';
import AgentLockUpdate from './agent-lock-update';
import AgentLockDeleteDialog from './agent-lock-delete-dialog';

const AgentLockRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<AgentLock />} />
    <Route path="new" element={<AgentLockUpdate />} />
    <Route path=":id">
      <Route index element={<AgentLockDetail />} />
      <Route path="edit" element={<AgentLockUpdate />} />
      <Route path="delete" element={<AgentLockDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default AgentLockRoutes;
